package hana.HollowKnight.view.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import hana.HollowKnight.controller.GameController;
import hana.HollowKnight.model.GameModel;
import hana.HollowKnight.model.items.Charm;
import hana.HollowKnight.model.items.CharmType;
import hana.HollowKnight.view.audio.AudioManager;
import hana.HollowKnight.view.screens.MainMenuView;

import java.util.HashMap;

public class InventoryMenu {

    private final GameController controller;

    private final Stage stage;
    private final Skin menuSkin;
    private final Texture dimTexture;
    private final Table root;

    private final Label settinglabel;
    private final Image logoImage;
    private final Image bilbilakLeft;
    private final Image bilbilakRight;
    private final HashMap<CharmType, Texture> charmTextures;

    public InventoryMenu(GameController controller, Runnable onResume) {
        this.controller = controller;

        this.stage = new Stage(new ScreenViewport(), controller.getBatch());
        this.menuSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0.85f);
        pixmap.fill();
        dimTexture = new Texture(pixmap);
        pixmap.dispose();

        Image dim = new Image(dimTexture);
        dim.setFillParent(true);
        stage.addActor(dim);

        root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        settinglabel = new Label("Inventory", menuSkin);
        settinglabel.setFontScale(1f);
        settinglabel.setAlignment(Align.center);
        stage.addActor(settinglabel);

        logoImage = new Image(new Texture(Gdx.files.internal("boardBottom.png")));
        logoImage.setOrigin(Align.center);
        stage.addActor(logoImage);

        bilbilakLeft = new Image(menuSkin.getDrawable("main_menu_pointer_anim0009"));
        bilbilakLeft.setOrigin(Align.center);
        bilbilakLeft.setVisible(false);

        bilbilakRight = new Image(menuSkin.getDrawable("main_menu_pointer_anim0009"));
        bilbilakRight.setOrigin(Align.center);
        bilbilakRight.setRotation(180f);
        bilbilakRight.setVisible(false);

        stage.addActor(bilbilakLeft);
        stage.addActor(bilbilakRight);

        charmTextures = new HashMap<>();
        GameModel model = controller.getModel();
        for (CharmType type : CharmType.values()) {
            Charm charm = model.getCharms().get(type);
            if (charm != null) {
                charmTextures.put(type, new Texture(Gdx.files.internal(charm.getPicPath())));
            }
        }
    }

    public Stage getStage() {
        return stage;
    }

    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    public void show() {
        root.clearChildren();

        settinglabel.pack();
        settinglabel.setPosition(
            (stage.getWidth() - settinglabel.getWidth()) / 2f,
            stage.getHeight() * 0.75f
        );

        logoImage.setPosition(
            (stage.getWidth() - logoImage.getWidth()) / 2f,
            stage.getHeight() * 0.5f
        );

        bilbilakLeft.setVisible(false);
        bilbilakRight.setVisible(false);

        GameModel model = controller.getModel();
        HashMap<CharmType, Charm> charms = model.getCharms();

        root.add().height(stage.getHeight() * 0.35f).colspan(4).row();

        int i = 0;
        for (CharmType charm : CharmType.values()) {
            Charm theCharm = charms.get(charm);
            if (theCharm == null) continue;

            Table charmTable = new Table();
            charmTable.center().top();

            Texture tex = charmTextures.get(charm);
            Image img = new Image(tex);
            changeColor(theCharm.isEquipped(),theCharm.isUnlocked(), img);
            charmTable.add(img).row();

            ImageTextButton label = new ImageTextButton(theCharm.getType().toString(), menuSkin, "mute");
            label.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    AudioManager.getInstance().clickMenuSound();
                    theCharm.toggleEquipped();
                    changeColor(theCharm.isEquipped(),theCharm.isUnlocked(), img);
                }
            });
            MainMenuView.setupButtonAnimation(label, bilbilakLeft, bilbilakRight);

            Label dis = new Label(charm.getDescription(), menuSkin, "settingmenu");
            dis.setFontScale(0.6f);
            dis.setWrap(true);
            dis.setAlignment(Align.center);

            charmTable.add(label).padBottom(10f).row();
            charmTable.add(dis).width(140f).center();

            i++;
            if (i % 4 == 0) {
                root.add(charmTable).pad(15f).top().row();
            } else {
                root.add(charmTable).pad(15f).top();
            }
        }

        bilbilakLeft.toFront();
        bilbilakRight.toFront();
    }

    public void changeColor(boolean isEquipped, boolean isUnlocked,Image img) {
        if (isEquipped && isUnlocked) {
            img.setColor(com.badlogic.gdx.graphics.Color.WHITE);
        } else {
            img.setColor(new com.badlogic.gdx.graphics.Color(0.25f, 0.25f, 0.25f, 0.8f));
        }
    }

    public void dispose() {
        stage.dispose();
        menuSkin.dispose();
        dimTexture.dispose();
        logoImage.getDrawable().setMinHeight(0);
        for (Texture tex : charmTextures.values()) {
            tex.dispose();
        }
    }
}
