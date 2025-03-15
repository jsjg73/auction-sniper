package test.auctionsniper.xmpp;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener;
import auctionsniper.xmpp.XMPPAuction;
import org.example.Main;
import org.example.acution_sniper.ApplicationRunner;
import org.example.acution_sniper.FakeAuctionServer;
import org.jivesoftware.smack.XMPPConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static auctionsniper.xmpp.XMPPAuctionHouse.AUCTION_RESOURCE;
import static org.example.acution_sniper.ApplicationRunner.SNIPER_XMPP_ID;

class XMPPAuctionTest {

    private final FakeAuctionServer auctionServer = new FakeAuctionServer("item54321");
    private XMPPConnection connection;

    @BeforeEach
    public void createConnection() throws Exception {
        connection = new XMPPConnection(FakeAuctionServer.XMPP_HOSTNAME);
        connection.connect();
        connection.login(ApplicationRunner.SNIPER_ID, ApplicationRunner.SNIPER_PASSWORD, AUCTION_RESOURCE);

        auctionServer.startSellingItem();
    }

    @AfterEach
    public void closeConnection() {
        if (connection != null) {
            connection.disconnect();
        }
        auctionServer.stop();
    }

    @Test
    void receivesEventsFromAuctionServerAfterJoining() throws Exception {
        CountDownLatch auctionWasClosed = new CountDownLatch(1);

        Auction auction = new XMPPAuction(connection, auctionServer.getItemId());
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