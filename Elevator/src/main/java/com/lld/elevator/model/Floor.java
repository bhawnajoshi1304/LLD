package com.lld.elevator.model;

import com.lld.elevator.mediator.HallPanel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Floor {
    private final int id;
    private final List<HallPanel> hallPanels;
    Floor(int id,int noOfElevators,List<Elevator> elevator){
        this.id = id;
        this.hallPanels = new ArrayList<>();
        for(int i=0;i<noOfElevators;i+=1){
            this.hallPanels.add(new HallPanel(elevator.get(i),id));
        }
    }
}
