package cs0x65.parkinglot.command;

import java.util.Arrays;

public class BadCommandException extends Exception{
    private static final String badCommandMessage = badCommandMessageTemplate();
    private static final String badArgumentMessage = badArgumentMessageTemplate();

    public BadCommandException(String verb) {
        super(String.format(badCommandMessage, verb));

    }

    public BadCommandException(String verb, String arg, String usageDetails) {
        super(String.format(badArgumentMessage, arg, verb, usageDetails));
    }

    private static String badCommandMessageTemplate(){
        StringBuilder stringBuilder = new StringBuilder("'%s' is not a supported command!\n");
        stringBuilder.append("Please try to use the one from the set of supported commands below:\n");
        Arrays.stream(Command.Verb.values())
                .forEach(value -> stringBuilder.append(value.name().toString().toLowerCase()+"\n"));
        return stringBuilder.toString();
    }

    private static String badArgumentMessageTemplate(){
        StringBuilder stringBuilder = new StringBuilder("'%s' - not a valid argument for the command: %s\n");
        stringBuilder.append("Correct usage:\n%s");
        return stringBuilder.toString();
    }
}
