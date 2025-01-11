/*
    Licensed to the Apache Software Foundation (ASF) under one or more
    contributor license agreements.  See the NOTICE file distributed with this
    work for additional information regarding copyright ownership.  The ASF
    licenses this file to you under the Apache License, Version 2.0
    (the "License"); you may not use this file except in compliance with the
    License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
    License for the specific language governing permissions and limitations
    under the License.
 */
package com.maehem.abyss.engine.babble;

import static com.maehem.abyss.Engine.LOGGER;
import com.maehem.abyss.engine.*;
import com.maehem.abyss.engine.Character;
import com.maehem.abyss.engine.audio.sound.SoundEffectManager;
import java.util.ArrayList;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class VendWidget extends VBox {

    private final Image COIN_IMAGE = new Image(getClass().getResourceAsStream("/icons/coin-icon.png"));
    public final static String FONT_PATH = "/fonts/Modenine-2OPd.ttf";
    public final static double FONT_SIZE = 22;
    private final Font FONT = Font.loadFont(getClass().getResourceAsStream(FONT_PATH), FONT_SIZE);
    private final Font HEADER_FONT = Font.loadFont(getClass().getResourceAsStream(FONT_PATH), FONT_SIZE * 0.8);
    private final double ITEM_GONE_OPACITY = 0.5;

    private static final String TITLE_BUY = "Price List";
    private static final String CREDITS = "Credits:";
    private static final String CURRENCY_SIGN = "$";

    private static final String HEADING_DESC_BUY = "Description";
    private static final String HEADING_COST_BUY = "Cost";
    private static final String HEADING_COST_SELL = "Value";

    private final VBox itemsBox = new VBox();
    private final ScrollPane sp = new ScrollPane();

    private final Player player;
    private final Character character;
    private EventHandler<ActionEvent> eventHandler;
    private final ArrayList<Thing> items = new ArrayList<>();
    private Text playerMoneyText;
    private boolean showQuantity = true;

    // TODO: Might add UPGRADE and WAREZ
    public enum VendMode {
        BUY, SELL, TRADE, ORGANS
    }

    private VendMode mode = VendMode.BUY;

    public VendWidget(Character character, Player player, double height) {
        this.character = character;
        this.player = player;

        setMaxHeight(height);
        setPrefHeight(height);

        setFillWidth(true);
        setSpacing(8);

    }

    /**
     * Call after Vignette sets up widget.
     *
     */
    public void init() {
        //setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
        initHeading();
        initCenter();
        initBottom();

        //layout();
    }

    public VendMode getMode() {
        return mode;
    }

    public void setMode(VendMode mode) {
        this.mode = mode;
    }

    private void initHeading() {
        // TODO: i18n Bundle strings.
        Text title = topText(TITLE_BUY);
        Text crLabel = topText(CREDITS);
        Text currencyText = topText(CURRENCY_SIGN);
        playerMoneyText = topText(String.valueOf(player.getMoney()));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox hBox = new HBox(title, spacer, crLabel, currencyText, playerMoneyText);
        //BorderPane.setMargin(hBox, new Insets(FONT_SIZE));
        getChildren().add(hBox);

        Text descLabel = headerText(" " + HEADING_DESC_BUY);
        Text qtyLabel;
        if (itemsHaveQuantity()) {
            qtyLabel = headerText("QTY   ");
        } else {
            qtyLabel = headerText("      ");
        }
        Text costLabel = headerText(mode.equals(VendMode.ORGANS) ? HEADING_COST_SELL : HEADING_COST_BUY);

        // Expanding spacer for extra space
        Region hSpacer = new Region();
        HBox.setHgrow(hSpacer, Priority.ALWAYS);

        // Spacer for over buttons columns
        Region hBtnSpacer = new Region();
        hBtnSpacer.setMinHeight(FONT_SIZE);
        int btnColWidth = 0;
        switch (mode) {
            case BUY, SELL -> {
                btnColWidth = 60;
            }
            case ORGANS, TRADE -> {
                btnColWidth = 120;
            }
        }

        hBtnSpacer.setMinWidth(btnColWidth);

        HBox hdrBox = new HBox();
        if (mode == VendMode.ORGANS) {
            hdrBox.getChildren().addAll(descLabel, hSpacer, costLabel, hBtnSpacer);
        } else {
            hdrBox.getChildren().addAll(descLabel, hSpacer, qtyLabel, costLabel, hBtnSpacer);
        }

        //BorderPane.setMargin(hBox, new Insets(FONT_SIZE));
        getChildren().add(hdrBox);
    }

    private Text topText(String s) {
        Text text = new Text(s);
        text.setFont(FONT);

        return text;
    }

    private Text headerText(String s) {
        Text text = new Text(s);
        text.setFont(HEADER_FONT);

        return text;
    }

    private void initCenter() {
        itemsBox.setFillWidth(true);
        itemsBox.setSpacing(FONT_SIZE / 8);
        itemsBox.setPadding(new Insets(12));
        //itemsBox.setBorder(new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));

        sp.setContent(new BorderPane(itemsBox));
        sp.setFitToHeight(true);
        sp.setFitToWidth(true);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        VBox.setVgrow(sp, Priority.ALWAYS);

        updateList();

        getChildren().add(sp);
    }

    private void updateList() {
        double vvalue = sp.getVvalue();
        ObservableList<Node> list = itemsBox.getChildren();
        list.clear();
        items.forEach((t) -> {
            list.add(vendItem(t));
        });
        Platform.runLater(() -> {
            itemsBox.layout();
            sp.setVvalue(vvalue);
        });
    }

    private Node vendItem(Thing thing) {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Text nameText = itemText(thing.getName());
        Text qtyText = itemText("999");
        updateQtyText(thing, qtyText);

        final Button b1;
        final Button b2;
        switch (mode) {
            default -> {
                b1 = new Button("Buy");
                b1.setDisable(thing.getVendQuantity() == 0);
                b2 = null;
            }
            case SELL -> {
                b1 = new Button("Sell");
                b1.setDisable(thing.getVendQuantity() < 0);
                b2 = null;
            }
            case ORGANS, TRADE -> {
                if (thing instanceof BodyPartThing bpt) {
                    if (thing.getVendQuantity() < 0) {
                        thing.setValue(bpt.getBodyPart().sellPrice);
                    } else {
                        thing.setValue(bpt.getBodyPart().buyPrice);
                        // TODO: discount price
                    }
                }
                b1 = new Button("Buy");
                b1.setDisable(thing.getVendQuantity() < 0);

                b2 = new Button("Sell");
                b2.setDisable(thing.getVendQuantity() > 0);
            }
        }

        Text priceText = itemText(String.format("%4d", thing.getValue()));

        updateItemOpacity(thing.getVendQuantity(), nameText, priceText, qtyText);

        b1.setOnAction((t) -> {
            if (thing.getVendQuantity() != 0) {
                if (mode == VendMode.ORGANS) { // Buy organ
                    if (player.takeMoney(thing.getValue())) {
                        playerMoneyText.setText(String.valueOf(player.getMoney()));
                        thing.setVendQuantity(-1); // Can sell it.
                        player.restoreBodyPart((BodyPartThing) thing);
                    } else {
                        SoundEffectManager.getInstance().play(SoundEffectManager.Sound.FAIL);
                        LOGGER.log(Level.CONFIG, "Not enough funds to buy organ.");
                        //LOGGER.log(Level.CONFIG, "TODO: Button should be greyed out.");
                    }

                } else { // Inventory Item
                    if (player.takeMoney(thing.getValue())) {
                        playerMoneyText.setText(String.valueOf(player.getMoney()));
                        if (thing.getVendQuantity() > 0) {
                            thing.setVendQuantity(thing.getVendQuantity() - 1);
                        }
                        updateQtyText(thing, qtyText);
                        // Add ting to player inventory.
                        Thing factoryThing = Thing.factory(thing.getClass().getCanonicalName());
                        if (player.getInventory().add(factoryThing)) {
                            SoundEffectManager.getInstance().play(SoundEffectManager.Sound.MONEY);
                            LOGGER.log(Level.CONFIG, "Purchased {0}", thing.getName());
                        } else {
                            LOGGER.log(Level.SEVERE, "VendWidget: Could not add item to inventory!");
                        }
                    } else {
                        SoundEffectManager.getInstance().play(SoundEffectManager.Sound.FAIL);
                        LOGGER.log(Level.CONFIG, "Not enough funds to buy item.");
                        // TODO: Cause player money to go red and fade back to black.
                    }
                }
            }
            // Update grey-out state of item. Move to updateList().
            if (thing.getVendQuantity() == 0) {
                b1.setDisable(true);
                updateItemOpacity(thing.getVendQuantity(), nameText, priceText, qtyText);
            }

            // update list
            updateList();
        });
        if (b2 != null) {
            b2.setOnAction((t) -> {
                if (thing.getVendQuantity() < 0) {
                    player.addMoney(thing.getValue());
                    playerMoneyText.setText(String.valueOf(player.getMoney()));
                    thing.setVendQuantity(thing.getVendQuantity() + 1);
                    updateQtyText(thing, qtyText);
                    // Add ting to player inventory.
                    if (mode == VendMode.ORGANS) {
                        if (thing instanceof BodyPartThing bpt) {
                            player.getSoldBodyParts().add(bpt.getBodyPart());
                            thing.setVendQuantity(1); // Vendor has your organ now and can sell it back.
                            SoundEffectManager.getInstance().play(SoundEffectManager.Sound.SQUISH);
                            //b2.setDisable(thing.getVendQuantity() < 0);
                            //b1.setDisable(false);
                        } else {
                            LOGGER.log(Level.SEVERE, "Tried to sell body part but vended thing is not a body part!!!");
                        }
                    } else {
                        Thing factoryThing = Thing.factory(thing.getClass().getCanonicalName());
                        if (player.getInventory().add(factoryThing)) {
                            // TODO: PLay a cash sound.
                            SoundEffectManager.getInstance().play(SoundEffectManager.Sound.MONEY);
                            LOGGER.log(Level.CONFIG, "Sold {0}", thing.getName());
                        } else {
                            LOGGER.log(Level.SEVERE, "VendWidget: Could not add item to inventory!");
                        }
                    }

                }

                // update list.
                updateList();
            });
        }

        // Update grey-out state of item.
        if (thing.getVendQuantity() == 0) {
            updateItemOpacity(thing.getVendQuantity(), nameText, priceText, qtyText);
        }

        HBox buttonBox = new HBox(b1);
        HBox lineBox = new HBox();
        if (mode == VendMode.ORGANS) {
            lineBox.getChildren().addAll(nameText, spacer, priceText, buttonBox);
        } else {
            lineBox.getChildren().addAll(nameText, spacer, qtyText, priceText, buttonBox);
        }
        if (b2 != null) {
            buttonBox.getChildren().add(b2);
        }

        lineBox.setAlignment(Pos.BOTTOM_LEFT);
        lineBox.setSpacing(FONT_SIZE);
        return lineBox;
    }

    private void updateItemOpacity(int qty, Node... n) {
        double opacity = qty == 0 ? ITEM_GONE_OPACITY : 1.0;
        for (Node nn : n) {
            nn.setOpacity(opacity);
        }
    }

    private void updateQtyText(Thing thing, Text t) {
        if (thing.getVendQuantity() < 0) {
            t.setText("  ");
        } else {
            t.setText(String.format("%2d", thing.getVendQuantity()));
        }

    }

    private Text itemText(String s) {
        Text text = new Text(s);
        text.setFont(FONT);

        return text;
    }

    private void initBottom() {

        Button okButton = new Button("DONE");
        okButton.setStyle("-fx-font-size: 26;");

        HBox box = new HBox(okButton);
        box.setAlignment(Pos.CENTER);

        getChildren().add(box);

        okButton.setOnAction((t) -> {
            if (eventHandler != null) {
                eventHandler.handle(t);
            }
        });
    }

    public final void setOnAction(EventHandler<ActionEvent> eh) {
        this.eventHandler = eh;
    }

    public ArrayList<Thing> getItems() {
        return items;
    }

    private boolean itemsHaveQuantity() {
        for (Thing t : items) {
            if (t.getVendQuantity() >= 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return the showQuantity
     */
    public boolean isShowQuantity() {
        return showQuantity;
    }

    /**
     * @param showQuantity the showQuantity to set
     */
    public void setShowQuantity(boolean showQuantity) {
        this.showQuantity = showQuantity;
    }

}
