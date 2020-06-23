package cs0x65.parkinglot.model;

public class Car {
    private String regNo;
    private String color;

    /**
     *
     * @param regNo the registration/license plate number of the car being parked. For e.g MH-12-AB-1234.
     * @param color the color of the car.
     */
    public Car(String regNo, String color) {
        this.regNo = regNo;
        this.color = color;
    }

    public Car(String regNo){
        this(regNo, "White");
    }

    public String getRegNo() {
        return regNo;
    }

    public String getColor() {
        return color;
    }

    @Override
    public int hashCode() {
        return (regNo+color).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Car))
            return false;
        Car car = (Car)o;
        return car.regNo.equals(regNo) && car.color.equals(color);
    }
}
