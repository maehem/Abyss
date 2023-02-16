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
package com.maehem.abyss.engine.gui.widgets.chip;

import com.maehem.abyss.engine.GameState;
import com.maehem.abyss.engine.Player;
import com.maehem.abyss.engine.SkillChipThing;
import com.maehem.abyss.engine.SkillChipThing.Buff;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * Appears the installed chips in the Chips Installation screen where chips are
 * selectively placed into the neck of the player.
 *
 * @author mark
 */
public class InstalledChipStatsPane extends HBox {

    private static final int TEXT_W = 226;
    private static final int ICON_D = 64;
    private static final Color UNSELECTED_COLOR_1 = new Color(1.0, 1.0, 1.0, 0.08);
    private static final Color UNSELECTED_COLOR_2 = new Color(1.0, 1.0, 1.0, 0.4);
    private static final Border DEFAULT_BORDER = new Border(new BorderStroke(
                UNSELECTED_COLOR_2,  BorderStrokeStyle.SOLID, 
                new CornerRadii(10), new BorderWidths(2)
    ));
    private static final Border SELECTED_BORDER = new Border(new BorderStroke(
                Color.CYAN,  BorderStrokeStyle.SOLID, 
                new CornerRadii(10), new BorderWidths(2)
    ));
    private static final Background DEFAULT_ICON_BACKGROUND = new Background(new BackgroundFill(
                    UNSELECTED_COLOR_1,
                    new CornerRadii(10),
                    Insets.EMPTY
    ));
    
    private final Player player;
    private final int slot;

    private final Text title = new Text("Title Text");
    private final Pane iconPane = new Pane();
    private final GridPane buffPane = new GridPane();
    private final GameState gameState;

    public InstalledChipStatsPane(GameState gs, Player p, int slot) {
        this.player = p;
        this.slot = slot;
        this.gameState = gs;

        setSelected(false);
        
        setFillHeight(true);

        //SkillChipThing chip = p.getChipSlots()[slot];

        // Title, Short Desc.  Image
        title.setFont(new Font(18));
        title.setFill(Color.SPRINGGREEN);
        TextFlow textFlow = new TextFlow(title);
        textFlow.setPadding(new Insets(0, 0, 0, 8));

        iconPane.setPrefSize(ICON_D, ICON_D);
        iconPane.setMaxSize(ICON_D, ICON_D);

        buffPane.setHgap(2);
        buffPane.setVgap(2);
        buffPane.setPadding(new Insets(0, 2, 2, 8));

        VBox leftArea = new VBox(textFlow, buffPane);

        HBox.setHgrow(leftArea, Priority.ALWAYS);
        getChildren().addAll(leftArea, iconPane);

        refresh();
    }

    public void refresh() {
        SkillChipThing t = player.getChipSlots()[slot];
        if (t == null) {
            title.setText("");
            fillBuffPane(null);
            iconPane.getChildren().clear();
            iconPane.setBackground(DEFAULT_ICON_BACKGROUND);
        } else {
            title.setText(t.getName());
            InputStream imgStream;
            String iconPath = t.getIconPath();
            if ( iconPath.startsWith("/content") ) {
                // Use class loader from content JAR.
                imgStream = gameState.getContentLoader().getStream(iconPath);
            } else {
                // Use class loader from engine JAR.
                imgStream = getClass().getResourceAsStream(iconPath);
            }
            Image iconImage = new Image(getClass().getResourceAsStream(t.getIconPath() ));
            ImageView iv = new ImageView(iconImage);
            iv.setFitHeight(ICON_D);
            iv.setPreserveRatio(true);
            iconPane.getChildren().add(iv);
            iconPane.setBackground(new Background(new BackgroundFill(
                    t.getColor(),
                    new CornerRadii(10),
                    Insets.EMPTY)));
            fillBuffPane(t);
        }
    }

    private void fillBuffPane(SkillChipThing t) {
        buffPane.getChildren().clear();
        int i = 0;
        if ( t != null ) {
            List<Entry<Buff, Integer>> entries = t.getBuffs().stream().map(Map.Entry::copyOf).toList();
        
            for ( ; i<entries.size(); i++ ) {
                Entry<Buff, Integer> buff = entries.get(i);
                Node bPane = makeBuffPane(
                        buff.getKey().mnemonic(), 
                        String.valueOf(buff.getValue()), 
                        buff.getKey().color()
                );
                buffPane.add(bPane, i/2, i%2);
            }
        }
        
        // Fill in remaining buffs with blanks.
        for ( ; i<4; i++ ) {
            Node bPane = makeBuffPane( "----", "---", UNSELECTED_COLOR_1 );
            buffPane.add(bPane, i/2, i%2);
        }
    }

    private HBox makeBuffPane(String name, String val, Color c) {
        HBox buff = new HBox();
        buff.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        buff.setPrefSize(80, 60);

        buff.setFillHeight(true);
        buff.setAlignment(Pos.CENTER);
        buff.setBackground(new Background(new BackgroundFill(
                c,
                new CornerRadii(3),
                new Insets(1)))
        );
        double fontSize = 14;
        String family = new Font(fontSize).getFamily();
        Label nameLbl = new Label(name + ": ");
        nameLbl.setFont(Font.font(family, FontWeight.BOLD, fontSize));
        Label valLbl = new Label(val);
        valLbl.setFont(Font.font(family, FontWeight.NORMAL, fontSize));
        buff.getChildren().addAll(nameLbl, valLbl);

        return buff;
    }
    
    public void setSelected( boolean selected ) {
        setBorder(selected?SELECTED_BORDER:DEFAULT_BORDER);
    }
    
    public boolean isSelected() {
        return getBorder() == SELECTED_BORDER;
    }

    public SkillChipThing getChip() {
        return player.getChipSlots()[slot];
    }

}
