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
import java.util.Properties;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Polygon;

/**
 *
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public class PawnShopVignette extends Vignette {

    private static final String PROP_NAME = "pawnshop";
    public static final double PLAYER_START_X = 200;
    public static final double PLAYER_START_Y = 400;
    private static final String CONTENT_BASE = "/content/vignette/pawn-shop/";
    private static final String COUNTERS_IMAGE_FILENAME   = CONTENT_BASE + "counters.png";
    private static final String DOOR_PATCH_IMAGE_FILENAME = CONTENT_BASE + "patch-left.png";
    private static final String BROKER_POSE_SHEET_FILENAME = CONTENT_BASE + "pawn-broker-pose-sheet.png";
    private static final Polygon WALK_AREA = new Polygon(
            200, 380,
            300, 380,
            330, 440,
            800, 440,
            960, 600,
            50, 600,
            100, 420,
            20, 400,
            20, 360,
            140, 360
    );

    private static final Patch leftDoorPatch = new Patch(
            0, 0, 425, 
            PawnShopVignette.class.getResourceAsStream(DOOR_PATCH_IMAGE_FILENAME)
    );
    private static final Port leftDoor = new Port(
            30, 360,  // port XY location
            40, 40,   // port size
            920, 470, // place player at this XY when they leave the pawn shop.
            PoseSheet.Direction.TOWARD,
            "StreetVignette2"  // this door leads to the StreetVignette2 scene
    );

    private Character shopOwnerCharacter;
    private int shopOwnerAnimationCount = 0;
    
    /**
     * 
     * @param w width of scene
     * @param h height of scene
     * @param prevPort where the player came from
     * @param player the @Player
     */
    public PawnShopVignette(int w, int h, Port prevPort, Player player) {
        super(w, h, CONTENT_BASE, prevPort, player);
        // Don't put things here.  Override @init() which is called shortly after creation.
    }

    @Override
    protected void init() {
        setHorizon(0.2);
        getPlayer().setLayoutX(PLAYER_START_X);
        getPlayer().setLayoutY(PLAYER_START_Y);

        initShopOwner();        
        initBackground();
        
        setWalkArea(WALK_AREA);  // Walk area
        addPort(leftDoor);
        addPatch(leftDoorPatch);
        
        // example Depth of field
        //fgGroup.setEffect(new BoxBlur(10, 10, 3));
    }

    private void initShopOwner() {
        shopOwnerCharacter = new Character(bundle.getString("character.eddie.name"));
        shopOwnerCharacter.setScale(1.2);
        shopOwnerCharacter.setLayoutX(600);
        shopOwnerCharacter.setLayoutY(350);

        // TODO:   Check that file exists.  The current exception message is cryptic.
        shopOwnerCharacter.setSkin(PawnShopVignette.class.getResourceAsStream(BROKER_POSE_SHEET_FILENAME), 1, 4);
        log.config("Add skin for pawn shop owner. " + BROKER_POSE_SHEET_FILENAME);
        // Dialog
        // Load dialog tree from file.
        shopOwnerCharacter.getDialog().init(getWidth(), getHeight());

        initShopOwnerDialog();

        getCharacterList().add(shopOwnerCharacter);
        getBgGroup().getChildren().add(
                shopOwnerCharacter
        );
    }

    private void initBackground() {

        // Display Cases (in front of shop owner )
        final ImageView counterView = new ImageView();
        counterView.setImage(new Image(PawnShopVignette.class.getResourceAsStream(COUNTERS_IMAGE_FILENAME)));
        counterView.setLayoutX(getWidth() - counterView.getImage().getWidth());
        counterView.setLayoutY(getHeight() - counterView.getImage().getHeight());
        //counterView.setBlendMode(BlendMode.MULTIPLY);

        // Add these in visual order.  Back to front.
        getBgGroup().getChildren().add( counterView );
    }

    @Override
    public void loop() {
        // animate shop owner.
        shopOwnerAnimationCount++;
        if (shopOwnerAnimationCount > 40) {
            shopOwnerAnimationCount = 0;
            shopOwnerCharacter.nextPose();
        }

    }

    // TODO:  Ways to automate this.   JSON file?
    private void initShopOwnerDialog() {

        // Eddie kicks the player out of the shop but gives him his item.
        DialogResponseAction exitAction = () -> {
            shopOwnerCharacter.getDialog().setExit(leftDoor);
            shopOwnerCharacter.getDialog().setActionDone(true);
            
            // Add cyberspace deck to inventory.
            shopOwnerCharacter.give(new KomodoDeckThing(), getPlayer());
            
            // TODO:
            // GameState set StreetVignette PawnShop door locked.
        };

        DialogSheet ds4 = new DialogSheet(shopOwnerCharacter.getDialog());
        ds4.setDialogText(bundle.getString("dialog.eddie.ds4"));
        ds4.addResponse(new DialogResponse(bundle.getString("dialog.p.ds4.1"), exitAction)); // Exit action

        DialogSheet ds3 = new DialogSheet(shopOwnerCharacter.getDialog());
        ds3.setDialogText(bundle.getString("dialog.eddie.ds3"));
        ds3.addResponse(new DialogResponse(bundle.getString("dialog.p.ds1.3"), ds4));

        DialogSheet ds2 = new DialogSheet(shopOwnerCharacter.getDialog());
        ds2.setDialogText(bundle.getString("dialog.eddie.ds2"));
        ds2.addResponse(new DialogResponse(bundle.getString("dialog.p.ds1.2"), ds3));
        ds2.addResponse(new DialogResponse(bundle.getString("dialog.p.ds1.3"), ds4));

        DialogSheet ds1 = new DialogSheet(shopOwnerCharacter.getDialog());
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
