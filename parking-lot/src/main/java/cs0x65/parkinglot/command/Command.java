package cs0x65.parkinglot.command;

import cs0x65.parkinglot.impl.ParkingLot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

public class Command<T> {
    public enum Verb{
        CREATE_PARKING_LOT("create"),
        PARK("park"),
        LEAVE("leave"),
        STATUS("status");

        private final String internal;

        Verb(String internal){
            this.internal = internal;
        }

        public String internal(){
            return internal;
        }

        public String lName(){
            return name().toLowerCase();
        }
    }

    private T target;
    private final Method verbMethod;
    private final Verb verb;
    private final Object[] args;
    private List<String> methods;
    private String outputTemplate;
    private Object result;

    private static final Logger LOGGER = LogManager.getLogger(Command.class.getName());

    public Command(Verb verb, Method verbMethod, Object... args) {
        this.verb = verb;
        this.verbMethod = verbMethod;
        this.args = args;
    }

    public Verb getVerb() {
        return verb;
    }

    public Object[] getArgs() {
        return args;
    }

    public Object getResult() {
        return result;
    }

    public void setMethods(List<String> methods) {
        this.methods = methods;
    }

    public void setOutputTemplate(String outputTemplate) {
        this.outputTemplate = outputTemplate;
    }

    public Command setTarget(T target) {
        this.target = target;
        return this;
    }

    public String execute() throws CommandOutputFormatException{
        return exec();
    }

    public void executeAndPrint(PrintStream printStream) throws CommandOutputFormatException{
        printStream.println(exec());
    }

    public void executeAndPrint(Writer writer) throws CommandOutputFormatException, IOException {
        writer.write(exec());
    }

    private String exec() throws CommandOutputFormatException{
        try {
            Object object = Modifier.isStatic(verbMethod.getModifiers()) ? ParkingLot.class : target;
            result = verbMethod.invoke(object, args);
            if (result instanceof String){
                return (String) result;
            }
            if (outputTemplate == null || outputTemplate.trim().isEmpty()){
                return "";
            }
            Object[] values = new Object[methods.size()];
            for (int i = 0; i < methods.size(); i++) {
                values[i] = result.getClass().getDeclaredMethod(methods.get(i)).invoke(result);
            }
            String output = String.format(outputTemplate, values);
            LOGGER.info(output);
            return output;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e){
            LOGGER.error(e.getStackTrace());
            if (e instanceof InvocationTargetException){
                result = ((InvocationTargetException) e).getCause();
                return ((Throwable) result).getMessage();
            }
            throw new CommandOutputFormatException(e);
        }
    }
}
