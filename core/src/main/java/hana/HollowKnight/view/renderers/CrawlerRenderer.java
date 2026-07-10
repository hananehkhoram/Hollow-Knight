package hana.HollowKnight.view.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import hana.HollowKnight.model.entities.CrawlerModel;

public class CrawlerRenderer {
    private final String baseDir;

    private final TextureAtlas walkAtlas;
    private final Animation<TextureAtlas.AtlasRegion> walkAnimation;
    private final TextureAtlas turnAtlas;
    private final Animation<TextureAtlas.AtlasRegion> turnAnimation;
    private final TextureAtlas deathAtlas;
    private final Animation<TextureAtlas.AtlasRegion> deathAnimation;

    private final float SPRITE_WIDTH = 157;
    private final float SPRITE_HEIGHT = 123;

    private float stateTime = 0f;
    private CrawlerModel.State currentStatus = CrawlerModel.State.WALK;

    public CrawlerRenderer(String name) {
        baseDir = "Animations/enemy/" + name + "/";
        walkAtlas = new TextureAtlas(Gdx.files.internal(baseDir + "walk.atlas"));
        walkAnimation = new Animation<>(0.05f, walkAtlas.findRegions("Walk"), Animation.PlayMode.LOOP);

        turnAtlas = new TextureAtlas(Gdx.files.internal(baseDir + "turn.atlas"));
        turnAnimation = new Animation<>(0.08f, turnAtlas.findRegions("Turn"), Animation.PlayMode.NORMAL);

        deathAtlas = new TextureAtlas(Gdx.files.internal(baseDir + "death.atlas"));
        deathAnimation = new Animation<>(0.1f, deathAtlas.findRegions("Death Air"), Animation.PlayMode.NORMAL);
    }

    public void render(SpriteBatch batch, CrawlerModel crawler) {
        float deltaTime = Gdx.graphics.getDeltaTime();
        stateTime += deltaTime;

        CrawlerModel.State nextStatus = crawler.getState();

        if (currentStatus != nextStatus) {
            stateTime = 0f;
            currentStatus = nextStatus;
        }

        TextureRegion region;
        switch (currentStatus) {
            case DEATH:
                region = deathAnimation.getKeyFrame(stateTime);
                break;
            case TURN:
                region = turnAnimation.getKeyFrame(stateTime);
                break;
            case WALK:
            default:
                region = walkAnimation.getKeyFrame(stateTime);
                break;
        }

        if (!crawler.isFacingRight() && region.isFlipX()) {
            region.flip(true, false);
        } else if (crawler.isFacingRight() && !region.isFlipX()) {
            region.flip(true, false);
        }

        float offsetX = (SPRITE_WIDTH - crawler.getWidth()) / 2f;
        float offsetY = 0f;

        batch.draw(
            region,
            crawler.getX() - offsetX,
            crawler.getY() - offsetY,
            SPRITE_WIDTH,
            SPRITE_HEIGHT
        );
    }

    public void dispose() {
        walkAtlas.dispose();
        turnAtlas.dispose();
        deathAtlas.dispose();
    }
}
