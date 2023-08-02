module com.maehem.abyss {
    requires java.logging;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.media;
    requires java.base;

    exports com.maehem.abyss;
    exports com.maehem.abyss.engine;
    exports com.maehem.abyss.engine.audio.music;
    //exports com.maehem.abyss.engine.audio.sound;
    exports com.maehem.abyss.engine.babble;
    exports com.maehem.abyss.engine.matrix;
    exports com.maehem.abyss.engine.bbs;
    exports com.maehem.abyss.engine.bbs.widgets;
}
