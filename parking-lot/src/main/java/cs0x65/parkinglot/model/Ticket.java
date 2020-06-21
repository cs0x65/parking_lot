package cs0x65.parkinglot.model;

import cs0x65.parkinglot.impl.ParkingLot;

/**
 * The class represents a unique ticket or a parking session for a given car at an allocated parking slot.
 * Additionally, each ticket is identified by the start and end times.
 * <br/>
 * If a car leaves the parking lot (by paying the due charges) and comes back again then a new ticket is created.
 */
public class Ticket {
    private int slot;
    private Car car;
    private long parkedAt;
    private long leftAt;
    private long charges;

    /**
     *
     * @param slot the parking slot number allocated for the given car.
     * @param car the car being parked.
     * @param parkedAt the time at which the car was parked, specified as the timestamp/epoch in milliseconds.
     *                 The charges start accruing from this time onwards.
     */
    public Ticket(int slot, Car car, long parkedAt) {
        this.slot = slot;
        this.car = car;
        this.parkedAt = parkedAt;
    }

    public int getSlot() {
        return slot;
    }

    public Car getCar() {
        return car;
    }

    public long getParkedAt() {
        return parkedAt;
    }

    public long getLeftAt() {
        return leftAt;
    }

    /**
     * Setting this field marks the end of parking session.
     * @param leftAt the time at which the car vacates the parking slot, and leaves the lot specified as the
     *               timestamp/epoch in milliseconds.
     */
    public void setLeftAt(long leftAt) {
        this.leftAt = leftAt;
    }

    /**
     *
     * @return the total duration of the parking session measured in milliseconds.
     */
    public long getDuration(){
        return leftAt - parkedAt;
    }

    /**
     * @param parkedTimeUnit {@link ParkingLot.ParkedTimeUnit}
     * @return the total duration of the parking session measured in specified time unit and ceil'ed to nearest
     * time unit.
     * <br/>
     * E.g. if the {@code parkedTimeUnit} is {@link ParkingLot.ParkedTimeUnit#HOUR}, and the duration
     * is 9000 seconds i.e. 2.5 hours, then this method returns ceil'ed hours i.e. 3.
     */
    public long getDuration(ParkingLot.ParkedTimeUnit parkedTimeUnit){
        return (long) Math.ceil((leftAt - parkedAt) * 1000 / parkedTimeUnit.getTimeInSeconds());
    }

    public long getCharges() {
        return charges;
    }

    /**
     *
     * @param charges for using the parking slot. The duration is either calculated by
     * {@link Ticket#getDuration(ParkingLot.ParkedTimeUnit)} or directly supplied when the charges are computed.
     */
    public void setCharges(long charges) {
        this.charges = charges;
    }
}
