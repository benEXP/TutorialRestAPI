package Tutorial.Exceptions.FileRepoExceptions;

public class NoIdException extends RuntimeException{
    public NoIdException(String message) {
        super(message);
    }

    public NoIdException(String message, Throwable cause) {
        super(message, cause);
    }
}
