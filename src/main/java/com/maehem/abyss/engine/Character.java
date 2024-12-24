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
package com.maehem.abyss.engine;

import static com.maehem.abyss.Engine.LOGGER;
import java.io.InputStream;
import java.util.logging.Level;
import javafx.animation.AnimationTimer;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

/**
 *  // TODO: Unlink Character model from view
 *
 * @author Mark J Koch [@maehem on GitHub]
 */
public class Character extends Group {

    private static final String TALK_ICON_IMAGE_FILENAME = "/icons/talk-icon.png";

    public static final int INVENTORY_SIZE = 35;
    private static final double CAMEO_H = 120;

    private final Inventory inventory = new Inventory(INVENTORY_SIZE);

    private String name;
    private String accountId = "0000000"; // Only used by some characters.
    private PoseSheet poseSheet;
    private Rectangle feetBoundary;
    private Ellipse hearingBoundary;
    private final ImageView talkIcon = new ImageView();
    private boolean allowTalk = false;
    private boolean talking = false;

    private AnimationTimer aniTimer;
    private long lastTime = 0;
    private final long TWAIT = 40000000;

    //private final DialogScreen dialogScreen;
    private double originX;
    private double originY;
    //private ImageView cameoView;
    private Image cameoImage;

    public Character() {
        this("???");
    }

    public Character(String name) {
        LOGGER.log(Level.CONFIG, "{0}: Create character: {1}", new Object[]{getClass().getSimpleName(), name});
        this.name = name;

        // Fill the inventory with EmptyThing placeholders.
        for (int i = 0; i < INVENTORY_SIZE; i++) {
            inventory.add(new EmptyThing());
        }

        this.poseSheet = new PoseSheet(200);
        getChildren().add(this.poseSheet);
        getChildren().add(talkIcon);

        // set origin
        // Translate the image such that our image position
        // is relative to the feet.
        setOrigin(0.5, 0.9);

        setDefaultHearingBoundary();
        initFeetBoundary();
        initTalkIcon();

        //dialogScreen = new DialogScreen(this);
    }

    /**
     * Translate the character image so that the center (layoutX/Y) is at this
     * place in the image. Usually near the feet of the character.
     * setOrigin(0.5, 0.8) is a good starting point.
     *
     * @param x range 0.0-1.0 (left to right)
     * @param y range 0.0-1.0 (top to bottom(feet))
     */
    public final void setOrigin(double x, double y) {
        LOGGER.log(Level.FINE, "character set origin: {0},{1}", new Object[]{x, y});
        this.originX = x;
        this.originY = y;

        updateOrigin();
    }

    private void updateOrigin() {
        // Original width and height.
        double pH = getPoseSheet().getHeight();
        double pW = getPoseSheet().getWidth();

        // Scaled width and height.
        Bounds b = getBoundsInParent();
        double bW = b.getWidth();
        double bH = b.getHeight();
        //LOGGER.log(Level.CONFIG, "Parent Bounds for character: {0}x{1}\n", new Object[]{b.getWidth(), b.getHeight()});
        // After scaling, translate new X or Y position back to 0, then translate to desired originX/Y.
        setTranslateX(((-pW + bW) / 2 - bW * originX));
        setTranslateY(((-pH + bH) / 2 - bH * originY));

    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String id) {
        this.accountId = id;
    }

    private void setDefaultHearingBoundary() {
        double clipW = getPoseSheet().getWidth();
        double clipH = getPoseSheet().getHeight();

        getChildren().remove(getHearingBoundary());

        hearingBoundary = new Ellipse(clipW / 2, clipW / 4);
        getHearingBoundary().setCenterX(clipW / 2);
        getHearingBoundary().setCenterY(clipH - getHearingBoundary().getRadiusY());

        getHearingBoundary().setStrokeWidth(2.0);
        getHearingBoundary().setStroke(Color.HONEYDEW);
        getHearingBoundary().setFill(Color.TRANSPARENT);

        getChildren().add(getHearingBoundary());
        showHearingBounds(false);
    }

    /**
     * Move hearing boundary by X/Y
     * @param x
     * @param y
     */
    public final void adjustHearingBoundary(double x, double y) {
        getHearingBoundary().setCenterX(getHearingBoundary().getCenterX() + x);
        getHearingBoundary().setCenterY(getHearingBoundary().getCenterY() + y);
    }

    public void setAllowTalk(boolean allow) {
        LOGGER.log(Level.CONFIG, "Allow talk changed to: " + String.valueOf(allow));
        this.allowTalk = allow;
        this.setTalking(false);
    }

    final void initTalkIcon() {
        double clipW = getPoseSheet().getWidth();

        //talkIcon = new ImageView();
        talkIcon.setImage(new Image(getClass().getResourceAsStream(TALK_ICON_IMAGE_FILENAME)));
        talkIcon.setPreserveRatio(true);
        talkIcon.setFitWidth(clipW / 2);
        talkIcon.setX(clipW - talkIcon.getFitWidth());

        // Display the talk icon such that the pointy bit is at mouth level.
        talkIcon.setY(-talkIcon.getBoundsInLocal().getHeight() / 2);

        talkIcon.setOnMouseClicked((event) -> {
            LOGGER.log(Level.WARNING, getName() + ": Talk Icon clicked");
            event.consume();
            //if ( talkIcon.getOpacity() > 0.0 ) {
            if (canTalk()) {
                setTalking(true);
            } else {
                LOGGER.log(Level.CONFIG, "    but not allowed to talk.");
            }
            //}
        });

        showTalkIcon(false);
    }

    public boolean moveRight(int i, Shape safeZone) {
        // Try moving our shape to the new location.
        setLayoutX(getLayoutX() + i);

        // See if we still intersect with the allowable walking area.
        if (Shape.intersect(feetBoundary, safeZone).getBoundsInLocal().getWidth() > 0) {
            getPoseSheet().setDirection(PoseSheet.Direction.RIGHT);
            getPoseSheet().nextPose();
            return true;
        } else {
            setLayoutX(getLayoutX() - i); // Nope. Put it back
            return false;
        }
    }

    public boolean moveLeft(int i, Shape safeZone) {
        setLayoutX(getLayoutX() - i);
        if (Shape.intersect(feetBoundary, safeZone).getBoundsInLocal().getWidth() > 0) {
            getPoseSheet().setDirection(PoseSheet.Direction.LEFT);
            getPoseSheet().nextPose();
            return true;
        } else {
            setLayoutX(getLayoutX() + i); // Put it back
            return false;
        }
    }

    public boolean moveUp(int i, Shape safeZone) {
        setLayoutY(getLayoutY() - i);
        if (Shape.intersect(feetBoundary, safeZone).getBoundsInLocal().getWidth() > 0) {
            getPoseSheet().setDirection(PoseSheet.Direction.AWAY);
            getPoseSheet().nextPose();
            return true;
        } else {
            setLayoutY(getLayoutY() + i); // Put it back
            return false;
        }
    }

    public boolean moveDown(int i, Shape safeZone) {
        setLayoutY(getLayoutY() + i);
        if (Shape.intersect(feetBoundary, safeZone).getBoundsInLocal().getWidth() > 0) {
            getPoseSheet().setDirection(PoseSheet.Direction.TOWARD);
            getPoseSheet().nextPose();
            return true;
        } else {
            setLayoutY(getLayoutY() - i); // Put it back
            return false;
        }
    }

    /**
     * Scale - Negative scale flips Left-Right
     *
     * @param scale
     */
    public void setScale(double scale) {
        // TODO:  refactor feet origin into new scale
        setScaleX(scale);
        setScaleY(Math.abs(scale));
        updateOrigin();
    }

    public boolean colidesWith(Shape s) {
        //return feetBoundary.getBoundsInParent().intersects(s.getBoundsInParent());
        return Shape.intersect(feetBoundary, s).getBoundsInLocal().getWidth() > 0;
    }

    public boolean canHear(Shape s) {
        if (!canTalk()) {
            return false;
        }
        return Shape.intersect(getHearingBoundary(), s).getBoundsInLocal().getWidth() > 0;
    }

    public boolean canTalk() {
        return allowTalk;
    }

    public void showCollisionBounds(boolean show) {
        feetBoundary.setOpacity(show ? 0.5 : 0.0);
    }

    public void showHearingBounds(boolean show) {
        getHearingBoundary().setOpacity(show ? 0.5 : 0.0);
    }

    public void showTalkIcon(boolean show) {
        //LOGGER.log(Level.CONFIG, getName() + " showTalkIcon(" + String.valueOf(show) + ")");
        if (canTalk()) {
            talkIcon.setOpacity(show ? 1.0 : 0.0);
        } else {
            talkIcon.setOpacity(0.0);
        }
        if (canTalk() && !show) {
            setTalking(false);
        }
    }

    public void stopAnimating() {
        if (aniTimer != null) {
            aniTimer.stop();
        }
    }

    public void walkToward(double x, double y, Shape pBounds) {
        LOGGER.log(Level.FINEST,
                "Walk toward:   Start X,Y:  {0},{1}    Dest: {2},{3}",
                new Object[]{getLayoutX(), getLayoutY(), x, y}
        );
        // Set toward view.
        stopAnimating();
        aniTimer = new AnimationTimer() {

            @Override
            public void handle(long now) {
                if (now < lastTime + TWAIT) {
                    return;
                }
                //  If the movement would be less than the regular amount of
                // travel, then make them the same.
                if (Math.abs(getLayoutX() - x) < 12) {
                    setLayoutX(x);
                }
                if (Math.abs(getLayoutY() - y) < 4) {
                    setLayoutY(y);
                }

                // If both X and Y movements are at their final values, then
                // end the timer.
                if (getLayoutX() == x && getLayoutY() == y) {
                    aniTimer.stop();
                    aniTimer = null;
                }

                // Figure out if we are moving left or right
                if (getLayoutX() < x) {
                    if (moveRight(12, pBounds)) {
                        LOGGER.finest("Moved Right");
                    } else {
                        stopAnimating();
                    }
                } else if (getLayoutX() > x) {
                    if (moveLeft(12, pBounds)) {
                        LOGGER.finest("Moved Left");
                    } else {
                        stopAnimating();
                    }
                }

                // Figure out if we are moving up or down
                if (getLayoutY() < y) {
                    if (moveDown(4, pBounds)) {
                        LOGGER.finest("Moved Down");
                    } else {
                        stopAnimating();
                    }
                } else if (getLayoutY() > y) {
                    if (moveUp(4, pBounds)) {
                        LOGGER.finest("Moved Up");
                    } else {
                        stopAnimating();
                    }
                }

                lastTime = now;
            }
        };

        aniTimer.start();
    }

    public final void initFeetBoundary() {
        feetBoundary = new Rectangle();
        feetBoundary.setStroke(Color.LAWNGREEN);
        feetBoundary.setStrokeWidth(2.0);
        feetBoundary.setFill(Color.TRANSPARENT);

        setFeetBoundary();

        getChildren().add(feetBoundary);
    }

    private void setFeetBoundary() {
        double clipW = getPoseSheet().getWidth();
        double clipH = getPoseSheet().getHeight();

        double feetW = clipW / 4;
        double feetH = clipH / 25;

        //double feetX = clipW/2 - feetW/2;
        double feetX = clipW * originX - feetW / 2;
        //double feetY = clipH - feetH;
        double feetY = clipH * originY - feetH / 2;

        feetBoundary.setX(feetX);
        feetBoundary.setY(feetY);
        feetBoundary.setWidth(feetW);
        feetBoundary.setHeight(feetH);
    }

    /**
     * @return the talking
     */
    public boolean isTalking() {
        return talking;
    }

    /**
     * @param talking the talking to set
     */
    public void setTalking(boolean talking) {
        if (canTalk()) {
            if (talking != this.talking) {
                LOGGER.log(Level.INFO, "Talking to NPC [{0}] set to {1}", new Object[]{getName(), talking});
            }
            this.talking = talking;
        } else {
            if (talking) {
                LOGGER.log(Level.WARNING,
                        "Tried to set talk on character {0} they are not allowed to talk!", getName());
            }
            this.talking = false;
        }
    }

    /**
     * @return the hearingBoundary
     */
    public Ellipse getHearingBoundary() {
        return hearingBoundary;
    }

    /**
     * @return the poseSheetImage
     */
    public final PoseSheet getPoseSheet() {
        return poseSheet;
    }

    public void setSkin(InputStream resourceAsStream, int rows, int cols) {
        getPoseSheet().setSkin(resourceAsStream, rows, cols);
        setFeetBoundary();

        // todo reset listen bounds
        setDefaultHearingBoundary();
        initTalkIcon();
    }

    public void setCameo(InputStream is) {
        //cameoView = new ImageView(new Image(is));
        //cameoView.setFitHeight(CAMEO_H);
        //cameoView.setPreserveRatio(true);
        cameoImage = new Image(is);
        //dialogPane.setCameo(cameoImage);
    }

    public Image getCameo() {
        return cameoImage;
    }

    void setDirection(PoseSheet.Direction playerDir) {
        getPoseSheet().setDirection(playerDir);
    }

    public void nextPose() {
        getPoseSheet().nextPose();
    }

    public void useDefaultSkin() {
        getPoseSheet().setDefaultSheet();
        setFeetBoundary();
        initTalkIcon();
    }

    /**
     * @return the name of this character
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the character name to set
     */
    public final void setName(String name) {
        this.name = name;
    }

//    /**
//     * @return the inventory
//     */
//    public Thing[] getInventory() {
//        return inventory;
//    }
    public Inventory getInventory() {
        return inventory;
    }

    public void setAInventory(int index, Thing thing) {
        inventory.set(index, thing);
    }

    public boolean isInventoryFull() {
        // Fill the inventory with EmptyThing placeholders.
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i) instanceof EmptyThing) {
                return false;
            }
        }
        return true;
    }

    /**
     * Transfer an item from one character/player to another.
     *
     * @param thing to give away
     * @param character to receive the item
     */
    public void give(Thing thing, Character character) {
        //getInventory().remove(thing);
        character.addInventoryItem(thing);
    }

    /**
     * Add a thing to inventory.
     *
     * @param thing to be added
     */
    public void addInventoryItem(Thing thing) {
//        for ( int i=0; i<inventory.length; i++) {
//            if ( inventory[i] == null ) {
//                inventory[i] = thing;
//                break;
//            }
//        }
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i) instanceof EmptyThing) {
                LOGGER.log(Level.INFO,
                        "Inventory Item: {0} changed from EmptyThing to {1}",
                        new Object[]{i, thing.getClass().getSimpleName()}
                );
                inventory.set(i, thing);
                break;
            }
        }
        //getInventory().add(thing);
    }

    /**
     * Used by Player to remove non-used talk icon.
     */
    protected void removeTalkIcon() {
        getChildren().remove(talkIcon);
    }

}
