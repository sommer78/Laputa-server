package com.laputa.integration.tcp;

import com.laputa.integration.IntegrationBase;
import com.laputa.integration.model.tcp.ClientPair;
import com.laputa.server.application.AppServer;
import com.laputa.server.core.BaseServer;
import com.laputa.server.core.model.Profile;
import com.laputa.server.core.model.auth.App;
import com.laputa.server.core.model.enums.ProvisionType;
import com.laputa.server.core.model.enums.Theme;
import com.laputa.server.hardware.HardwareServer;
import com.laputa.utils.JsonParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 2/2/2015.
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class AppWorkflowTest extends IntegrationBase {

    private BaseServer appServer;
    private BaseServer hardwareServer;
    private ClientPair clientPair;

    @Before
    public void init() throws Exception {
        this.hardwareServer = new HardwareServer(holder).start();
        this.appServer = new AppServer(holder).start();

        this.clientPair = initAppAndHardPair();
        Files.deleteIfExists(Paths.get(getDataFolder(), "laputa", "userProfiles"));
    }

    @After
    public void shutdown() {
        this.appServer.close();
        this.hardwareServer.close();
        this.clientPair.stop();
    }

    @Test
    public void testPrintApp() throws Exception {
        App app = new App();
        app.id = "1";
        app.projectIds = new int[] {1};
        app.name = "My App";
        app.provisionType = ProvisionType.STATIC;
        app.color = 0;
        app.icon = "myIcon";
        app.theme = Theme.Laputa;
        System.out.println(JsonParser.mapper.writeValueAsString(app));
    }

    @Test
    public void testAppCreated() throws Exception {
        clientPair.appClient.send("createApp {\"theme\":\"Laputa\",\"isMultiFace\":true,\"provisionType\":\"STATIC\",\"color\":0,\"name\":\"My App\",\"icon\":\"myIcon\",\"projectIds\":[1]}");
        App app = JsonParser.parseApp(clientPair.appClient.getBody());
        assertNotNull(app);
        assertNotNull(app.id);
        assertEquals(13, app.id.length());
        assertEquals(Theme.Laputa, app.theme);
        assertEquals(ProvisionType.STATIC, app.provisionType);
        assertEquals(0, app.color);
        assertEquals("My App", app.name);
        assertEquals("myIcon", app.icon);
        assertTrue(app.isMultiFace);
        assertArrayEquals(new int[]{1}, app.projectIds);
    }

    @Test
    public void testAppCreated2() throws Exception {
        clientPair.appClient.send("createApp {\"theme\":\"Laputa\",\"provisionType\":\"STATIC\",\"color\":0,\"name\":\"My App\",\"icon\":\"myIcon\",\"projectIds\":[1]}");
        App app = JsonParser.parseApp(clientPair.appClient.getBody());
        assertNotNull(app);
        assertNotNull(app.id);

        clientPair.appClient.send("createApp {\"theme\":\"Laputa\",\"provisionType\":\"STATIC\",\"color\":0,\"name\":\"My App\",\"icon\":\"myIcon\",\"projectIds\":[2]}");
        app = JsonParser.parseApp(clientPair.appClient.getBody(2));
        assertNotNull(app);
        assertNotNull(app.id);
    }

    @Test
    public void testUnicodeName() throws Exception {
        clientPair.appClient.send("createApp {\"theme\":\"Laputa\",\"provisionType\":\"STATIC\",\"color\":0,\"name\":\"Моя апка\",\"icon\":\"myIcon\",\"projectIds\":[1]}");
        App app = JsonParser.parseApp(clientPair.appClient.getBody());
        assertNotNull(app);
        assertNotNull(app.id);
        assertEquals("Моя апка", app.name);
    }

    @Test
    public void testCantCreateWithSameId() throws Exception {
        clientPair.appClient.send("createApp {\"theme\":\"Laputa\",\"provisionType\":\"STATIC\",\"color\":0,\"name\":\"My App\",\"icon\":\"myIcon\",\"projectIds\":[1]}");
        App app = JsonParser.parseApp(clientPair.appClient.getBody());
        assertNotNull(app);
        assertNotNull(app.id);

        clientPair.appClient.send("createApp {\"id\":\"" + app.id + "\",\"theme\":\"Laputa\",\"provisionType\":\"STATIC\",\"color\":0,\"name\":\"My App\",\"icon\":\"myIcon\",\"projectIds\":[2]}");
        app = JsonParser.parseApp(clientPair.appClient.getBody(2));
        assertNotNull(app);
        assertNotNull(app.id);
    }

    @Test
    public void testAppUpdated() throws Exception {
        clientPair.appClient.send("createApp {\"theme\":\"Laputa\",\"provisionType\":\"STATIC\",\"color\":0,\"name\":\"My App\",\"icon\":\"myIcon\",\"projectIds\":[1]}");
        App app = JsonParser.parseApp(clientPair.appClient.getBody());
        assertNotNull(app);
        assertNotNull(app.id);

        clientPair.appClient.send("updateApp {\"id\":\"" + app.id  + "\",\"theme\":\"LaputaLight\",\"provisionType\":\"DYNAMIC\",\"color\":1,\"name\":\"My App 2\",\"icon\":\"myIcon2\",\"projectIds\":[1,2]}");
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(ok(2)));

        clientPair.appClient.send("loadProfileGzipped");
        String s = clientPair.appClient.getBody(3);

        Profile profile = parseProfile(s);
        assertNotNull(profile);

        assertNotNull(profile.apps);
        assertEquals(1, profile.apps.length);
        App app2 = profile.apps[0];
        assertEquals(app.id, app2.id);
        assertEquals(Theme.LaputaLight, app2.theme);
        assertEquals(ProvisionType.DYNAMIC, app2.provisionType);
        assertEquals(1, app2.color);
        assertEquals("My App 2", app2.name);
        assertEquals("myIcon2", app2.icon);
        assertArrayEquals(new int[]{1, 2}, app2.projectIds);
    }

    @Test
    public void testAppDelete() throws Exception {
        clientPair.appClient.send("createApp {\"id\":1,\"theme\":\"Laputa\",\"provisionType\":\"STATIC\",\"color\":0,\"name\":\"My App\",\"icon\":\"myIcon\",\"projectIds\":[1]}");
        App app = JsonParser.parseApp(clientPair.appClient.getBody());
        assertNotNull(app);
        assertNotNull(app.id);

        clientPair.appClient.send("deleteApp " + app.id);
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(ok(2)));

        clientPair.appClient.send("loadProfileGzipped");
        String s = clientPair.appClient.getBody(3);

        Profile profile = parseProfile(s);
        assertNotNull(profile);

        assertNotNull(profile.apps);
        assertEquals(0, profile.apps.length);
    }
}
