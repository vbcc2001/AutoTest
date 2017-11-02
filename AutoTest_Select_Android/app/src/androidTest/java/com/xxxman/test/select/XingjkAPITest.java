package com.xxxman.test.select;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.xxxman.test.select.util.XingjkAPI;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class XingjkAPITest {

    private static final String TAG = XingjkAPITest.class.getName();
    @Test
    public void test() throws Exception {
        Log.d(TAG,XingjkAPI.loginIn());
    }
}