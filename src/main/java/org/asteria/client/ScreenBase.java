package org.asteria.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import static org.flowutils.Check.notNull;

/**
 * Provides a 2D scenegraph to work with
 */
public abstract class ScreenBase extends ScreenAdapter {

    public static final int SMALL_GAP = 2;
    public static final int GAP = 5;
    public static final int LARGE_GAP = 10;

    private Stage stage;
    private Skin skin;
    private TextureAtlas textureAtlas;

    private Table rootUi;

    protected final void create(Skin skin, TextureAtlas textureAtlas) {
        // Check and store provided skin and atlas parameters
        notNull(skin, "skin");
        notNull(textureAtlas, "textureAtlas");
        this.skin = skin;
        this.textureAtlas = textureAtlas;

        // Create 2D Scenegraph
        stage = new Stage();

        // Create UI table that fills screen, add it to scenegraph
        rootUi = new Table(skin);
        rootUi.setFillParent(true);
        stage.addActor(rootUi);

        // Let overriding classes do things
        onCreate(skin, textureAtlas, stage, rootUi);
    }

    @Override final public void render(float delta) {
        // Clear screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update scenegraph (e.g. animations)
        stage.act(Gdx.graphics.getDeltaTime());

        // Render scenegraph
        stage.draw();

        onRender();
    }

    @Override final public void show() {
        Gdx.input.setInputProcessor(stage);
        onVisibilityChanged(true);
    }

    @Override final public void hide() {
        Gdx.input.setInputProcessor(null);
        onVisibilityChanged(false);
    }

    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        onResize(width, height);
    }

    @Override final public void dispose() {
        stage.dispose();
        onDispose();
    }

    /**
     * @return UI skin, available after the screen is created the first time.
     */
    public final Skin getSkin() {
        return skin;
    }

    /**
     * @return texture atlas with textures from the asset directory
     */
    public final TextureAtlas getTextureAtlas() {
        return textureAtlas;
    }

    /**
     * @return UI table that fills screen, can be used as host for other UI components.
     */
    public final Table getRootUi() {
        return rootUi;
    }

    /**
     * @return 2D scenegraph for this screen.
     */
    public final Stage getStage() {
        return stage;
    }

    /**
     * @param skin UI skin to use (provides the look of the UI components)
     * @param textureAtlas texture atlas with custom images.
     * @param stage 2D scenegraph, contains the UI, can also contain other 2D actors.
     * @param rootUi root UI table, fills the screen.  Other UI components can be added to this (or directly to stage).
     */
    protected abstract void onCreate(Skin skin, TextureAtlas textureAtlas, Stage stage, Table rootUi);

    protected void onRender() {
    }
    protected void onVisibilityChanged(boolean screenVisible) {
    }
    protected void onResize(int width, int height) {
    }
    protected void onDispose() {
    }
}
