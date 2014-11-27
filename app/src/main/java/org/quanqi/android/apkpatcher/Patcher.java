package org.quanqi.android.apkpatcher;

/**
 * By cindy on 11/27/14 11:41 PM.
 */
public class Patcher {
    static {
        System.loadLibrary("Patcher");
    }

    /**
     *
     * @param oldApk
     * @param newApk
     * @param patch
     */
    public native void patcher(String oldApk, String newApk, String patch);
}
