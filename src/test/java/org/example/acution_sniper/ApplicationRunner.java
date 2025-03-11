package org.example.acution_sniper;

import auctionsniper.SniperState;
import org.example.Main;
import org.example.MainWindow;
import org.example.SnipersTableModel;

public class ApplicationRunner {
    public static final String XMPP_HOSTNAME = "localhost";
    public static final String STATUS_LOST = "Lost";
    public static final String SNIPER_ID = "sniper";
    public static final String SNIPER_PASSWORD = "sniper";
    public static final String SNIPER_XMPP_ID = "sniper@jaesung-kim/Auction";
    private AuctionSniperDriver driver;

    public void startBiddingIn(final FakeAuctionServer auction) {
        Thread thread = new Thread("Test Application") {
            public void run() {
                try {
                    Main.main(
                        XMPP_HOSTNAME,
                        SNIPER_ID,
                        SNIPER_PASSWORD,
                        auction.getItemId()
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
        driver = new AuctionSniperDriver(1000);
        driver.hasTitle(MainWindow.APPLICATION_TITLE);
        driver.hasColumnTitles();
        driver.showsSniperStatus(
            SnipersTableModel.textFor(SniperState.JOINING)
        );
    }

    public void showSniperHasLostAuction(FakeAuctionServer auction) {
        driver.showsSniperStatus(SnipersTableModel.textFor(SniperState.LOST));
    }

    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }

    public void hasShownSniperIsBidding(FakeAuctionServer auction, int lastPrice, int lastBid) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, SnipersTableModel.textFor(SniperState.BIDDING));
    }

    public void hasShownSniperIsWinning(FakeAuctionServer auction, int winningBid) {
        driver.showsSniperStatus(auction.getItemId(), winningBid, winningBid, SnipersTableModel.textFor(SniperState.WINNING));
    }

    public void showSniperHasWonAuction(FakeAuctionServer auction, int lastBid) {
        driver.showsSniperStatus(auction.getItemId(), lastBid, lastBid, MainWindow.STATUS_WON);
    }
}
