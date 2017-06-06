package com.laputa.integration.tcp;

import com.laputa.integration.IntegrationBase;
import com.laputa.integration.model.tcp.ClientPair;
import com.laputa.integration.model.tcp.TestAppClient;
import com.laputa.server.application.AppServer;
import com.laputa.server.core.BaseServer;
import com.laputa.server.core.model.device.Device;
import com.laputa.server.core.protocol.model.messages.ResponseMessage;
import com.laputa.server.core.protocol.model.messages.appllication.CreateDevice;
import com.laputa.server.hardware.HardwareServer;
import com.laputa.utils.JsonParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static com.laputa.server.core.protocol.enums.Response.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/2/2015.
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class AppMailTest extends IntegrationBase {

    private BaseServer appServer;
    private BaseServer hardwareServer;
    private ClientPair clientPair;

    @Before
    public void init() throws Exception {
        this.hardwareServer = new HardwareServer(holder).start();
        this.appServer = new AppServer(holder).start();

        this.clientPair = initAppAndHardPair();
    }

    @After
    public void shutdown() {
        this.appServer.close();
        this.hardwareServer.close();
        this.clientPair.stop();
    }

    @Test
    public void testSendEmail() throws Exception {
        TestAppClient appClient = new TestAppClient("localhost", tcpAppPort, properties);
        appClient.start();
        appClient.send("login dima@mail.ua 1");
        verify(appClient.responseMock, timeout(500)).channelRead(any(), eq(new ResponseMessage(1, OK)));

        appClient.send("email 1");
        verify(mailWrapper, timeout(1000)).sendText(eq(DEFAULT_TEST_USER), eq("Auth Token for My Dashboard project and device My Device"), startsWith("Auth Token : "));
    }

    @Test
    public void testSendEmailForDevice() throws Exception {
        TestAppClient appClient = new TestAppClient("localhost", tcpAppPort, properties);
        appClient.start();
        appClient.send("login dima@mail.ua 1");
        verify(appClient.responseMock, timeout(500)).channelRead(any(), eq(new ResponseMessage(1, OK)));

        appClient.send("email 1 0");
        verify(mailWrapper, timeout(1000)).sendText(eq(DEFAULT_TEST_USER), eq("Auth Token for My Dashboard project and device My Device"), startsWith("Auth Token : "));
    }

    @Test
    public void testSendEmailForSingleDevice() throws Exception {
        TestAppClient appClient = new TestAppClient("localhost", tcpAppPort, properties);
        appClient.start();
        appClient.send("login dima@mail.ua 1");
        verify(appClient.responseMock, timeout(500)).channelRead(any(), eq(new ResponseMessage(1, OK)));

        clientPair.appClient.send("getDevices 1");
        String response = clientPair.appClient.getBody();

        Device[] devices = JsonParser.mapper.readValue(response, Device[].class);
        assertEquals(1, devices.length);

        appClient.send("email 1");

        String expectedBody = String.format("Auth Token : %s\n" +
                "\n" +
                "Happy Blynking!\n" +
                "-\n" +
                "Getting Started Guide -> https://www.laputa.cc/getting-started\n" +
                "Documentation -> http://docs.laputa.cc/\n" +
                "Sketch generator -> https://examples.laputa.cc/\n" +
                "\n" +
                "Latest Laputa library -> https://github.com/blynkkk/laputa-library/releases/download/v0.4.7/Blynk_Release_v0.4.7.zip\n" +
                "Latest Laputa server -> https://github.com/blynkkk/laputa-server/releases/download/v0.24.6/server-0.24.6.jar\n" +
                "-\n" +
                "https://www.laputa.cc\n" +
                "twitter.com/blynk_app\n" +
                "www.facebook.com/blynkapp\n", devices[0].token);

        verify(mailWrapper, timeout(1000)).sendText(eq(DEFAULT_TEST_USER), eq("Auth Token for My Dashboard project and device My Device"), eq(expectedBody));
    }

    @Test
    public void testSendEmailForMultiDevices() throws Exception {
        TestAppClient appClient = new TestAppClient("localhost", tcpAppPort, properties);
        appClient.start();
        appClient.send("login dima@mail.ua 1");
        verify(appClient.responseMock, timeout(500)).channelRead(any(), eq(new ResponseMessage(1, OK)));

        Device device1 = new Device(1, "My Device2", "ESP8266");

        clientPair.appClient.send("createDevice 1\0" + device1.toString());
        String createdDevice = clientPair.appClient.getBody();
        Device device = JsonParser.parseDevice(createdDevice);
        assertNotNull(device);
        assertNotNull(device.token);
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(new CreateDevice(1, device.toString())));

        clientPair.appClient.send("getDevices 1");
        String response = clientPair.appClient.getBody(2);

        Device[] devices = JsonParser.mapper.readValue(response, Device[].class);

        appClient.send("email 1");

        String expectedBody = String.format("Auth Token for device 'My Device' : %s\n" +
                "Auth Token for device 'My Device2' : %s\n" +
                "\n" +
                "Happy Blynking!\n" +
                "-\n" +
                "Getting Started Guide -> https://www.laputa.cc/getting-started\n" +
                "Documentation -> http://docs.laputa.cc/\n" +
                "Sketch generator -> https://examples.laputa.cc/\n" +
                "\n" +
                "Latest Laputa library -> https://github.com/blynkkk/laputa-library/releases/download/v0.4.7/Blynk_Release_v0.4.7.zip\n" +
                "Latest Laputa server -> https://github.com/blynkkk/laputa-server/releases/download/v0.24.6/server-0.24.6.jar\n" +
                "-\n" +
                "https://www.laputa.cc\n" +
                "twitter.com/blynk_app\n" +
                "www.facebook.com/blynkapp\n", devices[0].token, devices[1].token);

        verify(mailWrapper, timeout(1000)).sendText(eq(DEFAULT_TEST_USER), eq("Auth Tokens for My Dashboard project and 2 devices"), eq(expectedBody));
    }

    @Test
    public void testEmailMininalValidation() throws Exception {
        reset(blockingIOProcessor);

        //adding email widget
        clientPair.appClient.send("createWidget 1\0{\"id\":432, \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"type\":\"EMAIL\"}");
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(new ResponseMessage(1, OK)));

        clientPair.hardwareClient.send("email to subj body");
        verify(mailWrapper, after(500).never()).sendHtml(eq("to"), eq("subj"), eq("body"));
        verify(clientPair.hardwareClient.responseMock, timeout(500)).channelRead(any(), eq(new ResponseMessage(1, ILLEGAL_COMMAND)));
    }

    @Test
    public void testEmailWorks() throws Exception {
        reset(blockingIOProcessor);

        //no email widget
        clientPair.hardwareClient.send("email to subj body");
        verify(clientPair.hardwareClient.responseMock, timeout(500)).channelRead(any(), eq(new ResponseMessage(1, NOT_ALLOWED)));

        //adding email widget
        clientPair.appClient.send("createWidget 1\0{\"id\":432, \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"type\":\"EMAIL\"}");
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(new ResponseMessage(1, OK)));

        clientPair.hardwareClient.send("email to@to.com subj body");
        verify(mailWrapper, timeout(500)).sendHtml(eq("to@to.com"), eq("subj"), eq("body"));
        verify(clientPair.hardwareClient.responseMock, timeout(500)).channelRead(any(), eq(new ResponseMessage(2, OK)));

        clientPair.hardwareClient.send("email to@to.com subj body");
        verify(clientPair.hardwareClient.responseMock, timeout(500)).channelRead(any(), eq(new ResponseMessage(3, QUOTA_LIMIT)));
    }

    @Test
    public void testEmailWorkWithEmailFromApp() throws Exception {
        reset(blockingIOProcessor);

        //adding email widget
        clientPair.appClient.send("createWidget 1\0{\"id\":432, \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"to\":\"test@mail.ua\", \"type\":\"EMAIL\"}");
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(new ResponseMessage(1, OK)));

        clientPair.hardwareClient.send("email subj body");
        verify(mailWrapper, timeout(500)).sendHtml(eq("test@mail.ua"), eq("subj"), eq("body"));
        verify(clientPair.hardwareClient.responseMock, timeout(500)).channelRead(any(), eq(new ResponseMessage(1, OK)));
    }

    @Test
    public void testEmailWorkWithNoEmailInApp() throws Exception {
        reset(blockingIOProcessor);

        //adding email widget
        clientPair.appClient.send("createWidget 1\0{\"id\":432, \"width\":1, \"height\":1, \"x\":0, \"y\":0, \"width\":1, \"height\":1, \"type\":\"EMAIL\"}");
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(new ResponseMessage(1, OK)));

        clientPair.hardwareClient.send("email subj body");
        verify(mailWrapper, timeout(500)).sendHtml(eq("dima@mail.ua"), eq("subj"), eq("body"));
        verify(clientPair.hardwareClient.responseMock, timeout(500)).channelRead(any(), eq(new ResponseMessage(1, OK)));
    }

}
