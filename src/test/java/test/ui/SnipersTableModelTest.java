package test.ui;

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

    @Test
    void holdsSnipersInAdditionOrder() {
        context.checking(new Expectations() {{
            ignoring(listener);
        }});

        model.addSniper(SniperSnapshot.joining("item 0"));
        model.addSniper(SniperSnapshot.joining("item 1"));

        assertEquals("item 0", cellValue(0, Column.ITEM_IDENTIFIER));
        assertEquals("item 1", cellValue(1, Column.ITEM_IDENTIFIER));
    }

    private Object cellValue(int row, Column column) {
        return model.getValueAt(row, column.ordinal());
    }

    @Test
    void updatesCorrectRowForSniper() {
        SniperSnapshot joining1 = SniperSnapshot.joining("item 1");
        SniperSnapshot joining2 = SniperSnapshot.joining("item 2");

        SniperSnapshot bidding = joining2.bidding(123, 234);

        context.checking(new Expectations() {{
            allowing(listener).tableChanged(with(anyInsertionEvent()));
            one(listener).tableChanged(with(aChangeInRow(1)));
        }});

        model.addSniper(joining1);
        model.addSniper(joining2);

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
