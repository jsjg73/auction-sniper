package org.example;

import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;

import javax.swing.*;

public class SwingThreadSniperListener implements SniperListener {
    private final SniperListener sniperListener;

    public SwingThreadSniperListener(SniperListener sniperListener) {
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
