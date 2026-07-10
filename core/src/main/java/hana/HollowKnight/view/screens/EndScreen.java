package hana.HollowKnight.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import hana.HollowKnight.controller.GameController;
import hana.HollowKnight.model.entities.PlayerModel;

public class EndScreen extends BaseScreen {

    private Stage stage;
    private PlayerModel player;
    private Skin menuSkin;
    private Image backGraoundImage;
    private Image logoImage;

    public EndScreen(GameController controller, PlayerModel player) {
        super(controller);
        this.player = player;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport(), batch);
        this.activeStage = stage;
        Gdx.input.setInputProcessor(stage);

        menuSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        backGraoundImage = new Image(new Texture(Gdx.files.internal("game_over_far_bg.png")));
        backGraoundImage.setAlign(Align.center);
        backGraoundImage.setFillParent(true);
        stage.addActor(backGraoundImage);

        logoImage = new Image(new Texture(Gdx.files.internal("boardBottom.png")));
        logoImage.setOrigin(Align.center);
        logoImage.setPosition(
            (stage.getWidth() - logoImage.getWidth()) / 2f,
            stage.getHeight() * 0.5f
        );
        stage.addActor(logoImage);

        Image bilbilakLeft = new Image(menuSkin.getDrawable("main_menu_pointer_anim0009"));
        bilbilakLeft.setOrigin(Align.center);
        bilbilakLeft.setVisible(false);

        Image bilbilakRight = new Image(menuSkin.getDrawable("main_menu_pointer_anim0009"));
        bilbilakRight.setOrigin(Align.center);
        bilbilakRight.setRotation(180f);
        bilbilakRight.setVisible(false);

        Label settinglabel = new Label("Game Completion", menuSkin);
        settinglabel.setFontScale(1f);
        settinglabel.setAlignment(Align.center);
        settinglabel.pack();
        settinglabel.setPosition(
            (stage.getWidth() - settinglabel.getWidth()) / 2f,
            stage.getHeight() * 0.75f
        );
        stage.addActor(settinglabel);

        Table table = new Table();
        table.setFillParent(true);
        table.top();
        table.add().height(stage.getHeight() * 0.4f).colspan(2).row();

        Label deathCountLabel = new Label("Death Count", menuSkin, "settingmenu");
        Label deathCount = new Label(Integer.toString(player.getPlayerDeathsCount()), menuSkin, "settingmenu");
        Label killsCountLabel = new Label("Kills Count", menuSkin, "settingmenu");
        Label killsCount = new Label(Integer.toString(player.getPlayerKillsCount()), menuSkin, "settingmenu");
        Label timeDurationLabel = new Label("Time Duration", menuSkin, "settingmenu");
        Label timeDuration = new Label(Float.toString(player.getTimePassed()), menuSkin, "settingmenu");

        ImageTextButton reStart = new ImageTextButton("Restart", menuSkin);
        ImageTextButton mainMenu = new ImageTextButton("Main Menu", menuSkin);

        reStart.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioManager.clickMenuSound();
                controller.startGame();
            }
        });
        MainMenuView.setupButtonAnimation(reStart, bilbilakLeft, bilbilakRight);

        mainMenu.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioManager.clickMenuSound();
                audioManager.stopCityofTears();
                audioManager.stopGreenpathSound();
                audioManager.playMenuSound();
                controller.goToMainMenu();
            }
        });
        MainMenuView.setupButtonAnimation(mainMenu, bilbilakLeft, bilbilakRight);

        table.add(deathCountLabel).width(200f).padRight(30f);
        table.add(deathCount).width(200f).padLeft(30f).row();
        table.add(killsCountLabel).width(200f).padRight(30f);
        table.add(killsCount).width(200f).padLeft(30f).row();
        table.add(timeDurationLabel).width(200f).padRight(30f);
        table.add(timeDuration).width(200f).padLeft(30f).row();
        table.add(reStart).colspan(2).padTop(20f).row();
        table.add(mainMenu).colspan(2).padTop(10f).row();

        stage.addActor(table);
        stage.addActor(bilbilakLeft);
        stage.addActor(bilbilakRight);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        super.render(delta);
        stage.act(delta);
        stage.draw();
        drawBrightnessOverlay();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        menuSkin.dispose();
        if (backGraoundImage != null && backGraoundImage.getDrawable() != null) {
            logoImage.getDrawable().getMinWidth();
        }
    }
}
