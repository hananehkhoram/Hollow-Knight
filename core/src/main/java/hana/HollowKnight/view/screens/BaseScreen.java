package hana.HollowKnight.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import hana.HollowKnight.controller.GameController;
import hana.HollowKnight.view.audio.AudioManager;

public abstract class BaseScreen implements Screen {

    public static final float WORLD_WIDTH = 1280f;
    public static final float WORLD_HEIGHT = 720f;
    public static Texture backgroundTexture = new Texture("MainMenuBackGround.png");
    protected final GameController controller;
    protected final SpriteBatch batch;
    protected final OrthographicCamera camera;

    protected final Viewport viewport;
    protected final AudioManager audioManager;
    protected Stage activeStage;

    public BaseScreen(GameController controller) {
        this.controller = controller;
        this.batch = controller.getBatch();
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        this.audioManager = AudioManager.getInstance();
        camera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);
        camera.update();
    }

    public static void changeBackground(String path) {
        backgroundTexture = new Texture(path);
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void render(float delta) {

        com.badlogic.gdx.utils.ScreenUtils.clear(0.031f, 0.039f, 0.058f, 1f);

        if (batch != null) {
            batch.setColor(1f, 1f, 1f, 1f);
        }
    }

    protected void drawBrightnessOverlay() {
        float b = GameController.brightness;

        if (b < 1.0f && batch != null) {
            float darknessAlpha = 1.0f - b;

            com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
            pixmap.setColor(0f, 0f, 0f, darknessAlpha);
            pixmap.fill();
            Texture blackTexture = new Texture(pixmap);
            pixmap.dispose();

            batch.begin();
            batch.draw(blackTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.end();

            blackTexture.dispose();
        }
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }
}
