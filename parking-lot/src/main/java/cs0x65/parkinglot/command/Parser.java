package cs0x65.parkinglot.command;

public interface Parser {
    <T, R> Command<T, R> parse(String commandString) throws BadCommandException;
}
