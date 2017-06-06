package com.laputa.server.core.dao;

import com.laputa.server.core.BlockingIOProcessor;
import com.laputa.server.core.model.DashBoard;
import com.laputa.server.core.model.auth.User;
import com.laputa.server.core.model.device.Device;
import com.laputa.server.redis.RedisClient;
import com.laputa.utils.TokenGeneratorUtil;

import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 14.10.16.
 */
public class TokenManager {

    private final RegularTokenManager regularTokenManager;
    private final SharedTokenManager sharedTokenManager;
    private final BlockingIOProcessor blockingIOProcessor;
    private final RedisClient redisClient;
    private final String currentIp;

    public TokenManager(ConcurrentMap<UserKey, User> users, BlockingIOProcessor blockingIOProcessor, RedisClient redisClient, String currentIp) {
        Collection<User> allUsers = users.values();
        this.regularTokenManager = new RegularTokenManager(allUsers);
        this.sharedTokenManager = new SharedTokenManager(allUsers);
        this.blockingIOProcessor = blockingIOProcessor;
        this.redisClient = redisClient;
        this.currentIp = currentIp;
    }

    public void deleteDevice(Device device) {
        String token = regularTokenManager.deleteDeviceToken(device);
        if (token != null) {
            blockingIOProcessor.executeDB(() -> {
                redisClient.removeToken(token);
            });
        }
    }

    public void deleteDash(DashBoard dash) {
        //todo clear shared token from redis?
        sharedTokenManager.deleteProject(dash);
        String[] removedTokens = regularTokenManager.deleteProject(dash);

        if (removedTokens.length > 0) {
            blockingIOProcessor.executeDB(() -> {
                redisClient.removeToken(removedTokens);
            });
        }

    }

    public TokenValue getUserByToken(String token) {
        return regularTokenManager.getUserByToken(token);
    }

    public SharedTokenValue getUserBySharedToken(String token) {
        return sharedTokenManager.getUserByToken(token);
    }

    public void assignToken(User user, int dashId, int deviceId, String newToken) {
        String oldToken = regularTokenManager.assignToken(user, dashId, deviceId, newToken);

        blockingIOProcessor.executeDB(() -> {
            redisClient.assignServerToToken(newToken, currentIp);
            if (oldToken != null) {
                redisClient.removeToken(oldToken);
            }
        });
    }

    public String refreshToken(User user, int dashId, int deviceId) {
        final String newToken = TokenGeneratorUtil.generateNewToken();
        assignToken(user, dashId, deviceId, newToken);
        return newToken;
    }

    public String refreshSharedToken(User user, DashBoard dash) {
        final String newToken = TokenGeneratorUtil.generateNewToken();
        sharedTokenManager.assignToken(user, dash, newToken);
        return newToken;
    }
}
