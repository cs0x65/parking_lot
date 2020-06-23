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

/**
 * This class represents the domain specific abstraction of the supplied text commands.
 * The class works in co-ordination of the domain specific implementation of {@code Parser} like
 * {@code DefaultParserImpl} for parking lot domain.
 *
 * <br><br/>
 * A typical {@code Command<T>} captures all the information required to execute the command on the provided target
 * class indicated by the instance of parameterized type. The information includes:
 * <ul>
 *     <li>
 *         The target T which is the target class implementing the domain specific commands, for e.g {@code ParkingLot}
 *         along with the arguments.
 *     </li>
 *     <li>
 *         The result object which is the domain specific result of executing the command on the target T
 *     </li>
 *     <li>
 *         The {@code verbMethod} {@link java.lang.reflect.Method} that will be invoked on the target T to obtain the
 *         result
 *      </li>
 *      <li>
 *          Because commands essentially produce formatted output that's rendered on {@code STOUT} or {@code STDERR}
 *          or to file streams/readers, the class also captures the output template and a mechanism to bind values
 *          to the template placeholders. Such a processed template is returned for rendering by the corresponding
 *          {@code Processor}. Additionally, the class provides {@code executeAndPrint()} variants that also flushes
 *          the output to the provided print stream or writer.
 *      </li>
 * </ul>
 * @param <T> the target class that implements the domain specific commands e.g. {@link ParkingLot}
 */
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
    private List<String> resultAccessors;
    private String outputTemplate;
    private Object result;

    private static final Logger LOGGER = LogManager.getLogger(Command.class.getName());

    /**
     *
     * @param verb {@link Verb} identifies the command verb/action/directive. Each command has a distinct, un-ambiguous
     *                         verb associated with it.
     * @param verbMethod {@link java.lang.reflect.Method} that will be invoked on the target T to obtain the
     *  *         result
     * @param args the arguments supplied for the given command.
     */
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

    /**
     *
     * @return the domain specific object that's returned by executing the {@code verbMethod} on the target.
     * {@code execute} and it's variant do the work of generating a formatted textual output that's more suitable for
     * rendering to {@code STOUT} or {@code STDERR} or to file streams/readers.
     * Make sure to call this method to retrieve the result only after invoking one of the {@code execute} method on
     * the current command, failing to do so will return null.
     */
    public Object getResult() {
        return result;
    }

    /**
     *
     * @param resultAccessors are the accessor/getter method names that are invoked on the underlying target so that
     *                        the returned values from the invocation are bind to the the output template placeholders.
     *                        They generate a formatted textual output out of output template that's more suitable for
     *                        rendering to {@code STOUT} or {@code STDERR} or to file streams/readers.
     */
    public void setResultAccessors(List<String> resultAccessors) {
        this.resultAccessors = resultAccessors;
    }

    /**
     *
     * @param outputTemplate is the text body with placeholders indicating the actual values to bind before generating
     *                       command output based on it. It works in co-ordination with {@code setResultAccessors}
     */
    public void setOutputTemplate(String outputTemplate) {
        this.outputTemplate = outputTemplate;
    }

    /**
     *
     * @param target sets the target - which is the domain specific implementation of the group of commands relevant to
     *               that domain. This is the core object that makes {@code Command} the very bridge between the textual
     *               command text read from the source and it's underlying implementation class.
     * @return the target
     */
    public Command<T> setTarget(T target) {
        this.target = target;
        return this;
    }

    /**
     * Because commands essentially produce formatted output that's rendered on {@code STOUT} or {@code STDERR}
     * or to file streams/readers, the method prefers to return this output rather than the domain specific result
     * object (which can be retrieved by calling {@code getResult} post invocation of this method.
     * <br/>
     * The method works with the provided output template and result accessors to generate this textual command output.
     * The returned result is ready to be rendered by {@link cs0x65.parkinglot.io.Processor}
     *
     * <br/><br/>
     * Use {@code executeAndPrint()} variants of this method to additionally pass the output to the supplied print
     * stream or writer, in such case {@code Processor} can provide such a rendering stream.
     *
     * @return the formatted textual result of the execution of this command that's more suitable for rendering to
     * {@code STOUT} or {@code STDERR} or to file streams/readers.
     * @throws CommandOutputFormatException
     */
    public String execute() throws CommandOutputFormatException{
        return exec();
    }

    /**
     * Behaves exactly like {@code execute()} additionally pass the output to the supplied print stream to print
     * @param printStream {@link PrintStream}
     * @throws CommandOutputFormatException
     */
    public void executeAndPrint(PrintStream printStream) throws CommandOutputFormatException{
        printStream.println(exec());
    }

    /**
     * Behaves exactly like {@code execute()} additionally pass the output to the supplied writer to print
     * @param writer {@link Writer}
     * @throws CommandOutputFormatException
     */
    public void executeAndPrint(Writer writer) throws CommandOutputFormatException, IOException {
        writer.write(exec());
    }

    private String exec() throws CommandOutputFormatException{
        try {
            Object object = Modifier.isStatic(verbMethod.getModifiers()) ? ParkingLot.class : target;
            result = verbMethod.invoke(object, args);
            // The underlying domain class method of the command itself returns String, so no need to bind template
            // params to values for e.g. status() method
            if (result instanceof String){
                return (String) result;
            }
            if (outputTemplate == null || outputTemplate.trim().isEmpty()){
                return "";
            }
            // There's a valid template, but it's params are already bound to values; so return template itself.
            if (resultAccessors == null || resultAccessors.isEmpty()){
                LOGGER.info(outputTemplate);
                return outputTemplate;
            }
            // Bind template params to values
            Object[] values = new Object[resultAccessors.size()];
            for (int i = 0; i < resultAccessors.size(); i++) {
                values[i] = result.getClass().getDeclaredMethod(resultAccessors.get(i)).invoke(result);
            }
            String output = String.format(outputTemplate, values);
            LOGGER.info(output);
            return output;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e){
            // InvocationTargetException is the exception that has been thrown by the underlying target method.
            // We would like to show the relevant error message and stack trace on the command console/STDOUT.
            // For such cases, grab the exception and the corresponding message and return it as the output to be
            // rendered to relevant out/rendering stream.
            if (e instanceof InvocationTargetException){
                result = e.getCause();
                return ((Throwable) result).getMessage();
            }else {
                LOGGER.error(e.getMessage());
            }
            throw new CommandOutputFormatException(e);
        }
    }
}
