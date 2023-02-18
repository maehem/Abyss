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
package com.maehem.abyss;

import com.maehem.abyss.debug.DebugTab;
import com.maehem.abyss.engine.Loop;
import com.maehem.abyss.engine.Player;
import com.maehem.abyss.engine.Vignette;
import com.maehem.abyss.engine.GameState;
import com.maehem.abyss.engine.GameStateListener;
import com.maehem.abyss.engine.MatrixPane;
import com.maehem.abyss.engine.view.ViewPane;
import com.maehem.abyss.engine.VignetteTrigger;
import com.maehem.abyss.engine.view.ChipsConfiguratorPane;
import com.maehem.abyss.engine.gui.InventoryPane;
import com.maehem.abyss.engine.gui.GameControlsPane;
import com.maehem.abyss.engine.gui.NarrationPane;
import com.maehem.abyss.engine.gui.RomConstructPane;
import com.maehem.abyss.engine.gui.TerminalPane;
import com.maehem.abyss.engine.bbs.BBSTerminal;
import com.maehem.abyss.engine.bbs.PublicTerminalSystem;
import com.maehem.abyss.logging.LoggingFormatter;
import com.maehem.abyss.logging.LoggingHandler;
import com.maehem.abyss.logging.LoggingMessageList;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
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
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 *  TODO:   For when we deploy on GitHub:
 *          https://stackoverflow.com/questions/14013644/hosting-a-maven-repository-on-github
 *
 * @author Mark J Koch [@maehem on GitHub]
 */
public class Engine extends Application implements GameStateListener {

    public static final Logger LOGGER = Logger.getLogger("com.maehem.abyss");
    public static final String VERSION = "0.0.0";
    
    //private double SCALE = 0.75;
    private static final double SCALE = 0.66;
    
    private final Group vignetteGroup = new Group();
    private final StackPane topArea = new StackPane();
    private NarrationPane narrationPane;
    private RomConstructPane romPane;
    private GameControlsPane gameControls;
    private ChipsConfiguratorPane chipsPane;
    private TerminalPane terminalPane;
    private MatrixPane matrixPane;
    private InventoryPane inventoryPane;
    private ImageView splashScreen;

    private static final int PRELOADER_SHOWTIME_MILLIS = 2000;
    // This is a class name.
    private static final String STARTING_VIGNETTE = "StreetVignette2";

    private Stage window;
    private Loop loop;   // Game logic Loop
    
    private final HBox bottomArea = new HBox();  // gui and naration
    private final VBox gamePane = new VBox(new Group(topArea), new Group(bottomArea));
    
    private final StackPane root = new StackPane(gamePane);
    private final Scene scene = new Scene(root); //, 1280, 920);

    private final LoggingMessageList messageLog = new LoggingMessageList();
    private final LoggingHandler loggingHandler = new LoggingHandler(messageLog);

    private final Stage debugWindow = new Stage();
    
    private final GameState gameState;// = new GameState();
    
    
    // TODO:   Music track system
    //          - Blend music scene to scene
    
    // TODO:   Cut Scenes
    
    
    
    public Engine( ) {
        configureLogging();
        
        // TRUE for debug.
        LOGGER.setUseParentHandlers(false);  // Prevent INFO and HIGHER from going to stderr.
        this.gameState = new GameState();

    }
    
    
    @Override
    public void start(Stage window) {
        this.window = window;
        topArea.setPrefSize(ViewPane.WIDTH, ViewPane.HEIGHT);
        initDebugWindow();

        getGameState().addListenter(this);
        
        LOGGER.info("Abyss Engine version:  " + VERSION);
        LOGGER.log(Level.INFO, "JavaFX Version: {0}", 
                System.getProperties().get("javafx.runtime.version")
        );
        LOGGER.log(Level.INFO, "Game: {0} {1}", new Object[]{gameState.getLongGameName(), gameState.getGameVersion()});
        
        LOGGER.fine("Fine log message.");
        LOGGER.finer("Finer log message.");
        LOGGER.finest("Finest log message.");
        
        
        gameState.init();

        splashScreen = new ImageView(new Image(getClass().getResourceAsStream("/splash.png")));
        //topArea.getChildren().add(splashScreen);

        window.setScene(this.scene);
        window.setResizable(false);
        window.setOnCloseRequest(e -> { //quit when the window is close().
            // TODO: Check if user wants to save game.
            Platform.exit();
        });
        
        gameControls = new GameControlsPane(getGameState(), ViewPane.WIDTH/2);

        // Narration - Shares space with ROM, but only one is visible at a time.
        narrationPane = new NarrationPane(ViewPane.WIDTH/2);     
        // ROM Adviser -- Hint System and Third Hand
        romPane = new RomConstructPane(getGameState(), ViewPane.WIDTH/2);
        StackPane rightPane = new StackPane(narrationPane,romPane);
        
        narrationPane.setVisible(true);
        romPane.setVisible(false);
        bottomArea.getChildren().addAll(gameControls ,rightPane  );
        
        configureGuiLayout();
        
        window.show(); // Show the window so that other GUI elements can layout().

        // Inventory
        inventoryPane = new InventoryPane(gameState);
        
        // Chips  -- Confgurable Buffs
        chipsPane = new ChipsConfiguratorPane(gameState);
        
        // Terminal -- Base BBS style system
        terminalPane = new TerminalPane(gameState );
        terminalPane.setTerminal(new PublicTerminalSystem(gameState), false);
        
        // Deck Bench -- Configure your Deck with inventory components
        
        // Cyberspace  -- ROM Helper replaces Narration Window
        matrixPane = new MatrixPane(gameState );  ///, Vignette.NATIVE_WIDTH, Vignette.NATIVE_HEIGHT);
        
        topArea.getChildren().addAll(vignetteGroup, //splashScreen,
                chipsPane, inventoryPane, terminalPane, matrixPane
        );
        
        // Initilize the game
        getGameState().load(STARTING_VIGNETTE);

        matrixPane.updateHudSoftwareTabs(); /// Might not need this here.
        
        initHotKeys();
        initKeyInput();
        
         // Load the starting room.
        String roomName = getGameState().getProperty(
                GameState.PROP_CURRENT_VIGNETTE, 
                STARTING_VIGNETTE
        );
        notifyVignetteExit(new VignetteTrigger(roomName));  // Just leveraging the Room Loading System here.
        gameState.setShowing(GameState.Display.VIGNETTE);
   }

    private void initDebugWindow() {
        DebugTab debugTab = new DebugTab( messageLog, gameState);
        debugTab.setFormatter(loggingHandler.getFormatter());
        Scene debugScene = new Scene(debugTab);
        
        debugWindow.setScene(debugScene);
        debugWindow.setTitle("Debug Window");
        debugWindow.setAlwaysOnTop(true);
        debugWindow.setOnHidden((t) -> {
            gameState.setShowDebug(false);
        });
        debugWindow.setOnShowing((t) -> {
            gameState.setShowDebug(true);
        });
        
        debugWindow.show();
        Rectangle2D bounds = Screen.getPrimary().getBounds();
        debugWindow.setX(bounds.getWidth()-debugWindow.getWidth());
        debugWindow.setY(bounds.getHeight()-debugWindow.getHeight());
    }

    private void setVignette(Vignette v) {
        Vignette currentVignette = gameState.getCurrentVignette();
        
        vignetteGroup.getChildren().remove(currentVignette);
        gameState.setCurrentVignette(v);
        v.loadState(gameState);        
        vignetteGroup.getChildren().add(0, v);
        
        window.setTitle(v.getName());

        //loop = new Loop(this, v);
        loop = new Loop(this);
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

    public void notifyVignetteExit(VignetteTrigger nextRoom) {
        if (loop != null) {
            loop.stop();
        }
        // Player - stop doing any movement/actions
        gameState.getPlayer().stopAnimating();
        
        
// Don't do this.  GameState should already have info on vignette changes.
//        // Save relevant game data/goals from scene?
//        Vignette currentVignette = gameState.getCurrentVignette();
//        if ( currentVignette != null ) {
//            currentVignette.saveState(getGameState());
//        }
        
        try {
            String packageName = gameState.getContentPack().getClass().getPackageName();
            Class<?> c = Class.forName(packageName + ".content.vignette." + nextRoom.getDestination());
            Constructor<?> cons = c.getConstructor(GameState.class, VignetteTrigger.class, Player.class);
            Object object = cons.newInstance( gameState, nextRoom, getPlayer());
            setVignette((Vignette) object);
            LOGGER.log(Level.FINER, "[Engine] Loaded Vignette: {0}", nextRoom.getDestination());
        } catch (ClassNotFoundException | 
                NoSuchMethodException | 
                SecurityException | 
                InstantiationException | 
                IllegalAccessException | 
                IllegalArgumentException | 
                InvocationTargetException   ex) {
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
        
        
        LOGGER.warning("Exit called.  Quitting game.");
        window.close();
    }

    private void initKeyInput() {
        scene.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
              if(key.getCode()==KeyCode.ENTER) {
                  LOGGER.log(Level.INFO, "You pressed ENTER" );
              }
        });
    }
    
    private void configureGuiLayout() {
        topArea.setPrefWidth(ViewPane.WIDTH);
        topArea.setScaleX(SCALE);
        topArea.setScaleY(SCALE);
        
        bottomArea.setPrefWidth(ViewPane.WIDTH);
        bottomArea.setScaleX(SCALE);
        bottomArea.setScaleY(SCALE);
        
        bottomArea.setSpacing(8);
        bottomArea.setPadding(new Insets(6));
        bottomArea.setBackground(new Background(new BackgroundFill(
                Color.DARKGREY, CornerRadii.EMPTY, Insets.EMPTY
        )));        
    }
    
    private void configureLogging() {
        loggingHandler.setFormatter(new LoggingFormatter());
        // Get the top most logger and add our handler.
        LOGGER.setUseParentHandlers(false);  // Prevent INFO and HIGHER from going to stderr.
        LOGGER.addHandler(loggingHandler);

        // For our java package only, log ony FINE and above.
        LOGGER.setLevel(Level.FINEST);

        //ConsoleHandler handler = new ConsoleHandler();
        // Add console handler as handler of logs
        //Logger.getLogger("com.maehem.abyss").addHandler(handler);
        //Logger.getLogger("com.maehem.abyss").setUseParentHandlers(false);
        
    }
    
    /**
     * @return the inventoryPane
     */
    public InventoryPane getInventoryPane() {
        return inventoryPane;
    }

    public MatrixPane getMatrixPane() {
        return matrixPane;
    }
    
    public Group getVignetteGroup() {
        return vignetteGroup;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void gameStateVignetteChanged(GameState gs) {
        narrationPane.setTitle(gs.getCurrentVignette().getName());
        narrationPane.setText(gs.getCurrentVignette().getNarration());
    }

    @Override
    public void gameStatePropertyChanged(GameState gs, String propKey) {}

//    @Override
//    public void gameStateShowInventory(GameState gs, boolean state) {
//        LOGGER.log(Level.INFO, "Set show inventory pane: {0}", state);
//        hideSpecialPanes();
//        if ( state ) { inventoryPane.updateItemGrid(); }
//        inventoryPane.setVisible(state);
//    }

//    @Override
//    public void gameStateShowChips(GameState gs, boolean state) {
//        LOGGER.log(Level.INFO, "Set show chips pane: {0}", state);
//        hideSpecialPanes();
//        chipsPane.setVisible(state);
//    }
    
//    private void hideSpecialPanes() {
//        chipsPane.setVisible(false);
//        inventoryPane.setVisible(false);
//        terminalPane.setVisible(false);
//    }

    @Override
    public void gameStateShowDebug(GameState gs, boolean state) {
        if ( state ) {
            debugWindow.show();
        } else {
            debugWindow.hide();
        }
    }

    @Override
    public void gameStateTerminalChanged(GameState gs, BBSTerminal term) {
        terminalPane.setTerminal(term, true);
        // TODO: see if terminal pane already handles or could handle this.
    }

//    @Override
//    public void gameStateShowTerminal(GameState aThis, boolean state) {
//        LOGGER.log(Level.INFO, "Set show terminal pane: {0}", state);
//        hideSpecialPanes();
//        terminalPane.setVisible(state);
//    }

    @Override
    public void gameStateDisplayChanged(GameState aThis, GameState.Display d) {
    }

    @Override
    public void gameStateMatrixSiteChanged(GameState gs, int newAddr) {}

}
