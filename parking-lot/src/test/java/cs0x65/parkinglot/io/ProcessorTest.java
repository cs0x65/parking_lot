package cs0x65.parkinglot.io;

import cs0x65.parkinglot.command.BadCommandException;
import cs0x65.parkinglot.command.Command;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class ProcessorTest {

    @Test
    void processCommands() {
        String inputFilepath = "src"+ File.separator+"test"+File.separator+"resources"+
                File.separator+"file_inputs.txt";
        String filePath = new File(inputFilepath).getAbsolutePath();
        Processor processor = new Processor(filePath);
        assertDoesNotThrow(() -> processor.processCommands());
    }

    @Test
    void processCommandsWhenOutFilePathIsSupplied() {
        String inputFilepath = "src"+ File.separator+"test"+File.separator+"resources"+
                File.separator+"file_inputs.txt";
        String filePath = new File(inputFilepath).getAbsolutePath();
        Processor processor = new Processor(filePath, "output.txt");
        assertDoesNotThrow(() -> processor.processCommands());
        
        StringBuilder stringBuilder = new StringBuilder();
        try(BufferedReader reader = new BufferedReader(new FileReader("output.txt"))){
            String text = null;
            
            while ((text = reader.readLine()) != null){
                stringBuilder.append(text);
            }
        }catch (IOException e){
        }
        
        String expectedOutput = "Created parking lot with 6 slots" +
                "Allocated slot number: 1" +
                "Allocated slot number: 2" +
                "Allocated slot number: 3" +
                "Allocated slot number: 4" +
                "Allocated slot number: 5" +
                "Allocated slot number: 6" +
                "Registration number KA-01-HH-" +
                "3141 with Slot Number 6 is free with Charge 30" +
                "Slot No. Registration No." +
                "1        KA-01-HH-1234" +
                "2        KA-01-HH-9999" +
                "3        KA-01-BB-0001" +
                "4        KA-01-HH-7777" +
                "5        KA-01-HH-2701" +
                "Allocated slot number: 6" +
                "Sorry, parking lot is full" +
                "Registration number KA-01-HH-" +
                "1234 with Slot Number 1 is free with Charge 30" +
                "Registration number KA-01-BB-" +
                "0001 with Slot Number 3 is free with Charge 50" +
                "Registration number DL-12-AA-9999 not found" +
                "Allocated slot number: 1" +
                "Allocated slot number: 3" +
                "Sorry, parking lot is full" +
                "Slot No. Registration No." +
                "1        KA-09-HH-0987" +
                "2        KA-01-HH-9999" +
                "3        CA-09-IO-1111" +
                "4        KA-01-HH-7777" +
                "5        KA-01-HH-2701" +
                "6        KA-01-P-333";
        assertEquals(expectedOutput, stringBuilder.toString());
    }

    @Test
    void processCommandsThrowsExceptionWhenCreateParkingLotIsNotTheFirstCommand() {
        String inputFilepath = "src"+ File.separator+"test"+File.separator+"resources"+
                File.separator+"file_inputs_create_missing.txt";
        String filePath = new File(inputFilepath).getAbsolutePath();
        Processor processor = new Processor(filePath);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> processor.processCommands());
        String expectedMessage = "Illegal state: Parking lot doesn't exist! Please create one by executing command: "
                + Command.Verb.CREATE_PARKING_LOT.lName() + " before issuing any other commands.";
        assertEquals(expectedMessage, exception.getMessage());
    }
}