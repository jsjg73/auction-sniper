package auctionsniper;

public class MissingValueException extends RuntimeException{
    public MissingValueException(String fieldName) {
        super(fieldName);
    }
}
