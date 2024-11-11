package com.xiuxiu.core;

public class Pair {
    public byte a = -1;
    public byte b = -1;
    public byte c = -1;

    public Pair(byte a, byte b) {
        this.a = a;
        this.b = b;
    }

    public Pair(byte a, byte b, byte c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public boolean equals(Object obj) {
        Pair p = (Pair) obj;
        if (null == p) {
            return false;
        }
        return a == p.a && b == p.b && c == p.c;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "a=" + a +
                ", b=" + b +
                ", c=" + c +
                '}';
    }
}
