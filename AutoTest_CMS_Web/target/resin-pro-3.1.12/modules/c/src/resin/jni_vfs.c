/*
 * Copyright (c) 1998-2008 Caucho Technology -- all rights reserved
 *
 * @author Scott Ferguson
 */

#ifdef WIN32
#ifndef _WINSOCKAPI_ 
#define _WINSOCKAPI_
#endif 
#include <windows.h>
#include <winsock2.h>
#include <io.h>
#else
#include <sys/types.h>
#include <sys/socket.h>
#include <dirent.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <sys/time.h>
#ifdef EPOLL
#include <sys/epoll.h>
#endif
#ifdef POLL
#include <sys/poll.h>
#else
#include <sys/select.h>
#endif
#include <pwd.h>
#include <syslog.h>
#include <netdb.h>
#endif

#ifdef linux
#include <linux/version.h>
#endif

#include <sys/stat.h>
#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <string.h>
#include <signal.h>
#include <errno.h>
/* probably system-dependent */
#include <jni.h>

#include "resin.h"

#define SELECT_MAX 65536

typedef struct select_t {
  int max;
  int has_more;
  int has_update;

  int pipe[2];
  pthread_t thread_id;
  int epoll_fd;
  
#ifndef WIN32
  pthread_mutex_t lock;
#endif
#ifdef POLL
  struct pollfd poll_items[SELECT_MAX];
#else
  fd_set select_items;
#endif
} select_t;

#define STACK_BUFFER_SIZE (16 * 1024)

void
cse_log(char *fmt, ...)
{
#ifdef DEBUG  
  va_list list;

  va_start(list, fmt);
  vfprintf(stderr, fmt, list);
  va_end(list);
#endif
}

static char *
q_strdup(char *str)
{
  int len = strlen(str);
  char *dup = cse_malloc(len + 1);

  strcpy(dup, str);

  return dup;
}

/*
 * Issues with SetByteArrayRegion, Windows/Solaris
 * see RSN-113, RSN-34, RSN-230
 */
#if 1

static int
set_byte_array_region(JNIEnv *env, jbyteArray j_buf, jint offset, jint sublen,
		      char *c_buf)
{
  (*env)->SetByteArrayRegion(env, j_buf, offset, sublen, (void*) c_buf);
  
  return 1;
}

static int
get_byte_array_region(JNIEnv *env, jbyteArray buf, jint offset, jint sublen,
		      char *buffer)
{
  (*env)->GetByteArrayRegion(env, buf, offset, sublen, (void*) buffer);
  
  return 1;
}

#else

static int
set_byte_array_region(JNIEnv *env, jbyteArray buf, jint offset, jint sublen,
		      char *buffer)
{
  jbyte *cBuf;
  
  cBuf = (*env)->GetPrimitiveArrayCritical(env, buf, 0);

  if (cBuf) {
    memcpy(cBuf + offset, buffer, sublen);

    (*env)->ReleasePrimitiveArrayCritical(env, buf, cBuf, 0);

    return 1;
  }
  
  return 0;
}

static int
get_byte_array_region(JNIEnv *env, jbyteArray buf, jint offset, jint sublen,
		      char *buffer)
{
  jbyte *cBuf;
 
  cBuf = (*env)->GetPrimitiveArrayCritical(env, buf, 0);

  if (cBuf) {
    memcpy(buffer, cBuf + offset, sublen);

    (*env)->ReleasePrimitiveArrayCritical(env, buf, cBuf, 0);

    return 1;
  }
  
  return 0;
}

#endif

JNIEXPORT jlong JNICALL
Java_com_caucho_vfs_JniSocketImpl_nativeAllocate(JNIEnv *env,
						 jobject obj)
{
  connection_t *conn;

  conn = (connection_t *) cse_malloc(sizeof(connection_t));
  
  memset(conn, 0, sizeof(connection_t));
  conn->fd = -1;
  conn->client_sin = (struct sockaddr *) conn->client_data;
  conn->server_sin = (struct sockaddr *) conn->server_data;

  conn->ops = &std_ops;

#ifdef WIN32
  // conn->event = WSACreateEvent();
#endif

  return (jlong) (PTR) conn;
}

JNIEXPORT jint JNICALL
Java_com_caucho_vfs_JniSocketImpl_readNative(JNIEnv *env,
					     jobject obj,
					     jlong conn_fd,
					     jbyteArray buf,
					     jint offset,
					     jint length,
					     jlong timeout)
{
  connection_t *conn = (connection_t *) (PTR) conn_fd;
  int sublen;
  char buffer[STACK_BUFFER_SIZE];

  if (! conn || conn->fd < 0)
    return -1;

  conn->jni_env = env;

  if (length < STACK_BUFFER_SIZE)
    sublen = length;
  else
    sublen = STACK_BUFFER_SIZE;

  sublen = conn->ops->read(conn, buffer, sublen, (int) timeout);

  /* Should probably have a different response for EINTR */
  if (sublen < 0) {
    return sublen;
  }

  set_byte_array_region(env, buf, offset, sublen, buffer);

  return sublen;
}

JNIEXPORT jint JNICALL
Java_com_caucho_vfs_JniStream_readNonBlockNative(JNIEnv *env,
						 jobject obj,
						 jlong conn_fd,
						 jbyteArray buf,
						 jint offset,
						 jint length)
{
  connection_t *conn = (connection_t *) (PTR) conn_fd;
  int sublen;
  char buffer[STACK_BUFFER_SIZE];

  if (! conn || conn->fd < 0)
    return -1;

  conn->jni_env = env;

  if (length < STACK_BUFFER_SIZE)
    sublen = length;
  else
    sublen = STACK_BUFFER_SIZE;

  sublen = conn->ops->read_nonblock(conn, buffer, sublen);

  /* Should probably have a different response for EINTR */
  if (sublen < 0)
    return sublen;

  set_byte_array_region(env, buf, offset, sublen, buffer);

  return sublen;
}

JNIEXPORT jint JNICALL
Java_com_caucho_vfs_JniSocketImpl_writeNative(JNIEnv *env,
					      jobject obj,
					      jlong conn_fd,
					      jbyteArray buf,
					      jint offset,
					      jint length)
{
  connection_t *conn = (connection_t *) (PTR) conn_fd;
  char buffer[STACK_BUFFER_SIZE];
  int sublen;
  int write_length = 0;

  if (! conn || conn->fd < 0 || ! buf)
    return -1;
  
  conn->jni_env = env;

  while (length > 0) {
    int result;
    
    if (length < sizeof(buffer))
      sublen = length;
    else
      sublen = sizeof(buffer);

    get_byte_array_region(env, buf, offset, sublen, buffer);
    
    result = conn->ops->write(conn, buffer, sublen);
    
    if (result < 0) {
      return result;
    }

    length -= result;
    offset += result;
    write_length += result;
  }

  return write_length;
}

JNIEXPORT jint JNICALL
Java_com_caucho_vfs_JniSocketImpl_writeNative2(JNIEnv *env,
					       jobject obj,
					       jlong conn_fd,
					       jbyteArray buf1,
					       jint off1,
					       jint len1,
					       jbyteArray buf2,
					       jint off2,
					       jint len2)
{
  connection_t *conn = (connection_t *) (PTR) conn_fd;
  char buffer[2 * STACK_BUFFER_SIZE];
  int sublen;
  int buffer_offset;
  int write_length = 0;

  buffer_offset = 0;

  if (! conn || conn->fd < 0 || ! buf1 || ! buf2)
    return -1;
  
  conn->jni_env = env;

  while (sizeof(buffer) < len1) {
    sublen = sizeof(buffer);
    
    get_byte_array_region(env, buf1, off1, sublen, buffer);
      
    sublen = conn->ops->write(conn, buffer, sublen);

    if (sublen < 0) {
      /* XXX: probably should throw exception */
      return sublen;
    }

    len1 -= sublen;
    off1 += sublen;
    write_length += sublen;
  }

  get_byte_array_region(env, buf1, off1, len1, buffer);
  buffer_offset = len1;

  while (buffer_offset + len2 > 0) {
    int result;
    
    if (len2 < sizeof(buffer) - buffer_offset)
      sublen = len2;
    else
      sublen = sizeof(buffer) - buffer_offset;

    get_byte_array_region(env, buf2, off2, sublen,
			       buffer + buffer_offset);
      
    result = conn->ops->write(conn, buffer, buffer_offset + sublen);

    if (result < 0) {
      /* XXX: probably should throw exception */
      return result;
    }

    len2 -= sublen;
    off2 += sublen;
    write_length += sublen + buffer_offset;
    buffer_offset = 0;
  }

  return write_length;
}

JNIEXPORT jint JNICALL
Java_com_caucho_vfs_JniSocketImpl_flushNative(JNIEnv *env,
					      jobject obj,
					      jlong conn_fd)
{
  connection_t *conn = (connection_t *) (PTR) conn_fd;

  if (! conn)
    return -1;
  else
    return 0;

  /* return cse_flush_request(res); */
}

JNIEXPORT void JNICALL
Java_com_caucho_vfs_JniSocketImpl_nativeClose(JNIEnv *env,
					      jobject obj,
					      jlong conn_fd)
{
  connection_t *conn = (connection_t *) (PTR) conn_fd;

  if (conn && conn->fd >= 0) {
    conn->jni_env = env;

    conn->ops->close(conn);
  }
}

JNIEXPORT void JNICALL
Java_com_caucho_vfs_JniSocketImpl_nativeFree(JNIEnv *env,
					     jobject obj,
					     jlong conn_fd)
{
  connection_t *conn = (connection_t *) (PTR) conn_fd;

  if (conn) {
#ifdef WIN32
	  /*
    if (conn->event)
      WSACloseEvent(conn->event);
	  */
#endif
    cse_free(conn);
  }
}

JNIEXPORT jboolean JNICALL
Java_com_caucho_vfs_JniSocketImpl_isSecure(JNIEnv *env,
                                        jobject obj,
                                        jlong conn_fd)
{
  connection_t *conn = (connection_t *) (PTR) conn_fd;

  if (! conn)
    return 0;
  
  return conn->sock != 0 && conn->ssl_cipher != 0;
}

JNIEXPORT jstring JNICALL
Java_com_caucho_vfs_JniSocketImpl_getCipher(JNIEnv *env,
                                         jobject obj,
                                         jlong conn_fd)
{
  connection_t *conn = (connection_t *) (PTR) conn_fd;

  if (! conn || ! conn->sock || ! conn->ssl_cipher)
    return 0;

  return (*env)->NewStringUTF(env, conn->ssl_cipher);
}

JNIEXPORT jint JNICALL
Java_com_caucho_vfs_JniSocketImpl_getCipherBits(JNIEnv *env,
					     jobject obj,
					     jlong conn_fd)
{
  connection_t *conn = (connection_t *) (PTR) conn_fd;

  if (! conn || ! conn->sock)
    return 0;
  else
    return conn->ssl_bits;
}

#ifdef POLL
JNIEXPORT jboolean JNICALL
Java_com_caucho_vfs_JniSocketImpl_nativeReadNonBlock(JNIEnv *env,
                                                  jobject obj,
                                                  jlong conn_fd,
						  jint ms)
{
  connection_t *conn = (connection_t *) (PTR) conn_fd;
  struct pollfd poll_item[1];
  int fd;

  if (! conn)
    return 0;

  fd = conn->fd;

  if (fd < 0)
    return 0;

  poll_item[0].fd = fd;
  poll_item[0].events = POLLIN|POLLPRI;
  poll_item[0].revents = 0;

  return (poll(poll_item, 1, ms) > 0);
}
#else /* SELECT */
JNIEXPORT jboolean JNICALL
Java_com_caucho_vfs_JniSocketImpl_nativeReadNonBlock(JNIEnv *env,
                                                  jobject obj,
                                                  jlong conn_fd,
						  jint ms)
{
  connection_t *conn = (connection_t *) (PTR) conn_fd;
  fd_set read_set;
  struct timeval timeout;
  int result;
  int fd;

  if (! conn)
    return 0;

  fd = conn->fd;

  if (fd < 0)
    return 0;

  FD_ZERO(&read_set);
  FD_SET((unsigned int) fd, &read_set);

  timeout.tv_sec = ms / 1000;
  timeout.tv_usec = (ms % 1000) * 1000;

  result = select(fd + 1, &read_set, 0, 0, &timeout);

  return result > 0;
}
#endif

#ifdef AI_NUMERICHOST

static struct sockaddr_in *
lookup_addr(JNIEnv *env, char *addr_name, int port,
	    char *buffer, int *p_family, int *p_protocol,
	    int *p_sin_length)
{
  struct addrinfo hints;
  struct addrinfo *addr;
  struct sockaddr_in *sin;
  int sin_length;
  char port_name[16];
  
  memset(&hints, 0, sizeof(hints));

  hints.ai_socktype = SOCK_STREAM;
  hints.ai_family = PF_UNSPEC;
  hints.ai_flags = AI_NUMERICHOST;

  sprintf(port_name, "%d", port);

  if (getaddrinfo(addr_name, port_name, &hints, &addr)) {
    resin_printf_exception(env, "java/net/SocketException", "can't find address %s", addr_name);
    return 0;
  }

  *p_family = addr->ai_family;
  *p_protocol = addr->ai_protocol;
  sin_length = addr->ai_addrlen;
  memcpy(buffer, addr->ai_addr, sin_length);
  sin = (struct sockaddr_in *) buffer;
  freeaddrinfo(addr);

  *p_sin_length = sin_length;

  return sin;
}

#else

static struct sockaddr_in *
lookup_addr(JNIEnv *env, char *addr_name, int port,
	    char *buffer, int *p_family, int *p_protocol, int *p_sin_length)
{
  struct sockaddr_in *sin = (struct sockaddr_in *) buffer;
  
  memset(sin, 0, sizeof(struct sockaddr_in));

  *p_sin_length = sizeof(struct sockaddr_in);
  
  sin->sin_family = AF_INET;
  *p_family = AF_INET;
  *p_protocol = 0;

  sin->sin_addr.s_addr = inet_addr(addr_name);
 
  sin->sin_port = htons((unsigned short) port);

  return sin;
}

#endif

static void
init_server_socket(JNIEnv *env, server_socket_t *ss)
{
  jclass jniServerSocketClass;
  
  jniServerSocketClass = (*env)->FindClass(env, "com/caucho/vfs/JniSocketImpl");

  if (jniServerSocketClass) {
    ss->_isSecure = (*env)->GetFieldID(env, jniServerSocketClass,
				       "_isSecure", "Z");
    if (! ss->_isSecure)
      resin_throw_exception(env, "com/caucho/config/ConfigException",
			    "can't load _isSecure field");
      
    ss->_localAddrBuffer = (*env)->GetFieldID(env, jniServerSocketClass,
					      "_localAddrBuffer", "[B");
    if (! ss->_localAddrBuffer)
      resin_throw_exception(env, "com/caucho/config/ConfigException",
			    "can't load _localAddrBuffer field");
    
    ss->_localAddrLength = (*env)->GetFieldID(env, jniServerSocketClass,
					      "_localAddrLength", "I");
    if (! ss->_localAddrLength)
      resin_throw_exception(env, "com/caucho/config/ConfigException",
			    "can't load _localAddrLength field");
    
    ss->_localPort = (*env)->GetFieldID(env, jniServerSocketClass,
					"_localPort", "I");
    if (! ss->_localPort)
      resin_throw_exception(env, "com/caucho/config/ConfigException",
			    "can't load _localPort field");
      
    ss->_remoteAddrBuffer = (*env)->GetFieldID(env, jniServerSocketClass,
					       "_remoteAddrBuffer", "[B");
    if (! ss->_remoteAddrBuffer)
      resin_throw_exception(env, "com/caucho/config/ConfigException",
			    "can't load _remoteAddrBuffer field");
    
    ss->_remoteAddrLength = (*env)->GetFieldID(env, jniServerSocketClass,
					      "_remoteAddrLength", "I");
    if (! ss->_remoteAddrLength)
      resin_throw_exception(env, "com/caucho/config/ConfigException",
			    "can't load _remoteAddrLength field");
    
    ss->_remotePort = (*env)->GetFieldID(env, jniServerSocketClass,
					 "_remotePort", "I");
    if (! ss->_remotePort)
      resin_throw_exception(env, "com/caucho/config/ConfigException",
			    "can't load _remotePort field");
      
  }
}

JNIEXPORT jlong JNICALL
Java_com_caucho_vfs_JniServerSocketImpl_bindPort(JNIEnv *env,
						 jobject obj,
						 jstring jaddr,
						 jint port)
{
  int val = 0;
  char addr_name[256];
  const char *temp_string = 0;
  int sock;
  int family;
  int protocol;
  server_socket_t *ss;
  char sin_data[256];
  struct sockaddr_in *sin;
  int sin_length;

#ifdef WIN32
  {
	  WSADATA data;
	  WORD version = MAKEWORD(2,2);
	  WSAStartup(version, &data);
  }
#endif
  
  addr_name[0] = 0;

  if (jaddr != 0) {
    temp_string = (*env)->GetStringUTFChars(env, jaddr, 0);
  
    if (temp_string) {
      strncpy(addr_name, temp_string, sizeof(addr_name));
      addr_name[sizeof(addr_name) - 1] = 0;
  
      (*env)->ReleaseStringUTFChars(env, jaddr, temp_string);
    }
  }

  if (temp_string == 0) {
    resin_throw_exception(env, "java/lang/NullPointerException", "missing addr");
    return 0;
  }

  sin = lookup_addr(env, addr_name, port, sin_data,
		    &family, &protocol, &sin_length);
  if (! sin)
    return 0;

  sock = socket(family, SOCK_STREAM, 0);
  if (sock < 0) {
    return 0;
  }
  
  val = 1;
  if (setsockopt(sock, SOL_SOCKET, SO_REUSEADDR,
		 (char *) &val, sizeof(int)) < 0) {
    closesocket(sock);
    return 0;
  }

  if (bind(sock, (struct sockaddr *) sin, sin_length) < 0) {
    int i = 5;
    int result = 0;
    
    /* somewhat of a hack to clear the old connection. */
    while (result == 0 && i-- >= 0) {
      int fd = socket(AF_INET, SOCK_STREAM, 0);
      result = connect(fd, (struct sockaddr *) &sin, sizeof(sin));
      closesocket(fd);
    }

    result = -1;
    for (i = 50; result < 0 && i >= 0; i--) {
      result = bind(sock, (struct sockaddr *) sin, sin_length);

      if (result < 0) {
	struct timeval tv;

	tv.tv_sec = 0;
	tv.tv_usec = 100000;

	select(0, 0, 0, 0, &tv);
      }
    }

    if (result < 0) {
      closesocket(sock);
      return 0;
    }
  }

  /* must be 0 if the poll is missing for accept */
#if 0 && defined(O_NONBLOCK)
  /*
   * sets nonblock to ensure the timeout work in the case of multiple threads.
   */
  {
    int flags;
    int result;
    
    flags = fcntl(sock, F_GETFL);
    result = fcntl(sock, F_SETFL, O_NONBLOCK|flags);
  }
#endif

  ss = (server_socket_t *) cse_malloc(sizeof(server_socket_t));
  memset(ss, 0, sizeof(server_socket_t));

  ss->fd = sock;
  ss->port = port;
  
  ss->conn_socket_timeout = 65000;

  ss->accept = &std_accept;
  ss->close = &std_close_ss;

#ifdef WIN32
  ss->accept_lock = CreateMutex(0, 0, 0);
  ss->ssl_lock = CreateMutex(0, 0, 0);
#endif

  init_server_socket(env, ss);
  
  return (PTR) ss;
}

JNIEXPORT jlong JNICALL
Java_com_caucho_vfs_JniServerSocketImpl_nativeOpenPort(JNIEnv *env,
						       jobject obj,
						       jint sock,
						       jint port)
{
  server_socket_t *ss;

#ifdef WIN32
  {
	  WSADATA data;
	  WORD version = MAKEWORD(2,2);
	  WSAStartup(version, &data);
  }
#endif

  if (sock < 0)
    return 0;

  ss = (server_socket_t *) cse_malloc(sizeof(server_socket_t));

  if (ss == 0)
    return 0;
  
  memset(ss, 0, sizeof(server_socket_t));

  ss->fd = sock;
  ss->port = port;
  
  ss->conn_socket_timeout = 65000;

  ss->accept = &std_accept;
  ss->close = &std_close_ss;

#ifdef WIN32
  ss->accept_lock = CreateMutex(0, 0, 0);
  ss->ssl_lock = CreateMutex(0, 0, 0);
#endif
  
  init_server_socket(env, ss);
  
  return (PTR) ss;
}

JNIEXPORT void JNICALL
Java_com_caucho_vfs_JniServerSocketImpl_nativeSetConnectionSocketTimeout(JNIEnv *env,
						       jobject obj,
						       jlong ss_fd,
						       jint timeout)
{
  server_socket_t *ss = (server_socket_t *) (PTR) ss_fd;

  if (! ss)
    return;

  if (timeout < 0)
    timeout = 600 * 1000;
  else if (timeout < 500)
    timeout = 500;
  
  ss->conn_socket_timeout = timeout;
}

JNIEXPORT void JNICALL
Java_com_caucho_vfs_JniServerSocketImpl_nativeListen(JNIEnv *env,
						     jobject obj,
						     jlong ss_fd,
						     jint backlog)
{
  server_socket_t *ss = (server_socket_t *) (PTR) ss_fd;

  if (! ss || ss->fd < 0)
    return;

  if (backlog < 0)
    backlog = 0;

  if (backlog < 0)
    listen(ss->fd, 100);
  else
    listen(ss->fd, backlog);
}

JNIEXPORT jboolean JNICALL
Java_com_caucho_vfs_JniServerSocketImpl_nativeAccept(JNIEnv *env,
						     jobject obj,
						     jlong ss_fd,
						     jlong conn_fd)
{
  server_socket_t *socket = (server_socket_t *) (PTR) ss_fd;
  connection_t *conn = (connection_t *) (PTR) conn_fd;
  jboolean value;

  if (! socket || ! conn)
    return 0;

  value = socket->accept(socket, conn);

  return value;
}

JNIEXPORT jint JNICALL
Java_com_caucho_vfs_JniServerSocketImpl_getLocalPort(JNIEnv *env,
                                                  jobject obj,
                                                  jlong ss)
{
  server_socket_t *socket = (server_socket_t *) (PTR) ss;

  if (! socket)
    return 0;
  else
    return socket->port;
}

JNIEXPORT jint JNICALL
Java_com_caucho_vfs_JniServerSocketImpl_nativeGetSystemFD(JNIEnv *env,
							  jobject obj,
							  jlong ss)
{
  server_socket_t *socket = (server_socket_t *) (PTR) ss;

  if (! socket)
    return -1;
  else
    return socket->fd;
}

JNIEXPORT jboolean JNICALL
Java_com_caucho_vfs_JniServerSocketImpl_nativeSetSaveOnExec(JNIEnv *env,
							    jobject obj,
							    jlong ss)
{
#ifdef WIN32
  return 0;
#else
  server_socket_t *socket = (server_socket_t *) (PTR) ss;

  if (! socket)
    return 0;
  else {
    int fd = socket->fd;
    int arg = 0;

    if (fd < 0)
      return 0;

    /* sets the close on exec flag */
    arg = fcntl(fd, F_GETFD, 0);
    arg &= ~FD_CLOEXEC;
    return fcntl(fd, F_SETFD, arg) >= 0;
  }
#endif
}

JNIEXPORT jint JNICALL
Java_com_caucho_vfs_JniServerSocketImpl_closeNative(JNIEnv *env,
                                                 jobject obj,
                                                 jlong ss)
{
  server_socket_t *socket = (server_socket_t *) (PTR) ss;

  if (! socket)
    return 0;

  socket->close(socket);

  cse_free(socket);

  return 0;
}

#if ! defined(AF_INET6) || defined(WIN32)
static int
get_address(struct sockaddr *addr, char *dst, int length)
{
  struct sockaddr_in *sin = (struct sockaddr_in *) addr;
  char *result;

  if (! sin)
	  return 0;
      
  result = inet_ntoa(sin->sin_addr);

  if (result) {
    strncpy(dst, result, length);
	dst[length - 1] = 0;

    return strlen(dst);
  }
  else
    return 0;
}
#else

static int
get_address(struct sockaddr *addr, char *dst, int length)
{
  struct sockaddr_in *sin = (struct sockaddr_in *) addr;
  const char *result;
      
  if (sin->sin_family == AF_INET6) {
    struct sockaddr_in6 *sin6 = (struct sockaddr_in6 *) sin;
      
    result = inet_ntop(AF_INET6, &sin6->sin6_addr, dst, length);
  }
  else {
    result = inet_ntop(AF_INET, &sin->sin_addr, dst, length);
  }

  if (! result)
    return 0;
  else
    return strlen(result);
}
#endif

JNIEXPORT jboolean JNICALL
Java_com_caucho_vfs_JniSocketImpl_nativeInit(JNIEnv *env,
					     jobject obj,
					     jlong conn_fd)
{
  connection_t *conn = (connection_t *) (PTR) conn_fd;
  server_socket_t *ss;
  
  if (! conn || ! env || ! obj)
    return 0;

  ss = conn->ss;

  if (! ss)
    return 0;

  if (ss->_isSecure) {
    jboolean is_secure = conn->sock != 0 && conn->ssl_cipher != 0;
    
    (*env)->SetBooleanField(env, obj, ss->_isSecure, is_secure);
  }

  if (ss->_localAddrBuffer && ss->_localAddrLength) {
    jbyteArray addrBuffer;
    char temp_buf[1024];
    int len;

    addrBuffer = (*env)->GetObjectField(env, obj, ss->_localAddrBuffer);

    if (addrBuffer) {
      /* the 256 must match JniServerImpl */
      len = get_address(conn->server_sin, temp_buf, 256);

      set_byte_array_region(env, addrBuffer, 0, len, temp_buf);

      (*env)->SetIntField(env, obj, ss->_localAddrLength, len);
    }
  }

  if (ss->_localPort) {
    struct sockaddr_in *sin = (struct sockaddr_in *) conn->server_sin;
    jint local_port = ntohs(sin->sin_port);

    (*env)->SetIntField(env, obj, ss->_localPort, local_port);
  }

  if (ss->_remoteAddrBuffer && ss->_remoteAddrLength) {
    jbyteArray addrBuffer;
    char temp_buf[1024];
    int len;

    addrBuffer = (*env)->GetObjectField(env, obj, ss->_remoteAddrBuffer);

    if (addrBuffer) {
      /* the 256 must match JniServerImpl */
      len = get_address(conn->client_sin, temp_buf, 256);

      set_byte_array_region(env, addrBuffer, 0, len, temp_buf);

      (*env)->SetIntField(env, obj, ss->_remoteAddrLength, len);
    }
  }

  if (ss->_remotePort) {
    struct sockaddr_in *sin = (struct sockaddr_in *) conn->client_sin;
    jint remote_port = ntohs(sin->sin_port);

    (*env)->SetIntField(env, obj, ss->_remotePort, remote_port);
  }

  return 1;
}

JNIEXPORT jint JNICALL
Java_com_caucho_vfs_JniSocketImpl_getRemoteIP(JNIEnv *env,
                                           jobject obj,
                                           jlong conn_fd,
					   jbyteArray j_buffer,
					   jint offset,
					   jint length)
{
  connection_t *conn = (connection_t *) (PTR) conn_fd;
  int len = 0;
  char temp_buf[1024];
  if (! conn || ! j_buffer || ! env)
    return 0;

  len = get_address(conn->client_sin, temp_buf, sizeof(temp_buf));
  if (len > 0 && len < sizeof(temp_buf) && len < length)
    set_byte_array_region(env, j_buffer, offset, len, temp_buf);

 return len;
}

JNIEXPORT jint JNICALL
Java_com_caucho_vfs_JniSocketImpl_getRemotePort(JNIEnv *env,
                                             jobject obj,
                                             jlong conn_fd)
{
  connection_t *conn = (connection_t *) (PTR) conn_fd;
  struct sockaddr_in *sin;

  if (! conn)
    return 0;

  sin = (struct sockaddr_in *) conn->client_sin;

  return ntohs(sin->sin_port);
}

JNIEXPORT jint JNICALL
Java_com_caucho_vfs_JniSocketImpl_getLocalIP(JNIEnv *env,
					  jobject obj,
					  jlong conn_fd,
					  jbyteArray buffer,
					  jint offset,
					  jint length)
{
  connection_t *conn = (connection_t *) (PTR) conn_fd;
  int len = 0;
  char temp_buf[1024];
  
  if (! conn)
    return 0;

  len = get_address(conn->server_sin, temp_buf, length);

  set_byte_array_region(env, buffer, offset, len, temp_buf);

  return len;
}

JNIEXPORT jint JNICALL
Java_com_caucho_vfs_JniSocketImpl_getLocalPort(JNIEnv *env,
					    jobject obj,
					    jlong conn_fd)
{
  connection_t *conn = (connection_t *) (PTR) conn_fd;
  struct sockaddr_in *sin;

  if (! conn)
    return 0;

  sin = (struct sockaddr_in *) conn->server_sin;

  return ntohs(sin->sin_port);
}

JNIEXPORT jint JNICALL
Java_com_caucho_vfs_JniSocketImpl_getClientCertificate(JNIEnv *env,
                                                    jobject obj,
                                                    jlong conn_fd,
                                                    jbyteArray buf,
                                                    jint offset,
                                                    jint length)
{
  connection_t *conn = (connection_t *) (PTR) conn_fd;
  int sublen;
  char buffer[8192];

  if (! conn)
    return -1;

  if (length < 8192)
    sublen = length;
  else
    sublen = 8192;

  sublen = conn->ops->read_client_certificate(conn, buffer, sublen);

  /* Should probably have a different response for EINTR */
  if (sublen < 0 || length < sublen)
    return sublen;

  set_byte_array_region(env, buf, offset, sublen, buffer);

  return sublen;
}

JNIEXPORT jint JNICALL
Java_com_caucho_vfs_JniSocketImpl_getNativeFd(JNIEnv *env,
                                           jobject obj,
                                           jlong conn_fd)
{
  connection_t *conn = (connection_t *) (PTR) conn_fd;

  if (! conn)
    return -1;
  else
    return conn->fd;
}

static void
handle_alarm(int fo)
{
}

#ifdef WIN32

JNIEXPORT jlong JNICALL
Java_com_caucho_server_port_JniSelectManager_createNative(JNIEnv *env,
                                                          jobject obj)
{
  return 0;
}

JNIEXPORT void JNICALL
Java_com_caucho_server_port_JniSelectManager_initNative(JNIEnv *env,
                                                        jobject obj,
                                                        jlong manager_fd)
{
}

JNIEXPORT jint JNICALL
Java_com_caucho_server_port_JniSelectManager_addNative(JNIEnv *env,
                                                       jobject obj,
                                                       jlong manager_fd,
                                                       jint fd)
{
  return -1;
}

JNIEXPORT jint JNICALL
Java_com_caucho_server_port_JniSelectManager_removeNative(JNIEnv *env,
							  jobject obj,
							  jlong manager_fd,
							  jint fd)
{
  return -1;
}

JNIEXPORT jint JNICALL
Java_com_caucho_server_port_JniSelectManager_selectNative(JNIEnv *env,
                                                          jobject obj,
                                                          jlong manager_fd,
                                                          jlong ms)
{
  return -1;
}

JNIEXPORT void JNICALL
Java_com_caucho_server_port_JniSelectManager_closeNative(JNIEnv *env,
                                                         jobject obj,
                                                         jlong manager_fd)
{
}
#else

JNIEXPORT jlong JNICALL
Java_com_caucho_server_port_JniSelectManager_createNative(JNIEnv *env,
                                                          jobject obj)
{
  select_t *sel = cse_malloc(sizeof(select_t));

  if (sel == 0)
    return 0;

  memset(sel, 0, sizeof(select_t));
  
  return (PTR) sel;
}

#ifdef EPOLL
JNIEXPORT void JNICALL
Java_com_caucho_server_port_JniSelectManager_initNative(JNIEnv *env,
                                                        jobject obj,
                                                        jlong manager_fd)
{
  select_t *sel = (select_t *) (PTR) manager_fd;
  struct epoll_event ev;

  if (! sel)
    return;

  sel->thread_id = pthread_self();
  sel->epoll_fd = epoll_create(4096);
  
  pipe(sel->pipe);
  sel->max = 1;

  ev.events = EPOLLIN|EPOLLPRI|EPOLLERR|EPOLLHUP;
  ev.data.fd = sel->pipe[0];

  int result = epoll_ctl(sel->epoll_fd, EPOLL_CTL_ADD, sel->pipe[0], &ev);

  if (result < 0) {
    resin_printf_exception(env, "java/io/IOException",
			   "failed to add EPOLL for pipe=%d (errno=%d)",
			   sel->pipe[0],
			   result);

    return;
  }
}

JNIEXPORT int JNICALL
Java_com_caucho_server_port_JniSelectManager_addNative(JNIEnv *env,
                                                       jobject obj,
                                                       jlong manager_fd,
                                                       jint fd)
{
  select_t *sel = (select_t *) (PTR) manager_fd;
  int is_update = 0;
  
  if (! sel || fd < 0)
    return -1;
  
  pthread_mutex_lock(&sel->lock);

  if (sel->max > 0 && sel->epoll_fd > 0) {
    struct epoll_event ev;

    ev.events = EPOLLIN|EPOLLPRI|EPOLLERR|EPOLLHUP;
    ev.data.fd = fd;

    int result = epoll_ctl(sel->epoll_fd, EPOLL_CTL_ADD, fd, &ev);
    if (result < 0) {
      resin_printf_exception(env, "java/io/IOException",
			     "failed to add EPOLL for fd=%d (errno=%d)\n",
			     fd,
			     result);

      return;
    }
  
    sel->max++;

    if (! sel->has_update) {
      is_update = 1;
      sel->has_update = 1;
    }
  }

  pthread_mutex_unlock(&sel->lock);

  if (is_update)
    write(sel->pipe[1], "t", 1);

  return sel->max < 0 ? -1 : fd;
}

JNIEXPORT jint JNICALL
Java_com_caucho_server_port_JniSelectManager_removeNative(JNIEnv *env,
							  jobject obj,
							  jlong manager_fd,
							  jint fd)
{
  select_t *sel = (select_t *) (PTR) manager_fd;
  int is_update = 0;
  int i;

  
  if (! sel || fd < 0)
    return -1;

  pthread_mutex_lock(&sel->lock);

  if (sel->max >= 0) {
    struct epoll_event ev;

    ev.events = EPOLLIN|EPOLLET;
    ev.data.fd = fd;

    int result = epoll_ctl(sel->epoll_fd, EPOLL_CTL_DEL, fd, &ev);
    if (result < 0) {
      resin_printf_exception(env, "java/io/IOException",
			     "failed to remove EPOLL for fd=%d (errno=%d)\n",
			     fd,
			     errno);

      return -1;
    }

    if (! sel->has_update) {
      is_update = 1;
      sel->has_update = 1;
    }      
  }
  
  pthread_mutex_unlock(&sel->lock);
  
  /* pthread_kill(sel->thread_id, SIGALRM); */

  if (is_update)
    write(sel->pipe[1], "t", 1);

  return sel->max < 0 ? -1 : fd;
}

JNIEXPORT jint JNICALL
Java_com_caucho_server_port_JniSelectManager_selectNative(JNIEnv *env,
                                                          jobject obj,
                                                          jlong manager_fd,
                                                          jlong ms)
{
  select_t *sel = (select_t *) (PTR) manager_fd;
  int maxevents = 1;
  struct epoll_event events[maxevents];
  struct epoll_event ev;
  int result;
  int i;
  int max;
  char buf[4];
  int fd;

  if (! sel)
    return -1;

  if (! sel->has_more) {
    while ((max = sel->max) > 0) {
      if (sel->has_update) {
	sel->has_update = 0;
	continue;
      }

      result = epoll_wait(sel->epoll_fd, events, maxevents, ms);

      if (result <= 0)
	return -1;

      if (events[0].data.fd != sel->pipe[0]
	  || read(sel->pipe[0], buf, 1) <= 0) {
	/* the select pipe forces a loop, others break */
	break;
      }
    }
  }

  sel->has_more = 0;
  fd = events[0].data.fd;
  
  pthread_mutex_lock(&sel->lock);

  max = sel->max;

  if (epoll_ctl(sel->epoll_fd, EPOLL_CTL_DEL, fd, &ev) < 0) {
    resin_printf_exception(env, "java/io/IOException",
			   "failed to add EPOLL for fd=%d\n",
			   fd);

    return -1;
  }

  pthread_mutex_unlock(&sel->lock);

  return fd;
}
#else
#ifdef POLL
JNIEXPORT void JNICALL
Java_com_caucho_server_port_JniSelectManager_initNative(JNIEnv *env,
                                                        jobject obj,
                                                        jlong manager_fd)
{
  select_t *sel = (select_t *) (PTR) manager_fd;

  if (! sel)
    return;

  sel->thread_id = pthread_self();
  
  pipe(sel->pipe);
  sel->max = 1;
  sel->poll_items[0].fd = sel->pipe[0];
  sel->poll_items[0].events = POLLIN|POLLPRI;
  sel->poll_items[0].revents = 0;
}

JNIEXPORT int JNICALL
Java_com_caucho_server_port_JniSelectManager_addNative(JNIEnv *env,
                                                       jobject obj,
                                                       jlong manager_fd,
                                                       jint fd)
{
  select_t *sel = (select_t *) (PTR) manager_fd;
  int is_update = 0;
  
  if (! sel || fd < 0)
    return -1;
  
  pthread_mutex_lock(&sel->lock);

  if (sel->max > 0) {
    sel->poll_items[sel->max].fd = fd;
    sel->poll_items[sel->max].events = POLLIN|POLLPRI;
    sel->poll_items[sel->max].revents = 0;
  
    sel->max++;

    if (! sel->has_update) {
      is_update = 1;
      sel->has_update = 1;
    }
  }

  pthread_mutex_unlock(&sel->lock);
    
  /* pthread_kill(sel->thread_id, SIGALRM); */

  if (is_update)
    write(sel->pipe[1], "t", 1);

  return sel->max < 0 ? -1 : fd;
}

JNIEXPORT jint JNICALL
Java_com_caucho_server_port_JniSelectManager_removeNative(JNIEnv *env,
							  jobject obj,
							  jlong manager_fd,
							  jint fd)
{
  select_t *sel = (select_t *) (PTR) manager_fd;
  int is_update = 0;
  int i;

  
  if (! sel || fd < 0)
    return -1;

  pthread_mutex_lock(&sel->lock);

  if (sel->max >= 0) {
    for (i = sel->max - 1; i > 0; i--) {
      if (fd == sel->poll_items[i].fd) {
	sel->poll_items[i].events = 0;
	break;
      }
    }

    if (! sel->has_update) {
      is_update = 1;
      sel->has_update = 1;
    }      
  }
  
  pthread_mutex_unlock(&sel->lock);
  
  /* pthread_kill(sel->thread_id, SIGALRM); */

  if (is_update)
    write(sel->pipe[1], "t", 1);

  return sel->max < 0 ? -1 : fd;
}

JNIEXPORT jint JNICALL
Java_com_caucho_server_port_JniSelectManager_selectNative(JNIEnv *env,
                                                          jobject obj,
                                                          jlong manager_fd,
                                                          jlong ms)
{
  select_t *sel = (select_t *) (PTR) manager_fd;
  int result;
  int i;
  int max;
  char buf[4];
  int fd;

  if (! sel)
    return -1;

  if (! sel->has_more) {
    while ((max = sel->max) > 0) {
      if (sel->has_update) {
	sel->has_update = 0;
	continue;
      }
      
      result = poll(sel->poll_items, max, ms);

      if (result <= 0)
	return -1;

      if (! sel->poll_items[0].revents || read(sel->pipe[0], buf, 1) <= 0)
	break;
    }
  }

  sel->has_more = 0;
  fd = -1;
  
  pthread_mutex_lock(&sel->lock);

  max = sel->max;

  for (i = 1; i < max; i++) {
    if (! sel->poll_items[i].events) {
      /* remove deleted items */
      memcpy(&sel->poll_items[i], &sel->poll_items[i + 1],
	     (max - 1 - i) * sizeof (struct pollfd));

      max = max - 1;
      sel->max = max;
      i--;
    }
    else if (! sel->poll_items[i].revents) {
      /* no event for this item */
    }
    else {
      int testFd = sel->poll_items[i].fd;

      if (fd < 0) {
	memcpy(&sel->poll_items[i], &sel->poll_items[i + 1],
	       (max - 1 - i) * sizeof (struct pollfd));

	max = max - 1;
	sel->max = max;
      
	fd = testFd;
      }
      else
	sel->has_more = 1;
    }
  }

  pthread_mutex_unlock(&sel->lock);

  return fd;
}
#else /* select */
JNIEXPORT void JNICALL
Java_com_caucho_server_port_JniSelectManager_initNative(JNIEnv *env,
                                                        jobject obj,
                                                        jlong manager_fd)
{
  select_t *sel = (select_t *) (PTR) manager_fd;

  if (! sel)
    return;

  sel->thread_id = pthread_self();
  
  pipe(sel->pipe);
  
  sel->max = sel->pipe[0];
  FD_ZERO(&sel->select_items);
  FD_SET(sel->pipe[0], &sel->select_items);
}

JNIEXPORT jint JNICALL
Java_com_caucho_server_port_JniSelectManager_addNative(JNIEnv *env,
                                                       jobject obj,
                                                       jlong manager_fd,
                                                       jint fd)
{
  select_t *sel = (select_t *) (PTR) manager_fd;
  int has_update = 0;
  
  if (! sel || fd < 0)
    return -1;

  pthread_mutex_lock(&sel->lock);
  
  if (sel->max >= 0) {
    FD_SET(fd, &sel->select_items);

    if (sel->max < fd)
      sel->max = fd;

    if (! sel->has_update) {
      sel->has_update = 1;
      has_update = 1;
    }
  }
  
  pthread_mutex_unlock(&sel->lock);

  /* pthread_kill(sel->thread_id, SIGALRM); */

  if (has_update)
    write(sel->pipe[1], "t", 1);

  return (sel->max) < 0 ? -1 : fd;
}

JNIEXPORT jint JNICALL
Java_com_caucho_server_port_JniSelectManager_removeNative(JNIEnv *env,
							  jobject obj,
							  jlong manager_fd,
							  jint fd)
{
  select_t *sel = (select_t *) (PTR) manager_fd;
  int i, j;
  int has_update = 0;
  
  if (! sel || fd < 0)
    return -1;

  pthread_mutex_lock(&sel->lock);
  
  if (sel->max >= 0) {
    FD_CLR(fd, &sel->select_items);

    if (! sel->has_update) {
      sel->has_update = 1;
      has_update = 1;
    }
  }
  
  pthread_mutex_unlock(&sel->lock);

  /* pthread_kill(sel->thread_id, SIGALRM); */

  if (has_update)
    write(sel->pipe[1], "t", 1);

  return (sel->max < 0) ? -1 : fd;
}

JNIEXPORT jint JNICALL
Java_com_caucho_server_port_JniSelectManager_selectNative(JNIEnv *env,
                                                          jobject obj,
                                                          jlong manager_fd,
                                                          jlong ms)
{
  select_t *sel = (select_t *) (PTR) manager_fd;
  fd_set read_mask;
  struct timeval timeout;
  int result;
  int i;
  int max;
  char buf[4];
  int fd;

  if (! sel)
    return -1;

  while ((max = sel->max) > 0) {
    if (sel->has_update) {
      sel->has_update = 0;
      continue;
    }
      
    read_mask = sel->select_items;

    timeout.tv_sec = ms / 1000;
    timeout.tv_usec = (ms % 1000) * 1000;

    result = select(max + 1, &read_mask, 0, 0, &timeout);

    if (result <= 0)
      return -1;

    if (! FD_ISSET(sel->pipe[0], &read_mask) ||
	read(sel->pipe[0], buf, 1) <= 0 ||
	result > 1) {
      break;
    }
  }

  fd = -1;
  
  pthread_mutex_lock(&sel->lock);
  
  for (i = max; i >= 0; i--) {
    if (i == sel->max && ! FD_ISSET(i, &sel->select_items))
      sel->max = i;
    
    if (i == sel->pipe[0]) {
    }
    else if (FD_ISSET(i, &read_mask)) {
      fd = i;
     
      FD_CLR(i, &sel->select_items);

      break;
    }
  }

  pthread_mutex_unlock(&sel->lock);

  return fd;
}
#endif
#endif /* EPOLL */

JNIEXPORT void JNICALL
Java_com_caucho_server_port_JniSelectManager_closeNative(JNIEnv *env,
                                                         jobject obj,
                                                         jlong manager_fd)
{
  select_t *sel = (select_t *) (PTR) manager_fd;

  if (sel && sel->max >= 0) {
    sel->has_update = 1;
    sel->max = -1;
    write(sel->pipe[0], " ", 1);
    close(sel->pipe[0]);
    close(sel->pipe[1]);
    /*
    cse_free(sel);
    */
  }

#ifdef EPOLL
 {
   int epoll_fd = sel->epoll_fd;
   sel->epoll_fd = 0;

   if (epoll_fd > 0)
     close(epoll_fd);
 }
#endif
}
#endif

#ifdef linux
static void
get_linux_version(char *version)
{
  FILE *file = fopen("/proc/version", "r");

  if (! file || fscanf(file, "Linux version %s", version) != 1)
    strcpy(version, "2.4.0-unknown");

  fclose(file);
}
#endif

#ifndef WIN32
JNIEXPORT jint JNICALL
Java_com_caucho_util_CauchoSystem_setUserNative(JNIEnv *env,
						jobject obj,
						jstring juser,
						jstring jgroup)
{
  char userbuf[256];
  char groupbuf[256];
  char *user = 0;
  char *group = 0;
  int uid = -1;
  int gid = -1;
  const char *temp_string;
  struct passwd *passwd;
  
  if (juser != 0) {
    temp_string = (*env)->GetStringUTFChars(env, juser, 0);
  
    if (temp_string) {
      strncpy(userbuf, temp_string, sizeof(userbuf));
      userbuf[sizeof(userbuf) - 1] = 0;
      user = userbuf;
  
      (*env)->ReleaseStringUTFChars(env, juser, temp_string);
    }
  }

  if (jgroup != 0) {
    temp_string = (*env)->GetStringUTFChars(env, jgroup, 0);
  
    if (temp_string) {
      strncpy(groupbuf, temp_string, sizeof(groupbuf));
      groupbuf[sizeof(groupbuf) - 1] = 0;
      group = groupbuf;
  
      (*env)->ReleaseStringUTFChars(env, jgroup, temp_string);
    }
  }

  if (user == 0)
    return -1;

  passwd = getpwnam(user);
  if (passwd == 0)
    return -1;

  uid = passwd->pw_uid;

  if (group) {
    passwd = getpwnam(group);
    if (passwd)
      gid = passwd->pw_gid;
  }

  if (gid > 0)
    setgid(gid);

  if (uid == getuid())
    return uid;

#ifdef linux
  {
    char buf[1024];
    char version[1024];
    jclass clazz;

    get_linux_version(version);

    /*
    if (1 || strcmp(version, "2.5.0") < 0 && strcmp(version, "2.4.20-6")) {
    */
    if (1) {
      sprintf(buf, "Linux %s does not properly implement setuid for threaded processes, so the <user-name> is not properly available.  Consider using iptables or some other port mapping function to avoid the need for root.",
	      version);

      clazz = (*env)->FindClass(env, "java/io/IOException");

      if (clazz)
	(*env)->ThrowNew(env, clazz, buf);

      return -1;
    }
  }
#endif

  if (uid > 0) {
    int result;

    result = setuid(uid);
    
    if (result > 0)
      return -1;
  }

  return getuid();
}
#else /* WIN32 */
JNIEXPORT jint JNICALL
Java_com_caucho_util_CauchoSystem_setUserNative(JNIEnv *env,
						jobject obj,
						jstring user,
						jstring group)
{
  return -1;
}
#endif				 

JNIEXPORT jint JNICALL
Java_com_caucho_vfs_JniFilePathImpl_nativeIsEnabled(JNIEnv *env,
						    jobject obj)
{
  return 1;
}

JNIEXPORT jint JNICALL
Java_com_caucho_vfs_JniFilePathImpl_nativeGetLastModified(JNIEnv *env,
							  jobject obj,
							  jbyteArray name,
							  jint length)
{
  char buffer[8192];
  struct stat st;

  if (! name || length <= 0 || sizeof(buffer) <= length)
    return -1;

  get_byte_array_region(env, name, 0, length, buffer);

  buffer[length] = 0;

#ifdef WIN32
  if (length > 0 && (buffer[length - 1] == '/' || buffer[length - 1] == '\\')) {
	  length--;
	  buffer[length] = 0;
  }
#endif

  if (stat(buffer, &st))
    return -1;
  
  return st.st_mtime;
}

JNIEXPORT jint JNICALL
Java_com_caucho_vfs_JniFilePathImpl_nativeGetLength(JNIEnv *env,
						    jobject obj,
						    jbyteArray name,
						    jint length)
{
  char buffer[8192];
  struct stat st;

  if (! name || length <= 0 || sizeof(buffer) <= length)
    return -1;

  get_byte_array_region(env, name, 0, length, buffer);

  buffer[length] = 0;

#ifdef WIN32
  if (length > 0 && (buffer[length - 1] == '/' || buffer[length - 1] == '\\')) {
	  length--;
	  buffer[length] = 0;
  }
#endif

  if (stat(buffer, &st))
    return -1;

  return st.st_size;
}  

JNIEXPORT jboolean JNICALL
Java_com_caucho_vfs_JniFilePathImpl_nativeCanRead(JNIEnv *env,
						  jclass cl,
						  jbyteArray name)
{
  char buffer[8192];
  int length;
  int result;

  if (! name)
    return 0;

  length = (*env)->GetArrayLength(env, name);
  
  if (length <= 0 || sizeof(buffer) <= length)
    return 0;

  get_byte_array_region(env, name, 0, length, buffer);

  buffer[length] = 0;

#ifndef WIN32
  result = access(buffer, R_OK) == 0;
#else
  result = access(buffer, 4) == 0;
#endif

  return result;
}

JNIEXPORT jobject JNICALL 
Java_com_caucho_vfs_JniFilePathImpl_nativeStat(JNIEnv *env, 
                                               jclass cl,
                                               jbyteArray name,
                                               jboolean do_lstat)
{
  /* we can cache a method id... */
  static jmethodID file_status_constructor = NULL;

  /* but we can't cache classes */
  jclass file_status_class = NULL;

  char buffer[8192];
  int length;
  int result;
  struct stat st;
  jobject stat_obj;
  jboolean is_link;
  jboolean is_socket;
  jboolean is_file;
  jboolean is_block;
  jboolean is_char;
  jboolean is_dir;
  jboolean is_fifo;
  jlong block_size;
  jlong blocks;

  if (! name)
    return NULL;

  length = (*env)->GetArrayLength(env, name);
  
  if (length <= 0 || sizeof(buffer) <= length)
    return NULL;

  get_byte_array_region(env, name, 0, length, buffer);

  buffer[length] = 0;

#ifdef WIN32
  if (length > 0 && (buffer[length - 1] == '/' || buffer[length - 1] == '\\')) {
	  length--;
	  buffer[length] = 0;
  }
#endif

#ifdef WIN32
  result = stat(buffer, &st);
#else
  if (do_lstat) 
    result = lstat(buffer, &st);
  else 
    result = stat(buffer, &st);
#endif

  if (result != 0)
    return NULL;

  file_status_class = (*env)->FindClass(env, "com/caucho/vfs/FileStatus");

  if (file_status_class == NULL)
    return NULL;

  if (file_status_constructor == NULL) {
    file_status_constructor = (*env)->GetMethodID(env, file_status_class, 
                                                  "<init>", 
                                                  "(JJIIIIJJJJJJJZZZZZZZ)V");

    if (file_status_constructor == NULL)
      return NULL;
  }

#ifndef WIN32
  is_file = (jboolean) (S_ISREG(st.st_mode));
  is_dir = (jboolean) (S_ISDIR(st.st_mode));
  is_char = (jboolean) (S_ISCHR(st.st_mode));
  is_block = (jboolean) (S_ISBLK(st.st_mode));
  is_fifo = (jboolean) (S_ISFIFO(st.st_mode));
  is_link = (jboolean) (S_ISLNK(st.st_mode));
  is_socket = (jboolean) (S_ISSOCK(st.st_mode));
  block_size = (jlong) st.st_blksize;
  blocks = (jlong) st.st_blocks;
#else
  is_file = (jboolean) ((st.st_mode & S_IFREG) != 0);
  is_dir = (jboolean) ((st.st_mode & S_IFDIR) != 0);
  is_char = (jboolean) ((st.st_mode & S_IFCHR) != 0);
  is_block = 0;
  is_fifo = 0;
  is_link = 0;
  is_socket = 0;
  block_size = 1024;
  blocks = (st.st_size + block_size - 1) / block_size;
#endif

  return (*env)->NewObject(env, file_status_class, file_status_constructor,
                           (jlong)st.st_dev, (jlong)st.st_ino, 
                           (jint)st.st_mode, (jint)st.st_nlink, 
                           (jint)st.st_uid, (jint)st.st_gid,
                           (jlong)st.st_rdev, (jlong)st.st_size, 
                           block_size, blocks, 
                           (jlong)st.st_atime, (jlong)st.st_mtime,
                           (jlong)st.st_ctime,
                           is_file, is_dir, is_char, is_block, is_fifo,
                           is_link, is_socket);
}

JNIEXPORT jboolean JNICALL 
Java_com_caucho_vfs_JniFilePathImpl_nativeLink(JNIEnv *env, 
                                               jclass cl, 
                                               jbyteArray name, 
                                               jbyteArray target, 
                                               jboolean hard_link)
{
  jclass ioExceptionClass;
  char name_buffer[8192];
  char target_buffer[8192];
  int name_length;
  int target_length;
  int result;

#ifdef WIN32
  return (jboolean) 0;
#else
  if (! name)
	  return (jboolean) 0;
  
  name_length = (*env)->GetArrayLength(env, name);
  target_length = (*env)->GetArrayLength(env, target);
  
  if (name_length <= 0 || sizeof(name_buffer) <= name_length)
    return (jboolean) 0;

  if (target_length <= 0 || sizeof(target_buffer) <= target_length)
    return (jboolean) 0;

  get_byte_array_region(env, name, 0, name_length, name_buffer);
  get_byte_array_region(env, target, 0, target_length, target_buffer);

  name_buffer[name_length] = 0;
  target_buffer[target_length] = 0;

  if (hard_link)
    result = link(target_buffer, name_buffer);
  else
    result = symlink(target_buffer, name_buffer);

  if (result == 0)
    return (jboolean) 1;

  ioExceptionClass = (*env)->FindClass(env, "java/io/IOException");

  if (ioExceptionClass == NULL)
    return (jboolean) 0;

  (*env)->ThrowNew(env, ioExceptionClass, strerror(errno));

  return (jboolean) 0;
#endif
}


JNIEXPORT jboolean JNICALL
Java_com_caucho_vfs_JniFilePathImpl_nativeIsDirectory(JNIEnv *env,
						      jobject obj,
						      jbyteArray name)
{
  char buffer[8192];
  int length;
  struct stat st;

  if (! name)
	  return 0;
  
  length = (*env)->GetArrayLength(env, name);
  
  if (length <= 0 || sizeof(buffer) <= length)
    return 0;

  get_byte_array_region(env, name, 0, length, buffer);

  buffer[length] = 0;

#ifdef WIN32
  if (length > 0 && (buffer[length - 1] == '/' || buffer[length - 1] == '\\')) {
	  length--;
	  buffer[length] = 0;
  }
#endif

  if (stat(buffer, &st))
    return 0;
#ifndef WIN32
  return S_ISDIR(st.st_mode);
#else
  return (st.st_mode & S_IFDIR) != 0;
#endif
}

JNIEXPORT int JNICALL
Java_com_caucho_vfs_JniFilePathImpl_nativeChmod(JNIEnv *env,
						jobject obj,
						jbyteArray name,
						jint length,
						jint mode)
{
  char buffer[8192];

  if (! name || length <= 0 || sizeof(buffer) <= length)
    return -1;

  get_byte_array_region(env, name, 0, length, buffer);

  buffer[length] = 0;

#ifndef WIN32
  chmod(buffer, mode);
#endif

  return 0;
}

JNIEXPORT jboolean JNICALL
Java_com_caucho_vfs_JniFilePathImpl_nativeChangeOwner(JNIEnv *env,
						      jobject obj,
						      jbyteArray name,
						      jint length,
						      jstring owner)
{
  char buffer[8192];
  char userbuf[8192];
  struct passwd *passwd;
  const char *temp_string;
  int uid = -1;

  if (! name || length <= 0 || sizeof(buffer) <= length || ! owner)
    return 0;

  get_byte_array_region(env, name, 0, length, buffer);

  buffer[length] = 0;

#ifndef WIN32
  temp_string = (*env)->GetStringUTFChars(env, owner, 0);

  if (! temp_string)
    return 0;

  strncpy(userbuf, temp_string, sizeof(userbuf));
  userbuf[sizeof(userbuf) - 1] = 0;
  
  (*env)->ReleaseStringUTFChars(env, owner, temp_string);

  passwd = getpwnam(userbuf);
  if (passwd == 0)
    return -1;

  uid = passwd->pw_uid;

  chown(buffer, uid, -1);
  
  return 1;
#else
  return 0;
#endif
}

#ifndef WIN32

JNIEXPORT jlong JNICALL
Java_com_caucho_vfs_JniFilePathImpl_nativeCrc64(JNIEnv *env,
                                                jobject obj,
                                                jbyteArray name,
                                                jint length)
{
  char buffer[8192];
  struct stat st;
  jlong crc64 = 0;
  struct dirent *dp;
  DIR *dir;

  if (! name || length <= 0 || sizeof(buffer) <= length)
    return -1;

  get_byte_array_region(env, name, 0, length, buffer);

  buffer[length] = 0;

  dir = opendir(buffer);

  /* directory only for now */
  if (! dir)
    return 0;

  while ((dp = readdir(dir))) {
    crc64 = crc64_generate(crc64, dp->d_name);
  }

  closedir(dir);

  return crc64;
}

#else

JNIEXPORT jlong JNICALL
Java_com_caucho_vfs_JniFilePathImpl_nativeCrc64(JNIEnv *env,
                                                jobject obj,
                                                jbyteArray name,
                                                jint length)
{
  return 0;
}

#endif

JNIEXPORT jint JNICALL
Java_com_caucho_vfs_JniFilePathImpl_nativeOpenRead(JNIEnv *env,
						   jobject obj,
						   jbyteArray name,
						   jint length)
{
  char buffer[8192];
  int fd;
  int flags;

  if (! name || length <= 0 || sizeof(buffer) <= length)
    return -1;

  get_byte_array_region(env, name, 0, length, buffer);

  buffer[length] = 0;

#ifdef S_ISDIR
 /* On Linux, check if the file is a directory first. */
 {
   struct stat st;

   if (stat(buffer, &st) || S_ISDIR(st.st_mode)) {
     return -1;
   }
 }
#endif

  flags = O_RDONLY;
  
#ifdef O_BINARY
  flags |= O_BINARY;
#endif
  
#ifdef O_LARGEFILE
  flags |= O_LARGEFILE;
#endif

  fd = open(buffer, flags);

  return fd;
}

JNIEXPORT jint JNICALL
Java_com_caucho_vfs_JniFilePathImpl_nativeOpenWrite(JNIEnv *env,
						    jobject obj,
						    jbyteArray name,
						    jint length,
						    jboolean is_append)
{
  char buffer[8192];
  int fd;
  int flags;

  if (! name || length <= 0 || sizeof(buffer) <= length)
    return -1;

  get_byte_array_region(env, name, 0, length, buffer);

  buffer[length] = 0;

  flags = 0;
  
#ifdef O_BINARY
  flags |= O_BINARY;
#endif
  
#ifdef O_LARGEFILE
  flags |= O_LARGEFILE;
#endif

  if (is_append)
    fd = open(buffer, O_WRONLY|O_CREAT|O_APPEND|flags, 0666);
  else
    fd = open(buffer, O_WRONLY|O_CREAT|O_TRUNC|flags, 0666);

  if (fd < 0) {
    switch (errno) {
    case EISDIR:
      resin_printf_exception(env, "java/io/IOException",
			     "'%s' is a directory", buffer);
      break;
    case EACCES:
      resin_printf_exception(env, "java/io/IOException",
			     "'%s' permission denied", buffer);
      break;
    case ENOTDIR:
      resin_printf_exception(env, "java/io/IOException",
			     "'%s' parent directory does not exist", buffer);
      break;
    case EMFILE:
    case ENFILE:
      resin_printf_exception(env, "java/io/IOException",
			     "too many files open", buffer);
      break;
    case ENOENT:
      resin_printf_exception(env, "java/io/FileNotFoundException",
			     "'%s' unable to open", buffer);
      break;
    default:
      resin_printf_exception(env, "java/io/IOException",
			     "'%s' unknown error (errno=%d).", buffer, errno);
      break;
    }
  }

  return fd;
}

#ifdef POLL
JNIEXPORT jint JNICALL
Java_com_caucho_vfs_JniFileStream_nativeAvailable(JNIEnv *env,
						  jobject obj,
						  jint fd)
{
  struct pollfd poll_item[1];

  if (fd < 0)
    return 0;

  poll_item[0].fd = fd;
  poll_item[0].events = POLLIN|POLLPRI;
  poll_item[0].revents = 0;

  return (poll(poll_item, 1, 0) > 0);
}
#else /* select */
JNIEXPORT jint JNICALL
Java_com_caucho_vfs_JniFileStream_nativeAvailable(JNIEnv *env,
						  jobject obj,
						  jint fd)
{
  fd_set read_set;
  struct timeval timeval;
  int result;
  
  if (fd < 0)
    return 0;

  FD_ZERO(&read_set);

  FD_SET(fd, &read_set);

  memset(&timeval, 0, sizeof(timeval));

  result = select(fd + 1, &read_set, 0, 0, &timeval);

  return result > 0;
}
#endif /* select */

JNIEXPORT jint JNICALL
Java_com_caucho_vfs_JniFileStream_nativeRead(JNIEnv *env,
					     jobject obj,
					     jint fd,
					     jbyteArray buf,
					     jint offset,
					     jint length)
{
  int sublen;
  char buffer[STACK_BUFFER_SIZE];
  int read_length = 0;

  if (fd < 0 || ! buf)
    return -1;

  while (length > 0) {
    int result;
    
    if (length < sizeof(buffer))
      sublen = length;
    else
      sublen = sizeof(buffer);

#ifdef RESIN_DIRECT_JNI_BUFFER
   {
     jbyte *cBuf = (*env)->GetPrimitiveArrayCritical(env, buf, 0);

     if (! cBuf)
       return -1;
     
     result = read(fd, cBuf + offset, sublen);

     (*env)->ReleasePrimitiveArrayCritical(env, buf, cBuf, 0);
     
     if (result <= 0)
       return read_length == 0 ? -1 : read_length;
   }
#else
   {
     result = read(fd, buffer, sublen);

     if (result <= 0)
       return read_length == 0 ? -1 : read_length;

     set_byte_array_region(env, buf, offset, result, buffer);
   }
#endif    

    read_length += result;
    
    if (result < sublen)
      return read_length;
    
    offset += result;
    length -= result;
  }

  return read_length;
}

JNIEXPORT jint JNICALL
Java_com_caucho_vfs_JniFileStream_nativeWrite(JNIEnv *env,
					      jobject obj,
					      jint fd,
					      jbyteArray buf,
					      jint offset,
					      jint length)
{
  int sublen;
  char buffer[STACK_BUFFER_SIZE];
  int read_length = 0;

  if (fd < 0 || ! buf)
    return -1;

  while (length > 0) {
    int result;
    
    if (length < sizeof(buffer))
      sublen = length;
    else
      sublen = sizeof(buffer);

    get_byte_array_region(env, buf, offset, sublen, buffer);

    result = write(fd, buffer, sublen);

    if (result <= 0)
      return -1;
    
    offset += result;
    length -= result;
  }

  return 1;
}

JNIEXPORT jlong JNICALL
Java_com_caucho_vfs_JniFileStream_nativeSkip(JNIEnv *env,
					     jobject obj,
					     jint fd,
					     jlong offset)
{
  if (fd < 0)
    return 0;

  return lseek(fd, (off_t) offset, SEEK_CUR);
}

JNIEXPORT jint JNICALL
Java_com_caucho_vfs_JniFileStream_nativeClose(JNIEnv *env,
					      jobject obj,
					      jint fd)
{
  if (fd >= 0) {
    return close(fd);
  }
  else
    return -1;
}

JNIEXPORT jint JNICALL
Java_com_caucho_vfs_JniFileStream_nativeFlushToDisk(JNIEnv *env,
						    jobject obj,
						    jint fd)
{
  if (fd >= 0) {
#ifndef WIN32
    return fsync(fd);
#else
	  return -1;
#endif
  }
  else
    return -1;
}

JNIEXPORT jint JNICALL
Java_com_caucho_vfs_JniFileStream_nativeSeekStart(JNIEnv *env,
						  jobject obj,
						  jint fd,
						  jlong offset)
{
  if (fd >= 0) {
    return lseek(fd, (off_t) offset, SEEK_SET);
  }
  else
    return -1;
}

JNIEXPORT jint JNICALL
Java_com_caucho_vfs_JniFileStream_nativeSeekEnd(JNIEnv *env,
						jobject obj,
						jint fd,
						jlong offset)
{
  if (fd >= 0) {
    return lseek(fd, (off_t) offset, SEEK_END);
  }
  else
    return -1;
}

JNIEXPORT void JNICALL
Java_com_caucho_vfs_JniFilePathImpl_nativeTruncate(JNIEnv *env,
						   jobject obj,
						   jbyteArray name,
						   jint length)
{
  char buffer[8192];
  int result;

  if (! name || length <= 0 || sizeof(buffer) <= length)
    return;

  get_byte_array_region(env, name, 0, length, buffer);

  buffer[length] = 0;

#ifdef WIN32
  result = open(buffer, O_WRONLY|O_CREAT|O_TRUNC, 0664);
  
  if (result > 0)
    close(result);
#else
  result = truncate(buffer, 0);
#endif

  if (result < 0) {
    switch (errno) {
    case EISDIR:
      resin_printf_exception(env, "java/io/IOException",
			     "'%s' is a directory", buffer);
      break;
    case EACCES:
      resin_printf_exception(env, "java/io/IOException",
			     "'%s' permission denied", buffer);
      break;
    case ENOTDIR:
      resin_printf_exception(env, "java/io/IOException",
			     "'%s' parent directory does not exist", buffer);
      break;
    case EMFILE:
    case ENFILE:
      resin_printf_exception(env, "java/io/IOException",
			     "too many files open", buffer);
      break;
    case ENOENT:
      return;
      /*
      resin_printf_exception(env, "java/io/FileNotFoundException",
			     "'%s' unable to open", buffer);
      break;
      */
    default:
      resin_printf_exception(env, "java/io/IOException",
			     "'%s' unknown error (errno=%d).", buffer, errno);
      break;
    }
  }
}

#ifdef WIN32

JNIEXPORT void JNICALL
Java_com_caucho_vfs_Syslog_nativeSyslog(JNIEnv *env,
					jobject obj,
					jint priority,
					jstring msg)
{
}

JNIEXPORT void JNICALL
Java_com_caucho_vfs_Syslog_nativeOpenSyslog(JNIEnv *env,
					    jobject obj)
{
}

#else

JNIEXPORT void JNICALL
Java_com_caucho_vfs_Syslog_nativeOpenSyslog(JNIEnv *env,
					    jobject obj)
{
  openlog("Resin", 0, LOG_DAEMON);
}

JNIEXPORT void JNICALL
Java_com_caucho_vfs_Syslog_nativeSyslog(JNIEnv *env,
					jobject obj,
					jint priority,
					jstring msg)
{
  char buffer[8192];
  const char *temp_string;
  
  temp_string = (*env)->GetStringUTFChars(env, msg, 0);
  
  if (temp_string) {
    strncpy(buffer, temp_string, 8191);
    buffer[sizeof(buffer) - 1] = 0;
  
    (*env)->ReleaseStringUTFChars(env, msg, temp_string);
    
    syslog(priority, "%s", buffer);
  }
}


#endif

JNIEXPORT double JNICALL
Java_com_caucho_server_util_JniCauchoSystemImpl_nativeGetLoadAvg(JNIEnv *env,
								 jobject obj)
{
  double avg[3];

#ifndef WIN32
  
  getloadavg(avg, 3);

#else

  avg[0] = 0;

#endif
  
  return avg[0];
}
