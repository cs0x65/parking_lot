package cs0x65.parkinglot.command;

public class Command<T, R> {
    enum Verb{
        CREATE_PARKING_LOT("build"),
        PARK("park"),
        LEAVE("leave"),
        STATUS("status");

        private String internal;

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

    private final Verb verb;
    private final Object[] args;

    @SafeVarargs
    public Command(Verb verb, Object... args) {
        this.verb = verb;
        this.args = args;
    }

    public Verb getVerb() {
        return verb;
    }

    public Object[] getArgs() {
        return args;
    }

    public R execute(T target){
        return null;
    }

    public R execute(Class<T> clazz){
        return null;
    }
}
