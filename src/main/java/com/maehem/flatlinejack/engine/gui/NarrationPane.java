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

import static com.maehem.flatlinejack.Engine.log;
import java.io.InputStream;
import java.util.logging.Level;
import javafx.animation.AnimationTimer;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.transform.Scale;

/**
 *
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public class NarrationPane extends BorderPane {

    private static final String SCREEN_FONT = "/fonts/VT323-Regular.ttf";
    private static final String SCREEN_BEZEL = "/ui/crt-bezel-2.png";
    private static final double SCREEN_FONT_H = 37;
    private static final double SCREEN_LINE_SPACE = -7.17; // default is 0.0
    private static final Color SCREEN_BG_COLOR = new Color(0.1, 0.1, 0.1, 1.0);
    private static final Color SCREEN_FG_COLOR = new Color(0.1, 1.0, 0.1, 1.0);
    private final TextFlow flow = new TextFlow();
    private final Group textGroup = new Group(flow);
    private final CrtLabelPane crtLabelPane = new CrtLabelPane();
    private double menuPopY = 0;
    public final static double MENU_TAB_SHOW = 80;
    private final static double MENU_HEIGHT = 400;
    private final static double MENU_WIDTH = 1024;
    private final static double MENU_POP_Y_MAX = MENU_HEIGHT - MENU_TAB_SHOW - 8;
    private final static double MENU_POP_Y_INCR = 12;
    private final Font font = Font.loadFont(
            this.getClass().getResource(SCREEN_FONT).toExternalForm(),
            SCREEN_FONT_H
    );
    private final double scale;

    public NarrationPane(double width) {

        scale = width/MENU_WIDTH;
        
        // Create a custom transform for this pane that has the scaling
        // pivot point at the same location as the layoutX/Y.
        Scale xf = new Scale();
        xf.setPivotX(0);
        xf.setPivotY(0);
        getTransforms().add(xf);
        xf.setX(scale);
        xf.setY(scale);
        
        
        setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        InputStream is = getClass().getResourceAsStream(SCREEN_BEZEL);
        if ( is == null ) {
            log.log(Level.SEVERE, "Cannot find image for: {0}", SCREEN_BEZEL);
            return;
        }
        
        Image bezelImage = new Image(is);
        ImageView bezView = new ImageView(bezelImage);
        bezView.setPreserveRatio(true);
        bezView.setFitWidth(MENU_WIDTH);
        bezView.setSmooth(false);

        crtLabelPane.setLayoutX(MENU_WIDTH/2 - CrtLabelPane.LABEL_WIDTH/2);
        crtLabelPane.setLayoutY(5);

        this.setPadding(new Insets(16));
        
        // Text flow
        int pad = 60; // crt bezel thickness
        flow.setPrefSize(MENU_WIDTH-pad*2, MENU_HEIGHT);
        flow.setLayoutX(pad);
        flow.setLayoutY(pad);
        flow.setBackground(new Background(new BackgroundFill(SCREEN_BG_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));
        flow.setLineSpacing(SCREEN_LINE_SPACE);
        flow.setPadding(new Insets(80, 90, 0, 90)); //  T,R,B,L
        setText("Narration Area");
                
        WritableImage im = new WritableImage((int)MENU_WIDTH, (int)MENU_HEIGHT);
        PixelWriter pw = im.getPixelWriter();
        for (int y = 0; y < MENU_HEIGHT; y += 3) {
            for (int x = 0; x < MENU_WIDTH; x++) {
                pw.setColor(x, y, SCREEN_BG_COLOR);
                //pw.setColor(x, y, Color.MAGENTA);  // For calibration
            }
        }
        ImageView scanLines = new ImageView(im);
        //scanLines.setPreserveRatio(true);
        scanLines.setLayoutX(20);
        scanLines.setLayoutY(21);
        textGroup.getChildren().addAll(scanLines, bezView, crtLabelPane);
        
        setCenter(textGroup);        
        setPrefSize(MENU_WIDTH, MENU_HEIGHT);
        //setScaleX(scale);
        //setScaleY(scale);
        initMenuPopper();

    }

    public void pop() {
        // Start with the narration window popped and roll it down after
        // a few seconds.
        getOnMouseEntered().handle(null);

        AnimationTimer timer = new AnimationTimer() {
            int delay = 400;

            @Override
            public void handle(long now) {
                delay--;

                if (delay <= 0) {
                    this.stop();
                    getOnMouseExited().handle(null);
                }
            }
        };
        timer.start();
    }
    
    public final void setText( String text ) {
        Text t = new Text(text);
        t.setFill(SCREEN_FG_COLOR);
        t.setFont(font);
        ObservableList<Node> children = flow.getChildren();
        children.clear();
        children.add(t);
    }
    
    /**
     * Animations for popping menu up and down when user moves mouse over it.
     *
     */
    private void initMenuPopper() {
        setOnMouseEntered((event) -> {
            AnimationTimer timer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    if (menuPopY <= MENU_POP_Y_MAX ) {
                        menuPopY += MENU_POP_Y_INCR ;
                        setTranslateY(-menuPopY * scale);  // Negative as we are sliding upward.
                    } else {
                        this.stop();
                    }
                }
            };
            timer.start();
        });

        setOnMouseExited((event) -> {
            AnimationTimer timer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    if (menuPopY > 0) {
                        menuPopY -= MENU_POP_Y_INCR;
                        setTranslateY(-menuPopY * scale);  // Negative as we are sliding upward.
                    } else {
                        this.stop();
                    }
                }
            };
            timer.start();
        });
    }

    public void setTitle(String text) {
        crtLabelPane.setTitle(text);
    }
//    /**
//     * @return the titleText
//     */
//    public Text getTitleText() {
//        return titleText;
//    }

//    /**
//     * @return the textArea
//     */
//    public TextArea getTextArea() {
//        return textArea;
//    }

}
