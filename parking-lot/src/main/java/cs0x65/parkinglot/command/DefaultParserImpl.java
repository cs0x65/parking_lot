package cs0x65.parkinglot.command;

import cs0x65.parkinglot.impl.ParkingLot;
import cs0x65.parkinglot.model.Car;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Formatter;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultParserImpl implements Parser<ParkingLot> {
    private static final Logger LOGGER = LogManager.getLogger(DefaultParserImpl.class.getName());

    @Override
    public Command<ParkingLot> parse(String commandString) throws BadCommandException{
        // Makes sure that redundant white spaces are ignored.
        List<String> components = Arrays.stream(commandString.trim().split(" ")).
                filter(s -> !s.trim().equals("")).
                collect(Collectors.toList());

        try {
            Command.Verb verb = Command.Verb.valueOf(components.get(0).toUpperCase());
            List<String> args = components.subList(1, components.size());
            switch (verb){
                case CREATE_PARKING_LOT:
                    return parseCreateCommand(args);
                case PARK:
                    return  parseParkCommand(args);
                case LEAVE:
                    return parseLeaveCommand(args);
                case STATUS:
                    return parseStatusCommand(args);
            }
        }catch (IllegalArgumentException e){
            throw new BadCommandException(components.get(0));
        }
        return null;
    }

    private Command<ParkingLot> parseCreateCommand(List<String> args) throws BadCommandException {
        LOGGER.info("Command: {} Args: {}", Command.Verb.CREATE_PARKING_LOT.lName(), args);
        try {
            if (args.size() < 1)
                throw new IllegalArgumentException();

            int size = Integer.parseInt(args.get(0));
            if (size < 0)
                throw new IllegalArgumentException();

            Method method = ParkingLot.class.getMethod(Command.Verb.CREATE_PARKING_LOT.internal(), int.class);
            Command<ParkingLot> command = new Command<>(Command.Verb.CREATE_PARKING_LOT, method, Integer.parseInt(args.get(0)));
            command.setMethods(Collections.singletonList("getSize"));
            command.setOutputTemplate("Created parking lot with %s slots");
            return command;
        }catch (IllegalArgumentException | NoSuchMethodException e){
            LOGGER.error("Failed to parse {} command: invalid size", Command.Verb.CREATE_PARKING_LOT.lName(),
                    e);
            throw new BadCommandException(
                    Command.Verb.CREATE_PARKING_LOT.lName(),
                    args.size() == 1 ? args.get(0) : "size",
                    usageHelpText(Command.Verb.CREATE_PARKING_LOT)
            );
        }
    }

    private Command<ParkingLot> parseParkCommand(List<String> args) throws BadCommandException {
        LOGGER.info("Command: {} Args: {}", Command.Verb.PARK.lName(), args);
        try {
            if (args.size() < 1)
                throw new IllegalArgumentException();

            Method method = ParkingLot.class.getMethod(Command.Verb.PARK.internal(), Car.class);
            Command<ParkingLot> command = new Command<>(Command.Verb.PARK, method, new Car(args.get(0)));
            command.setMethods(Collections.singletonList("getSlot"));
            command.setOutputTemplate("Allocated slot number: %d");
            return command;
        }catch (IllegalArgumentException | NoSuchMethodException e){
            LOGGER.error("Failed to parse {} command: invalid registration no for the car",
                    Command.Verb.PARK.lName(), e);
            throw new BadCommandException(
                    Command.Verb.PARK.lName(),
                    args.size() == 1 ? args.get(0) : "registration no",
                    usageHelpText(Command.Verb.PARK)
            );
        }
    }

    private Command<ParkingLot> parseLeaveCommand(List<String> args) throws BadCommandException {
        LOGGER.info("Command: {} Args: {}", Command.Verb.LEAVE.lName(), args);
        try {
            if (args.size() < 1)
                throw new IllegalArgumentException();

            Command<ParkingLot> command;
            Method method;
            // Command variant with duration.
            if (args.size() > 1){
                method = ParkingLot.class.getMethod(Command.Verb.LEAVE.internal(), Car.class, long.class);
                command = new Command<>(Command.Verb.LEAVE, method, new Car(args.get(0)), Long.parseLong(args.get(1)));
            }else {
                // Command variant w/o duration.
                method = ParkingLot.class.getMethod(Command.Verb.LEAVE.internal(), Car.class);
                command = new Command<>(Command.Verb.LEAVE, method, new Car(args.get(0)));
            }
            //command.setMethods(Arrays.asList("getCar.getRegNo", "getSlot", "getCharges"));
            command.setMethods(Arrays.asList("getSlot", "getCharges"));
            command.setOutputTemplate("Registration number "+args.get(0)+" with Slot Number %d is free with Charge %d");
            return command;
        }catch (IllegalArgumentException | NoSuchMethodException e){
            LOGGER.error("Failed to parse {} command: invalid duration", Command.Verb.LEAVE.lName(), e);
            throw new BadCommandException(
                    Command.Verb.LEAVE.lName(),
                    args.size() == 2 ? args.get(1) : "duration",
                    usageHelpText(Command.Verb.LEAVE)
            );
        }
    }

    private Command<ParkingLot> parseStatusCommand(List<String> args) throws BadCommandException{
        LOGGER.info("Command: "+ Command.Verb.STATUS+" Args: "+args);

        try {
            Method method = ParkingLot.class.getMethod(Command.Verb.STATUS.internal());
            return new Command<>(Command.Verb.STATUS, method);
        } catch (NoSuchMethodException e) {
            throw new BadCommandException(e.getMessage());
        }
    }

    private static String usageHelpText(Command.Verb verb){
        String usage = null;
        Formatter formatter = new Formatter(new StringBuilder());
        switch (verb){
            case CREATE_PARKING_LOT:
                formatter.format("%s {size}", Command.Verb.CREATE_PARKING_LOT.lName());
                formatter.format("\n\twhere 'size' is the parking lot size specified as an integer");
                formatter.format("\n\tfor e.g. create_parking_lot 10");
                return formatter.toString();
            case PARK:
                formatter.format("%s {registration no}", Command.Verb.PARK.lName());
                formatter.format("\n\twhere 'registration no' is the registration number of the car to be parked" +
                        "in the format: <MH-12-AB-9876>");
                formatter.format("\n\tfor e.g. park KA-01-HH-1234");
                return formatter.toString();
            case LEAVE:
                formatter.format("%s {registration no} [duration]", Command.Verb.LEAVE.lName());
                formatter.format("\n\twhere 'registration no' is the registration number of the car to be parked" +
                        "in the format: <MH-12-AB-9876>");
                formatter.format("\n\twhere 'duration' is an optional argument specifying the duration for which" +
                        "the corresponding car was parked");
                formatter.format("\n\tfor e.g. with optional duration arg-  leave KA-01-HH-1234 4");
                formatter.format("\n\t\twithout optional duration arg-  leave KA-01-HH-1234");
                return formatter.toString();
            case STATUS:
                formatter.format(Command.Verb.STATUS.lName());
                formatter.format("\n\tprints the textual representation of the current snapshot of the parking lot");
                formatter.format("\n\tfor e.g. status");
                return formatter.toString();
        }
        return usage;
    }
}
