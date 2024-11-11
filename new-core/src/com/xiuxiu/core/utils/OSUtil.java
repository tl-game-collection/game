package com.xiuxiu.core.utils;

import java.util.Locale;

public final class OSUtil {
    /**
     * WIN:\r\n
     * MAC:\r
     * UNIX/LINUX: \n
     */
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");
    public static final String PATH_SEPARATOR = System.getProperty("path.separator");

    public static final String OS_NAME = System.getProperty("os.name").toLowerCase(Locale.US);
    public static final String OS_ARCH = System.getProperty("os.arch").toLowerCase(Locale.US);
    public static final String OS_VERSION = System.getProperty("os.version").toLowerCase(Locale.US);

    private static final String FAMILY_UNIX = "unix";
    private static final String FAMILY_LINUX = "linux";
    private static final String FAMILY_OPENVMS = "openvms";
    private static final String FAMILY_MAC = "mac";

    public static boolean isLinux() {
        return OSUtil.isOS(FAMILY_LINUX, null, null, null);
    }

    public static boolean isOS(String family, String name, String arch, String version) {
        boolean retVal = false;
        if (null != family || null != name || null != arch || null != version) {
            boolean isFamily = true;
            boolean isName = true;
            boolean isArch = true;
            boolean isVersion = true;
            if (null != family) {
                if (family.equals(FAMILY_UNIX)) {
                    isFamily = PATH_SEPARATOR.equals(":")
                            && !OSUtil.isOS(FAMILY_LINUX, null, null, null)
                            && !OSUtil.isOS(FAMILY_OPENVMS, null, null, null)
                            && (!OSUtil.isOS(FAMILY_MAC, null, null, null) || OSUtil.OS_NAME.endsWith("x"));
                } else if (family.equals(FAMILY_LINUX)) {
                    isFamily = OS_NAME.indexOf(FAMILY_LINUX) > -1;
                } else if (family.equals(FAMILY_OPENVMS)) {
                    isFamily = OS_NAME.indexOf(FAMILY_OPENVMS) > -1;
                } else if (family.equals(FAMILY_MAC)) {
                    isFamily = OS_NAME.indexOf(FAMILY_MAC) > -1;
                }
            }
            if (null != name) {
                isName = name.equals(OS_NAME);
            }
            if (null != arch) {
                isArch = arch.equals(OS_ARCH);
            }
            if (null != version) {
                isVersion = version.equals(OS_VERSION);
            }
            retVal = isFamily && isName && isArch && isVersion;
        }
        return retVal;
    }

    public static void addShutdownHook(Runnable runnable) {
    	/**
    	 * 在jvm中增加一个关闭的钩子，当jvm关闭的时候，会执行系统中已经设置的所有通过方法addShutdownHook添加的钩子，
    	 * 当系统执行完这些钩子后，jvm才会关闭。所以这些钩子可以在jvm关闭的时候进行内存清理、对象销毁等操作。
    	 */
        Runtime.getRuntime().addShutdownHook(new Thread(runnable, "ShutdownHook"));
    }
}
