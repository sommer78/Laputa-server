package com.laputa.server.redis;

import org.junit.Ignore;
import org.junit.Test;
import redis.clients.jedis.exceptions.JedisConnectionException;

import static org.junit.Assert.assertEquals;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 14.10.16.
 */
public class RedisClientTest {

    @Test(expected = JedisConnectionException.class)
    public void testCreationFailed() {
        RedisClient redisClient = new RedisClient("localhost", "", 6378, false);
        assertEquals(null, redisClient);
    }

    @Test
    @Ignore
    public void testGetTestString() {
        RedisClient redisClient = new RedisClient("localhost", "pass123", 6378, false);
        String result = redisClient.getServerByToken("test");
        assertEquals("It's working!", result);
    }


}
