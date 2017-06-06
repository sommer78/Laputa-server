package com.laputa.server.core.model.widgets.others.eventor.model.condition;

/**
 * The Laputa Project.
 * Created by Sommer
 * Created on 01.08.16.
 */
public class NotEqual extends BaseCondition {

    public double value;

    public NotEqual() {
    }

    public NotEqual(double value) {
        this.value = value;
    }

    @Override
    public boolean isValid(double in) {
        return in != value;
    }

}
