package com.laputa.server.core.model.widgets;

import com.laputa.server.core.model.Pin;
import com.laputa.server.core.model.enums.PinType;
import com.laputa.server.core.model.widgets.controls.RGB;
import com.laputa.server.core.model.widgets.outputs.ValueDisplay;
import com.laputa.utils.StringUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 25.12.15.
 */
public class PinGetJsonValueTest {

    private static Pin createPinWithValue(String val) {
        Pin pin = new Pin();
        pin.value = val;
        pin.pinType = PinType.VIRTUAL;
        pin.pin = 1;
        return pin;
    }

    @Test
    public void testSinglePin() {
        OnePinWidget onePinWidget = new ValueDisplay();
        onePinWidget.value = null;

        assertEquals("[]", onePinWidget.getJsonValue());

        onePinWidget.value = "1.0";
        assertEquals("[\"1.0\"]", onePinWidget.getJsonValue());
    }

    @Test
    public void testMultiPinSplit() {
        RGB multiPinWidget = new RGB();
        multiPinWidget.pins = null;
        multiPinWidget.splitMode = true;

        assertEquals("[]", multiPinWidget.getJsonValue());

        multiPinWidget.pins = new Pin[3];
        multiPinWidget.pins[0] = createPinWithValue("1");
        multiPinWidget.pins[1] = createPinWithValue("2");
        multiPinWidget.pins[2] = createPinWithValue("3");

        assertEquals("[\"1\",\"2\",\"3\"]", multiPinWidget.getJsonValue());
    }

    @Test
    public void testMultiPinMerge() {
        RGB multiPinWidget = new RGB();
        multiPinWidget.pins = null;
        multiPinWidget.splitMode = false;

        assertEquals("[]", multiPinWidget.getJsonValue());

        multiPinWidget.pins = new Pin[3];
        multiPinWidget.pins[0] = createPinWithValue("1 2 3".replaceAll(" ", StringUtils.BODY_SEPARATOR_STRING));

        assertEquals("[\"1\",\"2\",\"3\"]", multiPinWidget.getJsonValue());
    }

}
