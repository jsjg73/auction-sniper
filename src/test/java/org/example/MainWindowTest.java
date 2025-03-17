package org.example;

import auctionsniper.SniperPortfolio;
import com.objogate.wl.swing.probe.ValueMatcherProbe;
import org.example.acution_sniper.AuctionSniperDriver;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;

public class MainWindowTest {
    private final SniperPortfolio portfolio = new SniperPortfolio();
    private final MainWindow sut = new MainWindow(portfolio);
    private final AuctionSniperDriver driver = new AuctionSniperDriver(100);

    @Test
    void makesUserRequestWhenJoinButtonClicked() {
        final ValueMatcherProbe<Item> itemProbe =
                new ValueMatcherProbe<>(equalTo(new Item("an item id", 789)), "item request");

        sut.addUserRequestListener(
            new UserRequestListener() {
                public void joinAuction(Item item) {
                    itemProbe.setReceivedValue(item);
                }
            }
        );

        driver.startBiddingFor("an item id", 789);
        driver.check(itemProbe);
    }
}
