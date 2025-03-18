package auctionsniper.xmpp;

import auctionsniper.XMPPFailureReporter;

public class LoggingXMPPFailureReporter implements XMPPFailureReporter {
    private static final String MESSAGE_FORMAT = "<%s> Could not translate message \"%s\" because \"%s\"";

    private final MyLogger logger;

    public LoggingXMPPFailureReporter(MyLogger logger) {
        this.logger = logger;
    }

    public void cannotTranslateMessage(String auctionId, String failedMessage, Exception exception) {
        logger.log(String.format(MESSAGE_FORMAT, auctionId, failedMessage, exception.toString()));
    }
}
