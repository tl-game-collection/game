package com.xiuxiu.core.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.*;

public final class CompressUtil {
    public static byte[] zlib(byte[] data) {
        if (null == data) {
            return null;
        }
        byte[] output = null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            Deflater deflater = new Deflater();
            deflater.reset();
            deflater.setInput(data);
            deflater.finish();
            byte[] buff = new byte[1024];
            int ret = -1;
            while (!deflater.finished()) {
                ret = deflater.deflate(buff);
                bos.write(buff, 0, ret);
            }
            deflater.end();
            output = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }

    public static byte[] unzlib(byte[] data) {
        if (null == data) {
            return null;
        }
        byte[] output = null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            Inflater inflater = new Inflater();
            inflater.reset();
            inflater.setInput(data);
            byte[] buff = new byte[1024];
            int ret = -1;
            while (!inflater.finished()) {
                ret = inflater.inflate(buff);
                bos.write(buff, 0, ret);
            }
            inflater.end();
            output = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DataFormatException e) {
            e.printStackTrace();
        }
        return output;
    }

    public static byte[] zip(byte[] data) {
        byte[] dest = null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ZipOutputStream zip = new ZipOutputStream(bos)) {
            ZipEntry zipEntry = new ZipEntry("");
            zipEntry.setSize(data.length);
            zip.putNextEntry(zipEntry);
            zip.write(data);
            zip.closeEntry();
            zip.finish();
            dest = bos.toByteArray();
            return dest;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dest;
    }

    public static byte[] unZip(byte[] data) {
        byte[] dest = null;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
            ZipInputStream zip = new ZipInputStream(bis);
            ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buff = new byte[1024];
            int ret = -1;
            while (null != zip.getNextEntry()) {
                while (-1 != (ret = zip.read(buff, 0, 1024))) {
                    bos.write(buff, 0, ret);
                }
            }
            dest = bos.toByteArray();
            bos.flush();
            return dest;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dest;
    }

    public static byte[] gzip(byte[] data) {
        byte[] dest = null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             GZIPOutputStream gzip = new GZIPOutputStream(bos)) {
            gzip.write(data);
            gzip.finish();
            dest = bos.toByteArray();
            return dest;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dest;
    }

    public static byte[] unGZip(byte[] data) {
        byte[] dest = null;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
            GZIPInputStream gzip = new GZIPInputStream(bis);
            ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buff = new byte[1024];
            int ret = -1;
            while (-1 != (ret = gzip.read(buff, 0, 1024))) {
                bos.write(buff, 0, ret);
            }
            dest = bos.toByteArray();
            bos.flush();
            return dest;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dest;
    }
}
