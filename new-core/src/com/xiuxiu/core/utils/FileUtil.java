package com.xiuxiu.core.utils;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

public final class FileUtil {
    public static boolean mkdirs(String path) {
        File dir = new File(path);
        if (dir.exists()) {
            return true;
        }
        return dir.mkdirs();
    }

    public static boolean moveTo(File from, String toPath) {
        if (!from.exists()) {
            return false;
        }
        File to = new File(toPath);
        if (null != to.getParentFile()) {
            to.getParentFile().mkdirs();
        }
        return from.renameTo(new File(toPath));
    }

    public static boolean moveTo(String fromPath, String toPath) {
        File from = new File(fromPath);
        if (!from.exists()) {
            return false;
        }
        File to = new File(toPath);
        if (null != to.getParentFile()) {
            to.getParentFile().mkdirs();
        }
        return from.renameTo(to);
    }

    public static boolean delete(String path) {
        File file = new File(path);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }


    /**
     * 获取所有文件
     * @param file
     * @param fileList
     */
    public static void fetchFileList(java.io.File file, List<File> fileList) {
        if (file.isDirectory()) {
            for (java.io.File f : file.listFiles()) {
                fetchFileList(f, fileList);
            }
        } else {
            fileList.add(file);
        }
    }

    public static String readFileString(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        byte[] data = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            int len = fis.read(data);
            if (len == data.length) {
                return new String(data, Charsetutil.UTF8);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String readFileString(InputStream is) {
        if (null == is) {
            return null;
        }
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buff = new byte[2048];
            int ret = 0;
            while ((ret = is.read(buff, 0, 2048)) > 0) {
                bos.write(buff, 0, ret);
            }
            return new String(bos.toByteArray(), Charsetutil.UTF8);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] readFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        try (FileInputStream is = new FileInputStream(file)) {
            FileChannel fc = is.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) fc.size());
            while (fc.read(byteBuffer) > 0) {
            }
            fc.close();
            return byteBuffer.array();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] readFile(FileInputStream is) {
        if (null == is) {
            return null;
        }
        try {
            FileChannel fc = is.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) fc.size());
            while (fc.read(byteBuffer) > 0) {
            }
            fc.close();
            return byteBuffer.array();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] readFile(InputStream is) {
        if (null == is) {
            return null;
        }
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buff = new byte[2048];
            int ret = 0;
            while ((ret = is.read(buff, 0, 2048)) > 0) {
                bos.write(buff, 0, ret);
            }
            return bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void writeFile(String fullFileName, String content) {
        if (null == content) {
            return;
        }
        File file = new File(fullFileName);
        File parentFile = file.getParentFile();
        if (null != parentFile) {
            parentFile.mkdirs();
        }
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(content);
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeFile(String fullFileName, byte[] content) {
        if (null == content) {
            return;
        }
        File file = new File(fullFileName);
        File parentFile = file.getParentFile();
        if (null != parentFile) {
            parentFile.mkdirs();
        }
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(content);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
