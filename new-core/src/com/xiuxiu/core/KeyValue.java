package com.xiuxiu.core;

public class KeyValue<Key, Value> {
    private Key key;
    private Value value;

    public KeyValue() {

    }

    public KeyValue(Key key, Value value) {
        this.key = key;
        this.value = value;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }
}
