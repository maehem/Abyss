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
package com.maehem.flatlinejack;

import com.maehem.flatlinejack.engine.Port;
import com.maehem.flatlinejack.engine.Loop;
import com.maehem.flatlinejack.engine.Player;
import com.maehem.flatlinejack.engine.Vignette;
import com.maehem.flatlinejack.engine.GameState;
import com.maehem.flatlinejack.engine.GameStateListener;
import com.maehem.flatlinejack.engine.gui.ChipsConfiguratorPane;
import com.maehem.flatlinejack.engine.gui.InventoryPane;
import com.maehem.flatlinejack.engine.gui.CrtTextPane;
import com.maehem.flatlinejack.engine.gui.GameControlsPane;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *
 * @author Mark J Koch [flatlinejack at maehem dot com]
 */
public class Engine extends Application implements GameStateListener {

    //private double SCALE = 0.75;
    private static final double SCALE = 0.66;
    
    private CrtTextPane narrationPane;
    private GameControlsPane gameControls;
    private ChipsConfiguratorPane chipsPane;

    public static final Logger log = Logger.getLogger("flatline");

    private static final int PRELOADER_SHOWTIME_MILLIS = 2000;
    // This is a class name.
    private static final String STARTING_VIGNETTE = "StreetVignette2";

    //private GUI gui;
    private InventoryPane inventoryPane;
    private Stage window;
    private Loop loop;   // Game logic Loop
    
    private final Group vignetteGroup = new Group();
    private final StackPane topArea = new StackPane();
    private final HBox bottomArea = new HBox();  // gui and naration
    private final VBox gamePane = new VBox(new Group(topArea), new Group(bottomArea));
    
    private final StackPane root = new StackPane(gamePane);
    private final Scene scene = new Scene(root); //, 1280, 920);

    
    private final GameState gameState = new GameState();
    
    
    // TODO:   Music track system
    //          - Blend music scene to scene
    
//    static {
//          System.setProperty("java.util.logging.SimpleFormatter.format",
//                  "[%1$tF %1$tT %1$tL] [%4$-7s] %5$s%n");
//      }
    static {
          System.setProperty("java.util.logging.SimpleFormatter.format",
                  "[%4$-7s] %5$s%n");
     }

    @Override
    public void start(Stage window) {
        this.window = window;
        getGameState().addListenter(this);
        
        configureLogging();
        
        ImageView splashScreen = new ImageView(new Image(getClass().getResourceAsStream("/content/splash.png")));
        gameControls = new GameControlsPane(getGameState(), Vignette.NATIVE_WIDTH/2);
        narrationPane = new CrtTextPane(getGameState(), Vignette.NATIVE_WIDTH/2);
        
        bottomArea.getChildren().addAll(gameControls ,narrationPane  );

        // Game Panes
        // Inventory
        inventoryPane = new InventoryPane(gameState.getPlayer());
        inventoryPane.setVisible(gameState.inventoryShowing());
        
        // Chips  -- Confgurable Buffs
        chipsPane = new ChipsConfiguratorPane(Vignette.NATIVE_WIDTH, Vignette.NATIVE_HEIGHT);
        chipsPane.setVisible(gameState.chipsShowing());
        
        // ROM Adviser -- Hint System and Third Hand
        
        // Terminal -- Base BBS style system
        
        // Deck Bench -- Configure your Deck with inventory components
        
        // Cyberspace  -- ROM Helper replaces Narration Window

        topArea.getChildren().addAll(splashScreen , vignetteGroup, chipsPane, inventoryPane);
        
        configureGuiLayout();
        
        getGameState().load(STARTING_VIGNETTE);
        getPlayer().loadState(getGameState());
        
        String roomName = getGameState().getProperty(
                GameState.PROP_CURRENT_VIGNETTE, 
                STARTING_VIGNETTE
        );

        // Load the starting room.
        notifyVignetteExit(new Port(roomName));  // Just leveraging the Room Loading System here.

        initHotKeys();
        initKeyInput();

        window.setScene(this.scene);
        window.setResizable(false);
        //quit when the window is close().
        window.setOnCloseRequest(e -> Platform.exit());
        window.show();
        
        getInventoryPane().setVisible(false);
        getInventoryPane().setLayoutX(getScene().getWidth()/2 - getInventoryPane().getWidth()/2);
        getInventoryPane().setLayoutY(getScene().getHeight()/2 - getInventoryPane().getHeight()/2);

    }

    private void setVignette(Vignette v) {
        //root.getChildren().remove(currentVignette);
        //topArea.getChildren().remove(currentVignette);
        Vignette currentVignette = gameState.getCurrentVignette();
        
        vignetteGroup.getChildren().remove(currentVignette);
        gameState.setCurrentVignette(v);
        
        //this.currentVignette = v;
       //v.setLayoutX(-200);

        v.loadState(gameState);
        
        //root.getChildren().add(v);
        //gamePane.getChildren().add(0, v);
        //topArea.getChildren().add(0, v);
        vignetteGroup.getChildren().add(0, v);
        
        window.setTitle(v.getName());

        loop = new Loop(this, v);
        loop.start();

    }

    @Override
    public void init() throws Exception {
        long start = System.currentTimeMillis();

        // time consuming initializations
        long duration = System.currentTimeMillis() - start;
        long remainingShowTime = PRELOADER_SHOWTIME_MILLIS - duration;

        if (remainingShowTime > 0) {
            Thread.sleep(remainingShowTime);
        }
    }

    public void notifyVignetteExit(Port nextRoom) {
        if (loop != null) {
            loop.stop();
        }
        // Player - stop doing any movement/actions
        gameState.getPlayer().stopAnimating();
        
        // Save relevant game data/goals from scene?
        Vignette currentVignette = gameState.getCurrentVignette();
        if ( currentVignette != null ) {
            currentVignette.saveState(getGameState());
        }
        
        try {
            Class<?> c = Class.forName(getClass().getPackageName()+ ".content." + nextRoom.getDestination());
            Constructor<?> cons = c.getConstructor(int.class, int.class, Port.class, Player.class);
            Object object = cons.newInstance((int)(Vignette.NATIVE_WIDTH), (int)(Vignette.NATIVE_HEIGHT), nextRoom, getPlayer());
            setVignette((Vignette) object);
            log.log(Level.FINER, "[Engine] Loaded Vignette: {0}", nextRoom.getDestination());
        } catch (ClassNotFoundException | 
                NoSuchMethodException | 
                SecurityException | 
                InstantiationException | 
                IllegalAccessException | 
                IllegalArgumentException | 
                InvocationTargetException   ex) {
            //log.error(Level.SEVERE, ex.getMessage(), ex);
            //log.severe("exception loading scene.");
            //log.log(Level.SEVERE, "logged exception", ex);
            ex.printStackTrace();
        }
        
    }

    private void initHotKeys() {
        // Save Game   COMMAND+S
        scene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            final KeyCombination keyComb = new KeyCodeCombination(
                    KeyCode.S,KeyCombination.META_DOWN);

            @Override
            public void handle(KeyEvent ke) {
                if (keyComb.match(ke)) {
                    ke.consume(); // <-- stops passing the event to next node

                    doSave();
                } else {
                    loop.addInputEvent(ke);
                }
            }
        });
    }

    /**
     * @return the gameState
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * @return the scene
     */
    public Scene getScene() {
        return scene;
    }

    /**
     * @return the gui
     */
    public GameControlsPane getGui() {
        return gameControls;
    }

    /**
     * @return the player
     */
    public Player getPlayer() {
        return gameState.getPlayer();
    }

    public void doSave() {
        GameState gameState = getGameState();
        // Push all state values into game state.
        gameState.getCurrentVignette().saveState(gameState);

        // Save the file.
        gameState.quickSave();
    }

    public void doExit() {
        
        // TODO:   Ask if user is sure.
        
        // Check if we need to save.
        
        
        log.warning("Exit called.  Quitting game.");
        window.close();
    }

    private void initKeyInput() {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
              if(key.getCode()==KeyCode.ENTER) {
                  System.out.println("You pressed enter");
              }
        });
    }
    
    private void configureGuiLayout() {
        topArea.setPrefWidth(Vignette.NATIVE_WIDTH);
        topArea.setScaleX(SCALE);
        topArea.setScaleY(SCALE);
        
        bottomArea.setPrefWidth(Vignette.NATIVE_WIDTH);
        bottomArea.setScaleX(SCALE);
        bottomArea.setScaleY(SCALE);
        
        bottomArea.setSpacing(8);
        bottomArea.setPadding(new Insets(6));
        bottomArea.setBackground(new Background(new BackgroundFill(
                Color.DARKGREY, CornerRadii.EMPTY, Insets.EMPTY
        )));        
    }
    
    private void configureLogging() {
        ConsoleHandler handler = new ConsoleHandler();

        // TODO:  Bring in Rocket Logging system.
        
        // Add console handler as handler of logs
        log.addHandler(handler);
        log.setUseParentHandlers(false);
        
        log.setLevel(Level.FINE);
        for (Handler h : log.getHandlers()) {
            h.setLevel(Level.FINER);
        }        
    }
    
    /**
     * @return the inventoryPane
     */
    public InventoryPane getInventoryPane() {
        return inventoryPane;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void gameStateVignetteChanged(GameState gs) { }

    @Override
    public void gameStatePropertyChanged(GameState gs, String propKey) {}

    @Override
    public void gameStateShowInventory(GameState gs, boolean state) {
        log.log(Level.INFO, "Set show inventory pane: {0}", state);
        hideSpecialPanes();
        if ( state ) { inventoryPane.updateItemGrid(); }
        inventoryPane.setVisible(state);
    }

    @Override
    public void gameStateShowChips(GameState gs, boolean state) {
        log.log(Level.INFO, "Set show chips pane: {0}", state);
        hideSpecialPanes();
        chipsPane.setVisible(state);
    }
    
    private void hideSpecialPanes() {
        chipsPane.setVisible(false);
        inventoryPane.setVisible(false);
    }

}
