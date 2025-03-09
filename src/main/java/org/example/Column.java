package org.example;

import auctionsniper.SniperSnapshot;

public enum Column {
    ITEM_IDENTIFIER {
        @Override
        public Object valueIn(SniperSnapshot snapshot) {
            return snapshot.itemId;
        }
    },
    LAST_PRICE {
        @Override
        public Object valueIn(SniperSnapshot snapshot) {
            return snapshot.lastPrice;
        }
    },
    LAST_BID {
        @Override
        public Object valueIn(SniperSnapshot snapshot) {
            return snapshot.lastBid;
        }
    },
    SNIPER_STATE {
        @Override
        public Object valueIn(SniperSnapshot snapshot) {
            return snapshot.state;
        }
    };

    abstract public Object valueIn(SniperSnapshot snapShot);

    public static Column at(int offset) {
        return values()[offset];
    }
}
