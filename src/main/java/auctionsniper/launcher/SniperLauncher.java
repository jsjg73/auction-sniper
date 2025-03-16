package auctionsniper.launcher;

import auctionsniper.Auction;
import auctionsniper.AuctionHouse;
import auctionsniper.AuctionSniper;
import org.example.UserRequestListener;

public class SniperLauncher implements UserRequestListener {

    private final AuctionHouse auctionHouse;
    private final SniperCollector collector;

    public SniperLauncher(SniperCollector collector, AuctionHouse auctionHouse) {
        this.auctionHouse = auctionHouse;
        this.collector = collector;
    }

    @Override
    public void joinAuction(String itemId) {
        Auction auction = auctionHouse.auctionFor(itemId);
        AuctionSniper sniper = new AuctionSniper(itemId, auction);
        auction.addAuctionEventListener(sniper);
        collector.addSniper(sniper);
        auction.join();
    }
}
