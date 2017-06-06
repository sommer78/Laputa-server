package com.laputa.server.hardware.handlers;

import com.laputa.server.core.BlockingIOProcessor;
import com.laputa.server.core.dao.SessionDao;
import com.laputa.server.core.dao.UserDao;
import com.laputa.server.core.model.DashBoard;
import com.laputa.server.core.model.Profile;
import com.laputa.server.core.model.auth.User;
import com.laputa.server.core.model.widgets.notifications.Mail;
import com.laputa.server.core.protocol.enums.Command;
import com.laputa.server.core.protocol.exceptions.IllegalCommandException;
import com.laputa.server.core.protocol.exceptions.NotAllowedException;
import com.laputa.server.core.protocol.model.messages.MessageFactory;
import com.laputa.server.core.protocol.model.messages.hardware.MailMessage;
import com.laputa.server.core.session.HardwareStateHolder;
import com.laputa.server.hardware.handlers.hardware.logic.MailLogic;
import com.laputa.server.notifications.mail.MailWrapper;
import com.laputa.utils.ServerProperties;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 07.04.15.
 */
@RunWith(MockitoJUnitRunner.class)
public class MailHandlerTest {

    @Mock
    private BlockingIOProcessor blockingIOProcessor;

    @Mock
    private MailWrapper mailWrapper;

    private final MailLogic mailHandler = new MailLogic(blockingIOProcessor, mailWrapper, 1);

	@Mock
	private ChannelHandlerContext ctx;

	@Mock
	private UserDao userDao;

	@Mock
	private SessionDao sessionDao;

	@Mock
	private ServerProperties serverProperties;

    @Mock
    private User user;

    @Mock
    private Profile profile;

    @Mock
    private DashBoard dashBoard;

    @Mock
    private Channel channel;

    @Test(expected = NotAllowedException.class)
	public void testNoEmailWidget() throws InterruptedException {
		MailMessage mailMessage = (MailMessage) MessageFactory.produce(1, Command.EMAIL, "body");

        user.profile = profile;
        when(profile.getDashByIdOrThrow(1)).thenReturn(dashBoard);
        when(dashBoard.getWidgetByType(Mail.class)).thenReturn(null);

        HardwareStateHolder state = new HardwareStateHolder(1, 0, user, "x");
        mailHandler.messageReceived(ctx, state, mailMessage);
    }

    @Test(expected = IllegalCommandException.class)
	public void testNoToBody() throws InterruptedException {
		MailMessage mailMessage = (MailMessage) MessageFactory.produce(1, Command.EMAIL, "".replaceAll(" ", "\0"));

        user.profile = profile;
        when(profile.getDashByIdOrThrow(1)).thenReturn(dashBoard);
        Mail mail = new Mail();
        when(dashBoard.getWidgetByType(Mail.class)).thenReturn(mail);
        dashBoard.isActive = true;

        HardwareStateHolder state = new HardwareStateHolder(1, 0, user, "x");
        mailHandler.messageReceived(ctx, state, mailMessage);
    }

    @Test(expected = IllegalCommandException.class)
	public void testNoBody() throws InterruptedException {
		MailMessage mailMessage = (MailMessage) MessageFactory.produce(1, Command.EMAIL, "body".replaceAll(" ", "\0"));

        user.profile = profile;
        when(profile.getDashByIdOrThrow(1)).thenReturn(dashBoard);
        when(dashBoard.getWidgetByType(Mail.class)).thenReturn(new Mail());
        dashBoard.isActive = true;

        HardwareStateHolder state = new HardwareStateHolder(1, 0, user, "x");
        mailHandler.messageReceived(ctx, state, mailMessage);
    }

}
