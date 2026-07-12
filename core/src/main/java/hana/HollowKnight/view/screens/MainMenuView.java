package hana.HollowKnight.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import hana.HollowKnight.controller.GameController;

public class MainMenuView extends BaseScreen {
    private Stage stage;
    private Skin menuSkin;
    private Texture logoTexture;

    public MainMenuView(GameController gameController) {
        super(gameController);
    }

    public static void setupButtonAnimation(final ImageTextButton button, Image bilbilakLeft, Image bilbilakRight) {
        bilbilakLeft.setScale(0.5f);
        bilbilakRight.setScale(0.5f);
        button.setTransform(true);
        button.setOrigin(Align.center);

        button.addListener(new ClickListener() {
            private final Vector2 buttonCenter = new Vector2();

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                button.addAction(Actions.scaleTo(1.04f, 1.04f, 0.15f, Interpolation.circleOut));

                buttonCenter.set(button.getWidth() / 2f, button.getHeight() / 2f);
                button.localToStageCoordinates(buttonCenter);

                float gap = 160f;

                float leftX = buttonCenter.x - gap - bilbilakLeft.getWidth();
                float leftY = buttonCenter.y - (bilbilakLeft.getHeight() / 2f);
                bilbilakLeft.setPosition(leftX, leftY);
                bilbilakLeft.setVisible(true);

                float rightX = buttonCenter.x + gap;
                float rightY = buttonCenter.y - (bilbilakRight.getHeight() / 2f);
                bilbilakRight.setPosition(rightX, rightY);
                bilbilakRight.setVisible(true);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                button.addAction(Actions.scaleTo(1f, 1f, 0.15f, Interpolation.circleOut));
                bilbilakLeft.setVisible(false);
                bilbilakRight.setVisible(false);
            }
        });
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport(), batch);
        this.activeStage = stage;

        Gdx.input.setInputProcessor(stage);

        logoTexture = new Texture(Gdx.files.internal("HOLLOW KNIGHT .png"));
        menuSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);
        backgroundImage.setAlign(Align.center);
        stage.addActor(backgroundImage);

        Image gameTitle = new Image(logoTexture);
        gameTitle.setOrigin(Align.center);
        gameTitle.setScale(1.1f);

        Image bilbilakLeft = new Image(menuSkin.getDrawable("main_menu_pointer_anim0009"));
        bilbilakLeft.setOrigin(Align.center);
        bilbilakLeft.setVisible(false);

        Image bilbilakRight = new Image(menuSkin.getDrawable("main_menu_pointer_anim0009"));
        bilbilakRight.setOrigin(Align.center);
        bilbilakRight.setRotation(180f);
        bilbilakRight.setVisible(false);

        ImageTextButton.ImageTextButtonStyle buttonStyle = menuSkin.get("default", ImageTextButton.ImageTextButtonStyle.class);
        buttonStyle.imageUp = null;
        buttonStyle.imageOver = null;
        buttonStyle.imageDown = null;
        buttonStyle.imageChecked = null;


        ImageTextButton startButton = new ImageTextButton("Start Game", buttonStyle);
        ImageTextButton settingButton = new ImageTextButton("Settings", buttonStyle);
        ImageTextButton guideButton = new ImageTextButton("Guide", buttonStyle);
        ImageTextButton achievementsButton = new ImageTextButton("Achievements", buttonStyle);
        ImageTextButton changeTheme = new  ImageTextButton("Change Theme", buttonStyle);
        ImageTextButton exitButton = new ImageTextButton("Quit Game", buttonStyle);

        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioManager.clickMenuSound();
                controller.startGame();
            }
        });
        settingButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioManager.clickMenuSound();
                controller.openSettings();
            }
        });
        guideButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioManager.clickMenuSound();
                controller.openGuide();
            }
        });
        achievementsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioManager.clickMenuSound();
                controller.openAchievements();
            }
        });
        changeTheme.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioManager.clickMenuSound();


            }
        });
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioManager.clickMenuSound();
                Gdx.app.exit();
            }
        });


        setupButtonAnimation(startButton, bilbilakLeft, bilbilakRight);
        setupButtonAnimation(settingButton, bilbilakLeft, bilbilakRight);
        setupButtonAnimation(guideButton, bilbilakLeft, bilbilakRight);
        setupButtonAnimation(achievementsButton, bilbilakLeft, bilbilakRight);
        setupButtonAnimation(exitButton, bilbilakLeft, bilbilakRight);

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.center();

        mainTable.add(gameTitle).padBottom(40f).row();
        mainTable.defaults().padBottom(20f).width(300f).height(50f);

        mainTable.add(startButton).row();
        mainTable.add(settingButton).row();
        mainTable.add(guideButton).row();
        mainTable.add(achievementsButton).row();
        mainTable.add(exitButton);

        stage.addActor(mainTable);

        stage.addActor(bilbilakLeft);
        stage.addActor(bilbilakRight);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        stage.act(delta);
        stage.draw();
        drawBrightnessOverlay();

    }

    @Override
    public void dispose() {
        stage.dispose();
        menuSkin.dispose();
        backgroundTexture.dispose();
        logoTexture.dispose();
    }

}
