package org.example;

import auctionsniper.*;
import auctionsniper.SniperPortfolio.PortfolioListener;
import auctionsniper.launcher.SniperCollector;
import auctionsniper.util.Defect;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class SnipersTableModel extends AbstractTableModel implements SniperListener, PortfolioListener {
    private final ArrayList<AuctionSniper> notToBeGCd = new ArrayList<AuctionSniper>();
    private final static String[] STATUS_TEXT = {
        "Joining",
        "Bidding",
        "Winning",
            "Losing",
            "Lost",
            "Won"
    };
    private List<SniperSnapshot> sniperSnapshots = new ArrayList<>();

    @Override
    public int getRowCount() {
        return sniperSnapshots.size();
    }

    @Override
    public int getColumnCount() {
        return Column.values().length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return Column.at(columnIndex).valueIn(sniperSnapshots.get(rowIndex));
    }

    @Override
    public String getColumnName(int column) {
        return Column.at(column).name;
    }

    @Override
    public void sniperStateChanged(SniperSnapshot newSniperSnapshot) {
        int row = rowMatching(newSniperSnapshot);
        sniperSnapshots.set(row, newSniperSnapshot);
        fireTableRowsUpdated(row, row);
    }

    private int rowMatching(SniperSnapshot newSniperSnapshot) {
        for (int i = 0; i < sniperSnapshots.size(); i++) {
            if (newSniperSnapshot.isForSameItemAs(sniperSnapshots.get(i))) {
                return i;
            }
        }
        throw new Defect("Cannot find match for " + sniperSnapshots);
    }

    public static String textFor(SniperState state) {
        return STATUS_TEXT[state.ordinal()];
    }

    private void addSniperSnapshot(SniperSnapshot snapshot) {
        sniperSnapshots.add(snapshot);
        int row = sniperSnapshots.size() - 1;
        fireTableRowsInserted(row, row);
    }

    @Override
    public void sniperAdded(AuctionSniper sniper) {
        sniper.addSniperListener(new SwingThreadSniperListener(this));
        addSniperSnapshot(sniper.getSnapshot());
    }
}
