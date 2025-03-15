package org.example;

import auctionsniper.*;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";
    public static String SNIPER_STATUS_NAME = "sniper status";
    public static final String ITEM_ID_AS_LOGIN = "auction-%s";
    public static final String AUCTION_RESOURCE = "Auction";
    public static final String AUCTION_ID_FORMAT =
            ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;
    public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";

    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;
    private static final int ARG_ITEM_ID = 3;

    private final SnipersTableModel snipers = new SnipersTableModel();
    private MainWindow ui;

    @SuppressWarnings("unused")
    private List<Auction> notToBeGCd = new ArrayList<>();

    public Main() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                ui = new MainWindow(snipers);
            }
        });
    }

    public static void main(String... args) throws Exception {
        Main main = new Main();
        XMPPConnection connection = connect(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]);
        main.disconnectWhenUICloses(connection);
        main.addUserRequestListenerFor(connection);
    }

    private void addUserRequestListenerFor(final XMPPConnection connection) {
        ui.addUserRequestListener(new UserRequestListener() {
            @Override
            public void joinAuction(String itemId) {
                snipers.addSniper(SniperSnapshot.joining(itemId));

                Auction auction = new XMPPAuction(connection, itemId);
                notToBeGCd.add(auction);

                auction.addAuctionEventListener(
                    new AuctionSniper(auction, new SwingThreadSniperListener(snipers), itemId)
                );
                auction.join();
            }
        });
    }

    private static XMPPConnection connect(String hostName, String username, String password) throws XMPPException {
        XMPPConnection connection = new XMPPConnection(hostName);
        connection.connect();
        connection.login(username, password, AUCTION_RESOURCE);
        return connection;
    }

    private void disconnectWhenUICloses(XMPPConnection connection) {
        ui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                connection.disconnect();
            }
        });
    }

    public class SwingThreadSniperListener implements SniperListener {
        private final SniperListener sniperListener;

        public SwingThreadSniperListener (SniperListener sniperListener) {
            this.sniperListener = sniperListener;
        }

        @Override
        public void sniperStateChanged(final SniperSnapshot snapshot) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    sniperListener.sniperStateChanged(snapshot);
                }
            });
        }
    }

}