package cs0x65.parkinglot.command;

/**
 * The class captures the exception encountered during binding of the {@link Command}'s output template
 * with the supplied accessors.
 */
public class CommandOutputFormatException extends Exception{
    public CommandOutputFormatException() {
    }

    public CommandOutputFormatException(String s) {
        super(s);
    }

    public CommandOutputFormatException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public CommandOutputFormatException(Throwable throwable) {
        super(throwable);
    }

    public CommandOutputFormatException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
