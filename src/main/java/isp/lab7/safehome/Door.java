package isp.lab7.safehome;

import static isp.lab7.safehome.DoorStatus.*;

public class Door {

    DoorStatus status;

    public void lockDoor() {
        this.status = CLOSE;
    }

    public void unlockDoor() {
        this.status = OPEN;
    }

    public DoorStatus getStatus() {
        return status;
    }

    public void setStatus(DoorStatus status) {
        this.status = status;
    }
}
