package org.example;

import auctionsniper.AuctionSniper;
import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import auctionsniper.launcher.SniperCollector;
import auctionsniper.util.Defect;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class SnipersTableModel extends AbstractTableModel implements SniperListener, SniperCollector {
    private final static SniperSnapshot STARTING_UP = new SniperSnapshot("", 0, 0, SniperState.JOINING);
    private final static String[] STATUS_TEXT = {
        "Joining",
        "Bidding",
        "Winning",
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

    public void addSniper(SniperSnapshot snapshot) {
        int row = sniperSnapshots.size();
        sniperSnapshots.add(snapshot);
        fireTableRowsInserted(row, row);
    }

    @Override
    public void addSniper(AuctionSniper sniper) {

    }
}
