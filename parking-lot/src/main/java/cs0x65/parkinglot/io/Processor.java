package cs0x65.parkinglot.io;

import cs0x65.parkinglot.command.*;
import cs0x65.parkinglot.impl.ParkingLot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

/**
 * The class is at the core of the command processing infrastructure which abstracts all the IO.
 * It forms the entry point for the further command processing like parsing and execution.
 * <br/>
 * It reads commands from the specified source one by one and passes over to the parser for further processing.
 * The {@link Command} instances returned by parser are further executed and output is redirected/printed to the
 * desired target like - {@code STDOUT} or {@code STDERR} or a file.
 * <br/>
 * The processor can either print the output itself or pass a corresponding target stream/writer to the {@code Command}
 * while executing which takes care of printing the outcome.
 */
public class Processor {
    private final String filePath;
    private String outFilePath;
    private final Parser<ParkingLot> parser = new DefaultParserImpl();
    private static final Logger LOGGER = LogManager.getLogger(Processor.class.getName());

    public Processor(String filePath) {
        this(filePath, null);
    }

    public Processor(String filePath, String outFilePath) {
        this.filePath = filePath;
        this.outFilePath = outFilePath;
    }

    /**
     * Reads command one line at a time, executes it and renders the results.
     * If the output file is supplied, the command output is written to the given file else to the {@link System#out}
     */
    public void processCommands(){
        LOGGER.info("Reading commands from the file: {}", filePath);
        if (outFilePath != null)
            processWithPrintStream();
        else
            process();
    }

    private void process(){
        ParkingLot parkingLot = null;
        String cmdStr = null;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))){
            while ((cmdStr = bufferedReader.readLine()) != null){
                cmdStr = cmdStr.trim();
                Command<ParkingLot> command = parser.parse(cmdStr);
                LOGGER.info("Command read: {}", command.getVerb().lName());

                if (parkingLot == null){
                    if (command.getVerb() == Command.Verb.CREATE_PARKING_LOT){
                        command.executeAndPrint(System.out);
                        parkingLot = (ParkingLot) command.getResult();
                    }else {
                        LOGGER.error("Illegal state: Parking lot doesn't exist!");
                        throw new IllegalStateException("Illegal state: Parking lot doesn't exist! Please create one" +
                                " by executing command: " + Command.Verb.CREATE_PARKING_LOT.lName() +
                                " before issuing any other commands.");
                    }
                }else {
                    command.setTarget(parkingLot).executeAndPrint(System.out);
                }
            }
        }catch (IOException | BadCommandException | CommandOutputFormatException e){
            LOGGER.error("Failed to process command {}", cmdStr);
            LOGGER.error(e);
        }
    }

    private void processWithPrintStream(){
        ParkingLot parkingLot = null;
        String cmdStr = null;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
                PrintStream printStream = new PrintStream(outFilePath)){
            while ((cmdStr = bufferedReader.readLine()) != null){
                cmdStr = cmdStr.trim();
                Command<ParkingLot> command = parser.parse(cmdStr);
                LOGGER.info("Command read: {}", command.getVerb().lName());

                if (parkingLot == null){
                    if (command.getVerb() == Command.Verb.CREATE_PARKING_LOT){
                        command.executeAndPrint(printStream);
                        parkingLot = (ParkingLot) command.getResult();
                    }else {
                        LOGGER.error("Illegal state: Parking lot doesn't exist!");
                        throw new IllegalStateException("Illegal state: Parking lot doesn't exist! Please create one" +
                                " by executing command: " + Command.Verb.CREATE_PARKING_LOT.lName() +
                                " before issuing any other commands.");
                    }
                }else {
                    command.setTarget(parkingLot).executeAndPrint(printStream);
                }
            }
        }catch (IOException | BadCommandException | CommandOutputFormatException e){
            LOGGER.error("Failed to process command {}", cmdStr);
            LOGGER.error(e);
        }
    }
}
