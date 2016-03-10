package com.server.protocol.handlers;

import io.netty.channel.ChannelHandler;

/**
 * Created by tahirmacbook on 29/01/2016.
 */
public interface ClientForwarding extends ChannelHandler {

    void sendToClient(String message);
}
