package cs0x65.parkinglot.impl;

import cs0x65.parkinglot.model.Car;
import cs0x65.parkinglot.model.Ticket;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class ParkingLotTest {
    ParkingLot parkingLot;

    @Test
    void park() {
        parkingLot = new ParkingLot.Builder(6).build();

        assertEquals(0, parkingLot.getNumOccupiedSlots());

        assertEquals(1, parkingLot.getNearestAvailableSlotIndex());
        Car car = new Car("MH-12-AB-1234");
        Ticket ticket = parkingLot.park(car);
        assertEquals(1,  ticket.getSlot());
        assertEquals(car, ticket.getCar());
        assertEquals(1, parkingLot.getNumOccupiedSlots());

        assertEquals(2, parkingLot.getNearestAvailableSlotIndex());
        Car car2 = new Car("MH-13-CD-1234");
        ticket = parkingLot.park(car2);
        assertEquals(2,  ticket.getSlot());
        assertEquals(car2, ticket.getCar());
        assertEquals(2, parkingLot.getNumOccupiedSlots());

        assertEquals(3, parkingLot.getNearestAvailableSlotIndex());
        car = new Car("MH-14-E-1234");
        ticket = parkingLot.park(car);
        assertEquals(3,  ticket.getSlot());
        assertEquals(car, ticket.getCar());
        assertEquals(3, parkingLot.getNumOccupiedSlots());

        // verify if the car at 2nd slot leaves and so becomes free for use, the next car to come is allocated slot 2.
        parkingLot.leave(car2);
        assertEquals(2, parkingLot.getNearestAvailableSlotIndex());
        car = new Car("MH-15-FG-1234");
        ticket = parkingLot.park(car);
        assertEquals(2,  ticket.getSlot());
        assertEquals(car, ticket.getCar());
        assertEquals(3, parkingLot.getNumOccupiedSlots());

        assertEquals(4, parkingLot.getNearestAvailableSlotIndex());
        car = new Car("MH-15-H-1234");
        ticket = parkingLot.park(car);
        assertEquals(4,  ticket.getSlot());
        assertEquals(car, ticket.getCar());
        assertEquals(4, parkingLot.getNumOccupiedSlots());
    }

    @Test
    public void parkThrowsExceptionWhenReattemptedWithAlreadyParkedCar(){
        parkingLot = new ParkingLot.Builder(2).build();

        assertEquals(0, parkingLot.getNumOccupiedSlots());

        assertEquals(1, parkingLot.getNearestAvailableSlotIndex());
        Car car = new Car("MH-12-AB-1234");
        Ticket ticket = parkingLot.park(car);
        assertEquals(1,  ticket.getSlot());
        assertEquals(car, ticket.getCar());
        assertEquals(1, parkingLot.getNumOccupiedSlots());

        assertEquals(2, parkingLot.getNearestAvailableSlotIndex());
        final Car car2 = new Car("MH-12-AB-1234");
        IllegalArgumentException exception =  assertThrows(IllegalArgumentException.class, () -> parkingLot.park(car2));
        String expectedMessage = "The car: "+car.getRegNo()+" is already parked at slot: "+ ticket.getSlot();
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    public void parkThrowsExceptionWhenLotIsFull(){
        parkingLot = new ParkingLot.Builder(1).build();
        assertEquals(0, parkingLot.getNumOccupiedSlots());

        assertEquals(1, parkingLot.getNearestAvailableSlotIndex());
        Car car = new Car("MH-12-AB-1234");
        Ticket ticket = parkingLot.park(car);
        assertEquals(1,  ticket.getSlot());
        assertEquals(car, ticket.getCar());
        assertEquals(1, parkingLot.getNumOccupiedSlots());

        assertEquals(-1, parkingLot.getNearestAvailableSlotIndex());
        final Car car2 = new Car("MH-12-AB-1234");
        IllegalStateException exception =  assertThrows(IllegalStateException.class, () -> parkingLot.park(car2));
        assertEquals("Sorry, parking lot is full", exception.getMessage());
    }

    @Test
    void leave() {
        parkingLot = new ParkingLot.Builder(4).build();

        assertEquals(0, parkingLot.getNumOccupiedSlots());

        assertEquals(1, parkingLot.getNearestAvailableSlotIndex());
        Car car = new Car("MH-12-AB-1234");
        Ticket ticket = parkingLot.park(car);
        assertEquals(1,  ticket.getSlot());
        assertEquals(car, ticket.getCar());
        assertEquals(0, ticket.getLeftAt());
        assertEquals(1, parkingLot.getNumOccupiedSlots());

        ticket = parkingLot.leave(car, 3);
        assertEquals(ticket.getParkedAt()+10800000, ticket.getLeftAt());
        assertEquals(3, ticket.getDuration(parkingLot.getParkedTimeUnit()));
        assertEquals(20, ticket.getCharges());
        assertEquals(0, parkingLot.getNumOccupiedSlots());
        assertEquals(1, parkingLot.getNearestAvailableSlotIndex());

        car = new Car("MH-12-AB-1234");
        ticket = parkingLot.park(car);
        Car car2 = new Car("MH-13-CD-1234");
        ticket = parkingLot.park(car2);
        car = new Car("MH-14-E-1234");
        ticket = parkingLot.park(car);

        // verify if the car at 2nd slot leaves and so becomes free for use, the next car to come is allocated slot 2.
        assertEquals(3, parkingLot.getNumOccupiedSlots());
        ticket = parkingLot.leave(car2, 4);
        assertEquals(2, parkingLot.getNumOccupiedSlots());
        assertEquals(2, parkingLot.getNearestAvailableSlotIndex());
        assertEquals(4, ticket.getDuration(parkingLot.getParkedTimeUnit()));
        assertEquals(30, ticket.getCharges());
    }

    @Test
    void leaveWhenDurationIsNotSupplied() {
        parkingLot = new ParkingLot.Builder(6)
                .withName("PL1")
                .withParkedTimeUnit(ParkingLot.ParkedTimeUnit.MINUTE)
                .withInitialDuration(120)
                .withInitialDurationRate(15)
                .withSubsequentDuration(30)
                .withSubsequentDurationRate(5)
                .build();

        //TODO: try to do it with Mockito or some kinda time freeze lib
        assertEquals(0, parkingLot.getNumOccupiedSlots());

        assertEquals(1, parkingLot.getNearestAvailableSlotIndex());
        Car car = new Car("MH-12-AB-1234");
        Ticket ticket = parkingLot.park(car);
        assertEquals(1,  ticket.getSlot());
        assertEquals(car, ticket.getCar());
        assertEquals(0, ticket.getLeftAt());
        assertEquals(1, parkingLot.getNumOccupiedSlots());

        ticket = parkingLot.leave(car);
        assertEquals(15, ticket.getCharges());
        assertEquals(0, parkingLot.getNumOccupiedSlots());
        assertEquals(1, parkingLot.getNearestAvailableSlotIndex());
    }

    @Test
    public void leaveUnparkedCarThrowsException(){
        parkingLot = new ParkingLot.Builder(1).build();
        assertEquals(0, parkingLot.getNumOccupiedSlots());

        assertEquals(1, parkingLot.getNearestAvailableSlotIndex());
        Car car = new Car("MH-12-AB-1234");
        String expectedMessage = "Registration number " + car.getRegNo() + " not found";
        IllegalArgumentException exception =  assertThrows(IllegalArgumentException.class, () -> parkingLot.leave(car));
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void status() {
        parkingLot = new ParkingLot.Builder(1).build();

        String status = parkingLot.status();
        String[] rows = status.split("\n");
        assertEquals("Slot No. Registration No.", rows[0]);
        assertEquals("1        --", rows[1]);

        Car car = new Car("MH-12-AB-1234");
        parkingLot.park(car);
        status = parkingLot.status();
        assertEquals("1        MH-12-AB-1234", status.split("\n")[1]);
    }
}