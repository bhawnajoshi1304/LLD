package com.lld.elevator.mediator;

import com.lld.elevator.model.Direction;
import com.lld.elevator.model.Elevator;
import com.lld.elevator.model.ExternalRequest;
import com.lld.elevator.mediator.Button;
import lombok.Getter;

@Getter
public class HallPanel {
    private final int floor;
    private final Elevator elevator;
    private final Button upButton;
    private final Button downButton;

    public HallPanel(Elevator elevator, int floor) {
        this.floor = floor;
        this.elevator = elevator;
        upButton = new Button();
        upButton.setOnPressListener(() -> requestDirection(Direction.UP));
        downButton = new Button();
        downButton.setOnPressListener(() -> requestDirection(Direction.DOWN));
    }
    private void requestDirection(Direction direction) {
        System.out.println("Internal request: Elevator " + elevator.getId() + " needs to go " + ((direction == Direction.UP) ? "up.": "down."));
        elevator.addRequest(new ExternalRequest(floor,direction));
    }
}