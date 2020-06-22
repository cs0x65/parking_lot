package cs0x65.parkinglot.command;

import cs0x65.parkinglot.impl.ParkingLot;
import cs0x65.parkinglot.model.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Formatter;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultParserImpl implements Parser {
    private static final Logger LOGGER = LogManager.getLogger(DefaultParserImpl.class.getName());

    @Override
    public <T, R> Command<T, R> parse(String commandString) throws BadCommandException{
        // Makes sure that redundant white spaces are ignored.
        List<String> components = Arrays.stream(commandString.trim().split(" ")).
                filter(s -> !s.trim().equals("")).
                collect(Collectors.toList());

        try {
            Command.Verb verb = Command.Verb.valueOf(components.get(0).toUpperCase());
            List<String> args = components.subList(1, components.size());
            switch (verb){
                case CREATE_PARKING_LOT:
                    return (Command<T, R>) parseCreateCommand(args);
                case PARK:
                    return (Command<T, R>) parseParkCommand(args);
                case LEAVE:
                    return (Command<T, R>) parseLeaveCommand(args);
                case STATUS:
                    return (Command<T, R>) parseStatusCommand(args);
            }
        }catch (IllegalArgumentException e){
            throw new BadCommandException(components.get(0));
        }
        return null;
    }

    private Command<ParkingLot.Builder, ParkingLot> parseCreateCommand(List<String> args) throws BadCommandException {
        LOGGER.info("Command: {} Args: {}", Command.Verb.CREATE_PARKING_LOT.lName(), args);
        try {
            if (args.size() < 1)
                throw new IllegalArgumentException();

            int size = Integer.parseInt(args.get(0));
            if (size < 0)
                throw new IllegalArgumentException();

            return new Command<>(Command.Verb.CREATE_PARKING_LOT, Integer.parseInt(args.get(0)));
        }catch (IllegalArgumentException e){
            LOGGER.error("Failed to parse {} command: invalid size", Command.Verb.CREATE_PARKING_LOT.lName(),
                    e);
            throw new BadCommandException(
                    Command.Verb.CREATE_PARKING_LOT.lName(),
                    args.size() == 1 ? args.get(0) : "size",
                    usageHelpText(Command.Verb.CREATE_PARKING_LOT)
            );
        }
    }

    private Command<ParkingLot, Ticket> parseParkCommand(List<String> args) throws BadCommandException {
        LOGGER.info("Command: {} Args: {}", Command.Verb.PARK.lName(), args);
        try {
            if (args.size() < 1)
                throw new IllegalArgumentException();

            return new Command<>(Command.Verb.PARK, args.get(0));
        }catch (Exception e){
            LOGGER.error("Failed to parse {} command: invalid registration no for the car",
                    Command.Verb.PARK.lName(), e);
            throw new BadCommandException(
                    Command.Verb.PARK.lName(),
                    args.size() == 1 ? args.get(0) : "registration no",
                    usageHelpText(Command.Verb.PARK)
            );
        }
    }

    private Command<ParkingLot, Ticket> parseLeaveCommand(List<String> args) throws BadCommandException {
        LOGGER.info("Command: {} Args: {}", Command.Verb.LEAVE.lName(), args);
        try {
            if (args.size() < 1)
                throw new IllegalArgumentException();

            // Command variant with duration.
            if (args.size() > 1){
                return new Command<>(Command.Verb.LEAVE, args.get(0), Long.parseLong(args.get(1)));
            }
            // Command variant w/o duration.
            return new Command<>(Command.Verb.LEAVE, args.get(0));
        }catch (Exception e){
            LOGGER.error("Failed to parse {} command: invalid duration", Command.Verb.LEAVE.lName(), e);
            throw new BadCommandException(
                    Command.Verb.LEAVE.lName(),
                    args.size() == 2 ? args.get(1) : "duration",
                    usageHelpText(Command.Verb.LEAVE)
            );
        }
    }

    private Command<ParkingLot, String> parseStatusCommand(List<String> args) {
        LOGGER.info("Command: "+ Command.Verb.STATUS+" Args: "+args);
        return new Command<>(Command.Verb.STATUS);
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
