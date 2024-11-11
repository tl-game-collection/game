package com.xiuxiu.core.net.codec;

import java.security.InvalidParameterException;

public class Codecs {
    private static Encoder[] allEncoder = new Encoder[4];
    private static Decoder[] allDecoder = new Decoder[4];

    static {
        register(Type.JSON, new JsonEncoder(), new JsonDecoder());
    }

    private static void register(Type type, Encoder encoder, Decoder decoder) {
        if (type.ordinal() >= allEncoder.length) {
            Encoder[] newAllEncoder = new Encoder[type.ordinal() + 1];
            Decoder[] newAllDecoder = new Decoder[type.ordinal() + 1];
            System.arraycopy(allEncoder, 0, newAllEncoder, 0, allEncoder.length);
            System.arraycopy(allDecoder, 0, newAllDecoder, 0, allEncoder.length);
            allEncoder = newAllEncoder;
            allDecoder = newAllDecoder;
        }
        allEncoder[type.ordinal()] = encoder;
        allDecoder[type.ordinal()] = decoder;
    }

    public static int getCommandId(Type type, String className) {
        if (Type.JSON == type) {
            return JsonDecoder.getCommandId(className);
        }
        return -1;
    }

    public static Encoder getEncoder(byte type) {
        if (type < 0 || type >= allEncoder.length) {
            throw new InvalidParameterException("encoder in range [0, " + allEncoder.length + "]");
        }
        return allEncoder[type];
    }

    public static Encoder getEncoder(Type type) {
        if (null == type) {
            return null;
        }
        return allEncoder[type.ordinal()];
    }

    public static Decoder getDecoder(byte type) {
        if (type < 0 || type >= allDecoder.length) {
            throw new InvalidParameterException("decoder in range [0, " + allDecoder.length + "]");
        }
        return allDecoder[type];
    }

    public static Decoder getDecoder(Type type) {
        if (null == type) {
            return null;
        }
        return allDecoder[type.ordinal()];
    }

    public enum Type {
        PB,
        JSON;

        public static Type parse(byte value) {
            switch (value) {
                case 0:
                    return PB;
                case 1:
                    return JSON;
            }
            return null;
        }

        public static byte value(Type type) {
            return (byte) type.ordinal();
        }
    }
}
