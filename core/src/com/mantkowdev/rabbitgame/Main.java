package com.mantkowdev.rabbitgame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Main extends Game {
    public final static float WIDTH = 1280f;
    public final static float HEIGHT = 720f;

    private final AssetManager assetManager = new AssetManager();

    @Override
    public void create() {
        TextureParameter params = new TextureParameter();
        params.magFilter = Texture.TextureFilter.Linear;
        params.minFilter = Texture.TextureFilter.Linear;

        assetManager.load("1.png", Texture.class, params);
        assetManager.load("2.png", Texture.class, params);
        assetManager.load("3.png", Texture.class, params);
        assetManager.finishLoading();
        setScreen(new GameScreen(assetManager));
    }

    @Override
    public void render() {
        clearScreen();
        super.render();
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }
}
