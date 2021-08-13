package viewer;

public class BadQueryException extends Exception {

    public BadQueryException(String errorMessage) {
        super(errorMessage);
    }
}
