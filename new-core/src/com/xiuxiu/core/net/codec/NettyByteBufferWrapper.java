package com.xiuxiu.core.net.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class NettyByteBufferWrapper implements ByteBufferWrapper {
    private ByteBuf buffer;

    public NettyByteBufferWrapper() {

    }

    public NettyByteBufferWrapper(ByteBuf buffer) {
        this.buffer = buffer;
    }

    @Override
    public ByteBufferWrapper get(int capacity) {
        this.buffer = Unpooled.buffer(capacity);
        return this;
    }

    @Override
    public void writeByte(byte value) {
        this.buffer.writeByte(value);
    }

    @Override
    public byte readByte() {
        return this.buffer.readByte();
    }

    @Override
    public void writeShort(short value) {
        this.buffer.writeShort(value);
    }

    @Override
    public short readShort() {
        return this.buffer.readShort();
    }

    @Override
    public void writeInt(int value) {
        this.buffer.writeInt(value);
    }

    @Override
    public void writeIntLE(int value) {
        this.buffer.writeIntLE(value);
    }

    @Override
    public int readInt() {
        return this.buffer.readInt();
    }

    @Override
    public int readIntLE() {
        return this.buffer.readIntLE();
    }

    @Override
    public void writeLong(long value) {
        this.buffer.writeLong(value);
    }

    @Override
    public void writeLongLE(long value) {
        this.buffer.writeLongLE(value);
    }

    @Override
    public long readLong() {
        return this.buffer.readLong();
    }

    @Override
    public long readLongLE() {
        return this.buffer.readLongLE();
    }

    @Override
    public void writeBytes(byte[] value) {
        this.buffer.writeBytes(value);
    }

    @Override
    public void readBytes(byte[] value) {
        this.buffer.readBytes(value);
    }

    @Override
    public int readableBytes() {
        return this.buffer.readableBytes();
    }

    @Override
    public int readerIndex() {
        return this.buffer.readerIndex();
    }

    @Override
    public void markReaderIndex() {
        this.buffer.markReaderIndex();
    }

    @Override
    public void resetReaderIndex() {
        this.buffer.resetReaderIndex();
    }

    @Override
    public boolean isReadable() {
        return this.buffer.isReadable();
    }
}
