package test.auctionsniper;

import auctionsniper.Auction;
import auctionsniper.AuctionHouse;
import auctionsniper.AuctionSniper;
import auctionsniper.launcher.SniperCollector;
import auctionsniper.launcher.SniperLauncher;
import org.example.Item;
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

        final Item item = new Item("item 123", 456);

        context.checking(new Expectations() {{
            allowing(auctionHouse).auctionFor(with(any(Item.class)));
            will(returnValue(auction));

            oneOf(auction).addAuctionEventListener(with(sniperForItem(item))); when(auctionState.is("not joined"));
            oneOf(sniperCollector).addSniper(with(sniperForItem(item))); when(auctionState.is("not joined"));

            one(auction).join(); then(auctionState.is("joined"));
        }});

        sut.joinAuction(item);
    }

    private Matcher<AuctionSniper> sniperForItem(Item item) {
        return new FeatureMatcher<AuctionSniper, String>(equalTo(item.identifier), "sniper with item id", "item") {
            @Override
            protected String featureValueOf(AuctionSniper actual) {
                return actual.getSnapshot().itemId;
            }
        };
    }
}
