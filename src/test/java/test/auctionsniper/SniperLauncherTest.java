package test.auctionsniper;

import auctionsniper.Auction;
import auctionsniper.AuctionHouse;
import auctionsniper.AuctionSniper;
import auctionsniper.launcher.SniperCollector;
import auctionsniper.launcher.SniperLauncher;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.JMockFieldExtension;

import static org.hamcrest.Matchers.equalTo;


@ExtendWith(JMockFieldExtension.class)
public class SniperLauncherTest {
    private final Mockery context = new Mockery();
    private final States auctionState = context.states("auction state").startsAs("not joined");
    private final Auction auction = context.mock(Auction.class);
    private final AuctionHouse auctionHouse = context.mock(AuctionHouse.class);
    private final SniperCollector sniperCollector = context.mock(SniperCollector.class);
    private final SniperLauncher sut = new SniperLauncher(sniperCollector, auctionHouse);

    @Test
    void addsNessSniperToCollectorAndThenJoinsAuction() {
        final String itemId = "item 123";

        context.checking(new Expectations() {{
            allowing(auctionHouse).auctionFor(itemId);
            will(returnValue(auction));

            oneOf(auction).addAuctionEventListener(with(sniperForItem(itemId))); when(auctionState.is("not joined"));
            oneOf(sniperCollector).addSniper(with(sniperForItem(itemId))); when(auctionState.is("not joined"));

            one(auction).join(); then(auctionState.is("joined"));
        }});

        sut.joinAuction(itemId);
    }

    private Matcher<AuctionSniper> sniperForItem(String itemId) {
        return new FeatureMatcher<AuctionSniper, String>(equalTo(itemId), "sniper with itemId id", "itemId") {
            @Override
            protected String featureValueOf(AuctionSniper actual) {
                return actual.getSnapshot().itemId;
            }
        };
    }
}
