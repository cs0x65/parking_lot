package cs0x65.parkinglot.model;

import cs0x65.parkinglot.impl.ParkingLot;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TicketTest {
    Ticket ticket = new Ticket(1, new Car(""), System.currentTimeMillis());

    @Test
    void getDuration() {
        long leftAt = ticket.getParkedAt() + 3600000;
        ticket.setLeftAt(leftAt);
        assertEquals(3600000, ticket.getDuration());
    }

    @Test
    void getDurationInSuppliedTimeUnit() {
        long leftAt = ticket.getParkedAt() + 3600000;
        ticket.setLeftAt(leftAt);

        assertEquals(1, ticket.getDuration(ParkingLot.ParkedTimeUnit.HOUR));
        assertEquals(60, ticket.getDuration(ParkingLot.ParkedTimeUnit.MINUTE));

        // verify that duration is ceiled to nearest value in given time unit.
        leftAt = ticket.getParkedAt() + 5400000;
        ticket.setLeftAt(leftAt);
        assertEquals(2, ticket.getDuration(ParkingLot.ParkedTimeUnit.HOUR));

        leftAt = ticket.getParkedAt() + 5400500;
        ticket.setLeftAt(leftAt);
        assertEquals(91, ticket.getDuration(ParkingLot.ParkedTimeUnit.MINUTE));
    }
}