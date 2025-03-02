package model;

public class GameAlreadyTakenException extends RuntimeException {
    public GameAlreadyTakenException(String message) {
        super(message);
    }
}
