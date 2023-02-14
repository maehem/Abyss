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

import static com.maehem.flatlinejack.Engine.LOGGER;
import java.util.logging.Level;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 *
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public class CrtTextPane2 extends ScrollPane {
    private final static double CRT_WIDTH = 1000;
    private final static double CRT_HEIGHT = 1000;
    private static final double CRT_FONT_H = 60.5;
    private static final String CRT_FONT_PATH = "/fonts/VT323-Regular.ttf";
    private final Font CRT_FONT = Font.loadFont(
            this.getClass().getResource(CRT_FONT_PATH).toExternalForm(),
            CRT_FONT_H
    );

    private static final double LINE_SMEAR = 5.0;  // Blur the lines.
    private static final double SCANS_PER_LINE = 11;
    private static final double SCREEN_LINE_SPACE = -CRT_FONT_H/SCANS_PER_LINE; // default is 0.0
    public static final Color SCREEN_BG_COLOR = new Color(0.15, 0.15, 0.15, 1.0);
    private static final Color SCREEN_FG_COLOR = new Color(0.1, 1.0, 0.1, 1.0);

    private final Text text = new Text();    
    private final TextFlow flow = new TextFlow(text);
    private final  StackPane contentPane = new StackPane();
    
    public CrtTextPane2(double width) {
        LOGGER.log(Level.INFO, "W:{0}  H:{1}", new Object[]{width, width});
        double ratio = 1.0;
        //double hClip = height*ratio;
        setFitToHeight(true);
        setFitToWidth(true);
        setHbarPolicy(ScrollBarPolicy.NEVER);
        setVbarPolicy(ScrollBarPolicy.NEVER);
        
        setContent(new Group(contentPane));
        setBackground(new Background(new BackgroundFill(SCREEN_BG_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));
        contentPane.setBackground(new Background(new BackgroundFill(
               SCREEN_BG_COLOR, CornerRadii.EMPTY, Insets.EMPTY
        )));
        
        flow.setBackground(new Background(
                new BackgroundFill(SCREEN_BG_COLOR, CornerRadii.EMPTY, Insets.EMPTY)
        ));
        flow.setLineSpacing(SCREEN_LINE_SPACE);
        flow.setPadding(new Insets(4, 16, 0, 16)); //  T,R,B,L
        flow.setTabSize(4);
        flow.setEffect(new GaussianBlur( LINE_SMEAR ));
        flow.setPrefSize(CRT_WIDTH, CRT_WIDTH*ratio);
        
        // We must clip the flow because alot of text can make
        // it's height longer than any requested limits.
        flow.setClip(new Rectangle(CRT_WIDTH, CRT_WIDTH*ratio));
        
        Group flowPane = new Group(flow);
        contentPane.getChildren().add(flowPane);
                
        text.setFill(SCREEN_FG_COLOR);
        text.setFont(CRT_FONT);
        text.setText("Narration Area.\n\tHello\nSuper long text stuff, I can see my house from here!\n" +
                "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ!@#$%^&*()_+{}[]|\\" +
                "\n" +
                "012 345678901234567 8901234567890 12345678901234 5678901234 567890123456789 0123456789" +
                "012345 678901234567 8901 234567890123 45678901234 567890123456789 012345678901 23456789" +
                "012345 67890123456 7890123456789012345678 9012345678901234567890123456789012 3456789" +
                "  89012345678901234567890 123456789012345678901 23456789012 3456789" +
                "012345678901234567 8901234567 89012345678 9012345 67890123456789012345678 90123456789" +
                "0123456789 0123456789012 345678901234567 89012345678901234567890 1234567890 123456789"
        );
        
        // Scan line negative space.
        WritableImage im = new WritableImage((int)CRT_WIDTH, (int)(CRT_WIDTH*ratio));
        PixelWriter pw = im.getPixelWriter();
        for (int y = 0; y < CRT_WIDTH*ratio; y += CRT_FONT_H/SCANS_PER_LINE) {
            for (int x = 0; x < CRT_WIDTH; x++) {
                if ( y > 0 ) {
                    pw.setColor(x, y-1, SCREEN_BG_COLOR);
                }
                pw.setColor(x, y, SCREEN_BG_COLOR);
            }
        }
        ImageView scanLines = new ImageView(im);
        scanLines.setEffect(new GaussianBlur(LINE_SMEAR/2.0));
        Group scanLinesGroup = new Group(scanLines);
        scanLines.setClip(new Rectangle(CRT_WIDTH, CRT_WIDTH*ratio));
        
        contentPane.getChildren().add(scanLinesGroup);
    }
    
    public void setText( String text ) {
        this.text.setText(text);
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        
        contentPane.setScaleX(getWidth()/CRT_WIDTH);
        contentPane.setScaleY(getWidth()/CRT_WIDTH);
    }
    
    
}
