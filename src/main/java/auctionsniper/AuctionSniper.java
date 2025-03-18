package auctionsniper;

import org.example.Item;
import org.jmock.example.announcer.Announcer;

public class AuctionSniper implements AuctionEventListener {
    private final Announcer<SniperListener> sniperListener = Announcer.to(SniperListener.class);
    private final Auction auction;
    private SniperSnapshot snapshot;
    private final Item item;

    public AuctionSniper(Item item, Auction auction) {
        this.auction =auction;
        this.snapshot = SniperSnapshot.joining(item.identifier);
        this.item = item;
    }


    public void addSniperListener(SniperListener listener) {
        sniperListener.addListener(listener);
    }

    @Override
    public void auctionFailed() {
        snapshot = snapshot.failed();
        notifyChange();
    }

    @Override
    public void auctionClosed() {
        snapshot = snapshot.closed();
        notifyChange();
    }

    @Override
    public void currentPrice(int price, int increment, PriceSource priceSource) {
        switch (priceSource) {
            case FromSniper:
                snapshot = snapshot.winning(price);
                break;
            case FromOtherBidder:
                final int bid = price + increment;
                if (item.allowsBid(bid)) {
                    auction.bid(bid);
                    snapshot = snapshot.bidding(price, bid);
                } else {
                    snapshot = snapshot.losing(price);
                }
        }
        notifyChange();
    }

    private void notifyChange() {
        sniperListener.announce().sniperStateChanged(snapshot);
    }

    public SniperSnapshot getSnapshot() {
        return snapshot;
    }
}
