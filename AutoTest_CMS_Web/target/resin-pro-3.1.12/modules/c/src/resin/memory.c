/*
 * Copyright (c) 1999-2004 Caucho Technology.  All rights reserved.
 *
 * Caucho Technology permits modification and use of this file in
 * source and binary form ("the Software") subject to the Caucho
 * Developer Source License 1.1 ("the License") which accompanies
 * this file.  The License is also available at
 *   http://www.caucho.com/download/cdsl1-1.xtp
 *
 * In addition to the terms of the License, the following conditions
 * must be met:
 *
 * 1. Each copy or derived work of the Software must preserve the copyright
 *    notice and this notice unmodified.
 *
 * 2. Each copy of the Software in source or binary form must include 
 *    an unmodified copy of the License in a plain ASCII text file named
 *    LICENSE.
 *
 * 3. Caucho reserves all rights to its names, trademarks and logos.
 *    In particular, the names "Resin" and "Caucho" are trademarks of
 *    Caucho and may not be used to endorse products derived from
 *    this software.  "Resin" and "Caucho" may not appear in the names
 *    of products derived from this software.
 *
 * This Software is provided "AS IS," without a warranty of any kind. 
 * ALL EXPRESS OR IMPLIED REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED.
 *
 * CAUCHO TECHNOLOGY AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE OR ANY THIRD PARTY AS A RESULT OF USING OR
 * DISTRIBUTING SOFTWARE. IN NO EVENT WILL CAUCHO OR ITS LICENSORS BE LIABLE
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE SOFTWARE, EVEN IF HE HAS BEEN ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGES.      
 *
 * @author Scott Ferguson
 */

#include <sys/types.h>
#ifdef WIN32
#ifndef _WINSOCKAPI_ 
#define _WINSOCKAPI_
#endif 
#include <windows.h>
#include <winsock2.h>
#else
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>
#endif
#include <sys/stat.h>
#include <fcntl.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <string.h>
/* probably system-dependent */
#include <jni.h>
#include <errno.h>

#include "resin.h"

typedef struct chunk_t {
  int bucket;
  struct chunk_t *next;
} chunk_t;

static int is_init;
static pthread_mutex_t mem_lock;
static int alloc = 0;
static chunk_t *buckets[1024];

static int
get_bucket(int size)
{
  size += sizeof(chunk_t);

  if (size < 4096)
    return (size + 255) / 256;
  else
    return ((size + 4095) / 4096) + 16;
}

static int
get_chunk_size(int size)
{
  if (size + sizeof(chunk_t) < 4096)
    return 256 * ((size + sizeof(chunk_t) + 255) / 256);
  else
    return 4096 * ((size + sizeof(chunk_t) + 4095) / 4096);
}

static void
cse_init_bucket(int size, int alloc_size)
{
  char *data = malloc(alloc_size);
  int bucket = get_bucket(size);
  int chunk_size = get_chunk_size(size);
  int i;

  if (bucket >= 1024)
    fprintf(stderr, "bad bucket size:%d bucket:%d\n", size, bucket);

  for (i = 0; i < alloc_size; i += chunk_size) {
    chunk_t *chunk = (chunk_t *) (data + i);
    chunk->bucket = bucket;
    chunk->next = buckets[bucket];
    buckets[bucket] = chunk;
  }
}

void *
cse_malloc(int size)
{
  int bucket;
  chunk_t *chunk = 0;

  bucket = get_bucket(size);
  
  pthread_mutex_lock(&mem_lock);
  chunk = buckets[bucket];
  if (chunk)
    buckets[bucket] = chunk->next;
  pthread_mutex_unlock(&mem_lock);

  if (chunk) {
  }
  else if (size + sizeof(chunk_t) <= 4096) {
    pthread_mutex_lock(&mem_lock);
    cse_init_bucket(size, 64 * 1024);
    
    chunk = buckets[bucket];
    buckets[bucket] = chunk->next;
    pthread_mutex_unlock(&mem_lock);
  }
  else {
    chunk = (chunk_t *) malloc(get_chunk_size(size));

    if (chunk == 0)
      return 0;

    chunk->bucket = bucket;
  }
  
  chunk->next = 0;
  
  return ((char *) chunk) + sizeof(chunk_t);
}

void
cse_free(void *v_data)
{
  chunk_t *chunk = (chunk_t *) (((char *) v_data) - sizeof(chunk_t));
  int bucket = chunk->bucket;

  if (bucket >= 0 && bucket < 1024) {
    pthread_mutex_lock(&mem_lock);
    chunk->next = buckets[bucket];
    buckets[bucket] = chunk;
    pthread_mutex_unlock(&mem_lock);
  }
  else
    fprintf(stderr, "no bucket\n");
}
