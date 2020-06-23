package cs0x65.parkinglot.command;

public interface Parser<T> {
    Command<T> parse(String commandString) throws BadCommandException;
}
