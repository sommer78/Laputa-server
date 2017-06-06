package com.laputa.integration.websocket;

import com.laputa.integration.BaseTest;
import com.laputa.integration.model.websocket.WebSocketClient;
import com.laputa.server.api.http.HttpAPIServer;
import com.laputa.server.api.http.HttpsAPIServer;
import com.laputa.server.core.BaseServer;
import com.laputa.server.core.protocol.model.messages.ResponseMessage;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static com.laputa.server.core.protocol.enums.Response.OK;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 13.01.16.
 */
@RunWith(MockitoJUnitRunner.class)
public class WebSslSocketTest extends BaseTest {

    private static BaseServer webSocketServer;
    public static int sslWebSocketPort = httpsPort;

    @AfterClass
    public static void shutdown() throws Exception {
        webSocketServer.close();
    }

    @Before
    public void init() throws Exception {
        if (webSocketServer == null) {
            webSocketServer = new HttpsAPIServer(holder, false).start();
        }
    }

    @Override
    public String getDataFolder() {
        return getRelativeDataFolder("/profiles");
    }

    @Test
    public void testBasicWebSocketCommandsOk() throws Exception{
        WebSocketClient webSocketClient = new WebSocketClient("localhost", sslWebSocketPort, HttpAPIServer.WEBSOCKET_PATH, true);
        webSocketClient.start();
        webSocketClient.send("login 4ae3851817194e2596cf1b7103603ef8");
        verify(webSocketClient.responseMock, timeout(500)).channelRead(any(), eq(new ResponseMessage(1, OK)));
        webSocketClient.send("ping");
        verify(webSocketClient.responseMock, timeout(500)).channelRead(any(), eq(new ResponseMessage(2, OK)));
    }

}
