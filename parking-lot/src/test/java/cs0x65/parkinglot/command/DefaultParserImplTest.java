package cs0x65.parkinglot.command;

import cs0x65.parkinglot.impl.ParkingLot;
import cs0x65.parkinglot.model.Ticket;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DefaultParserImplTest {
    Parser parser = new DefaultParserImpl();

    @Test
    void parseCreateParkingLot() {
        String cmdStr = "create_parking_lot 6";
        try {
            Command<ParkingLot.Builder, ParkingLot> command = parser.parse(cmdStr);
            assertEquals(Command.Verb.CREATE_PARKING_LOT,  command.getVerb());
            assertEquals("build",  command.getVerb().internal());
            assertArrayEquals(new Object[]{6}, command.getArgs());
        }catch (BadCommandException e){
        }
    }

    @Test
    void parsePark() {
        String cmdStr = "park MH-12-NE-9010";
        try {
            Command<ParkingLot, Ticket> command = parser.parse(cmdStr);
            assertEquals(Command.Verb.PARK,  command.getVerb());
            assertEquals("park",  command.getVerb().internal());
            assertArrayEquals(new Object[]{"MH-12-NE-9010"}, command.getArgs());
        }catch (BadCommandException e){
        }
    }

    @Test
    void parseLeave() {
        String cmdStr = "leave MH-12-NE-9010 5";
        try {
            Command<ParkingLot, Ticket> command = parser.parse(cmdStr);
            assertEquals(Command.Verb.LEAVE,  command.getVerb());
            assertEquals("leave",  command.getVerb().internal());
            assertArrayEquals(new Object[]{"MH-12-NE-9010", 5L}, command.getArgs());
        }catch (BadCommandException e){
        }
    }

    @Test
    void parseLeaveWithoutDuration() {
        String cmdStr = "leave MH-12-N-910";
        try {
            Command<ParkingLot, Ticket> command = parser.parse(cmdStr);
            assertEquals(Command.Verb.LEAVE,  command.getVerb());
            assertEquals("leave",  command.getVerb().internal());
            assertArrayEquals(new Object[]{"MH-12-N-910"}, command.getArgs());
        }catch (BadCommandException e){
        }
    }

    @Test
    void parseStatus() {
        String cmdStr = "status";
        try {
            Command<ParkingLot, String> command = parser.parse(cmdStr);
            assertEquals(Command.Verb.STATUS,  command.getVerb());
            assertEquals("status",  command.getVerb().internal());
            assertArrayEquals(new Object[]{}, command.getArgs());
        }catch (BadCommandException e){
        }
    }

    @Test
    void parseThrowsExceptionWhenInvalidCommand() {
        String cmdStr = "current_status";
        BadCommandException badCommandException = assertThrows(BadCommandException.class, () -> parser.parse(cmdStr));
        List<String> expected = Arrays.asList("'current_status' is not a supported command!");
        List<String > actual = Arrays.asList(badCommandException.getMessage().split("\n")[0]);
        assertLinesMatch(expected, actual);
    }

    @Test
    void parseCreateParkingLotThrowsExceptionWhenPassedInvalidArg() {
        String cmdStr1 = "create_parking_lot";
        BadCommandException badCommandException = assertThrows(BadCommandException.class, () -> parser.parse(cmdStr1));
        List<String> expected = Arrays.asList("'size' - not a valid argument for the command: create_parking_lot");
        List<String > actual = Arrays.asList(badCommandException.getMessage().split("\n")[0]);

        String cmdStr2 = "create_parking_lot garbage";
        badCommandException = assertThrows(BadCommandException.class, () -> parser.parse(cmdStr2));

        String cmdStr3 = "create_parking_lot -1";
        badCommandException = assertThrows(BadCommandException.class, () -> parser.parse(cmdStr3));
        expected = Arrays.asList("'-1' - not a valid argument for the command: create_parking_lot");
        actual = Arrays.asList(badCommandException.getMessage().split("\n")[0]);
    }
}