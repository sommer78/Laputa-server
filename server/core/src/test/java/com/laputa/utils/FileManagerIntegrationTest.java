package com.laputa.utils;

import com.laputa.server.core.dao.FileManager;
import com.laputa.server.core.dao.UserKey;
import com.laputa.server.core.model.AppName;
import com.laputa.server.core.model.auth.User;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * User: ddumanskiy
 * Date: 09.12.13
 * Time: 8:07
 */
public class FileManagerIntegrationTest {

    private final User user1 = new User("name1", "pass1", AppName.LAPUTA, "local", false, false);
    private final User user2 = new User("name2", "pass2", AppName.LAPUTA, "local", false, false);

    private FileManager fileManager;

    @Before
    public void cleanup() throws IOException {
        String dataFolder = Paths.get(System.getProperty("java.io.tmpdir"), "laputa").toString();
        org.apache.commons.io.FileUtils.deleteDirectory(Paths.get(dataFolder).toFile());
        fileManager = new FileManager(dataFolder);
    }

    @Test
    public void testGenerateFileName() {
        Path file = fileManager.generateFileName(user1.email, user1.appName);
        assertEquals("name1.Laputa.user", file.getFileName().toString());
    }

    @Test
    public void testNotNullTokenManager() throws IOException {
        fileManager.overrideUserFile(user1);

        Map<UserKey, User> users = fileManager.deserializeUsers();
        assertNotNull(users);
        assertNotNull(users.get(new UserKey(user1.email, AppName.LAPUTA)));
    }

    @Test
    public void testCreationTempFile() throws IOException {
        fileManager.overrideUserFile(user1);
        //file existence ignored
        fileManager.overrideUserFile(user1);
    }

    @Test
    public void testReadListOfFiles() throws IOException {
        fileManager.overrideUserFile(user1);
        fileManager.overrideUserFile(user2);
        Path fakeFile = Paths.get(fileManager.getDataDir().toString(), "123.txt");
        Files.deleteIfExists(fakeFile);
        Files.createFile(fakeFile);

        Map<UserKey, User> users = fileManager.deserializeUsers();
        assertNotNull(users);
        assertEquals(2, users.size());
        assertNotNull(users.get(new UserKey(user1.email, AppName.LAPUTA)));
        assertNotNull(users.get(new UserKey(user2.email, AppName.LAPUTA)));
    }

    @Test
    public void testOverrideFiles() throws IOException {
        fileManager.overrideUserFile(user1);
        fileManager.overrideUserFile(user1);

        Map<UserKey, User> users = fileManager.deserializeUsers();
        assertNotNull(users);
        assertNotNull(users.get(new UserKey(user1.email, AppName.LAPUTA)));
    }

}
