package com.laputa.server.core.protocol.model.messages.appllication;

import com.laputa.server.core.protocol.model.messages.BinaryMessage;

import static com.laputa.server.core.protocol.enums.Command.*;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/1/2015.
 */
public class LoadProfileGzippedBinaryMessage extends BinaryMessage {

    public LoadProfileGzippedBinaryMessage(int messageId, byte[] data) {
        super(messageId, LOAD_PROFILE_GZIPPED, data);
    }

    @Override
    public String toString() {
        return "LoadProfileGzippedBinaryMessage{" + super.toString() + "}";
    }
}
