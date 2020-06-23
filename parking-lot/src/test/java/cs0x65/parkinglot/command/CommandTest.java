package cs0x65.parkinglot.command;

import cs0x65.parkinglot.impl.ParkingLot;
import cs0x65.parkinglot.model.Ticket;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;

class CommandTest {
    Parser<ParkingLot> parser = new DefaultParserImpl();

    @Test
    void executeCreate() {
        try {
            Command<ParkingLot> command = parser.parse("create_parking_lot 5");
            String output = command.execute();
            assertEquals("Created parking lot with 5 slots", output);
            assertTrue(command.getResult() instanceof ParkingLot);
            assertEquals(5, ((ParkingLot) command.getResult()).getSize());
        }catch (BadCommandException | CommandOutputFormatException e){
        }
    }

    @Test
    void executePark() {
        try {
            Command<ParkingLot> command = parser.parse("create_parking_lot 1");
            command.execute();

            ParkingLot parkingLot = (ParkingLot) command.getResult();

            command = parser.parse("park MH-10-PP-0101");
            String output = command.setTarget(parkingLot).execute();
            assertEquals("Allocated slot number: 1", output);
            assertTrue(command.getResult() instanceof Ticket);
        }catch (BadCommandException | CommandOutputFormatException e){
        }
    }

    @Test
    void executeLeave() {
        try {
            Command<ParkingLot> command = parser.parse("create_parking_lot 2");
            command.execute();

            ParkingLot parkingLot = (ParkingLot) command.getResult();

            command = parser.parse("park MH-10-PP-0101");
            command.setTarget(parkingLot).execute();

            command = parser.parse("park MH-13-A-4994");
            command.setTarget(parkingLot).execute();

            command = parser.parse("leave MH-10-PP-0101 4");
            String output = command.setTarget(parkingLot).execute();
            assertEquals("Registration number MH-10-PP-0101 with Slot Number 1 is free with Charge 30",
                    output);
            assertTrue(command.getResult() instanceof Ticket);

            command = parser.parse("leave MH-13-A-4994");
            output = command.setTarget(parkingLot).execute();
            assertEquals("Registration number MH-13-A-4994 with Slot Number 2 is free with Charge 10",
                    output);
        }catch (BadCommandException | CommandOutputFormatException e){
        }
    }

    @Test
    void executeStatus() {
        try {
            Command<ParkingLot> command = parser.parse("create_parking_lot 2");
            command.execute();

            ParkingLot parkingLot = (ParkingLot) command.getResult();

            // Includes empty slots
            command = parser.parse("status true");
            String output = command.setTarget(parkingLot).execute();
            List<String> expected = Arrays.asList(
                    "Slot No. Registration No.",
                    "1        --",
                    "2        --"
            );
            assertLinesMatch(expected, Arrays.asList(output.split("\n")));

            command = parser.parse("park MH-10-PP-0101");
            command.setTarget(parkingLot).execute();

            command = parser.parse("park MH-13-A-4994");
            command.setTarget(parkingLot).execute();

            // Excludes empty slots - default behaviour.
            command = parser.parse("status");
            output = command.setTarget(parkingLot).execute();
            expected = Arrays.asList(
                    "Slot No. Registration No.",
                    "1        MH-10-PP-0101",
                    "2        MH-13-A-4994"
            );
            assertLinesMatch(expected, Arrays.asList(output.split("\n")));
        }catch (BadCommandException | CommandOutputFormatException e){
        }
    }
}