package com.laputa.server.core.model.widgets.outputs;

import com.laputa.server.core.model.widgets.OnePinReadingWidget;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 21.03.15.
 */
public class LabeledValueDisplay extends OnePinReadingWidget {

    private TextAlignment textAlignment;

    private String valueFormatting;

    @Override
    public String getModeType() {
        return "in";
    }

    @Override
    public int getPrice() {
        return 400;
    }

}
