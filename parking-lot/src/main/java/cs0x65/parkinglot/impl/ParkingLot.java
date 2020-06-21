package cs0x65.parkinglot.impl;

import cs0x65.parkinglot.model.Car;
import cs0x65.parkinglot.model.Ticket;

import java.util.*;

/**
 * The class represents the parking lot that provides the certain number of parking slots to park the cars.
 * The other characteristics, typical of the parking lot are also captured like - parking rate etc.
 *
 * {@link ParkedTimeUnit} specifies the time unit used for measuring the parked time duration.
 * For simplicity, the class keeps a single field - that let us specify the time unit for both initial and subsequent
 * durations.
 * But it's totally possible to use different time units for the initial and subsequent durations. This can be done by
 * introducing two distinct fields and corresponding setters like - {@code setParkedTimeUnitInitialDuration()} &
 * {@code setParkedTimeUnitSubsequentDuration()}.
 * For e.g. parked time unit for initial duration can be HOUR whereas that for the subsequent duration can be MINUTE.
 *
 */
public class ParkingLot implements Parkable<Car, Ticket>{
    /**
     * The builder class that helps build the instance of the {@link ParkingLot} class for the given
     * criteria/configuration.
     */
    public static final class Builder{
        private int size;
        private String name;
        private ParkedTimeUnit parkedTimeUnit = ParkedTimeUnit.HOUR;
        private int initialDuration = 2;
        private int subsequentDuration = 1;
        private int initialDurationRate = 10;
        private int subsequentDurationRate = 10;

        public Builder(int size){
            this.size = size;
        }

        public ParkingLot build(){
            if (name == null)
                name = "ParkingLot:"+ new Random().nextInt();
            return new ParkingLot(this);
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withParkedTimeUnit(ParkedTimeUnit parkedTimeUnit) {
            this.parkedTimeUnit = parkedTimeUnit;
            return this;
        }

        public Builder withInitialDuration(int initialDuration) {
            this.initialDuration = initialDuration;
            return this;
        }

        public Builder withSubsequentDuration(int subsequentDuration) {
            this.subsequentDuration = subsequentDuration;
            return this;
        }

        public Builder withInitialDurationRate(int initialDurationRate) {
            this.initialDurationRate = initialDurationRate;
            return this;
        }

        public Builder withSubsequentDurationRate(int subsequentDurationRate) {
            this.subsequentDurationRate = subsequentDurationRate;
            return this;
        }
    }

    /**
     * The enum captures the unit applied to while measuring the parked time of a car at a given parking slot.
     */
    public enum ParkedTimeUnit {
        HOUR(3600),
        MINUTE(60);

        private int timeInSeconds;

        ParkedTimeUnit(int timeInSeconds){
            this.timeInSeconds = timeInSeconds;
        }

        public int getTimeInSeconds() {
            return timeInSeconds;
        }
    }
    
    private int size;
    private String name;
    private ParkedTimeUnit parkedTimeUnit;
    private int initialDuration;
    private int subsequentDuration;
    private int initialDurationRate;
    private int subsequentDurationRate;

    /**
     * Represents the list of parking slots where each slot number is the list index.
     * If the slot is occupied, it has a non-null {@link Car} entry at the corresponding index in the slot.
     * <br/>
     * The parking slot with index i-1 is nearest to the parking lot entry than the slot with index i.
     * Each new car coming into the parking lot is always allocated to the nearest available slot from the entry.
     */
    private List<Car> slots;

    /**
     * A handy way to quickly:<br/>
     * - lookup for an existence of a car in the parking lot O(1) v/s O(n) required for lookup within slots list.
     * <br/>
     * - ability to directly identify the parking slot index maintained by {@link #slots} and reset it to null
     * whenever a car leaves the parking lot. Again this reduces the time required to identify the slot index occupied
     * by the car from O(n) to O(1).
     */
    private Map<Car, Ticket> carTicketMap;

    private int numOccupiedSlots;

    private ParkingLot(Builder builder) {
        this.size = builder.size;
        this.name = builder.name;
        this.parkedTimeUnit = builder.parkedTimeUnit;
        this.initialDuration = builder.initialDuration;
        this.subsequentDuration = builder.subsequentDuration;
        this.initialDurationRate = builder.initialDurationRate;
        this.subsequentDurationRate = builder.subsequentDurationRate;
        carTicketMap = new HashMap<>(size);
        initSlots();
    }

    private void initSlots(){
        slots = new ArrayList<>(size);
        for (int i = 0; i < size ; i++) {
            slots.add(null);
        }
    }

    /**
     *
     * @return the size of the parking lot.
     */
    public int getSize() {
        return size;
    }

    /**
     * Sets the size of the current parking lot.<br/>
     * This is the maximum number of parking slots available in the current parking lot (and so therefore the maximum
     * number of cars that can be parked).
     * @param size the size of the current parking lot.
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     *
     * @return the name of the parking lot.
     */
    public String getName() {
        return name;
    }

    /**
     * The name can be any descriptive text that can be associated with the parking lot, for e.g. address, a landmark
     * etc.
     * @param name name of the current parking lot.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the time unit applied for measuring the duration for which the car is parked at a particular parking
     * slot.
     */
    public ParkedTimeUnit getParkedTimeUnit() {
        return parkedTimeUnit;
    }

    /**
     * Sets the time unit that shall be applied while measuring the duration for which the car is parked at a
     * particular parking slot. <br/>
     *
     * For e.g. if the unit is set to {@link ParkedTimeUnit#HOUR}, then both - initial duration and subsequent duration
     * are measured in hours and the corresponding rates also need to specified per integral multiple of hours like,
     * 10$ per 1st 2 hours & 10$ for each subsequent hour. <br/>
     *
     * Use {@link ParkedTimeUnit#MINUTE} if the duration needs to be measured in sub-hours.
     * @param parkedTimeUnit {@link ParkedTimeUnit}
     */
    public void setParkedTimeUnit(ParkedTimeUnit parkedTimeUnit) {
        this.parkedTimeUnit = parkedTimeUnit;
    }

    /**
     *
     * @return the initial duration measured in the unit that's set by {@link ParkingLot#getParkedTimeUnit()}
     */
    public int getInitialDuration() {
        return initialDuration;
    }

    /**
     * Sets the initial duration. The total parked time is composed of the initial duration and subsequent duration,
     * and charges are calculated based on the corresponding rates.
     * @param initialDuration the initial duration measured in the unit that's set by {@code setParkedTimeUnit}
     */
    public void setInitialDuration(int initialDuration) {
        this.initialDuration = initialDuration;
    }

    /**
     * @return the subsequent duration measured in the unit that's set by {@link ParkingLot#getParkedTimeUnit()}
     */
    public int getSubsequentDuration() {
        return subsequentDuration;
    }

    /**
     * Sets the subsequent duration. See {@link ParkingLot#setInitialDuration(int)} for more details.
     * @param subsequentDuration the subsequent duration measured in the unit that's set by {@code setParkedTimeUnit}
     */
    public void setSubsequentDuration(int subsequentDuration) {
        this.subsequentDuration = subsequentDuration;
    }

    /**
     * @return the parking rate applicable for the initial duration.
     */
    public int getInitialDurationRate() {
        return initialDurationRate;
    }

    /**
     * @param initialDurationRate the parking rate applicable for the initial duration.
     */
    public void setInitialDurationRate(int initialDurationRate) {
        this.initialDurationRate = initialDurationRate;
    }

    /**
     * @return the parking rate applicable for the subsequent duration.
     */
    public int getSubsequentDurationRate() {
        return subsequentDurationRate;
    }

    /**
     * @param subsequentDurationRate the parking rate applicable for the subsequent duration.
     */
    public void setSubsequentDurationRate(int subsequentDurationRate) {
        this.subsequentDurationRate = subsequentDurationRate;
    }

    /**
     *
     * @return the number of currently occupied slots for the given parking lot.
     */
    public int getNumOccupiedSlots() {
        return numOccupiedSlots;
    }

    public Ticket park(Car car) {
        if (numOccupiedSlots == size)
            throw new IllegalStateException("Sorry, parking lot is full");

        Ticket ticket = carTicketMap.get(car);
        if (ticket != null)
            throw new IllegalArgumentException("The car: "+car.getRegNo()+" is already parked at slot: "+
                    ticket.getSlot());

        int index = getNearestAvailableSlotIndex();
        ticket = new Ticket(index, car, System.currentTimeMillis());
        slots.set(index-1, car);
        carTicketMap.put(car, ticket);
        numOccupiedSlots++;
        return ticket;
    }

    public Ticket leave(Car car, long duration) {
        Ticket ticket = carTicketMap.get(car);
        if (ticket == null)
            throw new IllegalArgumentException("Sorry, the car: "+car.getRegNo()+" is not found in the parking lot, " +
                    "please verify and provide the correct registration number for your car!");

        return removeCar(car, duration);
    }

    /**
     * A convenient method to un-park a car without requiring to specify the duration for which it was parked.
     * The method marks the exit time of the car by calling {@link Ticket#setLeftAt(long)} on the corresponding
     * {@link Ticket} instance.
     * @param car the car being un-parked from the lot.
     * @return the ticket corresponding to the parked car which is slated to leave.
     */
    public Ticket leave(Car car) {
        Ticket ticket = carTicketMap.get(car);
        if (ticket == null)
            throw new IllegalArgumentException("Sorry, the car: "+car.getRegNo()+" is not found in the parking lot, " +
                    "please verify and provide the correct registration number for your car!");

        return removeCar(car);
    }

    public String status() {
        StringBuilder stringBuilder = new StringBuilder("Slot No. Registration No.");
        for (int i = 0; i < slots.size(); i++) {
            Car car = slots.get(i);
            if (car != null){
                // TODO: format columns/add width formatter
                stringBuilder.append(i+" "+car.getRegNo());
            }
        }
        return stringBuilder.toString();
    }

    /**
     *
     * @return the index of the nearest slot from the entry that's available to park an incoming car.
     */
    public int getNearestAvailableSlotIndex(){
        for (int i = 0; i < slots.size() ; i++) {
            if (slots.get(i) == null){
                return i+1;
            }
        }
        return -1;
    }

    private Ticket removeCar(Car car, long duration) {
        Ticket ticket = carTicketMap.get(car);
        slots.set(ticket.getSlot() - 1, null);
        carTicketMap.remove(car);
        // When the duration is directly provided, need to set leftAt = parkedAt + duration.
        if (ticket.getLeftAt() == 0)
            ticket.setLeftAt(ticket.getParkedAt() + duration * parkedTimeUnit.getTimeInSeconds() * 1000);
        ticket.setCharges(calculateCharges(duration));
        numOccupiedSlots--;
        return ticket;
    }

    private Ticket removeCar(Car car){
        Ticket ticket = carTicketMap.get(car);
        // Duration was not provided, so leftAt needs to be set before computing the duration.
        ticket.setLeftAt(System.currentTimeMillis());
        return removeCar(car, ticket.getDuration(parkedTimeUnit));
    }

    private long calculateCharges(long duration){
        long charges = initialDurationRate;
        long additionalDuration = duration - initialDuration;
        if (additionalDuration > 0)
            charges+= (additionalDuration * subsequentDurationRate);
        return charges;
    }
}
