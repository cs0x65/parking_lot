package cs0x65.parkinglot.io;

import cs0x65.parkinglot.command.*;
import cs0x65.parkinglot.impl.ParkingLot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Processor {
    private String filePath;
    private Parser<ParkingLot> parser = new DefaultParserImpl();
    private static final Logger LOGGER = LogManager.getLogger(Processor.class.getName());

    public Processor(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void processCommands(){
        LOGGER.info("Reading commands from the file: {}", filePath);

        ParkingLot parkingLot = null;
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))){
            String cmdStr;
            while ((cmdStr = bufferedReader.readLine()) != null){
                Command<ParkingLot> command = parser.parse(cmdStr);
                LOGGER.info("Command read: {}", command.getVerb().lName());

                if (parkingLot == null){
                    if (command.getVerb() == Command.Verb.CREATE_PARKING_LOT){
                        command.executeAndPrint(System.out);
                        parkingLot = (ParkingLot) command.getResult();
                    }else {
                        LOGGER.error("Illegal state: Parking lot doesn't exist!");
                        throw new IllegalStateException("Illegal state: Parking lot doesn't exist! Please create one" +
                                "by executing command: " + Command.Verb.CREATE_PARKING_LOT.lName() + " before issuing" +
                                " any other commands.");
                    }
                }else {
                    command.setTarget(parkingLot).executeAndPrint(System.out);
                }
            }
        }catch (IOException | BadCommandException | CommandOutputFormatException e){

        }
    }
}
