package com.xiuxiu.core.log;

import com.xiuxiu.core.utils.Charsetutil;
import com.xiuxiu.core.utils.FileUtil;
import com.xiuxiu.core.utils.OSUtil;
import com.xiuxiu.core.utils.StringUtil;

import java.io.*;

public class LogFileWriter {
    private QuietWriter writer;
    private boolean immediateFlush;

    public LogFileWriter() {

    }

    public void init(String fullFileName) throws IOException {
        this.init(fullFileName, true);
    }

    public void init(String fullFileName, boolean append) throws IOException {
        this.init(fullFileName, append, false);
    }

    public void init(String fullFileName, boolean append, boolean bufferedIo) throws IOException {
        this.init(fullFileName, append, bufferedIo, 4096);
    }

    public void init(String fullFileName, boolean append, boolean bufferedIo, int buffSize) throws IOException {
        if (StringUtil.isEmptyOrNull(fullFileName)) {
            throw new IllegalArgumentException("fullFileName is null");
        }
        if (bufferedIo && buffSize < 1) {
            throw new IllegalArgumentException("buffSize:" + buffSize + " is invalid");
        }
        this.immediateFlush = !bufferedIo;
        this.close();
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(fullFileName, append);
        } catch (FileNotFoundException e) {
            int index = fullFileName.lastIndexOf(File.separator);
            if (-1 == index) {
                throw e;
            }
            String dir = fullFileName.substring(0, index);
            if (!FileUtil.mkdirs(dir)) {
                throw e;
            }
            fileOutputStream = new FileOutputStream(fullFileName, append);
        }
        Writer writer = new OutputStreamWriter(fileOutputStream, Charsetutil.UTF8);
        if (bufferedIo) {
            writer = new BufferedWriter(writer, buffSize);
        }
        this.writer = new QuietWriter(writer);
    }

    public boolean write(String line) {
        if (null == this.writer) {
            return false;
        }
        if (StringUtil.isEmptyOrNull(line)) {
            return false;
        }
        this.writer.write(line);
        if (this.immediateFlush) {
            this.writer.flush();
        }
        return true;
    }

    public boolean writeLine(String line) {
        if (null == this.writer) {
            return false;
        }
        if (StringUtil.isEmptyOrNull(line)) {
            return false;
        }
        this.writer.write(line);
        this.writer.write(OSUtil.LINE_SEPARATOR);
        if (this.immediateFlush) {
            this.writer.flush();
        }
        return true;
    }

    public void close() {
        if (null != this.writer) {
            try {
                this.writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
