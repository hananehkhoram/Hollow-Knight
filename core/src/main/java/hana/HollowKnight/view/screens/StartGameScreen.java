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
import hana.HollowKnight.model.data.GameData;
import hana.HollowKnight.view.audio.AudioManager;

public class StartGameScreen extends BaseScreen {

    private static final int SLOT_COUNT = 4;

    private Stage stage;
    private Skin menuSkin;
    private Texture logoTexture;

    public StartGameScreen(GameController gameController) {
        super(gameController);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport(), batch);
        this.activeStage = stage;
        Gdx.input.setInputProcessor(stage);

        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);
        backgroundImage.setAlign(Align.center);
        stage.addActor(backgroundImage);

        logoTexture = new Texture(Gdx.files.internal("boardBottom.png"));
        Image logoImage = new Image(logoTexture);
        logoImage.setPosition(
            (stage.getWidth() - logoImage.getWidth()) / 2f,
            stage.getHeight() * 0.5f
        );
        stage.addActor(logoImage);

        menuSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        Image bilbilakLeft = new Image(menuSkin.getDrawable("main_menu_pointer_anim0009"));
        bilbilakLeft.setOrigin(Align.center);
        bilbilakLeft.setVisible(false);

        Image bilbilakRight = new Image(menuSkin.getDrawable("main_menu_pointer_anim0009"));
        bilbilakRight.setOrigin(Align.center);
        bilbilakRight.setRotation(180f);
        bilbilakRight.setVisible(false);

        ImageTextButton.ImageTextButtonStyle baseStyle = menuSkin.get("default", ImageTextButton.ImageTextButtonStyle.class);
        ImageTextButton.ImageTextButtonStyle buttonStyle = new ImageTextButton.ImageTextButtonStyle(baseStyle);
        buttonStyle.imageOver = null;
        buttonStyle.imageDown = null;

        Label profileLabel = new Label("Select Profile", menuSkin);
        profileLabel.setFontScale(1.2f);
        profileLabel.setAlignment(Align.center);

        profileLabel.pack();

        profileLabel.setPosition(
            (stage.getWidth() - profileLabel.getWidth()) / 2f,
            stage.getHeight() * 0.75f
        );

        stage.addActor(profileLabel);

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top();
        mainTable.add().height(stage.getHeight()*0.4f).row();
        mainTable.defaults().padBottom(14f).width(360f).height(56f);

        for (int i = 0; i < SLOT_COUNT; i++) {
            final int slot = i;
            ImageTextButton slotButton = new ImageTextButton(buildSlotLabel(slot), buttonStyle);

            slotButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    audioManager.clickMenuSound();
                    if (controller.hasSave(slot)) {
                        controller.loadGame(slot);
                    } else {
                        AudioManager.getInstance().playCityOfTearsSound();
                        controller.startNewGame(slot);
                    }
                }
            });

            MainMenuView.setupButtonAnimation(slotButton, bilbilakLeft, bilbilakRight);
            mainTable.add(slotButton).row();
        }

        ImageTextButton backButton = new ImageTextButton("Back", buttonStyle);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioManager.clickMenuSound();
                controller.goToMainMenu();
            }
        });
        MainMenuView.setupButtonAnimation(backButton, bilbilakLeft, bilbilakRight);
        mainTable.add(backButton).padTop(10f);

        stage.addActor(mainTable);

        stage.addActor(bilbilakLeft);
        stage.addActor(bilbilakRight);
    }

    private String buildSlotLabel(int slot) {
        if (!controller.hasSave(slot)) {
            return "Slot " + (slot + 1) + " - New Game";
        }
        GameData data = controller.peekSave(slot);
        String date = (data != null && data.saveDate != null) ? data.saveDate : "";
        return "Slot " + (slot + 1) + " - Continue (" + date + ")";
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
