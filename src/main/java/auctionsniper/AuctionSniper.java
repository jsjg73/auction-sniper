package auctionsniper;

import org.jmock.example.announcer.Announcer;

public class AuctionSniper implements AuctionEventListener {
    private final Announcer<SniperListener> sniperListener;
    private final Auction auction;
    private SniperSnapshot snapshot;

    public AuctionSniper(String itemId, Auction auction) {
        this.auction =auction;
        this.sniperListener = Announcer.to(SniperListener.class);
        this.snapshot = SniperSnapshot.joining(itemId);
    }


    public void addSniperListener(SniperListener listener) {
        sniperListener.addListener(listener);
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
                auction.bid(bid);
                snapshot = snapshot.bidding(price, bid);
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
