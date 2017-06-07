package com.laputa.server.core.model.widgets.outputs;

import com.laputa.server.core.model.Pin;
import com.laputa.server.core.model.enums.PinType;
import com.laputa.server.core.model.widgets.OnePinWidget;
import com.laputa.utils.JsonParser;
import com.laputa.utils.ParseUtil;
import com.laputa.utils.structure.LimitedArrayDeque;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import static com.laputa.server.core.protocol.enums.Command.APP_SYNC;
import static com.laputa.utils.LaputaByteBufUtil.makeUTF8StringMessage;
import static com.laputa.utils.StringUtils.prependDashIdAndDeviceId;


/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 21.03.15.
 */
public class Map extends OnePinWidget {

    private static final int POOL_SIZE = ParseUtil.parseInt(System.getProperty("map.strings.pool.size", "25"));
    private transient final LimitedArrayDeque<String> lastCommands = new LimitedArrayDeque<>(POOL_SIZE);

    public boolean isPinToLatestPoint;

    public boolean isMyLocationSupported;

    public String labelFormat;

    public int radius; //zoom level / radius which user selected.

    public float lat; // last user position on map

    public float lon; // last user position on map

    @Override
    public boolean updateIfSame(int deviceId, byte pin, PinType type, String value) {
        if (isSame(deviceId, pin, type)) {
            switch (value) {
                case "clr" :
                    this.value = null;
                    this.lastCommands.clear();
                    break;
                default:
                    this.value = value;
                    this.lastCommands.add(value);
                    break;
            }
            return true;
        }
        return false;
    }

    @Override
    public void sendAppSync(Channel appChannel, int dashId, int targetId) {
        if (pin == Pin.NO_PIN || pinType == null || lastCommands.size() == 0) {
            return;
        }
        if (targetId == ANY_TARGET || this.deviceId == targetId) {
            for (String storedValue : lastCommands) {
                String body = prependDashIdAndDeviceId(dashId, deviceId, makeHardwareBody(pinType, pin, storedValue));
                appChannel.write(makeUTF8StringMessage(APP_SYNC, SYNC_DEFAULT_MESSAGE_ID, body));
            }
        }
    }

    @Override
    public void sendHardSync(ChannelHandlerContext ctx, int msgId, int deviceId) {
    }

    @Override
    public String getJsonValue() {
        return JsonParser.toJson(lastCommands);
    }

    @Override
    public String getModeType() {
        return "in";
    }

    @Override
    public int getPrice() {
        return 600;
    }

}
