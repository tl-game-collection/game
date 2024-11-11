package com.xiuxiu.core.net.protocol;

import java.security.InvalidParameterException;

public class ProtocolFactory {
    private static Protocol[] allProtocol = new Protocol[4];

    static {
        register(new ClientProtocol());
        register(new ClientProtocolV2());
        register(new ServiceProtocol());
    }

    private static void register(Protocol protocol) {
        if (protocol.getVersion() >= allProtocol.length) {
            Protocol[] newAllProtocol = new Protocol[protocol.getVersion() + 1];
            System.arraycopy(allProtocol, 0, newAllProtocol, 0, allProtocol.length);
            allProtocol = newAllProtocol;
        }
        allProtocol[protocol.getVersion()] = protocol;
    }

    public static Protocol getProtocol(byte protocolVersion) {
        if (protocolVersion < 0 || protocolVersion >= allProtocol.length) {
            throw new InvalidParameterException("protocol version in [0, " + allProtocol.length + ")");
        }
        return allProtocol[protocolVersion];
    }

    public static Protocol getProtocol(Protocol.Version protocolVersion) {
        if (null == protocolVersion) {
            return null;
        }
        return allProtocol[protocolVersion.ordinal()];
    }
}
