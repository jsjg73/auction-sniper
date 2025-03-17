package auctionsniper.xmpp;

import auctionsniper.Auction;
import auctionsniper.AuctionHouse;
import org.example.Item;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public class XMPPAuctionHouse implements AuctionHouse {

    private final XMPPConnection connection;

    public static final String AUCTION_RESOURCE = "Auction";
    public static final String ITEM_ID_AS_LOGIN = "auction-%s";
    public static final String AUCTION_ID_FORMAT =
            ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

    public XMPPAuctionHouse (XMPPConnection connection) {
        this.connection = connection;
    }

    public static XMPPAuctionHouse connect(String hostName, String username, String password) throws XMPPException {
        XMPPConnection connection = new XMPPConnection(hostName);
        connection.connect();
        connection.login(username, password, AUCTION_RESOURCE);
        return new XMPPAuctionHouse(connection);
    }

    @Override
    public Auction auctionFor(Item item) {
        return new XMPPAuction(connection, item);
    }

    public void disconnect() {
        connection.disconnect();
    }
}
