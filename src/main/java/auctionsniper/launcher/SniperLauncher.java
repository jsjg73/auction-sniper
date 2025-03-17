package auctionsniper.launcher;

import auctionsniper.Auction;
import auctionsniper.AuctionHouse;
import auctionsniper.AuctionSniper;
import org.example.Item;
import org.example.UserRequestListener;

public class SniperLauncher implements UserRequestListener {

    private final AuctionHouse auctionHouse;
    private final SniperCollector collector;

    public SniperLauncher(SniperCollector collector, AuctionHouse auctionHouse) {
        this.auctionHouse = auctionHouse;
        this.collector = collector;
    }

    @Override
    public void joinAuction(Item item) {
        Auction auction = auctionHouse.auctionFor(item);
        AuctionSniper sniper = new AuctionSniper(item, auction);
        auction.addAuctionEventListener(sniper);
        collector.addSniper(sniper);
        auction.join();
    }
}
