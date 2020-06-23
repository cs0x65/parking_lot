package cs0x65.parkinglot;

import cs0x65.parkinglot.io.Processor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Application {
    private static final Logger LOGGER = LogManager.getLogger(Application.class.getName());
    public static void main(String[] args){
        LOGGER.info("Starting parking lot app..." );
        if (args.length < 1) {
            LOGGER.error("Please supply the file to read commands from!");
            throw new IllegalArgumentException("Missing commands file! Please provide the file to read commands from");
        }
        LOGGER.info("Reading commands from the file {}", args[0]);
        Processor processor = new Processor(args[0]);
        processor.processCommands();
    }
}
