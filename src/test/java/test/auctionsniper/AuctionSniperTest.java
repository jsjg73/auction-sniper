package test.auctionsniper;

import auctionsniper.*;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.JMockFieldExtension;

import static auctionsniper.AuctionEventListener.PriceSource.FromOtherBidder;
import static auctionsniper.AuctionEventListener.PriceSource.FromSniper;

@ExtendWith(JMockFieldExtension.class)
public class AuctionSniperTest {
    private static final String ITEM_ID = "item-id";
    private final Mockery context = new Mockery();
    private final Auction auction = context.mock(Auction.class);
    private final SniperListener sniperListener =
            context.mock(SniperListener.class);

    private final AuctionSniper sniper = new AuctionSniper(auction, sniperListener, ITEM_ID);

    @Test
    void reportsLostWhenAuctionCloses() {
        context.checking(new Expectations() {{
            one(sniperListener).sniperLost();
        }});

        sniper.auctionClosed();
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
            atLeast(1).of(sniperListener).sniperBidding(new SniperSnapshot(ITEM_ID, price, bid));
        }});

        sniper.currentPrice(price, increment, FromOtherBidder);
    }

    @Test
    void reportsIsWinningWhenCurrentPriceComesFromSniper() {
        context.checking(new Expectations() {{
            atLeast(1).of(sniperListener).sniperWinning();
        }});

        sniper.currentPrice(123, 45, FromSniper);
    }

    @Test
    void reportsLostIfAuctionClosesImmediately() {
        context.checking(new Expectations() {{
            atLeast(1).of(sniperListener).sniperLost();
        }});

        sniper.auctionClosed();
    }

    private final States sniperState = context.states("sniper");

    @Test
    void reportsLostIfAuctionClosesWhenBidding() {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperBidding(with(any(SniperSnapshot.class)));
                then(sniperState.is("bidding"));
            atLeast(1).of(sniperListener).sniperLost();
                when(sniperState.is("bidding"));
        }});

        sniper.currentPrice(123, 45, FromOtherBidder);
        sniper.auctionClosed();
    }

    @Test
    void reportsWonIfAuctionClosesWhenWinning() {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperWinning();
                then(sniperState.is("winning"));
            atLeast(1).of(sniperListener).sniperWon();
                when(sniperState.is("winning"));
        }});

        sniper.currentPrice(123, 45, FromSniper);
        sniper.auctionClosed();
    }
}
