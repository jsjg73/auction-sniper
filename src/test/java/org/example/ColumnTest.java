package org.example;

import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ColumnTest {

    @Test
    void retrievesValuesFromASniperSnapshot() {
        SniperSnapshot snapshot = new SniperSnapshot("item-id", 123, 0, SniperState.JOINING);

        assertEquals("item-id", Column.ITEM_IDENTIFIER.valueIn(snapshot));
        assertEquals(123, Column.LAST_PRICE.valueIn(snapshot));
        assertEquals(0, Column.LAST_BID.valueIn(snapshot));
        assertEquals("Joining", Column.SNIPER_STATE.valueIn(snapshot));

    }

}