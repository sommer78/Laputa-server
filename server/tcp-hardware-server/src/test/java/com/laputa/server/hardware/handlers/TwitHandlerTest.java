package com.laputa.server.hardware.handlers;

import com.laputa.server.core.BlockingIOProcessor;
import com.laputa.server.core.dao.SessionDao;
import com.laputa.server.core.dao.UserDao;
import com.laputa.server.core.model.DashBoard;
import com.laputa.server.core.model.Profile;
import com.laputa.server.core.model.auth.User;
import com.laputa.server.core.model.widgets.notifications.Twitter;
import com.laputa.server.core.protocol.enums.Command;
import com.laputa.server.core.protocol.exceptions.NotificationBodyInvalidException;
import com.laputa.server.core.protocol.exceptions.QuotaLimitException;
import com.laputa.server.core.protocol.model.messages.MessageFactory;
import com.laputa.server.core.protocol.model.messages.hardware.TwitMessage;
import com.laputa.server.core.session.HardwareStateHolder;
import com.laputa.server.hardware.handlers.hardware.logic.TwitLogic;
import com.laputa.server.notifications.twitter.TwitterWrapper;
import com.laputa.utils.ServerProperties;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * The Laputa Project.
 * Created by Andrew Zakordonets.
 * Created on 26.04.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class TwitHandlerTest {

	@Mock
	private static ServerProperties props;

	@Mock
	private BlockingIOProcessor blockingIOProcessor;

	@Mock
	private TwitterWrapper twitterWrapper;

	@Mock
	private ChannelHandlerContext ctx;

	@Mock
	private UserDao userDao;

	@Mock
	private SessionDao sessionDao;

	@Mock
	private TwitLogic tweetHandler;

	@Mock
	private User user;

	@Mock
	private Profile profile;

	@Mock
	private DashBoard dash;


    private HardwareStateHolder state;

    @Before
    public void setup() {
        state = new HardwareStateHolder(1, 0, user, "x");
    }

	@Test(expected = NotificationBodyInvalidException.class)
	public void testTweetMessageWithEmptyBody() {
		TwitMessage twitMessage = (TwitMessage) MessageFactory.produce(1, Command.TWEET, "");
        state.user.profile = profile;
		TwitLogic tweetHandler = new TwitLogic(blockingIOProcessor, twitterWrapper, 60);
		tweetHandler.messageReceived(ctx, state, twitMessage);
	}

	@Test(expected = NotificationBodyInvalidException.class)
	public void testTweetMessageWithBodyMoreThen140Symbols() {
		final String longBody = RandomStringUtils.random(150);
		TwitMessage twitMessage = (TwitMessage) MessageFactory.produce(1, Command.TWEET, longBody);
        state.user.profile = profile;
		TwitLogic tweetHandler = new TwitLogic(blockingIOProcessor, twitterWrapper, 60);
		tweetHandler.messageReceived(ctx, state, twitMessage);
	}

	@Test(expected = QuotaLimitException.class)
	public void testSendQuotaLimitationException() throws InterruptedException {
		TwitMessage twitMessage = (TwitMessage) MessageFactory.produce(1, Command.TWEET, "this is a test tweet");
		TwitLogic tweetHandler = spy(new TwitLogic(blockingIOProcessor, twitterWrapper, 60));
        state.user.profile = profile;
		Twitter twitter = new Twitter();
		twitter.token = "token";
		twitter.secret = "secret_token";
		when(state.user.profile.getDashByIdOrThrow(1)).thenReturn(dash);
		when(dash.getWidgetByType(Twitter.class)).thenReturn(twitter);
		dash.isActive = true;

		tweetHandler.messageReceived(ctx, state, twitMessage);
		tweetHandler.messageReceived(ctx, state, twitMessage);
	}

	@Test
	public void testSendQuotaLimitationIsWorking() throws InterruptedException {
		TwitMessage twitMessage = (TwitMessage) MessageFactory.produce(1, Command.TWEET, "this is a test tweet");
		ServerProperties props = new ServerProperties(Collections.emptyMap());
		props.setProperty("notifications.frequency.user.quota.limit", "1");
		final long defaultQuotaTime = props.getLongProperty("notifications.frequency.user.quota.limit") * 1000;
		TwitLogic tweetHandler = spy(new TwitLogic(blockingIOProcessor, twitterWrapper, 60));
		state.user.profile = profile;
		Twitter twitter = new Twitter();
		twitter.token = "token";
		twitter.secret = "secret_token";
		when(state.user.profile.getDashByIdOrThrow(1)).thenReturn(dash);
		when(dash.getWidgetByType(Twitter.class)).thenReturn(twitter);
		dash.isActive = true;

		tweetHandler.messageReceived(ctx, state, twitMessage);
		TimeUnit.MILLISECONDS.sleep(defaultQuotaTime);
		tweetHandler.messageReceived(ctx, state, twitMessage);
	}

}
