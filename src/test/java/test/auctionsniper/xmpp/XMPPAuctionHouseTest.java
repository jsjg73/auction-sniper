package test.auctionsniper.xmpp;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener;
import auctionsniper.xmpp.XMPPAuctionHouse;
import org.example.Item;
import org.example.acution_sniper.ApplicationRunner;
import org.example.acution_sniper.FakeAuctionServer;
import org.jivesoftware.smack.XMPPConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.example.acution_sniper.ApplicationRunner.SNIPER_XMPP_ID;

class XMPPAuctionHouseTest {

    private final FakeAuctionServer auctionServer = new FakeAuctionServer("item54321");
    private XMPPConnection connection;
    private XMPPAuctionHouse auctionHouse;

    @BeforeEach
    public void createConnection() throws Exception {
        auctionHouse = XMPPAuctionHouse.connect(FakeAuctionServer.XMPP_HOSTNAME, ApplicationRunner.SNIPER_ID, ApplicationRunner.SNIPER_PASSWORD);
        auctionServer.startSellingItem();
    }

    @AfterEach
    public void closeConnection() {
        if (auctionHouse != null) {
            auctionHouse.disconnect();
        }
        auctionServer.stop();
    }

    @Test
    void receivesEventsFromAuctionServerAfterJoining() throws Exception {
        CountDownLatch auctionWasClosed = new CountDownLatch(1);

        Auction auction = auctionHouse.auctionFor(new Item(auctionServer.getItemId(), 567));
        auction.addAuctionEventListener(auctionClosedListener(auctionWasClosed));

        auction.join();
        auctionServer.hasReceivedJoinRequestFrom(SNIPER_XMPP_ID);
        auctionServer.announceClosed();

        Assertions.assertTrue(auctionWasClosed.await(2, TimeUnit.SECONDS), "should have been closed");
    }

    private AuctionEventListener auctionClosedListener(final CountDownLatch auctionWasClosed) {
        return new AuctionEventListener() {
            public void auctionClosed() {
                auctionWasClosed.countDown();
            }

            public void currentPrice(int price, int increment, PriceSource priceSource) {
                // 구현하지 않음
            }
        };
    }
}