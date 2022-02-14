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
package com.maehem.flatlinejack.content;

import com.maehem.flatlinejack.engine.Patch;
import com.maehem.flatlinejack.engine.Player;
import com.maehem.flatlinejack.engine.Port;
import com.maehem.flatlinejack.engine.PoseSheet;
import com.maehem.flatlinejack.engine.Vignette;
import com.maehem.flatlinejack.engine.Character;
import com.maehem.flatlinejack.engine.babble.DialogResponse;
import com.maehem.flatlinejack.engine.babble.DialogResponseAction;
import com.maehem.flatlinejack.engine.babble.DialogSheet;
import com.maehem.flatlinejack.content.things.KomodoDeckThing;
import java.util.ArrayList;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Polygon;

/**
 *
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public class PawnShopVignette extends Vignette {

    //private static final String TITLE = "Pawn Shop";

    private static final String PROP_NAME = "pawnshop";
    public static final double PLAYER_START_X = 200;
    public static final double PLAYER_START_Y = 360;
    private static final String CONTENT_BASE = "/content/vignette/pawn-shop/";
    //private static final String BACKGROUND_IMAGE_FILENAME = CONTENT_BASE + "/backdrop.png";
    private static final String COUNTERS_IMAGE_FILENAME   = CONTENT_BASE + "counters.png";
    private static final String DOOR_PATCH_IMAGE_FILENAME = CONTENT_BASE + "patch-left.png";
    private static final String BROKER_POSE_SHEET_FILENAME = CONTENT_BASE + "pawn-broker-pose-sheet.png";
    private static final Polygon WALK_AREA = new Polygon(
            200, 380,
            300, 380,
            330, 488,
            800, 488,
            960, 700,
            50, 700,
            100, 400,
            20, 400,
            20, 372,
            140, 372
    );

    private static final Patch p = new Patch(0, 0, 375, PawnShopVignette.class.getResourceAsStream(DOOR_PATCH_IMAGE_FILENAME));
    private static final Port leftDoor = new Port(
            20, 372,
            30, 28,
            880, 400, PoseSheet.Direction.LEFT,
            "StreetVignette2");

    private Character shopOwnerCharacter;
    private int shopOwnerAnimationCount = 0;
    
    private ResourceBundle bundle;

    public PawnShopVignette(int w, int h, Port prevPort, Player player) {
        super(w, h, CONTENT_BASE, prevPort, player);
        // Don't put things here.  Use init().
    }

    @Override
    protected void init() {
        // Called during super class initialization.
        bundle = ResourceBundle.getBundle("content.messages.PawnShop");
        setName(bundle.getString("title"));
        getPlayer().setLayoutX(PLAYER_START_X);
        getPlayer().setLayoutY(PLAYER_START_Y);
        // Add narration bundle string here.
        getNarrationPane().setText(bundle.getString("narration"));
        
        
        initBackground();
        initMainArea();
        initPatches();
        initForeground();

    }

    private void initShopOwner() {
        shopOwnerCharacter = new Character(bundle.getString("character.eddie.name"));
        shopOwnerCharacter.setScale(1.2);
        shopOwnerCharacter.setLayoutX(600);
        shopOwnerCharacter.setLayoutY(350);

        // TODO:   Check that file exists.  The current exception message is cryptic.
        shopOwnerCharacter.setSkin(PawnShopVignette.class.getResourceAsStream(BROKER_POSE_SHEET_FILENAME), 1, 4);
        log.config("Add skin for pawn shop owner." + BROKER_POSE_SHEET_FILENAME);
        // Dialog
        // Load dialog tree from file.
        shopOwnerCharacter.getDialog().init(getWidth(), getHeight());

        initShopOwnerDialog();

        getCharacterList().add(shopOwnerCharacter);
    }

    private void initBackground() {
//        // Load images for BACKGROUND_IMAGE_FILENAME.
//        final ImageView backDrop = new ImageView();
//        backDrop.setImage(new Image(PawnShopVignette.class.getResourceAsStream(BACKGROUND_IMAGE_FILENAME)));

        initShopOwner();

        // Counters (in front of shop owner )
        final ImageView counterView = new ImageView();
        counterView.setImage(new Image(PawnShopVignette.class.getResourceAsStream(COUNTERS_IMAGE_FILENAME)));
        counterView.setLayoutX(getWidth() - counterView.getImage().getWidth());
        counterView.setLayoutY(getHeight() - counterView.getImage().getHeight());
        counterView.setBlendMode(BlendMode.MULTIPLY);

        // Add these in visual order.  Back to front.
        getBgGroup().getChildren().addAll(
               // backDrop,
                shopOwnerCharacter,
                counterView);
    }

    private void initMainArea() {

        // Character
        //getPlayer().setLayoutX(getWidth() / 2); //  Near middle
        //getPlayer().setLayoutY(2 * getHeight() / 4);  // Head about a 1/3 down

        // Walk area
        setWalkArea(WALK_AREA);

        // Doors
        getDoors().add(leftDoor);

        getMainGroup().getChildren().addAll(
                getPlayer(),
                leftDoor.getTrigger()
        );
    }

    private void initForeground() {

        // Depth of field
        //bgGroup.setEffect(new BoxBlur(10, 10, 3));
    }

    private void initPatches() {

        getPatchList().add(p);
        getFgGroup().getChildren().add(p);
        getFgGroup().getChildren().add(p.getBox());
    }

    @Override
    public Port processEvents(ArrayList<String> input) {
        Port nextRoom = super.processEvents(input);
        if (nextRoom != null) {
            return nextRoom;
        }
        // Simulate perspective when player walks closer/further from view.
        // TODO: an overidable method in @Vignette that calls this.
        // TODO: default and setable values for scale and trim.
        //getPlayer().setScale((8 * getPlayer().getLayoutY() / getHeight()) - 2.4);
        getPlayer().setScale((5.2 * getPlayer().getLayoutY() / getHeight()) - 1.15);

        shopOwnerAnimationCount++;
        if (shopOwnerAnimationCount > 40) {
            shopOwnerAnimationCount = 0;
            shopOwnerCharacter.nextPose();
        }

        // Player within shopOwner talkBlock and facing shopOwner
        // makes floating dialog target visible.
        return nextRoom;
    }

    private void initShopOwnerDialog() {

        DialogResponseAction exitAction = () -> {
            shopOwnerCharacter.getDialog().setExit(leftDoor);
            shopOwnerCharacter.getDialog().setActionDone(true);
            
            
            // TODO:
            // Add cyberspace deck to inventory.
            shopOwnerCharacter.give(new KomodoDeckThing(), getPlayer());
            
            // TODO:
            // GameState set StreetVignette PawnShop door locked.
        };

        DialogSheet ds4 = new DialogSheet(shopOwnerCharacter.getDialog());
        //MessageFormat mf4 = new MessageFormat(bundle.getString("dialog.eddie.ds4"));
        ds4.setDialogText(bundle.getString("dialog.eddie.ds4"));
        ds4.addResponse(new DialogResponse(bundle.getString("dialog.p.ds4.1"), exitAction)); // Exit action

        DialogSheet ds3 = new DialogSheet(shopOwnerCharacter.getDialog());
        //MessageFormat mf3 = new MessageFormat("Pay up.  It's $100.");
        ds3.setDialogText(bundle.getString("dialog.eddie.ds3"));
        ds3.addResponse(new DialogResponse(bundle.getString("dialog.p.ds1.3"), ds4));

        DialogSheet ds2 = new DialogSheet(shopOwnerCharacter.getDialog());
        //MessageFormat mf2 = new MessageFormat(bundle.getString("dialog.eddie.ds2"));
        ds2.setDialogText(bundle.getString("dialog.eddie.ds2"));
        ds2.addResponse(new DialogResponse(bundle.getString("dialog.p.ds1.2"), ds3));
        ds2.addResponse(new DialogResponse(bundle.getString("dialog.p.ds1.3"), ds4));

        DialogSheet ds1 = new DialogSheet(shopOwnerCharacter.getDialog());
        //MessageFormat mf1 = new MessageFormat("Ah! You back?\nI have your deck.");
        ds1.setDialogText(bundle.getString("dialog.eddie.ds1"));
        ds1.addResponse(new DialogResponse(bundle.getString("dialog.p.ds1.1"), ds2));
        ds1.addResponse(new DialogResponse(bundle.getString("dialog.p.ds1.2"), ds3));
        ds1.addResponse(new DialogResponse(bundle.getString("dialog.p.ds1.3"), ds4));

        shopOwnerCharacter.getDialog().addDialogSheet(ds1);
        shopOwnerCharacter.getDialog().addDialogSheet(ds2);
        shopOwnerCharacter.getDialog().addDialogSheet(ds3);
        shopOwnerCharacter.getDialog().addDialogSheet(ds4);

    }

    @Override
    public String getPropName() {
        return PROP_NAME;
    }

    @Override
    public Properties saveProperties() {
        Properties p = new Properties();
//        p.setProperty(PROPERTY_CONDITION, condition.toString());
        
        return p;
    }

    @Override
    public void loadProperties(Properties p) {
        // Empty for now
        //setCondition(Integer.valueOf(p.getProperty(PROPERTY_CONDITION, String.valueOf(CONDITION_DEFAULT))));
    }
}