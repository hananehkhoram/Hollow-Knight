package hana.HollowKnight.view.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import hana.HollowKnight.controller.GameController;
import hana.HollowKnight.controller.InputHandler;

public class GuideScreen extends BaseScreen {
    private Stage stage;
    private Skin menuSkin;
    private Texture logoTexture;

    public GuideScreen(GameController controller) {
        super(controller);
    }

    private Label createActionLabel(String text) {
        Label label = new Label(text, menuSkin, "settingmenu");
        label.setAlignment(Align.left);
        label.setColor(Color.LIGHT_GRAY);
        return label;
    }

    private ImageTextButton createStaticKeyButton(InputHandler.PlayerAction action, ImageTextButton.ImageTextButtonStyle style) {
        var inputHandler = controller.getInputHandler();
        String currentKeyName = inputHandler.getKeyNameFor(action);
        ImageTextButton btn = new ImageTextButton(currentKeyName, style);
        btn.setDisabled(true);
        return btn;
    }

    private Label createSectionTitle(String text) {
        Label label = new Label(text , menuSkin, "Guide");
        label.setColor(Color.GOLDENROD);
        label.setFontScale(1.1f);
        label.setAlignment(Align.center);
        return label;
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
            stage.getHeight() * 0.58f
        );
        stage.addActor(logoImage);

        Image bilbilakLeft = new Image(menuSkin.getDrawable("main_menu_pointer_anim0009"));
        bilbilakLeft.setOrigin(Align.center);
        bilbilakLeft.setVisible(false);

        Image bilbilakRight = new Image(menuSkin.getDrawable("main_menu_pointer_anim0009"));
        bilbilakRight.setOrigin(Align.center);
        bilbilakRight.setRotation(180f);
        bilbilakRight.setVisible(false);

        Label settinglabel = new Label("GUIDE MENU", menuSkin);
        settinglabel.setFontScale(1.4f);
        settinglabel.setColor(Color.WHITE);
        settinglabel.setAlignment(Align.center);
        settinglabel.pack();
        settinglabel.setPosition(
            (stage.getWidth() - settinglabel.getWidth()) / 2f,
            stage.getHeight() * 0.86f
        );
        stage.addActor(settinglabel);

        Table contentTable = new Table();
        contentTable.top().pad(100f);

        ImageTextButton.ImageTextButtonStyle keyStyle = menuSkin.get("default", ImageTextButton.ImageTextButtonStyle.class);

        float labelWidth = 220f;
        float keyWidth = 110f;
        float columnGap = 50f;

        contentTable.add(createSectionTitle("CONTROLS")).colspan(4).padBottom(20f).center().row();

        contentTable.add(createActionLabel(InputHandler.PlayerAction.MOVE_RIGHT.getDisplayName())).width(labelWidth).left();
        contentTable.add(createStaticKeyButton(InputHandler.PlayerAction.MOVE_RIGHT, keyStyle)).width(keyWidth).padRight(columnGap);
        contentTable.add(createActionLabel(InputHandler.PlayerAction.MOVE_LEFT.getDisplayName())).width(labelWidth).left();
        contentTable.add(createStaticKeyButton(InputHandler.PlayerAction.MOVE_LEFT, keyStyle)).width(keyWidth).row();

        contentTable.add(createActionLabel(InputHandler.PlayerAction.JUMP.getDisplayName())).width(labelWidth).left().padTop(8f);
        contentTable.add(createStaticKeyButton(InputHandler.PlayerAction.JUMP, keyStyle)).width(keyWidth).padRight(columnGap).padTop(8f);
        contentTable.add(createActionLabel(InputHandler.PlayerAction.FOCUS_SOUL.getDisplayName())).width(labelWidth).left().padTop(8f);
        contentTable.add(createStaticKeyButton(InputHandler.PlayerAction.FOCUS_SOUL, keyStyle)).width(keyWidth).padTop(8f).row();

        contentTable.add(createActionLabel(InputHandler.PlayerAction.ATTACK.getDisplayName())).width(labelWidth).left().padTop(8f);
        contentTable.add(createStaticKeyButton(InputHandler.PlayerAction.ATTACK, keyStyle)).width(keyWidth).padRight(columnGap).padTop(8f);
        contentTable.add(createActionLabel(InputHandler.PlayerAction.DASH.getDisplayName())).width(labelWidth).left().padTop(8f);
        contentTable.add(createStaticKeyButton(InputHandler.PlayerAction.DASH, keyStyle)).width(keyWidth).padTop(8f).padBottom(35f).row();

        contentTable.add(createSectionTitle("ABILITIES & SOUL SYSTEM")).colspan(4).padBottom(15f).center().row();

        String healthInfo = "• Health (Masks): Represents your life. If it hits zero, you die.\n" +
            "• Soul Gauge: Strike enemies with your Nail to gather Soul.\n" +
            "• Focus: Hold the Focus button to spend Soul and heal your Masks.\n" +
            "• Vengeful Spirit: Release gathered Soul as a powerful ranged projectile.";

        Label infoLabel = new Label(healthInfo, menuSkin, "settingmenu");
        infoLabel.setWrap(true);
        infoLabel.setAlignment(Align.left);
        infoLabel.setColor(Color.WHITE);
        contentTable.add(infoLabel).colspan(4).width(740f).padBottom(35f).left().row();

        contentTable.add(createSectionTitle("CHEAT CODES")).colspan(4).padBottom(15f).center().row();

        String cheatInfo = "• GHOST: Press [G] to toggle God Mode (Invincibility).\n" +
            "• GEOS: Press [M] to add 1000 Geo instantly.\n" +
            "• INF_SOUL: Press [P] to fully restore your Soul Gauge.";

        Label cheatLabel = new Label(cheatInfo, menuSkin, "settingmenu");
        cheatLabel.setWrap(true);
        cheatLabel.setAlignment(Align.left);
        cheatLabel.setColor(Color.WHITE);
        contentTable.add(cheatLabel).colspan(4).width(740f).padBottom(15f).left().row();

        ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle();
        ScrollPane scrollPane = new ScrollPane(contentTable, scrollStyle);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top();
        mainTable.add().height(stage.getHeight() * 0.20f).row();

        if (menuSkin.has("textfield", com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle.class)) {
            mainTable.setBackground(menuSkin.getDrawable("textfield"));
        }

        mainTable.add(scrollPane).width(820f).height(stage.getHeight() * 0.5f).row();

        ImageTextButton.ImageTextButtonStyle baseStyle = menuSkin.get("default", ImageTextButton.ImageTextButtonStyle.class);
        ImageTextButton.ImageTextButtonStyle buttonStyle = new ImageTextButton.ImageTextButtonStyle(baseStyle);

        ImageTextButton back = new ImageTextButton("Back", buttonStyle);
        back.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                audioManager.clickMenuSound();
                controller.goToMainMenu();
            }
        });
        MainMenuView.setupButtonAnimation(back, bilbilakLeft, bilbilakRight);

        mainTable.add(back).width(280f).padTop(25f);

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
