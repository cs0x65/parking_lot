package cs0x65.parkinglot.impl;

/**
 * Represents an entity or resource that is able provide the <b>parkable</b> behaviour.
 * <br/>
 * A Parkable resource is the one which lets user park, un-park the objects - like cars, or for that matter any goods
 * and query the current status of the resource.
 * <br/>
 * E.g. Parking lot, something like Warehouse can also be viewed a parkable resource.
 * How about bank account? (park, un-park funds :) )
 * <br/>
 * Implemented by {@link ParkingLot}
 */
public interface Parkable<O, T> {
    /**
     *
     * @param object the object to be parked.
     * @return the corresponding entity/record that captures the details of parking transaction.
     * Implementing classes are free to define their semantics based on the parameterized types.
     * For e.g. in case of ParkingLot, the object being parked is Car whereas the returned object is a Ticket instance.
     */
    T park(O object);

    /**
     *
     * @param object the object to be un-parked/withdrawn from the parkable resource.
     * @param duration the duration for which the object was parked
     * @return the corresponding entity/record that captures the details of un-parking transaction.
     */
    T leave(O object, long duration);

    /**
     * @param includeEmptySlots specifies whether the status information shall include empty slots.
     * @return the status of the parkable resource. The implementing classes are free to return whatever is relevant
     * to their domain and satisfy their semantics.
     */
    String status(boolean includeEmptySlots);
}
