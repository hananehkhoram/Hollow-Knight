package hana.HollowKnight.view.overlays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import hana.HollowKnight.model.stats.GameStats;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class AchievementPopup implements GameStats.AchievementListener {

    private static final float SLIDE_DURATION = 0.2f;
    private static final float HOLD_DURATION = 2.6f;

    private final Stage stage;
    private final Skin menuSkin;
    private final Texture panelTexture;
    private final Map<GameStats.Achievement, Texture> iconTextures = new HashMap<>();

    private final Queue<GameStats.Achievement> queue = new LinkedList<>();
    private GameStats.Achievement current;

    private final Table banner;
    private final Image iconImage;
    private final Label titleLabel;
    private final Label descLabel;

    public AchievementPopup(SpriteBatch batch) {
        stage = new Stage(new ScreenViewport(), batch);
        menuSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0.05f, 0.05f, 0.05f, 0.85f);
        pixmap.fill();
        panelTexture = new Texture(pixmap);
        pixmap.dispose();

        banner = new Table();
        banner.setBackground(new TextureRegionDrawable(new TextureRegion(panelTexture)));
        banner.pad(12f);
        banner.setVisible(false);

        iconImage = new Image();
        Label header = new Label("ACHIEVEMENT UNLOCKED", menuSkin, "settingmenu");
        titleLabel = new Label("", menuSkin);
        titleLabel.setFontScale(1.05f);
        descLabel = new Label("", menuSkin, "settingmenu");
        descLabel.setWrap(true);

        Table textTable = new Table();
        textTable.left().top();
        textTable.add(header).left().row();
        textTable.add(titleLabel).left().padTop(2f).row();
        textTable.add(descLabel).left().width(320f).padTop(4f);

        banner.add(iconImage).size(56f, 56f).padRight(15f);
        banner.add(textTable);

        stage.addActor(banner);
    }

    @Override
    public void onAchievementUnlocked(GameStats.Achievement achievement) {
        queue.add(achievement);
    }

    public void render(float delta) {
        if (current == null && !queue.isEmpty()) {
            current = queue.poll();
            showBanner(current);
        }
        stage.act(delta);
        stage.draw();
    }

    private void showBanner(GameStats.Achievement achievement) {
        Texture icon = iconTextures.computeIfAbsent(achievement, a ->
            new Texture(Gdx.files.internal("achievement_" + a.name().toLowerCase() + ".png")));
        iconImage.setDrawable(new TextureRegionDrawable(new TextureRegion(icon)));
        titleLabel.setText(achievement.getTitle());
        descLabel.setText(achievement.getDescription());

        banner.pack();
        float x = (stage.getWidth() - banner.getWidth()) / 2f;
        float hiddenY = stage.getHeight() + 10f;
        float shownY = stage.getHeight() - banner.getHeight() - 30f;

        banner.setPosition(x, hiddenY);
        banner.setVisible(true);
        banner.clearActions();
        banner.addAction(Actions.sequence(
            Actions.moveTo(x, shownY, SLIDE_DURATION, Interpolation.exp10Out),
            Actions.delay(HOLD_DURATION),
            Actions.moveTo(x, hiddenY, SLIDE_DURATION, Interpolation.exp10In),
            Actions.run(() -> {
                banner.setVisible(false);
                current = null;
            })
        ));
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
        menuSkin.dispose();
        panelTexture.dispose();
        for (Texture t : iconTextures.values()) t.dispose();
    }
}
