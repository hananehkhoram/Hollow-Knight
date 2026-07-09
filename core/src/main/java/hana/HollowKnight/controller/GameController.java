package hana.HollowKnight.controller;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import hana.HollowKnight.model.GameModel;
import hana.HollowKnight.model.data.GameData;
import hana.HollowKnight.view.GameView;
import hana.HollowKnight.view.screens.*;

public class GameController {
    public static float brightness = 1.0f;
    private final Game game;
    private final SpriteBatch batch;
    private final InputHandler inputHandler = new InputHandler();
    private GameModel model;

    public GameController(Game game, SpriteBatch batch) {
        this.game = game;
        this.batch = batch;
        this.model = new GameModel();
    }

    public void updateGameplay(float delta) {
        inputHandler.update(model.getPlayer());
        model.getPlayer().update(delta);
    }

    public InputHandler getInputHandler() {
        return inputHandler;
    }

    public void goToMainMenu() {
        game.setScreen(new MainMenuView(this));
    }

    public void pauseGame() {
        game.setScreen(new PauseMenuView(this));
    }

    public void resumeGame() {
        game.setScreen(new GameView(this));
    }

    public void openInventory() {
        game.setScreen(new InventoryScreenView(this));
    }

    public void openSettings() {
        game.setScreen(new SettingScreenView(this));
    }

    public void openDialogue() {
        game.setScreen(new DialogueScreenView(this));
    }

    public void startGame() {
        game.setScreen(new StartGameScreen(this));
    }

    public void endGame(boolean playerWon) {
        game.setScreen(new EndScreen(this, playerWon));
    }

    public void openGuide() {
        game.setScreen(new GuideScreen(this));
    }

    public void openAchievements() {
        game.setScreen(new AchievementsScreen(this));
    }

    public void startNewGame(int slot) {
        model = new GameModel();
        model.setActiveSlot(slot);
        game.setScreen(new GameView(this));
    }

    public void loadGame(int slot) {
        if (model.load(slot)) {
            game.setScreen(new GameView(this));
        }
    }

    public void saveGame(int slot) {
        model.save(slot);
    }

    public void saveCurrentAndExit() {
        if (model.getActiveSlot() != -1) {
            model.save(model.getActiveSlot());
        }
        goToMainMenu();
    }

    public boolean hasSave(int slot) {
        return model.hasSave(slot);
    }

    public GameData peekSave(int slot) {
        return model.peekSave(slot);
    }

    public void openKeyboardSetting() {
        game.setScreen(new KeyboardSetting(this));
    }

    public void refreshCurrentScreen(String path) {
        Screen current = game.getScreen();
        BaseScreen.changeBackground(path);
        game.setScreen(new SettingScreenView(this));
        if (current != null) {
            current.dispose();
        }
    }    // --- Getters ---

    public GameModel getModel() {
        return model;
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public Game getGame() {
        return game;
    }

}
