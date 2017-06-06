package com.laputa.integration.model;


import com.laputa.server.core.protocol.model.messages.MessageBase;
import com.laputa.server.core.protocol.model.messages.ResponseMessage;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 3/1/2015.
 */
public class MockHolder {

    private final SimpleClientHandler mock;

    public MockHolder(SimpleClientHandler mock) {
        this.mock = mock;
    }

    public MockHolder check(int responseMessageCode) throws Exception {
        verify(mock).channelRead(any(), eq(new ResponseMessage(1, responseMessageCode)));
        return this;
    }

    public MockHolder check(int times, int responseMessageCode) throws Exception {
        verify(mock, times(times)).channelRead(any(), eq(new ResponseMessage(1, responseMessageCode)));
        return this;
    }

    public void check(MessageBase responseMessage) throws Exception {
        verify(mock).channelRead(any(), eq(responseMessage));
    }

}


