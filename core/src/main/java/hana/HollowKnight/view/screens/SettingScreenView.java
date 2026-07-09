package hana.HollowKnight.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import hana.HollowKnight.controller.GameController;

public class SettingScreenView extends BaseScreen {
    private Stage stage;
    private Skin menuSkin;
    private Texture logoTexture;

    public SettingScreenView(GameController controller) {
        super(controller);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport(), batch);
        this.activeStage = stage;

        Gdx.input.setInputProcessor(stage);

        menuSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        Image backGroundImage = new Image(backgroundTexture);
        backGroundImage.setFillParent(true);
        backGroundImage.setAlign(Align.center);
        stage.addActor(backGroundImage);

        logoTexture = new Texture("boardBottom.png");
        Image logoImage = new Image(logoTexture);
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

        ImageTextButton.ImageTextButtonStyle baseStyle = menuSkin.get("default", ImageTextButton.ImageTextButtonStyle.class);
        ImageTextButton.ImageTextButtonStyle buttonStyle = new ImageTextButton.ImageTextButtonStyle(baseStyle);

        Label settinglabel = new Label("Settings", menuSkin);
        settinglabel.setFontScale(1.2f);
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
        table.add().height(stage.getHeight() * 0.40f).colspan(3).row();

        table.defaults().padBottom(12f).height(50f);

        ImageTextButton bgmDown = new ImageTextButton("-", buttonStyle);
        bgmDown.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioManager.clickMenuSound();
                float currentVol = audioManager.getBgmVolume();
                audioManager.setBgmVolume(Math.max(0.0f, currentVol - 0.1f));
            }
        });
        table.add(bgmDown).width(100f).padRight(30f);

        ImageTextButton bgmButton = new ImageTextButton("Music Volume", menuSkin, "mute");
        bgmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (audioManager.getBgmVolume() > 0.0f) {
                    audioManager.setBgmVolume(0);
                } else {
                    audioManager.setBgmVolume(1.0f);
                }
            }
        });
        table.add(bgmButton).width(320f);

        ImageTextButton bgmUp = new ImageTextButton("+", buttonStyle);
        bgmUp.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioManager.clickMenuSound();
                float currentVol = audioManager.getBgmVolume();
                audioManager.setBgmVolume(Math.min(1.0f, currentVol + 0.1f));
            }
        });
        table.add(bgmUp).width(100f).padLeft(30f).row();

        ImageTextButton sfxDown = new ImageTextButton("MUTE", menuSkin, "mute");
        sfxDown.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioManager.setVolume(0f);
                audioManager.clickMenuSound();
            }
        });
        table.add(sfxDown).width(100f).padRight(30f);

        Label sfxLabel = new Label("Sound Volume", menuSkin, "settingmenu");
        sfxLabel.setAlignment(Align.center);
        table.add(sfxLabel).width(320f);

        ImageTextButton sfxUp = new ImageTextButton("Unmute", menuSkin, "mute");
        sfxUp.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioManager.setVolume(1f);
                audioManager.clickMenuSound();
            }
        });
        table.add(sfxUp).width(100f).padLeft(30f).row();

        ImageTextButton brightDown = new ImageTextButton("-", buttonStyle);
        brightDown.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioManager.clickMenuSound();
                GameController.brightness = Math.max(0.2f, GameController.brightness - 0.1f);
            }
        });
        table.add(brightDown).width(100f).padRight(30f);

        Label brightLabel = new Label("Brightness", menuSkin, "settingmenu");
        brightLabel.setAlignment(Align.center);
        table.add(brightLabel).width(320f);

        ImageTextButton brightUp = new ImageTextButton("+", buttonStyle);
        brightUp.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioManager.clickMenuSound();
                GameController.brightness = Math.min(1.0f, GameController.brightness + 0.1f);
            }
        });
        table.add(brightUp).width(100f).padLeft(30f).row();

        ImageTextButton resetSound = new ImageTextButton("Reset Settings", menuSkin, "mute");
        resetSound.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioManager.setVolume(1f);
                audioManager.setBgmVolume(0.75f);
                GameController.brightness = 1.0f;
                audioManager.clickMenuSound();
            }
        });
        MainMenuView.setupButtonAnimation(resetSound, bilbilakLeft, bilbilakRight);
        table.add(resetSound).colspan(3).width(450f).row();

        ImageTextButton keyboard = new ImageTextButton("KeyBoard", menuSkin, "mute");
        keyboard.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioManager.clickMenuSound();
                controller.openKeyboardSetting();
            }
        });
        MainMenuView.setupButtonAnimation(keyboard, bilbilakLeft, bilbilakRight);
        table.add(keyboard).colspan(3).width(450f).row();

        ImageTextButton changeLanguage = new ImageTextButton("Change Language", menuSkin, "mute");
        changeLanguage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioManager.clickMenuSound();
            }
        });
        MainMenuView.setupButtonAnimation(changeLanguage, bilbilakLeft, bilbilakRight);
        table.add(changeLanguage).colspan(3).width(450f).row();

        Label themeLabel = new Label("Menu Style:", menuSkin, "settingmenu");
        themeLabel.setAlignment(Align.center);
        table.add(themeLabel).colspan(3).padTop(10f).row();

        Table themeButtonsTable = new Table();
        themeButtonsTable.defaults().width(160f).pad(0, 10f, 0, 10f);

        ImageTextButton classic = new ImageTextButton("Classic", menuSkin, "mute");
        ImageTextButton voidHeart = new ImageTextButton("Void Heart", menuSkin, "mute");
        ImageTextButton god = new ImageTextButton("Glory", menuSkin, "mute");

        voidHeart.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioManager.clickMenuSound();
                controller.refreshCurrentScreen("MainMenuBackGround.png");
            }
        });

        classic.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioManager.clickMenuSound();
                controller.refreshCurrentScreen("classicStyle.png");
            }
        });

        god.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioManager.clickMenuSound();
                controller.refreshCurrentScreen("GodStyle.png");
            }
        });

        themeButtonsTable.add(classic);
        themeButtonsTable.add(voidHeart);
        themeButtonsTable.add(god);

        table.add(themeButtonsTable).colspan(3).padBottom(15f).row();

        ImageTextButton back = new ImageTextButton("Back", buttonStyle);
        back.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                audioManager.clickMenuSound();
                controller.goToMainMenu();
            }
        });
        MainMenuView.setupButtonAnimation(back, bilbilakLeft, bilbilakRight);
        table.add(back).colspan(3).width(450f).padTop(15f);

        stage.addActor(table);
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
        logoTexture.dispose();
    }
}
