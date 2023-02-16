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

import java.util.ArrayList;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 *
 * @author mark
 */
public class BBSSimpleMenu extends VBox {
    private final Font font;
    
    public BBSSimpleMenu( Font f, ArrayList<BBSText> list ) {
        this.font = f;
        getChildren().addAll(list);
    }
    
}
