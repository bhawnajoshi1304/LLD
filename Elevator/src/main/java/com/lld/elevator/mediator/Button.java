package com.lld.elevator.mediator;

public class Button {
    private Runnable action;
    public void setOnPressListener(Runnable action) {
        this.action = action;
    }
    public void press() {
        if (action != null) {
            action.run();
        }
    }
}