package test.auctionsniper;

import auctionsniper.*;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.JMockFieldExtension;

import static auctionsniper.AuctionEventListener.PriceSource.FromOtherBidder;
import static auctionsniper.AuctionEventListener.PriceSource.FromSniper;
import static auctionsniper.SniperState.*;
import static org.hamcrest.Matchers.equalTo;

@ExtendWith(JMockFieldExtension.class)
public class AuctionSniperTest {
    private static final String ITEM_ID = "item-id";
    private final Mockery context = new Mockery();
    private final Auction auction = context.mock(Auction.class);
    private final SniperListener sniperListener =
            context.mock(SniperListener.class);

    private final AuctionSniper sniper = new AuctionSniper(ITEM_ID, auction);

    @BeforeEach
    void addAuctionSniperListener() {
        sniper.addSniperListener(sniperListener);
    }

    @Test
    void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        final int price = 1001;
        final int increment = 25;
        final int bid = price + increment;
        context.checking(new Expectations() {{
            // aution에는 one을 사용하고, sniperListener에는 atLeast를 사용하는것.
            // 이는 리스너가 auction에 비해 좀 더 너그러운 협력 객체라는 의도를 표현한다.
            one(auction).bid(bid);
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, price, bid, SniperState.BIDDING));
        }});

        sniper.currentPrice(price, increment, FromOtherBidder);
    }

    @Test
    void reportsIsWinningWhenCurrentPriceComesFromSniper() {
        context.checking(new Expectations() {{
            allowing(auction);
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(BIDDING)));
            then(sniperState.is("bidding"));

            atLeast(1).of(sniperListener).sniperStateChanged(
                new SniperSnapshot(ITEM_ID, 135, 135, WINNING)
            );
            when(sniperState.is("bidding"));
        }});

        sniper.currentPrice(123, 12, FromOtherBidder);
        sniper.currentPrice(135, 45, FromSniper);
    }

    @Test
    void reportsLostIfAuctionClosesImmediately() {
        context.checking(new Expectations() {{
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 0, 0, LOST));
        }});

        sniper.auctionClosed();
    }

    private final States sniperState = context.states("sniper");

    @Test
    void reportsLostIfAuctionClosesWhenBidding() {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(SniperState.BIDDING)));
                then(sniperState.is("bidding"));
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 123, 168, LOST));
                when(sniperState.is("bidding"));
        }});

        sniper.currentPrice(123, 45, FromOtherBidder);
        sniper.auctionClosed();
    }

    @Test
    void reportsWonIfAuctionClosesWhenWinning() {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(WINNING)));
                then(sniperState.is("winning"));
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 123, 0, WON));
                when(sniperState.is("winning"));
        }});

        sniper.currentPrice(123, 45, FromSniper);
        sniper.auctionClosed();
    }

    private Matcher<SniperSnapshot> aSniperThatIs(final SniperState state) {
        return new FeatureMatcher<SniperSnapshot, SniperState>(equalTo(state), "sniper that is ", "was") {
            @Override
            protected SniperState featureValueOf(SniperSnapshot actual) {
                return actual.state;
            }
        };
    }
}
