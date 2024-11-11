package com.xiuxiu.core.utils;

import com.xiuxiu.core.IClassExecute;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class PackageUtil {
    /**
     * 扫描包
     * @param packageName
     * @param execute
     */
    public static void scanPackage(String packageName, IClassExecute execute) {
        if (StringUtil.isEmptyOrNull(packageName) || null == execute) {
            return;
        }
        String path = packageName.replace(".", File.separator);
        URL url = Thread.currentThread().getContextClassLoader().getResource(path);
        if (null == url) {
            return;
        }
        if (url.toString().startsWith("jar")) {
            try {
                JarFile jarFile = ((JarURLConnection) url.openConnection()).getJarFile();
                Enumeration<JarEntry> jarEntries = jarFile.entries();
                while (jarEntries.hasMoreElements()) {
                    JarEntry jarEntry = jarEntries.nextElement();
                    String jarEntryName = jarEntry.getName();
                    if (jarEntryName.startsWith(path) && jarEntryName.endsWith(".class")) {
                        String noSuffixFileName =
                                jarEntryName.substring(0, jarEntryName.indexOf(".class"));
                        String filePackage = noSuffixFileName.replace(File.separator, ".");
                        Class<?> clazz = Class.forName(filePackage, false, Thread.currentThread().getContextClassLoader());
                        execute.execute(clazz);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (url.toString().startsWith("file")) {
            try {
                String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                File dir = new File(filePath);
                ArrayList<File> allFile = new ArrayList<>();
                FileUtil.fetchFileList(dir, allFile);
                for (File f : allFile) {
                    String fileName = f.getAbsolutePath();
                    if (fileName.endsWith(".class")) {
                        String noSuffixFileName =
                                fileName.substring(8 + fileName.lastIndexOf("classes"), fileName.indexOf(".class"));
                        String filePackage = noSuffixFileName.replace(File.separator, ".");
                        Class<?> clazz = Class.forName(filePackage, false, Thread.currentThread().getContextClassLoader());
                        execute.execute(clazz);
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
