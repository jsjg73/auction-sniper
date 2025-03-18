package auctionsniper.xmpp;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.JavaReflectionImposteriser;
import org.junit.AfterClass;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.JMockFieldExtension;

import java.util.logging.LogManager;

@ExtendWith(JMockFieldExtension.class)
class LoggingXMPPFailureReporterTest {
    private Mockery context = new Mockery() {{
        setImposteriser(JavaReflectionImposteriser.INSTANCE);
    }};
    final MyLogger logger = context.mock(MyLogger.class);
    final LoggingXMPPFailureReporter report = new LoggingXMPPFailureReporter(logger);

    @AfterClass
    public static void resetLogging() {
        LogManager.getLogManager().reset();
    }

    @Test
    void writesMessageTranslationFailureToLog() {
        context.checking(new Expectations() {{
            oneOf(logger).log("<auction id> Could not translate message" +
                    " \"bad message\" because \"java.lang.Exception: bad\"");
        }});
        report.cannotTranslateMessage("auction id", "bad message", new Exception("bad"));
    }
}