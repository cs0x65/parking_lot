package cs0x65.parkinglot.io;

import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class ProcessorTest {

    @Test
    void processCommands() {
        String inputFilepath = "src"+ File.separator+"test"+File.separator+"resources"+
                File.separator+"file_inputs.txt";
        String filePath = new File(inputFilepath).getAbsolutePath();
        Processor processor = new Processor(filePath);
        processor.processCommands();
    }
}