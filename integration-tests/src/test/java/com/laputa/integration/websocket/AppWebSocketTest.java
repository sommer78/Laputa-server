package com.laputa.integration.websocket;

import com.laputa.integration.IntegrationBase;
import com.laputa.integration.model.tcp.ClientPair;
import com.laputa.integration.model.websocket.WebSocketClient;
import com.laputa.server.Holder;
import com.laputa.server.api.http.HttpAPIServer;
import com.laputa.server.application.AppServer;
import com.laputa.server.core.BaseServer;
import com.laputa.server.core.protocol.model.messages.ResponseMessage;
import com.laputa.server.hardware.HardwareServer;
import com.laputa.utils.properties.GCMProperties;
import com.laputa.utils.properties.MailProperties;
import com.laputa.utils.properties.SmsProperties;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

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
public class AppWebSocketTest extends IntegrationBase {

    private static BaseServer webSocketServer;
    private static BaseServer hardwareServer;
    private static BaseServer appServer;
    private static ClientPair clientPair;
    private static Holder localHolder;

    //web socket ports
    public static int tcpWebSocketPort;

    @AfterClass
    public static void shutdown() throws Exception {
        webSocketServer.close();
        appServer.close();
        hardwareServer.close();
        clientPair.stop();
        localHolder.close();
    }

    @BeforeClass
    public static void init() throws Exception {
        properties.setProperty("data.folder", getRelativeDataFolder("/profiles"));
        localHolder = new Holder(properties,
                new MailProperties(Collections.emptyMap()),
                new SmsProperties(Collections.emptyMap()),
                new GCMProperties(Collections.emptyMap()),
                false
        );
        tcpWebSocketPort = httpPort;
        webSocketServer = new HttpAPIServer(localHolder).start();
        appServer = new AppServer(localHolder).start();
        hardwareServer = new HardwareServer(localHolder).start();
        clientPair = initAppAndHardPair(tcpAppPort, tcpHardPort, properties);
    }

    @Test
    public void testAppWebSocketlogin() throws Exception{
        WebSocketClient webSocketClient = new WebSocketClient("localhost", tcpWebSocketPort, "/websockets", false);
        webSocketClient.start();
        webSocketClient.send("login " + DEFAULT_TEST_USER + " 1");
        verify(webSocketClient.responseMock, timeout(500)).channelRead(any(), eq(new ResponseMessage(1, OK)));
        webSocketClient.send("ping");
        verify(webSocketClient.responseMock, timeout(500)).channelRead(any(), eq(new ResponseMessage(2, OK)));
    }



}
