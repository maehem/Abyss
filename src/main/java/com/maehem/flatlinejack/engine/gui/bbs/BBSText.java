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

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 *
 * @author mark
 */
public class BBSText extends Text {
    private static final String SCREEN_FONT = "/fonts/AcPlus_IBM_VGA_9x14.ttf";
    private static final Color SCREEN_FG_COLOR = new Color(0.1, 1.0, 0.1, 1.0);

    private final Font FONT;

    private final int rows;
    private final int cols;
    
    public BBSText( double fontHeight, int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        FONT = Font.loadFont(
            this.getClass().getResource(SCREEN_FONT).toExternalForm(),
            fontHeight
        );        
        setFill(SCREEN_FG_COLOR);
        setFont(FONT);
    }
    
    public int getRows() {
        return rows;
    }
    
    public int getCols() {
        return cols;
    }
}
