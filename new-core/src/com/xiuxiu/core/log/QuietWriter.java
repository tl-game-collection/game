package com.xiuxiu.core.log;

import java.io.FilterWriter;
import java.io.IOException;
import java.io.Writer;

public class QuietWriter extends FilterWriter {
    protected QuietWriter(Writer out) {
        super(out);
    }

    @Override
    public void flush() {
        try {
            super.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(String str) {
        try {
            super.write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
