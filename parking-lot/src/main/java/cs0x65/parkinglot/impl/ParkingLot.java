package cs0x65.parkinglot.impl;

import cs0x65.parkinglot.model.Car;
import cs0x65.parkinglot.model.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
        private final int size;
        private String name;
        private ParkedTimeUnit parkedTimeUnit = ParkedTimeUnit.HOUR;
        private int initialDuration = 2;
        private int subsequentDuration = 1;
        private int initialDurationRate = 10;
        private int subsequentDurationRate = 10;

        /**
         * Creates a builder that will build the parking lot with the given size.<br/>
         * This is the maximum number of parking slots available in the generated parking lot (and therefore the maximum
         * number of cars that can be parked).
         *
         * @param size the size of the {@code ParkingLot} that will be built.
         */
        public Builder(int size){
            this.size = size;
        }

        /**
         * If none of the fields are configured, then the {@code ParkingLot} instance created by this builder defaults
         * to values below:<br/>
         * name: a random name <br/>
         * parked unit time: {@code ParkedTimeUnit.HOUR} <br/>
         * initial duration: 2 hours <br/>
         * subsequent duration: 1 hour <br/>
         * initial duration rate: 10 <br/>
         * subsequent duration rate: 10 <br/>
         * @return the {@link ParkingLot} object built with the given specification.
         */
        public ParkingLot build(){
            if (name == null)
                name = "ParkingLot:"+ new Random().nextInt();
            return new ParkingLot(this);
        }

        /**
         * The name can be any descriptive text that can be associated with the parking lot, for e.g. address,
         * a landmark etc.
         * @param name name of the current parking lot.
         */
        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        /**
         * Sets the time unit that shall be applied while measuring the duration for which the car is parked at a
         * particular parking slot. <br/><br/>
         *
         * For e.g. if the unit is set to {@link ParkedTimeUnit#HOUR}, then both - initial duration and subsequent
         * duration are measured in hours and the corresponding rates also need to specified per integral multiple of
         * hours like, 10$ per 1st 2 hours & 10$ for each subsequent hour. <br/><br/>
         *
         * Use {@link ParkedTimeUnit#MINUTE} if the duration needs to be measured in sub-hours.
         * @param parkedTimeUnit {@link ParkedTimeUnit}. Default value is HOUR
         */
        public Builder withParkedTimeUnit(ParkedTimeUnit parkedTimeUnit) {
            this.parkedTimeUnit = parkedTimeUnit;
            return this;
        }

        /**
         * Sets the initial duration. The total parked time is composed of the initial duration and subsequent duration,
         * and charges are calculated based on the corresponding rates.
         * @param initialDuration the initial duration measured in the unit that's set by {@code withParkedTimeUnit}
         *                        Default value is 2 hours.
         */
        public Builder withInitialDuration(int initialDuration) {
            this.initialDuration = initialDuration;
            return this;
        }

        /**
         * Sets the subsequent duration. See {@link #withInitialDuration(int)} for more details.
         * @param subsequentDuration the subsequent duration measured in the unit that's set by
         * {@code withParkedTimeUnit}. Default value is 1 hour.
         */
        public Builder withSubsequentDuration(int subsequentDuration) {
            this.subsequentDuration = subsequentDuration;
            return this;
        }

        /**
         * @param initialDurationRate the parking rate applicable for the initial duration.
         *                            Default value is 15, so rate is 10 for 1st 2 hours.
         */
        public Builder withInitialDurationRate(int initialDurationRate) {
            this.initialDurationRate = initialDurationRate;
            return this;
        }

        /**
         * @param subsequentDurationRate the parking rate applicable for the subsequent duration.
         *                               Default value is 10, so rate is 10 for each subsequent hour.
         */
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

        private final int timeInSeconds;

        ParkedTimeUnit(int timeInSeconds){
            this.timeInSeconds = timeInSeconds;
        }

        public int getTimeInSeconds() {
            return timeInSeconds;
        }
    }
    
    private final int size;
    private final String name;
    private final ParkedTimeUnit parkedTimeUnit;
    private final int initialDuration;
    private final int subsequentDuration;
    private final int initialDurationRate;
    private final int subsequentDurationRate;

    /**
     * Represents the list of parking slots where each slot number is the list index.
     * If the slot is occupied, it has a non-null {@link Car} entry at the corresponding index in the slot.
     * <br/>
     * The parking slot with index i-1 is nearest to the parking lot entry than the slot with index i.
     * Each new car coming into the parking lot is always allocated to the nearest available slot from the entry.
     */
    private final List<Car> slots;

    /**
     * A handy way to quickly:<br/>
     * - lookup for an existence of a car in the parking lot O(1) v/s O(n) required for lookup within slots list.
     * <br/>
     * - ability to directly identify the parking slot index maintained by {@link #slots} and reset it to null
     * whenever a car leaves the parking lot. Again this reduces the time required to identify the slot index occupied
     * by the car from O(n) to O(1).
     */
    private final Map<Car, Ticket> carTicketMap;

    private int numOccupiedSlots;

    private static final Logger LOGGER = LogManager.getLogger(ParkingLot.class.getName());

    private ParkingLot(Builder builder) {
        this.size = builder.size;
        this.name = builder.name;
        this.parkedTimeUnit = builder.parkedTimeUnit;
        this.initialDuration = builder.initialDuration;
        this.subsequentDuration = builder.subsequentDuration;
        this.initialDurationRate = builder.initialDurationRate;
        this.subsequentDurationRate = builder.subsequentDurationRate;
        carTicketMap = new HashMap<>(size);
        slots = new ArrayList<>(size);
        initSlots();
    }

    private void initSlots(){
        for (int i = 0; i < size ; i++) {
            slots.add(null);
        }
    }

    /**
     * A convenience method to create a ParkingLot object with default configuration.
     * This method is effectively same as: {@code new Builder().build(size)} with none of the other fields configured.
     * @param size the size of the parking lot
     * @return {@code ParkingLot} with the specified size while rest of the fields configured to default values.
     */
    public static ParkingLot create(int size){
        return new Builder(size).build();
    }

    /**
     *
     * @return the size of the parking lot.
     */
    public int getSize() {
        return size;
    }

    /**
     *
     * @return the name of the parking lot.
     */
    public String getName() {
        return name;
    }

    /**
     * @return the time unit applied for measuring the duration for which the car is parked at a particular parking
     * slot.
     */
    public ParkedTimeUnit getParkedTimeUnit() {
        return parkedTimeUnit;
    }

    /**
     *
     * @return the initial duration measured in the unit that's specified by {@link ParkingLot#getParkedTimeUnit()}
     */
    public int getInitialDuration() {
        return initialDuration;
    }

    /**
     * @return the subsequent duration measured in the unit that's specified by {@link ParkingLot#getParkedTimeUnit()}
     */
    public int getSubsequentDuration() {
        return subsequentDuration;
    }

    /**
     * @return the parking rate applicable for the initial duration.
     */
    public int getInitialDurationRate() {
        return initialDurationRate;
    }

    /**
     * @return the parking rate applicable for the subsequent duration.
     */
    public int getSubsequentDurationRate() {
        return subsequentDurationRate;
    }

    /**
     *
     * @return the number of currently occupied slots for the given parking lot.
     */
    public int getNumOccupiedSlots() {
        return numOccupiedSlots;
    }

    public Ticket park(Car car) {
        LOGGER.info("Request to park car: {} ", car.getRegNo());

        if (numOccupiedSlots == size){
            LOGGER.error("Parking lot is full! Can't park car: {}", car.getRegNo());
            throw new IllegalStateException("Sorry, parking lot is full");
        }

        Ticket ticket = carTicketMap.get(car);
        if (ticket != null)
            throw new IllegalArgumentException("The car: " + car.getRegNo() + " is already parked at slot: " +
                    ticket.getSlot());

        int index = getNearestAvailableSlotIndex();
        ticket = new Ticket(index, car, System.currentTimeMillis());
        slots.set(index-1, car);
        carTicketMap.put(car, ticket);
        numOccupiedSlots++;
        LOGGER.info("Car: {} parked at slot: {}", car.getRegNo(), index);
        LOGGER.info("Current num occupied slots: {} out of Total slots: {}", numOccupiedSlots, size);
        return ticket;
    }

    public Ticket leave(Car car, long duration) {
        LOGGER.info("Request to un-park car: {} ", car.getRegNo());
        Ticket ticket = carTicketMap.get(car);
        if (ticket == null)
            throw new IllegalArgumentException("Registration number " + car.getRegNo() + " not found");

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
        LOGGER.info("Request to un-park car: {} ", car.getRegNo());
        Ticket ticket = carTicketMap.get(car);
        if (ticket == null)
            throw new IllegalArgumentException("Registration number " + car.getRegNo() + " not found");

        return removeCar(car);
    }

    /**
     * A utility method to get the formatted text representation of the current status of the parking lot.<br/>
     *
     * The formatted text contains two columns/headers: the 1st column is Slot No. & 2nd Registration No.
     * with each entry on a separate row.
     * <br/><br/>
     * Note: If the parking slot is empty & {@code includeEmptySlots=true}, it's represented as "--" under the 2nd
     * column, whereas if {@code includeEmptySlots=false} then the slot entry is all together dropped from the status.
     * <br/><br/>
     * E.g. if the parking lot has 3 slots and if 2nd slot is empty, then the status looks like:<br/><br/>
     *
     * with {@code includeEmptySlots=true}
     *
     * <table>
     *     <th>Slot No.</th>
     *     <th>Registration No.</th>
     *     <tr>
     *         <td>1</td>
     *         <td>MH-12-AB-1234</td>
     *     </tr>
     *     <tr>
     *         <td>2</td>
     *         <td>--</td>
     *     </tr>
     *     <tr>
     *         <td>3</td>
     *         <td>MH-13-AC-9999</td>
     *     </tr>
     * </table>
     *
     * <br/><br/>
     *
     * with {@code includeEmptySlots=false}, 2nd slot is dropped from the status info.
     * <table>
     *     <th>Slot No.</th>
     *     <th>Registration No.</th>
     *     <tr>
     *         <td>1</td>
     *         <td>MH-12-AB-1234</td>
     *     </tr>
     *     <tr>
     *         <td>3</td>
     *         <td>MH-13-AC-9999</td>
     *     </tr>
     * </table>
     *
     * @param includeEmptySlots specifies whether the status information shall include empty slots.
     * @return the formatted text that captures the current status of the parking lot.
     */
    public String status(boolean includeEmptySlots) {
        LOGGER.info("Gathering current status of the parking lot...");
        Formatter formatter = new Formatter(new StringBuilder());
        formatter.format("%-8s %s\n", "Slot No.", "Registration No.");
        for (int i = 0; i < slots.size(); i++) {
            Car car = slots.get(i);
            if (car != null)
                formatter.format("%-8d %s\n", i+1, car.getRegNo());
            else if (includeEmptySlots)
                formatter.format("%-8d %s\n", i+1, "--");
        }
        String status = formatter.toString();
        // Remove last \n
        status = status.substring(0, status.length()-1);
        LOGGER.info("\n"+status);
        return status;
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
        LOGGER.info("Car: {} left from slot: {}", car.getRegNo(), ticket.getSlot());
        LOGGER.info("Charges accrued: {}", ticket.getCharges());
        LOGGER.info("Current num occupied slots: {} out of Total slots: {}", numOccupiedSlots, size);
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
