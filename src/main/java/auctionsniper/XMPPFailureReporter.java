package auctionsniper;

public interface XMPPFailureReporter {
    void cannotTranslateMessage(String auctionId, String failMessage, Exception exception);
}
