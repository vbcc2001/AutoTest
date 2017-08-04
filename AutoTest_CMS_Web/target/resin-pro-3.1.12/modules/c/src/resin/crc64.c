/*
 * Copyright (c) 1998-2007 Caucho Technology -- all rights reserved
 *
 * @author Scott Ferguson
 */

#include <jni.h>

#define POLY64REV 0xd800000000000000LL

static jlong CRC_TABLE[256];
static int g_crc64_is_init;

static void
crc64_init()
{
  int i;
  
  if (g_crc64_is_init)
    return;

  for (i = 0; i < 256; i++) {
    jlong v = i;
    int j;

    for (j = 0; j < 8; j++) {
      int flag = (v & 1) != 0;

      jlong newV = v >> 1;

      if ((v & 0x100000000LL) != 0)
        newV |= 0x100000000LL;

      if ((v & 1) != 0)
        newV ^= POLY64REV;

      v = newV;
    }

    CRC_TABLE[i] = v;
  }

  g_crc64_is_init = 1;
}


/**
 * Calculates the next crc value.
 */
static jlong crc64_next(jlong crc, int ch)
{
  return (crc >> 8) ^ CRC_TABLE[((int) crc ^ ch) & 0xff];
}

/**
 * Calculates CRC from a string.
 */
jlong crc64_generate(jlong crc, char *value)
{
  int ch;

  if (! g_crc64_is_init)
    crc64_init();
  
  while ((ch = *value++)) {
    crc = crc64_next(crc, ch);
  }

  return crc;
}
