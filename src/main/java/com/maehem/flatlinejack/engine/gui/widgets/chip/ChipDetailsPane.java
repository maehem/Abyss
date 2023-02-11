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
package com.maehem.flatlinejack.engine.gui.widgets.chip;

import static com.maehem.flatlinejack.Engine.LOGGER;
import com.maehem.flatlinejack.engine.Player;
import com.maehem.flatlinejack.engine.SkillChipThing;
import com.maehem.flatlinejack.engine.gui.ChipsConfigurationListener;
import java.util.logging.Level;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

/**
 *
 * @author mark
 */
public class ChipDetailsPane extends HBox {

    private static final String BG_IMAGE_FILE = "/ui/chip-detail.png";
    private static final int ICON_SIZE = 70;
    private final double width;
    private final Text slotN = new Text("8");
    private final Text titleText = new Text("Title Text ABCD");
    private final Text descText = new Text(
            "This is a long description of the item.  It has many "
            + "words describing its parameters and more stuff that "
            + "you need to know about the item.  I like ice cream!"
            + "This is more description."
    );
    Pane itemIconPane = new StackPane();
    private final ImageView installButton = new ImageView();
    private final ImageView removeButton = new ImageView();
    private final Image bgImage = new Image(getClass().getResourceAsStream(BG_IMAGE_FILE));
    private final ImageView bgView = new ImageView(bgImage);
    private final BorderPane thePane = new BorderPane();

    private final HBox buffBar = new HBox();
    private final ChipsConfigurationListener chipsListener;

    public ChipDetailsPane(double w, ChipsConfigurationListener listener) {
        this.width = w;
        this.chipsListener = listener;

        Image installImg = new Image(getClass().getResourceAsStream("/ui/install-button.png"));
        Image removeImg = new Image(getClass().getResourceAsStream("/ui/remove-button.png"));
        installButton.setImage(installImg);
        installButton.setOpacity(0.5);
        installButton.setVisible(false);
        removeButton.setImage(removeImg);
        removeButton.setOpacity(0.5);
        removeButton.setVisible(false);
        setSpacing(8);
        Pane installRemoveButton = new Pane(installButton, removeButton);
        installRemoveButton.setBackground(new Background(new BackgroundFill(
                Color.DARKGREY,
                new CornerRadii(0, 10, 10, 0, false),
                Insets.EMPTY
        )));
        installRemoveButton.setPrefSize(60, 100);
        installRemoveButton.setEffect(new InnerShadow(20, -8, 4, new Color(0.0, 0.0, 0.0, 0.4)));
        HBox.setMargin(installRemoveButton, new Insets(20, 0, 20, 0));
        Pane chipDetails = chipDetails();
        updateChipDetails(null, null);
        getChildren().addAll(installRemoveButton, chipDetails);//, spacer);
    }

    public final void updateChipDetails(SkillChipThing t, Player p) {
        if (t != null) {
            bgView.setOpacity(1.0);
            thePane.setOpacity(1.0);
            titleText.setText(t.getName());
            descText.setText(t.getDescription());
            itemIconPane.setBackground(new Background(new BackgroundFill(
                    t.getColor(),
                    new CornerRadii(12),
                    Insets.EMPTY)));
            updateBuffBar(t);
            Image icon = new Image(getClass().getResourceAsStream(t.getIconPath()));
            ImageView itemIconView = new ImageView(icon);
            itemIconView.setFitWidth(ICON_SIZE * 0.9);
            itemIconView.setPreserveRatio(true);
            itemIconPane.getChildren().clear();
            itemIconPane.getChildren().add(itemIconView);
            boolean chipIsInventory = p.getAInventory().contains(t);
            installButton.setVisible(chipIsInventory);
            if (p.chipSlotAvailable()) {
                installButton.setOnMouseClicked((tt) -> {
                    int chipNum = p.installChip(t);
                    if (chipNum >= 0) {
                        LOGGER.log(Level.INFO,
                                "Installed chip {0} in slot {1}",
                                new Object[]{t.getName(), chipNum});
                    }
                    chipsListener.chipMoved(t);
                });
            } else {
                installButton.setVisible(false);
                // TODO: "Full" image.
            }
            if (!p.isInventoryFull()) {
                removeButton.setVisible(!chipIsInventory);
                removeButton.setOnMouseClicked((tt) -> {
                    LOGGER.log(Level.INFO, "User clicked remove chip.");
                    int slotNum = p.getSlotFor(t);
                    if (slotNum >= 0) {
                        if (p.removeChip(slotNum)) {
                            LOGGER.log(Level.INFO,
                                    "Removed chip {0} in slot {1}",
                                    new Object[]{t.getName(), slotNum});
                            chipsListener.chipMoved(t);
                        }
                    }
                });
            } else {
                removeButton.setVisible(false);
                // TODO: Display a "forbidden" "inventory full" image.
            }
        } else {
            // Clear the details.
            bgView.setOpacity(0.5);
            thePane.setOpacity(0.5);
            titleText.setText("");
            descText.setText("");
            itemIconPane.getChildren().clear();
            itemIconPane.setBackground(new Background(new BackgroundFill(
                    new Color(0.1, 0.1, 0.1, 0.5),
                    new CornerRadii(12),
                    Insets.EMPTY)));
            installButton.setVisible(false);
            removeButton.setVisible(false);
            updateBuffBar(null);
        }
    }

    private Pane chipDetails() {
        bgView.setFitWidth(width);
        bgView.setPreserveRatio(true);
        bgView.setEffect(new DropShadow(20, 10, 10,
                new Color(0.0, 0.0, 0.0, 0.4)));

        thePane.setMaxWidth(width - 80);
        thePane.setMaxHeight(bgView.getFitHeight() - 44);
        thePane.setLayoutX(20);
        thePane.setLayoutY(20);
        thePane.setBackground(new Background(new BackgroundFill(
                new Color(0.15, 0.15, 0.15, 1.0),
                new CornerRadii(12), Insets.EMPTY
        )));
        thePane.setBorder(new Border(new BorderStroke(
                new Color(0.2, 0.0, 0.9, 1.0),
                BorderStrokeStyle.SOLID,
                new CornerRadii(8),
                new BorderWidths(1)
        )));
        StackPane slotId = new StackPane();
        slotId.setPrefSize(36, 36);
        slotId.setMinSize(36, 36);
        slotId.setMaxSize(36, 36);
        slotId.setBackground(new Background(new BackgroundFill(
                Color.BLACK.brighter(),
                new CornerRadii(12, 0, 12, 0, false),
                Insets.EMPTY)));
        slotId.setBorder(new Border(new BorderStroke(
                Color.DARKGREY.darker(),
                BorderStrokeStyle.SOLID,
                new CornerRadii(8, 0, 8, 0, false),
                new BorderWidths(0, 4, 4, 0)
        )));

        slotN.setFill(Color.GRAY);
        slotN.setFont(new Font(26));
        slotId.getChildren().add(slotN);

        HBox.setMargin(titleText, new Insets(0, 0, 0, 16));
        titleText.setFill(Color.LIMEGREEN);
        titleText.setFont(new Font(30));

        VBox centerArea = new VBox();
        centerArea.setPadding(new Insets(0, 6, 6, 6));
        descText.setFill(Color.GRAY);
        descText.setFont(new Font(17));

        TextFlow textFlow = new TextFlow(descText);
        textFlow.setTextAlignment(TextAlignment.JUSTIFY);
        textFlow.setMaxWidth(Double.MAX_VALUE);
        textFlow.setMinHeight(40);
        textFlow.setLineSpacing(-3.4);
        textFlow.setPadding(new Insets(2, 0, 0, 8));

        centerArea.getChildren().add(textFlow);

        itemIconPane.setPrefSize(ICON_SIZE, ICON_SIZE);
        itemIconPane.setMinSize(ICON_SIZE, ICON_SIZE);
        itemIconPane.setMaxSize(ICON_SIZE, ICON_SIZE);
        itemIconPane.setBorder(new Border(new BorderStroke(
                Color.DARKGREY.darker(),
                BorderStrokeStyle.SOLID,
                new CornerRadii(8, 0, 0, 8, false),
                new BorderWidths(5, 0, 5, 7)
        )));

        buffBar.setAlignment(Pos.CENTER);
        buffBar.setSpacing(6);
        buffBar.setPadding(new Insets(4, 0, 0, 0));
        buffBar.setOpacity(0.6);
        InnerShadow is = new InnerShadow(16.0, 0.0, -7.0, new Color(0.0, 0.0, 0.0, 0.5));
        buffBar.setEffect(is);

        updateBuffBar(null);

        HBox topArea = new HBox(slotId, titleText);
        thePane.setTop(topArea);
        thePane.setCenter(centerArea);
        thePane.setRight(itemIconPane);
        thePane.setBottom(buffBar);

        return new Pane(bgView, thePane);
    }

    private void updateBuffBar(SkillChipThing t) {
        buffBar.getChildren().clear();
        if (t == null) {
            Color bg = new Color(1.0, 1.0, 1.0, 0.2);
            buffBar.getChildren().addAll(
                    makeBuffPane("....", "...", bg),
                    makeBuffPane("....", "...", bg),
                    makeBuffPane("....", "...", bg),
                    makeBuffPane("....", "...", bg)
            );
        } else {
            t.getBuffs().forEach((tt) -> {
                buffBar.getChildren().add(makeBuffPane(
                        tt.getKey().mnemonic(),
                        String.valueOf(tt.getValue()),
                        tt.getKey().color())
                );
            });
        }
    }

    private HBox makeBuffPane(String name, String val, Color c) {
        HBox buff = new HBox();

        buff.setPrefSize(width * 0.19, 32);
        buff.setAlignment(Pos.CENTER);

        buff.setBackground(new Background(new BackgroundFill(
                c,
                new CornerRadii(10, 10, 0, 0, false),
                new Insets(4, 0, 0, 0)))
        );
        buff.setPadding(new Insets(4, 0, 0, 0));
        buff.setAlignment(Pos.BASELINE_CENTER);
        buff.setEffect(new DropShadow());
        double fontSize = 15;
        String family = new Font(fontSize).getFamily();
        Label nameLbl = new Label(name + " : ");
        nameLbl.setFont(Font.font(family, FontWeight.NORMAL, fontSize));
        Label valLbl = new Label(val);
        valLbl.setFont(Font.font(family, FontWeight.BOLD, fontSize + 6.0));
        buff.getChildren().addAll(nameLbl, valLbl);

        return buff;
    }

}
