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
package com.maehem.abyss.engine.bbs.widgets;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *
 * @author mark
 */
public class BBSText extends Text {
    public static final Color FILL_COLOR = new Color(0.1, 1.0, 0.1, 1.0);
    public enum Shade { LIGHTER, NORMAL, DARKER }
    
    public BBSText( Font f, String text ) {
        this( f );
        setText(text);
    }
    
    public BBSText( Font f, String text, Shade shade) {
        this( f );
        setText(text);
        switch (shade) {
            case DARKER:
                setFill(FILL_COLOR.darker());
                break;
            case LIGHTER:
                setFill(FILL_COLOR.brighter());                
                break;
            default:
        }
    }

    public BBSText( Font f ) {
        super();
        setFill(FILL_COLOR);
        setFont(f);
    }
    
    /**
     * Convert a JavaFX Color to web hex.
     * 
     * @param c Color
     * @return web style hex string for color (i.e #FFFFFF )
     */
    public static String toHex(Color c) {
        int red = (int) (c.getRed() * 0xFF);
        String rr = Integer.toHexString(red);
 
        int green = (int) (c.getGreen() * 0xFF);
        String gg = Integer.toHexString(green);

        int blue = (int) (c.getBlue() * 0xFF);
        String bb = Integer.toHexString(blue);

        return "#" + rr + gg + bb;
    }
}
