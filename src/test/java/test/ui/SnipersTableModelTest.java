package test.ui;

import auctionsniper.AuctionSniper;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import auctionsniper.util.Defect;
import org.example.Column;
import org.example.SnipersTableModel;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.JMockFieldExtension;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(JMockFieldExtension.class)
public class SnipersTableModelTest {
    private final Mockery context = new Mockery();
    private TableModelListener listener = context.mock(TableModelListener.class);
    private final SnipersTableModel model = new SnipersTableModel();
    private final AuctionSniper sniper = new AuctionSniper("item 0", null);

    @BeforeEach
    void attachModelListener() {
        model.addTableModelListener(listener);
    }

    @Test
    void hasEnoughColumns() {
        assertThat(model.getColumnCount(), equalTo(Column.values().length));
    }

    @Test
    void setsSniperValuesInColumns() {
        context.checking(new Expectations() {{
            allowing(listener).tableChanged(with(anyInsertionEvent()));
            one(listener).tableChanged(with(aChangeInRow(0)));
        }});

        model.sniperAdded(sniper);
        SniperSnapshot bidding = sniper.getSnapshot();
        model.sniperStateChanged(bidding);

        assertRowMatchesSnapshot(0, bidding);
    }

    private void assertColumnEquals(Column column, Object expected) {
        final int rowIndex = 0;
        assertColumnEquals(rowIndex, column, expected);
    }

    private void assertColumnEquals(int row, Column column, Object expected) {
        final int columnIndex = column.ordinal();
        assertEquals(expected, model.getValueAt(row, columnIndex));
    }

    private Matcher<TableModelEvent> aRowChangesEvent() {
        return samePropertyValuesAs(new TableModelEvent(model, 0));
    }

    private Matcher<TableModelEvent> aChangeInRow(int row) {
        return samePropertyValuesAs(new TableModelEvent(model, row));
    }

    @Test
    void setsUpColumnHeadings() {
        for (Column column : Column.values()) {
            assertEquals(column.name, model.getColumnName(column.ordinal()));
        }
    }

    @Test
    void notifiesListenersWhenAddingASniper() {
        context.checking(new Expectations() {{
            one(listener).tableChanged(with(anInsertionAtRow(0)));
        }});

        assertEquals(0, model.getRowCount());
        
        model.sniperAdded(sniper);

        assertEquals(1, model.getRowCount());
        SniperSnapshot joining = sniper.getSnapshot();
        assertRowMatchesSnapshot(0, joining);
    }

    private Matcher<TableModelEvent> anInsertionAtRow(int row) {
        return samePropertyValuesAs(new TableModelEvent(model, row, row, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
    }

    private Matcher<TableModelEvent> anyInsertionEvent() {
        return hasProperty("type", equalTo(TableModelEvent.INSERT));
    }

    private void assertRowMatchesSnapshot(int row, SniperSnapshot snapshot) {
        assertColumnEquals(row, Column.ITEM_IDENTIFIER, snapshot.itemId);
        assertColumnEquals(row, Column.LAST_PRICE, snapshot.lastPrice);
        assertColumnEquals(row, Column.LAST_BID, snapshot.lastBid);
        assertColumnEquals(row, Column.SNIPER_STATE, SnipersTableModel.textFor(snapshot.state));
    }

    @Test
    void holdsSnipersInAdditionOrder() {
        AuctionSniper sniper2 = new AuctionSniper("item 1", null);
        context.checking(new Expectations() {{
            ignoring(listener);
        }});

        model.sniperAdded(sniper);
        model.sniperAdded(sniper2);

        assertEquals("item 0", cellValue(0, Column.ITEM_IDENTIFIER));
        assertEquals("item 1", cellValue(1, Column.ITEM_IDENTIFIER));
    }

    private Object cellValue(int row, Column column) {
        return model.getValueAt(row, column.ordinal());
    }

    @Test
    void updatesCorrectRowForSniper() {
        AuctionSniper sniper2 = new AuctionSniper("item 1", null);

        context.checking(new Expectations() {{
            allowing(listener).tableChanged(with(anyInsertionEvent()));
            one(listener).tableChanged(with(aChangeInRow(1)));
        }});

        model.sniperAdded(sniper);
        model.sniperAdded(sniper2);

        SniperSnapshot bidding = sniper2.getSnapshot().bidding(555, 666);
        model.sniperStateChanged(bidding);

        assertRowMatchesSnapshot(1, bidding);
    }

    @Test
    public void throwsDefectIfNoExistingSniperForAnUpdate() {
        assertThrows(
            Defect.class,
            () -> model.sniperStateChanged(new SniperSnapshot("item 1", 123, 234, SniperState.WINNING))
        );
    }
}
