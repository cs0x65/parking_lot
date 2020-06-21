package cs0x65.parkinglot.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ParkingLotBuilderTest {

    @Test
    public void parkingLotBuiltWithDefaultValuesWhenNoneSupplied(){
        ParkingLot parkingLot = new ParkingLot.Builder(10).build();
        assertNotNull(parkingLot.getName());
        assertEquals(10, parkingLot.getSize());
        assertEquals(ParkingLot.ParkedTimeUnit.HOUR, parkingLot.getParkedTimeUnit());
        assertEquals(2, parkingLot.getInitialDuration());
        assertEquals(10, parkingLot.getInitialDurationRate());
        assertEquals(1, parkingLot.getSubsequentDuration());
        assertEquals(10, parkingLot.getSubsequentDurationRate());
    }

    @Test
    public void parkingLotBuiltWithCorrectValuesWhenSupplied(){
        ParkingLot parkingLot = new ParkingLot.Builder(6)
                .withName("PL1")
                .withParkedTimeUnit(ParkingLot.ParkedTimeUnit.MINUTE)
                .withInitialDuration(120)
                .withInitialDurationRate(15)
                .withSubsequentDuration(30)
                .withSubsequentDurationRate(5)
                .build();
        assertEquals("PL1", parkingLot.getName());
        assertEquals(6, parkingLot.getSize());
        assertEquals(ParkingLot.ParkedTimeUnit.MINUTE, parkingLot.getParkedTimeUnit());
        assertEquals(120, parkingLot.getInitialDuration());
        assertEquals(15, parkingLot.getInitialDurationRate());
        assertEquals(30, parkingLot.getSubsequentDuration());
        assertEquals(5, parkingLot.getSubsequentDurationRate());
    }
}