package hana.HollowKnight.view.overlays;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import hana.HollowKnight.controller.GameController;
import hana.HollowKnight.model.entities.ZoteModel;
import hana.HollowKnight.view.audio.AudioManager;

public class ZoteDialog {

    private static final String PROMPT_TEXT = "Press ENTER";

    private final ZoteModel zote;
    private final Stage stage;
    private final Skin menuSkin;
    private final Texture panelTexture;

    private final Table root;
    private final Label dialogLabel;

    private boolean talking = false;

    public ZoteDialog(GameController controller, ZoteModel zote) {
        this.zote = zote;

        this.stage = new Stage(new ScreenViewport(), controller.getBatch());
        this.menuSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0.75f);
        pixmap.fill();
        panelTexture = new Texture(pixmap);
        pixmap.dispose();

        root = new Table();
        root.setBackground(new TextureRegionDrawable(new TextureRegion(panelTexture)));
        root.pad(16f);
        root.setVisible(false);
        stage.addActor(root);

        dialogLabel = new Label("", menuSkin, "settingmenu");
        dialogLabel.setAlignment(Align.center);
        dialogLabel.setWrap(true);
        root.add(dialogLabel).width(700f);
        dialogLabel.setFontScale(0.8f);
    }

    public Stage getStage() {
        return stage;
    }

    public void update(boolean playerInRange) {
        if (!playerInRange) {
            if (talking) endConversation();
            root.setVisible(false);
            return;
        }

        if (!talking) {
            showBox(PROMPT_TEXT);

            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                talking = true;
                AudioManager.getInstance().playZote();
                showBox(zote.talk());
            }
            return;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            AudioManager.getInstance().playZote();

            if (zote.getTalkingId() < 3) {
                showBox(zote.talk());
            } else {
                endConversation();
            }
        }
    }
    private void showBox(String text) {
        dialogLabel.setText(text);
        root.setVisible(true);
        root.pack();
        root.setPosition((stage.getWidth() - root.getWidth()) / 2f, stage.getHeight() - root.getHeight() - 40f);
    }

    private void endConversation() {
        talking = false;
        root.setVisible(false);
        zote.setTalking(false);
        zote.setState(ZoteModel.States.IDLE);
    }

    public boolean isTalking() {
        return talking;
    }

    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    public void dispose() {
        stage.dispose();
        menuSkin.dispose();
        panelTexture.dispose();
    }
}
