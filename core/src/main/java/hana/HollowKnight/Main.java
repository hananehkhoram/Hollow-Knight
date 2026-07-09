package hana.HollowKnight;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import hana.HollowKnight.controller.GameController;
import hana.HollowKnight.view.audio.AudioManager;
import hana.HollowKnight.view.screens.MainMenuView;

public class Main extends Game {
    private SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        GameController gameController = new GameController(this, batch);

        AudioManager.getInstance().playMenuSound();

        Gdx.graphics.setCursor(createCustomCursor());

        setScreen(new MainMenuView(gameController));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();

        if (getScreen() != null) {
            getScreen().dispose();
        }

        if (batch != null) {
            batch.dispose();
        }
    }

    private Cursor createCustomCursor() {
        Pixmap original = new Pixmap(Gdx.files.internal("Cursor.png"));

        int size = Integer.highestOneBit(Math.max(original.getWidth(), original.getHeight()) - 1) * 2;

        Pixmap padded = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        padded.setBlending(Pixmap.Blending.None);
        int offsetX = (size - original.getWidth()) / 2;
        int offsetY = (size - original.getHeight()) / 2;
        padded.drawPixmap(original, offsetX, offsetY);

        Cursor cursor = Gdx.graphics.newCursor(padded, size / 2, size / 2);

        original.dispose();
        padded.dispose();
        return cursor;
    }
}
