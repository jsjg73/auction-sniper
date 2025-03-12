package test.ui;

import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import org.example.Column;
import org.example.SnipersTableModel;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.JMockFieldExtension;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(JMockFieldExtension.class)
public class SnipersTableModelTest {
    private final Mockery context = new Mockery();
    private TableModelListener listener = context.mock(TableModelListener.class);
    private final SnipersTableModel model = new SnipersTableModel();

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
        SniperSnapshot joining = SniperSnapshot.joining("item id");
        SniperSnapshot bidding = joining.bidding(555, 666);
        context.checking(new Expectations() {{
            allowing(listener).tableChanged(with(anyInsertionEvent()));
            one(listener).tableChanged(with(aChangeInRow(0)));
        }});

        model.addSniper(joining);
        model.sniperStateChanged(bidding);

        assertRowMatchesSnapshot(0, bidding);
//        assertColumnEquals(Column.ITEM_IDENTIFIER, "item id");
//        assertColumnEquals(Column.LAST_PRICE, 555);
//        assertColumnEquals(Column.LAST_BID, 666);
//        assertColumnEquals(Column.SNIPER_STATE, SnipersTableModel.textFor(SniperState.BIDDING));
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
        SniperSnapshot joining = SniperSnapshot.joining("item123");
        context.checking(new Expectations() {{
            one(listener).tableChanged(with(anInsertionAtRow(0)));
        }});

        assertEquals(0, model.getRowCount());
        
        model.addSniper(joining);

        assertEquals(1, model.getRowCount());
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
}
