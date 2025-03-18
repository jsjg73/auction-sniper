package auctionsniper.xmpp;

import java.util.logging.Logger;

public class JULLoggerAdapter implements MyLogger {
    private final Logger logger;

    public JULLoggerAdapter(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void log(String msg) {
        logger.severe(msg);
    }
}
