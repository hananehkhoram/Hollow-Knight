package hana.HollowKnight.view.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import hana.HollowKnight.controller.GameController;
import hana.HollowKnight.model.entities.ZoteModel;

/**
 * Talk-to-Zote dialog box.
 *
 * Call update(playerInRange) once every frame from GameView with whether the
 * player is currently close enough to Zote to interact, THEN call render(delta)
 * (render last, so the box draws on top of everything else).
 *
 * States:
 *  - out of range              -> nothing drawn at all
 *  - in range, not yet talking -> small "Press UP to talk" prompt, no dim background
 *  - talking                   -> dim background + Zote's current line;
 *                                 UP advances to the next line (his rules repeat forever)
 *  - walking out of range while talking closes the conversation automatically
 *    and resets ZoteModel's own talking/state flags to match
 *
 * NOTE: this reads the UP arrow key directly via Gdx.input rather than through
 * InputHandler, because InputHandler.PlayerAction.MOVE_UP exists as an enum value
 * but isn't actually bound to a key in InputHandler's keyBindings map yet — routing
 * through it as-is would NPE. If you want this rebindable later, bind MOVE_UP to
 * Input.Keys.UP in InputHandler's constructor/reset() and swap the check below to
 * InputHandler.getInstance().isJustPressed(PlayerAction.MOVE_UP).
 */
public class ZoteDialog {

    private static final String PROMPT_TEXT = "Press UP Arrow Key to talk to Zote the Mighty!";

    private final ZoteModel zote;
    private final Stage stage;
    private final Skin menuSkin;
    private final Texture dimTexture;

    private final Image dim;
    private final Table root;
    private final Label dialogLabel;

    private boolean talking = false;

    public ZoteDialog(GameController controller, ZoteModel zote) {
        this.zote = zote;

        this.stage = new Stage(new ScreenViewport(), controller.getBatch());
        this.menuSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0.5f);
        pixmap.fill();
        dimTexture = new Texture(pixmap);
        pixmap.dispose();

        dim = new Image(dimTexture);
        dim.setFillParent(true);
        dim.setVisible(false);
        stage.addActor(dim);

        root = new Table();
        root.setFillParent(true);
        root.bottom().padBottom(80f);
        root.setVisible(false);
        stage.addActor(root);

        dialogLabel = new Label("", menuSkin, "default");
        dialogLabel.setAlignment(Align.center);
        dialogLabel.setWrap(true);
        root.add(dialogLabel).width(800f);
    }

    public Stage getStage() {
        return stage;
    }

    /** Call once per frame with whether the player is currently in Zote's interaction range. */
    public void update(boolean playerInRange) {
        if (!playerInRange) {
            if (talking) endConversation();
            root.setVisible(false);
            return;
        }

        if (!talking) {
            root.setVisible(true);
            dim.setVisible(false);
            dialogLabel.setText(PROMPT_TEXT);

            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                talking = true;
                dim.setVisible(true);
                dialogLabel.setText(zote.talk());
            }
            return;
        }

        // Already talking — UP advances to Zote's next line.
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            dialogLabel.setText(zote.talk());
        }
    }

    private void endConversation() {
        talking = false;
        dim.setVisible(false);
        zote.setTalking(false);
        zote.setState(ZoteModel.States.IDLE);
    }

    /** GameView should skip controller.updateGameplay(delta) while this is true, so the player can't move/attack mid-conversation. */
    public boolean isTalking() {
        return talking;
    }

    public void render(float delta) {
        stage.act(delta);
        stage.draw();
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
