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
import hana.HollowKnight.controller.InputHandler.PlayerAction;

import java.util.EnumMap;
import java.util.Map;

public class KeyboardSetting extends BaseScreen {
    private final Map<PlayerAction, ImageTextButton> keyButtonsMap = new EnumMap<>(PlayerAction.class);
    private Stage stage;
    private Skin menuSkin;
    private PlayerAction actionBeingRebound = null;
    private ImageTextButton buttonBeingRebound = null;

    public KeyboardSetting(GameController controller) {
        super(controller);
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport(), batch);
        this.activeStage = stage;
        Gdx.input.setInputProcessor(stage);

        menuSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        Image backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        Image bilbilakLeft = new Image(menuSkin.getDrawable("main_menu_pointer_anim0009"));
        bilbilakLeft.setOrigin(Align.center);
        bilbilakLeft.setVisible(false);

        Image bilbilakRight = new Image(menuSkin.getDrawable("main_menu_pointer_anim0009"));
        bilbilakRight.setOrigin(Align.center);
        bilbilakRight.setRotation(180f);
        bilbilakRight.setVisible(false);

        stage.addActor(bilbilakLeft);
        stage.addActor(bilbilakRight);

        Label titleLabel = new Label("KEYBOARD", menuSkin);
        titleLabel.setFontScale(1.4f);
        titleLabel.setAlignment(Align.center);
        titleLabel.setPosition((stage.getWidth() - titleLabel.getWidth()) / 2f, stage.getHeight() * 0.75f);
        stage.addActor(titleLabel);

        Texture logoTexture = new Texture("boardBottom.png");
        Image logoImage = new Image(logoTexture);
        logoImage.setPosition(
            (stage.getWidth() - logoImage.getWidth()) / 2f,
            stage.getHeight() * 0.5f
        );
        stage.addActor(logoImage);

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top();
        mainTable.add().height(stage.getHeight() * 0.45f).colspan(4).row();

        ImageTextButton.ImageTextButtonStyle keyStyle = menuSkin.get("default", ImageTextButton.ImageTextButtonStyle.class);

        float labelWidth = 200f;
        float keyWidth = 150f;
        float columnGap = 50f;

        var inputHandler = controller.getInputHandler();

        keyButtonsMap.clear();

        mainTable.add(createActionLabel(PlayerAction.MOVE_RIGHT.getDisplayName())).width(labelWidth).left();
        mainTable.add(createKeyButton(PlayerAction.MOVE_RIGHT, keyStyle)).width(keyWidth).padRight(columnGap);
        mainTable.add(createActionLabel(PlayerAction.MOVE_LEFT.getDisplayName())).width(labelWidth).left();
        mainTable.add(createKeyButton(PlayerAction.MOVE_LEFT, keyStyle)).width(keyWidth).row();

        mainTable.add(createActionLabel(PlayerAction.JUMP.getDisplayName())).width(labelWidth).left();
        mainTable.add(createKeyButton(PlayerAction.JUMP, keyStyle)).width(keyWidth).padRight(columnGap);
        mainTable.add(createActionLabel(PlayerAction.FOCUS_SOUL.getDisplayName())).width(labelWidth).left();
        mainTable.add(createKeyButton(PlayerAction.FOCUS_SOUL, keyStyle)).width(keyWidth).row();

        mainTable.add(createActionLabel(PlayerAction.ATTACK.getDisplayName())).width(labelWidth).left();
        mainTable.add(createKeyButton(PlayerAction.ATTACK, keyStyle)).width(keyWidth).padRight(columnGap);
        mainTable.add(createActionLabel(PlayerAction.DASH.getDisplayName())).width(labelWidth).left();
        mainTable.add(createKeyButton(PlayerAction.DASH, keyStyle)).width(keyWidth).row();


        mainTable.defaults().reset();

        ImageTextButton resetBtn = new ImageTextButton("RESET DEFAULTS", menuSkin, "mute");
        resetBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioManager.clickMenuSound();
                inputHandler.reset();

                for (Map.Entry<PlayerAction, ImageTextButton> entry : keyButtonsMap.entrySet()) {
                    PlayerAction action = entry.getKey();
                    ImageTextButton btnOnScreen = entry.getValue();
                    btnOnScreen.setText(inputHandler.getKeyNameFor(action));
                }
            }
        });
        MainMenuView.setupButtonAnimation(resetBtn, bilbilakLeft, bilbilakRight);
        mainTable.add(resetBtn).colspan(4).width(380f).padTop(35f).row();

        ImageTextButton backBtn = new ImageTextButton("BACK", menuSkin, "default");
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioManager.clickMenuSound();
                controller.openSettings();
            }
        });
        MainMenuView.setupButtonAnimation(backBtn, bilbilakLeft, bilbilakRight);
        mainTable.add(backBtn).colspan(4).width(220f).padTop(15f);

        stage.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (actionBeingRebound != null && buttonBeingRebound != null) {
                    inputHandler.rebind(actionBeingRebound, keycode);
                    buttonBeingRebound.setText(com.badlogic.gdx.Input.Keys.toString(keycode));
                    actionBeingRebound = null;
                    buttonBeingRebound = null;
                    audioManager.clickMenuSound();
                    return true;
                }
                return false;
            }
        });

        stage.addActor(mainTable);
    }

    private Label createActionLabel(String text) {
        Label label = new Label(text, menuSkin, "settingmenu");
        label.setAlignment(Align.left);
        return label;
    }

    private ImageTextButton createKeyButton(final PlayerAction action, ImageTextButton.ImageTextButtonStyle style) {
        var inputHandler = controller.getInputHandler();
        String currentKeyName = inputHandler.getKeyNameFor(action);
        final ImageTextButton btn = new ImageTextButton(currentKeyName, style);

        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioManager.clickMenuSound();
                if (buttonBeingRebound != null && actionBeingRebound != null) {
                    buttonBeingRebound.setText(controller.getInputHandler().getKeyNameFor(actionBeingRebound));
                }
                actionBeingRebound = action;
                buttonBeingRebound = btn;
                btn.setText("...");
            }
        });

        keyButtonsMap.put(action, btn);

        return btn;
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
    }
}
