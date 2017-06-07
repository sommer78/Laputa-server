package com.laputa.server.core.model.widgets.outputs;

import com.laputa.server.core.model.Pin;
import com.laputa.server.core.model.enums.PinType;
import com.laputa.server.core.model.widgets.FrequencyWidget;
import com.laputa.server.core.model.widgets.MultiPinWidget;
import com.laputa.server.core.model.widgets.ui.DeviceSelector;
import com.laputa.utils.ParseUtil;
import com.laputa.utils.structure.LimitedArrayDeque;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;

import static com.laputa.server.core.protocol.enums.Command.APP_SYNC;
import static com.laputa.server.core.protocol.enums.Command.HARDWARE;
import static com.laputa.utils.LaputaByteBufUtil.makeUTF8StringMessage;
import static com.laputa.utils.StringUtils.prependDashIdAndDeviceId;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 21.03.15.
 */
public class LCD extends MultiPinWidget implements FrequencyWidget {

    public boolean advancedMode;

    public String textFormatLine1;
    public String textFormatLine2;

    public boolean textLight;

    private int frequency;

    private transient long lastRequestTS;

    private static final int POOL_SIZE = ParseUtil.parseInt(System.getProperty("lcd.strings.pool.size", "6"));
    private transient final LimitedArrayDeque<String> lastCommands = new LimitedArrayDeque<>(POOL_SIZE);

    private static void sendSyncOnActivate(Pin pin, int dashId, int deviceId, Channel appChannel) {
        if (pin.notEmpty()) {
            String body = prependDashIdAndDeviceId(dashId, deviceId, pin.makeHardwareBody());
            appChannel.write(makeUTF8StringMessage(APP_SYNC, SYNC_DEFAULT_MESSAGE_ID, body), appChannel.voidPromise());
        }
    }

    @Override
    public boolean updateIfSame(int deviceId, byte pinIn, PinType type, String value) {
        boolean isSame = false;
        if (pins != null && this.deviceId == deviceId) {
            for (Pin pin : pins) {
                if (pin.isSame(pinIn, type)) {
                    pin.value = value;
                    isSame = true;
                }
            }
            if (advancedMode && isSame && value != null) {
                lastCommands.add(value);
            }
        }
        return isSame;
    }

    @Override
    public void sendAppSync(Channel appChannel, int dashId, int targetId) {
        if (pins == null) {
            return;
        }

        //do not send SYNC message for widgets assigned to device selector
        //as it will be duplicated later.
        if (this.deviceId >= DeviceSelector.DEVICE_SELECTOR_STARTING_ID) {
            return;
        }

        if (targetId == ANY_TARGET || this.deviceId == targetId) {
            if (advancedMode) {
                for (String command : lastCommands) {
                    pins[0].value = command;
                    sendSyncOnActivate(pins[0], dashId, deviceId, appChannel);
                }
            } else {
                for (Pin pin : pins) {
                    sendSyncOnActivate(pin, dashId, deviceId, appChannel);
                }
            }
        }
    }

    @Override
    public boolean isSplitMode() {
        return !advancedMode;
    }

    @Override
    public boolean isTicked(long now) {
        if (frequency > 0 && now > lastRequestTS + frequency) {
            this.lastRequestTS = now;
            return true;
        }
        return false;
    }

    @Override
    public void writeReadingCommand(Channel channel) {
        if (pins == null) {
            return;
        }
        for (Pin pin : pins) {
            if (pin.isNotValid()) {
                continue;
            }
            ByteBuf msg = makeUTF8StringMessage(HARDWARE, READING_MSG_ID, Pin.makeReadingHardwareBody(pin.pinType.pintTypeChar, pin.pin));
            channel.write(msg, channel.voidPromise());
        }
    }

    @Override
    public int getDeviceId() {
        return deviceId;
    }

    @Override
    public String getModeType() {
        return "in";
    }

    @Override
    public int getPrice() {
        return 400;
    }
}
