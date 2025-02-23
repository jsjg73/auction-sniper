package org.example.acution_sniper;

import org.example.Main;
import org.example.MainWindow;

import static org.example.MainWindow.STATUS_JOINING;

public class ApplicationRunner {
    public static final String XMPP_HOSTNAME = "localhost";
    public static final String STATUS_LOST = "Lost";
    public static final String SNIPER_ID = "sniper";
    public static final String SNIPER_PASSWORD = "sniper";
    public static final String SNIPER_XMPP_ID = "sniper@localhost/Auction";
    private AuctionSniperDriver driver;

    public void startBiddingIn(FakeAuctionServer auction) {
        Thread thread = new Thread("Test Application") {
            public void run() {
                try {
                    Main.main(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD,
                            auction.getItemId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
        driver = new AuctionSniperDriver(1000);
        driver.showsSniperStatus(STATUS_JOINING);
    }

    public void showSniperHasLostAuction() {
        driver.showsSniperStatus(STATUS_LOST);
    }

    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }

    public void hasShownSniperIsBidding() {
        driver.showsSniperStatus(MainWindow.STATUS_BIDDING);
    }
}
