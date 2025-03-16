package auctionsniper.launcher;

import auctionsniper.Auction;
import auctionsniper.AuctionHouse;
import auctionsniper.AuctionSniper;
import auctionsniper.SniperSnapshot;
import org.example.SnipersTableModel;
import org.example.SwingThreadSniperListener;
import org.example.UserRequestListener;

import java.util.ArrayList;
import java.util.List;

public class SniperLauncher implements UserRequestListener {

    @SuppressWarnings("unused")
    private List<Auction> notToBeGCd = new ArrayList<>();
    private final SnipersTableModel snipers;
    private final AuctionHouse auctionHouse;

    public SniperLauncher(SnipersTableModel snipers, AuctionHouse auctionHouse) {
        this.snipers = snipers;
        this.auctionHouse = auctionHouse;
    }

    @Override
    public void joinAuction(String itemId) {
        snipers.addSniper(SniperSnapshot.joining(itemId));

        Auction auction = auctionHouse.auctionFor(itemId);
        notToBeGCd.add(auction);

        auction.addAuctionEventListener(
            new AuctionSniper(auction, new SwingThreadSniperListener(snipers), itemId)
        );
        auction.join();

    }
}
