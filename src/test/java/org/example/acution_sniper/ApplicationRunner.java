package org.example.acution_sniper;

import auctionsniper.SniperState;
import org.example.Main;
import org.example.MainWindow;
import org.example.SnipersTableModel;

import static org.example.SnipersTableModel.textFor;

public class ApplicationRunner {
    public static final String XMPP_HOSTNAME = "localhost";
    public static final String STATUS_LOST = "Lost";
    public static final String SNIPER_ID = "sniper";
    public static final String SNIPER_PASSWORD = "sniper";
    public static final String SNIPER_XMPP_ID = "sniper@jaesung-kim/Auction";
    private AuctionSniperDriver driver;

    public void startBiddingIn(final FakeAuctionServer... auctions) {
        Thread thread = new Thread("Test Application") {
            public void run() {
                try {
                    Main.main(arguments(auctions));
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
        for (FakeAuctionServer auciton : auctions) {
            driver.showsSniperStatus(
//                auciton.getItemId(),
                "",
                0, 0,
                textFor(SniperState.JOINING)
            );
        }
    }

    private String[] arguments(FakeAuctionServer... auctions) {
        String[] arguments = new String[auctions.length + 3];
        arguments[0] = XMPP_HOSTNAME;
        arguments[1] = SNIPER_ID;
        arguments[2] = SNIPER_PASSWORD;
        for (int i = 0; i < auctions.length; i++) {
            arguments[i + 3] = auctions[i].getItemId();
        }
        return arguments;
    }

    public void showSniperHasLostAuction(FakeAuctionServer auction) {
        driver.showsSniperStatus(textFor(SniperState.LOST));
    }

    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }

    public void hasShownSniperIsBidding(FakeAuctionServer auction, int lastPrice, int lastBid) {
        driver.showsSniperStatus(auction.getItemId(), lastPrice, lastBid, textFor(SniperState.BIDDING));
    }

    public void hasShownSniperIsWinning(FakeAuctionServer auction, int winningBid) {
        driver.showsSniperStatus(auction.getItemId(), winningBid, winningBid, textFor(SniperState.WINNING));
    }

    public void showSniperHasWonAuction(FakeAuctionServer auction, int lastBid) {
        driver.showsSniperStatus(auction.getItemId(), lastBid, lastBid, MainWindow.STATUS_WON);
    }
}
