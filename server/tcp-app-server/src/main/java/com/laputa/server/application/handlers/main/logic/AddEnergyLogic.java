package com.laputa.server.application.handlers.main.logic;

import com.laputa.server.core.BlockingIOProcessor;
import com.laputa.server.core.model.auth.User;
import com.laputa.server.core.protocol.exceptions.NotAllowedException;
import com.laputa.server.core.protocol.model.messages.StringMessage;
import com.laputa.server.db.DBManager;
import com.laputa.server.db.model.Purchase;
import com.laputa.utils.ParseUtil;
import io.netty.channel.ChannelHandlerContext;

import static com.laputa.utils.BlynkByteBufUtil.ok;
import static com.laputa.utils.StringUtils.split2;


/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 14.03.16.
 */
public class AddEnergyLogic {

    private final BlockingIOProcessor blockingIOProcessor;
    private final DBManager dbManager;

    public AddEnergyLogic(DBManager dbManager, BlockingIOProcessor blockingIOProcessor) {
        this.blockingIOProcessor = blockingIOProcessor;
        this.dbManager = dbManager;
    }

    private static boolean isValidTransactionId(String id) {
        if (id == null || id.isEmpty() || id.startsWith("com.laputa.energy")) {
            return false;
        }

        if (id.length() == 36) {
            // fake example - "8077004819764738793.5939465896020147"
            String[] transactionParts = id.split("\\.");
            if (transactionParts.length == 2 && transactionParts[0].length() == 19 && transactionParts[1].length() == 16) {
                return false;
            }

            // fake example "51944AFD-1D24-4A22-A51F-93513A76CD28"
            transactionParts = id.split("-");
            if (transactionParts.length == 5 && transactionParts[0].length() == 8 && transactionParts[1].length() == 4 &&
                    transactionParts[2].length() == 4 && transactionParts[3].length() == 4 && transactionParts[4].length() == 12) {
                return false;
            }
        }

        return true;
    }

    public void messageReceived(ChannelHandlerContext ctx, User user, StringMessage message) {
        String[] bodyParts = split2(message.body);

        int energyAmountToAdd = ParseUtil.parseInt(bodyParts[0]);
        if (bodyParts.length == 2 && isValidTransactionId(bodyParts[1])) {
            insertPurchase(user.email, energyAmountToAdd, bodyParts[1]);
            user.purchaseEnergy(energyAmountToAdd);
            ctx.writeAndFlush(ok(message.id), ctx.voidPromise());
        } else {
            throw new NotAllowedException("Purchase with invalid transaction id. User " + user.email);
        }
    }

    private void insertPurchase(String email, int reward, String transactionId) {
        if (transactionId.equals("AdColonyAward") || transactionId.equals("homeScreen")) {
            return;
        }
        blockingIOProcessor.executeDB(
            () -> dbManager.insertPurchase(new Purchase(email, reward, transactionId))
        );
    }

}