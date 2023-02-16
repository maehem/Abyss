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
package com.maehem.abyss.engine.bbs;

import com.maehem.abyss.engine.bbs.widgets.BBSTextFlow;
import com.maehem.abyss.engine.bbs.widgets.BBSText;
import com.maehem.abyss.engine.bbs.widgets.BBSGotoButton;
import com.maehem.abyss.engine.bbs.widgets.BBSSimpleMenuItem;
import com.maehem.abyss.engine.bbs.widgets.BBSHeader;
import static com.maehem.abyss.Engine.LOGGER;
import static com.maehem.abyss.engine.bbs.BBSTerminal.FONT;

import com.maehem.abyss.engine.GameState;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.logging.Level;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/**
 *
 * @author mark
 */
public class HelpSystem extends BBSTerminal {
    private static final String BACK_BTN_TXT = "\u25c4\u25c4\u25c4 BACK";
    private static final String DONE_BTN_TXT = "DONE";
    
    private final String bPath = "messages.bbs.help";
    private String currentKey = "help";
    private final GameState gameState;
    private final BBSTerminal parent;
    private final Stack<String> returnTo = new Stack<>();
    
    public HelpSystem(GameState gs, BBSTerminal parent) {
        super(gs);
        this.gameState = gs;
        this.parent = parent;
        
        setHeader(new BBSHeader(FONT, SiteHeader.HELP_SYSTEM));
        updateContent(gs);
        setFooter(new BBSText(FONT,
                  "City Public               "
                + "                 Help System"
        ));
    }
    
    @Override
    public final void updateContent(GameState gs) {
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(bPath);
            BBSText titleText = new BBSText(FONT, 
                    bundle.getString(currentKey + "." + "title"),
                    BBSText.Shade.LIGHTER
            );
            
            BBSTextFlow text = new BBSTextFlow();
            text.getChildren().add(new BBSText(FONT, "\n"));
            BBSText bodyText = new BBSText(FONT, 
                    bundle.getString(currentKey + "." + "body"),
                    BBSText.Shade.DARKER
            );
            text.getChildren().add(bodyText);
            text.getChildren().add(new BBSText(FONT, "\n"));
            text.setTextAlignment(TextAlignment.JUSTIFY);
            
            VBox subMenu = new VBox();
            // If this help section has a sub-menu, set it up
            for ( int i=1; i<10; i++ ) {
                try {
                    String mTitle = bundle.getString(currentKey + "." + i + "." + "title");
                    subMenu.getChildren().add(createItem( "\u25ba " + mTitle, currentKey + "." +i));
                } catch ( MissingResourceException ex ) {
                    subMenu.getChildren().add(new BBSText(FONT));
                }
            }
            
            // Add DONE Button
            BBSGotoButton doneButton = new BBSGotoButton(FONT, 
                    returnTo.empty()?DONE_BTN_TXT:BACK_BTN_TXT
            );
            doneButton.setOnMouseClicked((t) -> {
                if ( returnTo.empty() ) {
                    // Exit Help Menu
                    gs.setCurrentTerminal(parent);
                } else {
                    String pop = returnTo.pop();
                    currentKey = pop;
                    this.updateContent(gs);
                }
            });
            
            VBox content = new VBox(
                    BBSTerminal.centeredNode(titleText), 
                    text, 
                    subMenu, 
                    BBSTerminal.centeredNode(doneButton) 
            );
            
            setBody(content);
            
            
        } catch (MissingResourceException ex) {
            LOGGER.log(Level.WARNING,
                    "Unable to locate help system resource bundle at: {0}", 
                    bPath
            );

            // TODO:  maybe load a default bundle here.
            throw ex;
        }
    }

    private BBSSimpleMenuItem createItem( String text, String destKey ) {
        BBSSimpleMenuItem item = new BBSSimpleMenuItem(FONT, text);
        item.setOnMouseClicked((t) -> {
            returnTo.push(currentKey);
            // Update current key
            this.currentKey = destKey;
            // Update current return
            updateContent(gameState);
        });
        
        return item;
    }

}
