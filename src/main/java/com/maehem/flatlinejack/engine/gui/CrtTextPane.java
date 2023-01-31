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
package com.maehem.flatlinejack.engine.gui;

import com.maehem.flatlinejack.engine.GameState;
import com.maehem.flatlinejack.engine.GameStateListener;
import com.maehem.flatlinejack.engine.gui.bbs.BBSTerminal;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.transform.Scale;

/**
 *
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public class CrtTextPane extends GUIPane implements GameStateListener {
    private static final String SCREEN_FONT = "/fonts/VT323-Regular.ttf";
    private static final double SCREEN_FONT_H = 37;
    private static final double SCREEN_LINE_SPACE = -7.171; // default is 0.0
    private static final Color SCREEN_BG_COLOR = new Color(0.1, 0.1, 0.1, 1.0);
    private static final Color SCREEN_FG_COLOR = new Color(0.1, 1.0, 0.1, 1.0);
    private final static double CRT_HEIGHT = 400;
    private final static double CRT_WIDTH = 1024;

    private final Font FONT = Font.loadFont(
            this.getClass().getResource(SCREEN_FONT).toExternalForm(),
            SCREEN_FONT_H
    );
    private final Text t = new Text();
    
    private final TextFlow flow = new TextFlow(t);
    private final double scale;
    
    public CrtTextPane(GameState gs, double width) {
        gs.addListenter(this);
        
        StackPane contentPane = new StackPane();
        getChildren().add(contentPane);
        
        scale = width/CRT_WIDTH;

        contentPane.setBackground(new Background(new BackgroundFill(
                Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY
        )));
        
        flow.setPrefSize(CRT_WIDTH, CRT_HEIGHT);
        
        flow.setBackground(new Background(
                new BackgroundFill(SCREEN_BG_COLOR, CornerRadii.EMPTY, Insets.EMPTY)
        ));
        
        flow.setLineSpacing(SCREEN_LINE_SPACE);
        flow.setPadding(new Insets(16, 16, 0, 16)); //  T,R,B,L
        flow.setTabSize(4);
        flow.setEffect(new GaussianBlur( 3));

        //flow.setPrefSize(CRT_WIDTH*scale, CRT_HEIGHT*scale);
        Group flowGroup = new Group(flow);
        Scale xf = new Scale();
        xf.setPivotX(0);
        xf.setPivotY(0);
        flow.getTransforms().add(xf);
        xf.setX(scale);
        xf.setY(scale);
        
        t.setFill(SCREEN_FG_COLOR);
        t.setFont(FONT);
        setText("Narration Area.\n\tHello\nSuper long text stuff, I can see my house from here!\n" +
                "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&*()_+{}[]|\\" +
                "\n" +
                "01234567890123456789012345678901234567890123456789012345678901234567890123456789"
        );
        
        // Scan line negative space.
        WritableImage im = new WritableImage((int)CRT_WIDTH, (int)CRT_HEIGHT);
        PixelWriter pw = im.getPixelWriter();
        for (int y = 0; y < CRT_HEIGHT; y += 3) {
            for (int x = 0; x < CRT_WIDTH; x++) {
                //pw.setColor(x, y, SCREEN_BG_COLOR);
//                pw.setColor(x, y, Color.MAGENTA);  // For calibration
                pw.setColor(x, y, Color.BLACK);  // For calibration
            }
        }
        ImageView scanLines = new ImageView(im);
        //scanLines.setPreserveRatio(true);
        //scanLines.setFitWidth(CRT_WIDTH*scale);
        //scanLines.setFitHeight(CRT_HEIGHT*scale);
        Group scanLinesGroup = new Group(scanLines);
        //flowGroup.getChildren().add(scanLines);
        
        Scale slxf = new Scale();
        slxf.setPivotX(0);
        slxf.setPivotY(0);
        scanLines.getTransforms().add(slxf);
        slxf.setX(scale);
        slxf.setY(scale);
        
        //scanLines.setPreserveRatio(true);
        //scanLines.setLayoutX(20);
        //scanLines.setLayoutY(21);
        contentPane.getChildren().addAll(flowGroup , scanLinesGroup ); // ,scanLines);
        
    }
    
    private final void setText( String text ) {
        //Text t = new Text(text);
        t.setText(text);
        //ObservableList<Node> children = flow.getChildren();
        //children.clear();
        //children.add(t);
    }

    @Override
    public void gameStateVignetteChanged(GameState gs) {
        // Load the new vignette text.
        setText(gs.getCurrentVignette().getNarration());
    }

    @Override
    public void gameStatePropertyChanged(GameState gs, String propKey) {
        // Nothing happens.
    }

    @Override
    public void gameStateShowDebug(GameState gs, boolean state) {}

    @Override
    public void gameStateTerminalChanged(GameState gs, BBSTerminal term) {}

    @Override
    public void gameStateDisplayChanged(GameState aThis, GameState.Display d) {}
    
    
}
