package hana.HollowKnight.view.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import hana.HollowKnight.controller.GameController;
import hana.HollowKnight.model.entities.PlayerModel;
import hana.HollowKnight.model.items.CharmType;
import hana.HollowKnight.view.audio.AudioManager;

public class PauseMenu {

    private final GameController controller;
    private final Runnable onResume;

    private final Stage stage;
    private final Skin menuSkin;
    private final Texture dimTexture;
    private final Table root;

    public PauseMenu(GameController controller, Runnable onResume) {
        this.controller = controller;
        this.onResume = onResume;

        this.stage = new Stage(new ScreenViewport(), controller.getBatch());
        this.menuSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0.6f);
        pixmap.fill();
        dimTexture = new Texture(pixmap);
        pixmap.dispose();

        Image dim = new Image(dimTexture);
        dim.setFillParent(true);
        stage.addActor(dim);

        root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        showMainPanel();
    }

    public Stage getStage() {
        return stage;
    }

    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    public void showMainPanel() {
        root.clearChildren();

        Label title = new Label("PAUSED", menuSkin);
        title.setFontScale(1.4f);
        root.add(title).padBottom(30f).row();

        root.add(menuButton("RESUME", onResume)).width(320f).height(55f).padBottom(15f).row();
        root.add(menuButton("SETTINGS", this::showSettingsPanel)).width(320f).height(55f).padBottom(15f).row();
        root.add(menuButton("SAVE & QUIT", controller::saveCurrentAndExit)).width(320f).height(55f).padBottom(15f).row();
        Label cheats = new Label("CHEAT CODES:", menuSkin, "settingmenu");
        root.add(cheats).padBottom(15f).row();
        root.add(new Label(" Boss Arena Teleport - \"BT\"", menuSkin, "settingmenu")).row();
        root.add(new Label(" Spectator Mode  - \"SM\"", menuSkin, "settingmenu")).row();
        root.add(new Label(" Emergency Heal - \"EH\"", menuSkin, "settingmenu")).row();
        root.add(new Label(" Refill Soul Vessel - \"RS\"", menuSkin, "settingmenu")).row();
        root.add(new Label(" God Mode - \"GM\"", menuSkin, "settingmenu")).row();
        root.add(new Label("Boss Kill - \"BK\"", menuSkin, "settingmenu")).row();


    }

    private void showSettingsPanel() {
        root.clearChildren();
        AudioManager audio = AudioManager.getInstance();

        Label title = new Label("SETTINGS", menuSkin);
        title.setFontScale(1.2f);
        root.add(title).colspan(3).padBottom(25f).row();

        ImageTextButton.ImageTextButtonStyle baseStyle =
            menuSkin.get("default", ImageTextButton.ImageTextButtonStyle.class);

        root.add(smallButton("-", baseStyle,
                () -> audio.setBgmVolume(Math.max(0f, audio.getBgmVolume() - 0.1f))))
            .width(80f).padRight(20f);
        root.add(new Label("Music Volume", menuSkin, "settingmenu")).width(260f);
        root.add(smallButton("+", baseStyle,
                () -> audio.setBgmVolume(Math.min(1f, audio.getBgmVolume() + 0.1f))))
            .width(80f).padLeft(20f).row();

        root.add(smallButton("MUTE", baseStyle, () -> audio.setVolume(0f)))
            .width(80f).padRight(20f).padTop(15f);
        root.add(new Label("Sound Volume", menuSkin, "settingmenu")).width(260f).padTop(15f);
        root.add(smallButton("UNMUTE", baseStyle, () -> audio.setVolume(1f)))
            .width(80f).padLeft(20f).padTop(15f).row();

        root.add(smallButton("-", baseStyle,
                () -> GameController.brightness = Math.max(0.2f, GameController.brightness - 0.1f)))
            .width(80f).padRight(20f).padTop(15f);
        root.add(new Label("Brightness", menuSkin, "settingmenu")).width(260f).padTop(15f);
        root.add(smallButton("+", baseStyle,
                () -> GameController.brightness = Math.min(1f, GameController.brightness + 0.1f)))
            .width(80f).padLeft(20f).padTop(15f).row();

        root.add(menuButton("BACK", this::showMainPanel)).colspan(3).width(320f).height(55f).padTop(30f);
    }

    private void showCharmsPanel() {
        root.clearChildren();
        PlayerModel player = controller.getModel().getPlayer();

        Label title = new Label("CHARMS  (" + player.getUsedNotches() + " / " + player.getMaxNotches() + " notches)", menuSkin);
        title.setFontScale(1.1f);
        root.add(title).padBottom(20f).row();

        for (CharmType type : CharmType.values()) {
            boolean unlocked = player.isCharmUnlocked(type);
            boolean equipped = player.getEquippedCharms().contains(type);

            String label = formatCharmName(type) + (equipped ? "  [EQUIPPED]" : unlocked ? "" : "  [LOCKED]");
            ImageTextButton btn = new ImageTextButton(label, menuSkin, unlocked ? "default" : "mute");
            btn.setTouchable(unlocked ? Touchable.enabled : Touchable.disabled);

            btn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    AudioManager.getInstance().clickMenuSound();
                    if (player.getEquippedCharms().contains(type)) {
                        player.unequipCharm(type);
                    } else {
                        player.equipCharm(type); // no-ops if not enough free notches
                    }
                    showCharmsPanel(); // rebuild to refresh labels + notch count
                }
            });

            root.add(btn).width(420f).height(45f).padBottom(8f).row();
        }

        root.add(menuButton("BACK", this::showMainPanel)).width(320f).height(55f).padTop(20f);
    }

    private String formatCharmName(CharmType type) {
        String[] words = type.name().split("_");
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            sb.append(w.charAt(0)).append(w.substring(1).toLowerCase()).append(' ');
        }
        return sb.toString().trim();
    }

    private ImageTextButton menuButton(String text, Runnable action) {
        ImageTextButton btn = new ImageTextButton(text, menuSkin, "mute");
        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                AudioManager.getInstance().clickMenuSound();
                action.run();
            }
        });
        return btn;
    }

    private ImageTextButton smallButton(String text, ImageTextButton.ImageTextButtonStyle style, Runnable action) {
        ImageTextButton btn = new ImageTextButton(text, style);
        btn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                AudioManager.getInstance().clickMenuSound();
                action.run();
            }
        });
        return btn;
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
        menuSkin.dispose();
        dimTexture.dispose();
    }
}
