package com.xiuxiu.core.net.codec;

public interface ByteBufferWrapper {
    ByteBufferWrapper get(int capacity);

    void writeByte(byte value);

    byte readByte();

    void writeShort(short value);

    short readShort();

    void writeInt(int value);
    void writeIntLE(int value);

    int readInt();
    int readIntLE();

    void writeLong(long value);
    void writeLongLE(long value);

    long readLong();
    long readLongLE();

    void writeBytes(byte[] value);

    void readBytes(byte[] value);

    int readableBytes();

    int readerIndex();

    void markReaderIndex();

    void resetReaderIndex();

    boolean isReadable();
}
