/**
 * The package provides all the infrastructure required to read, parse and execute the commands in fashion similar
 * to the bash shell. The main classes that support this abstraction are:
 *
 * <br/><br/> {@link cs0x65.parkinglot.io.Processor}:
 * In essence, commands - which are directives given in the form of string/text are typically read
 * from either the standard input i.e. {@code STDIN} or from the file (similar to how bash script file is written).
 * This task is performed by {@code Processor}. It reads commands from the specified source one by one and passes
 * over to the parser for further processing.
 *
 * <br/><br/> {@link cs0x65.parkinglot.command.Parser}:
 * Parser parses the command text and performs the following tasks:
 * <ul>
 *     <li>Syntactic validations: to make sure the command supplied is amongst the set of supported commands and
 *     & adheres to the grammar/syntax of the command.
 *     </li>
 *     <li>
 *         Building domain specific {@code Command<T>} instances that are backed by the domain implementation class
 *         and where {@code T} is the actual target to carry out the supplied command.
 *         Effectively, {@code Parser} also forms the bridge between the textual commands supplied and the supporting
 *         domain classes by building instances of the {@code Command<T>}.
 *         It is responsibility of the parser implementation to make sure all the necessary information and metadata
 *         is supplied to the {@code Command<T>} instances for their correct execution.
 *         Refer {@link cs0x65.parkinglot.command.DefaultParserImpl} which is the domain specific implementation of the
 *         {@code Parser} pertaining to {@link cs0x65.parkinglot.impl.ParkingLot}.
 *     </li>
 * </ul>
 *
 * <br/><br/> {@link cs0x65.parkinglot.command.Command}
 * This class builds the domain specific abstraction of the supplied text commands. The class works in co-ordination of
 * the domain specific implementation of {@code Parser} like {@code DefaultParserImpl} for parking lot domain.
 * A typical {@code Command<T>} captures all the information required to execute the command on the provided target
 * class indicated by the instance of parameterized type. The information includes:
 * <ul>
 *     <li>
 *         The target T which is the target class implementing the domain specific commands, for e.g {@code ParkingLot}
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
 *
 * <br/><br/> {@link cs0x65.parkinglot.command.DefaultParserImpl} which is the domain specific implementation of the
 * {@code Parser} pertaining to {@link cs0x65.parkinglot.impl.ParkingLot}.
 * It does an extensive work of parsing, syntactical analysis, emitting the usage help text in case of wrong command
 * usage etc. apart from the core responsibility of binding textual commands read from the source to the parking lot
 * specific commands: {@code Command<ParkingLot>}
 * The supported commands are
 * <ul>
 *     <li>create_parking_lot {size}</li>
 *     <li>park {registraionNo}</li>
 *     <li>leave {registraionNo [duration]}</li>
 *     <li>status {[includeEmptySlots]]}</li>
 * </ul>
 */
package cs0x65.parkinglot.command;
