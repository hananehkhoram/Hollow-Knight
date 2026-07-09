package hana.HollowKnight.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import hana.HollowKnight.controller.GameController;
import hana.HollowKnight.model.stats.GameStats;


public class EndScreen extends BaseScreen {

    private final boolean playerWon;
    private final GameStats stats;

    private static final String[] OPTIONS = {"Restart", "Main Menu"};
    private int selectedIndex = 0;

    public EndScreen(GameController controller, boolean playerWon) {
        super(controller);
        this.playerWon = playerWon;
        this.stats = controller.getModel().getStats();
    }

    @Override
    public void show() {
        if (playerWon) {
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.02f, 0.02f, 0.05f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        handleInput();

    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedIndex = (selectedIndex + 1) % OPTIONS.length;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedIndex = (selectedIndex - 1 + OPTIONS.length) % OPTIONS.length;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            selectOption();
        }
    }

    private void selectOption() {
        switch (selectedIndex) {
            case 0: controller.startNewGame(0); break;
            case 1: controller.goToMainMenu(); break;
        }
    }

    private String formatTime(float seconds) {
        int mins = (int)(seconds / 60);
        int secs = (int)(seconds % 60);
        return String.format("%02d:%02d", mins, secs);
    }
}
