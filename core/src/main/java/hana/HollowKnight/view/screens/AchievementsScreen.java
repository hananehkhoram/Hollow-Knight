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
import hana.HollowKnight.model.stats.GameStats;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AchievementsScreen extends BaseScreen {

    private Stage stage;
    private Skin menuSkin;
    private Texture logoTexture;

    private final Map<GameStats.Achievement, Texture> iconTextures = new HashMap<>();

    public AchievementsScreen(GameController controller) {
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
            stage.getHeight() * 0.6f
        );
        stage.addActor(logoImage);

        Image bilbilakLeft = new Image(menuSkin.getDrawable("main_menu_pointer_anim0009"));
        bilbilakLeft.setOrigin(Align.center);
        bilbilakLeft.setVisible(false);

        Image bilbilakRight = new Image(menuSkin.getDrawable("main_menu_pointer_anim0009"));
        bilbilakRight.setOrigin(Align.center);
        bilbilakRight.setRotation(180f);
        bilbilakRight.setVisible(false);

        Label titleLabel = new Label("ACHIEVEMENTS", menuSkin);
        titleLabel.setAlignment(Align.center);
        titleLabel.pack();
        titleLabel.setPosition(
            (stage.getWidth() - titleLabel.getWidth()) / 2f,
            stage.getHeight() * 0.85f
        );
        stage.addActor(titleLabel);

        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top();
        mainTable.add().height(stage.getHeight() * 0.25f).row();

        for (GameStats.Achievement a: GameStats.Achievement.values()) {
            String path = "achievement_" + a.toString().toLowerCase() + ".png";
            iconTextures.put(a, new Texture(Gdx.files.internal(path)));
        }

        Set<GameStats.Achievement> unlockedAchievements =
            controller.getModel().getStats().getUnlockedAchievements();

        Table cardsTable = new Table();
        cardsTable.defaults().padBottom(20f);

        for (GameStats.Achievement a : GameStats.Achievement.values()) {
            boolean isUnlocked = unlockedAchievements.contains(a);
            Table aCard = createAchievementCard(a, isUnlocked);
            cardsTable.add(aCard).width(650f).row();
        }

        mainTable.add(cardsTable).row();

        ImageTextButton back = new ImageTextButton("Back", menuSkin);
        back.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                audioManager.clickMenuSound();
                controller.goToMainMenu();
            }
        });
        MainMenuView.setupButtonAnimation(back, bilbilakLeft, bilbilakRight);
        mainTable.add(back).width(300f).padTop(20f);

        stage.addActor(mainTable);
        stage.addActor(bilbilakLeft);
        stage.addActor(bilbilakRight);
    }

    private Table createAchievementCard(GameStats.Achievement achievement, boolean isUnlocked) {
        Table card = new Table();
        Texture rawTexture = iconTextures.get(achievement);
        Image iconImage = new Image(rawTexture);

        Label titleLabel = new Label(achievement.getTitle(), menuSkin, "Guide");
        Label descLabel = new Label(achievement.getDescription(), menuSkin, "settingmenu");
        descLabel.setWrap(true);

        if (!isUnlocked) {
            iconImage.setColor(0.25f, 0.25f, 0.25f, 0.8f);

            titleLabel.setColor(Color.DARK_GRAY);
            descLabel.setColor(Color.GRAY);
        } else {
            iconImage.setColor(Color.WHITE);
            titleLabel.setColor(Color.GOLDENROD);
            descLabel.setColor(Color.WHITE);
        }

        card.left();
        card.add(iconImage).size(64f, 64f).pad(10f);

        Table textTable = new Table();
        textTable.left().top();
        textTable.add(titleLabel).left().row();
        textTable.add(descLabel).left().width(530f).padTop(4f);

        card.add(textTable).expandX().fillX().padLeft(15f);

        return card;
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
        for (Texture texture : iconTextures.values()) {
            texture.dispose();
        }
    }
}
