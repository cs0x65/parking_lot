package cs0x65.parkinglot.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CarTest {
    @Test
    public void defaultColorIsWhiteWhenNoneSet(){
        Car car = new Car("MH-12-TC-8900");
        assertEquals("White", car.getColor());
    }

    @Test
    public void carsHaveSameHashCodeWhenIdenticalAttributes(){
        Car car1 = new Car("TS-01-AB-1234", "Black");
        Car car2 = new Car("TS-01-AB-1234", "Black");
        assertEquals(car1.hashCode(), car2.hashCode());
    }

    @Test
    public void carsAreEqualWhenHaveIdenticalAttributes(){
        Car car1 = new Car("KA-09-A-1234", "Black");
        Car car2 = new Car("KA-09-A-1234", "Black");
        assertTrue(car1.equals(car2));
    }

    @Test
    public void carsHaveDifferentHashCodeWhenDifferentAttributes(){
        Car car1 = new Car("TS-01-AB-1234", "Black");
        Car car2 = new Car("TS-11-A-1234", "Black");
        assertNotEquals(car1.hashCode(), car2.hashCode());
    }

    @Test
    public void carsAreUnequalWhenHaveDifferentAttributes(){
        Car car1 = new Car("KA-09-A-1234", "Black");
        Car car2 = new Car("KA-09-B-1234", "Red");
        assertFalse(car1.equals(car2));
    }

}