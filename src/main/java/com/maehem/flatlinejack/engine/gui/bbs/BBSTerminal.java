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
package com.maehem.flatlinejack.engine.gui.bbs;

import com.maehem.flatlinejack.engine.GameState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 *  Good source of reference stuff:  https://int10h.org/oldschool-pc-fonts/
 * 
 *   Great tool for editing BBS headers:  https://texteditor.com
 * 
 *   ASCII Art:   http://chris.com/ascii
 * 
 * @author mark
 */
public class BBSTerminal extends BorderPane {
    public static final double LINE_SPACE = 0.0; // default is 0.0
    private static final Color BG_COLOR = new Color(0.2, 0.2, 0.2, 1.0);
    public static final double FONT_H = 32;
    private static final String FONT_FILE = "/fonts/AcPlus_IBM_VGA_9x14.ttf";
    public static final Font FONT= Font.loadFont(
            BBSTerminal.class.getResourceAsStream(FONT_FILE),
            FONT_H
        );

    //private BBSHeader header = new BBSHeader();
    //private final BBSTextFlow body = new BBSTextFlow();
    //private final BBSTextFlow footer = new BBSTextFlow();
    
//    private final int rows;
//    private final int cols;
    
    //private final double scale;

    public BBSTerminal(GameState gs /*, int width, int height , int rows, int cols */) {
//        this.rows = rows;
//        this.cols = cols;
        //setPrefSize(width, height);
        
        setPadding(new Insets(16, 90, 16, 90)); //  T,R,B,L
        setBackground(new Background(
                new BackgroundFill(BG_COLOR, 
                        new CornerRadii(80), 
                        Insets.EMPTY)
        ));
        setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(4))));
        
        //setEffect(new GaussianBlur( 3));
        
//        header = new BBSText(FONT_H, 9, cols);
//        body = new BBSText(FONT_H,  14, cols);
//        footer = new BBSText(FONT_H, 1, cols);
        
        ///setBody("1\n2\n3\n4\n5");
        //setFooter("01234567890123456789012345678901234567890123456789012345678901234567890123456789");
        
        //getChildren().addAll(header,body,footer);
    }
    
    public void setHeader( Node header ) {
        setTop(header);
    }
    
    public void setBody( Node body ) {
        setCenter(body);
    }
    
    public void setFooter( Node footer ) {
        setBottom(footer);
    }
    
    public static Node centeredNode( Node content ) {
        HBox centerBox = new HBox(content);
        centerBox.setAlignment(Pos.CENTER);
        VBox cBox = new VBox(centerBox);
        cBox.setAlignment(Pos.CENTER);

        return cBox;
    }
//    public void setHeader( String text ) {
//        setText(header, text);
//    }
//    
//    public void setBody( String text ) {
//        setText(body,  text);
//    }
//    
//    public void setFooter( String text ) {
//        setText(footer, text);
//    }
    
    /**
     * Update Text element with text. Limiting/padding to n-lines.
     * Width is set from constant.
     * 
     * @param text
     * @param lines
     * @return 
     */
//    private void setText( BBSText t, String textString ) { //, int lines ) {
//        int count = (int)(textString.lines().count());
//        int rows = getRows();
//        StringBuilder sb = new StringBuilder();
//        textString.lines().limit(rows).forEach((tt) -> {
//            sb.append(tt);
//            if ( rows > 1 ) sb.append("\n");
//        });
//        if ( count < t.getRows() ) {
//            for ( int i=count; i<rows; i++) {
//                sb.append("***\n");
//            }
//        }
//        
//        t.setText(sb.toString());
//    }
    public void updateContent(GameState gs) {}
}
