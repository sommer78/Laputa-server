package com.laputa.server.core.protocol.model.messages.hardware;

import com.laputa.server.core.protocol.model.messages.StringMessage;

import static com.laputa.server.core.protocol.enums.Command.*;

/**
 * The Laputa Project.
 * Created by Andrew Zakordonets.
 * Created on 27/4/2015.
 */
public class BridgeMessage extends StringMessage {

	public BridgeMessage(int messageId, String body) {
		super(messageId, BRIDGE, body.length(), body);
	}

	@Override
	public String toString() {
		return "BridgeMessage{" + super.toString() + "}";
	}
}
