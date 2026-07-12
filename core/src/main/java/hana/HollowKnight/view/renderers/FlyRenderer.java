package hana.HollowKnight.view.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import hana.HollowKnight.model.entities.FlyModel;

public class FlyRenderer {
    private final String baseDir = "Animations/enemy/WingedSentry/";

    private final TextureAtlas idleAtlas;
    private final Animation<TextureAtlas.AtlasRegion> idleAnimation;
    private final TextureAtlas deathAtlas;
    private final Animation<TextureAtlas.AtlasRegion> deathAnimation;
    private final TextureAtlas attackAtlas;
    private final Animation<TextureAtlas.AtlasRegion> attackAnimation;
    private final TextureAtlas flyAtlas;
    private final Animation<TextureAtlas.AtlasRegion> flyAnimation;
    private final Animation<TextureAtlas.AtlasRegion> death2Animation;

    private final float SPRITE_WIDTH = 509;
    private final float SPRITE_HEIGHT = 398;

    public FlyRenderer() {
        idleAtlas = new TextureAtlas(Gdx.files.internal(baseDir + "idle.atlas"));
        idleAnimation = new Animation<>(0.05f, idleAtlas.findRegions("Idle"), Animation.PlayMode.LOOP);

        deathAtlas = new TextureAtlas(Gdx.files.internal(baseDir + "death.atlas"));
        deathAnimation = new Animation<>(0.1f, deathAtlas.findRegions("Death Air"), Animation.PlayMode.NORMAL);
        death2Animation = new Animation<>(0.1f, deathAtlas.findRegions("Death Land"), Animation.PlayMode.NORMAL);

        flyAtlas = new TextureAtlas(Gdx.files.internal(baseDir + "flying.atlas"));
        flyAnimation = new Animation<>(0.1f, flyAtlas.findRegions("Charge"), Animation.PlayMode.LOOP);

        attackAtlas = new TextureAtlas(Gdx.files.internal(baseDir + "attacking.atlas"));
        attackAnimation = new Animation<>(0.1f, attackAtlas.findRegions("Charge Antic"), Animation.PlayMode.NORMAL);
    }

    public void render(SpriteBatch batch, FlyModel fly) {
        float stateTime = fly.getAnimationTimer();
        FlyModel.State status = fly.getState();

        TextureRegion region;
        switch (status) {
            case DEATH:
                region = deathAnimation.getKeyFrame(stateTime);
                break;
            case DEATH2:
                region = death2Animation.getKeyFrame(stateTime);
                break;
            case SPOTTED:
                region = attackAnimation.getKeyFrame(stateTime);
                break;
            case ATTACK:
                region = flyAnimation.getKeyFrame(stateTime);
                break;
            case IDLE:
            default:
                region = idleAnimation.getKeyFrame(stateTime);
                break;
        }

        if (!fly.isFacingRight() && region.isFlipX()) {
            region.flip(true, false);
        } else if (fly.isFacingRight() && !region.isFlipX()) {
            region.flip(true, false);
        }

        float offsetX = (SPRITE_WIDTH - fly.getWidth()) / 2f;
        float offsetY = (SPRITE_HEIGHT - fly.getHeight()) / 2f + 50;

        batch.draw(
            region,
            fly.getX() - offsetX,
            fly.getY() - offsetY,
            SPRITE_WIDTH,
            SPRITE_HEIGHT
        );
    }

    public void dispose() {
        idleAtlas.dispose();
        deathAtlas.dispose();
        attackAtlas.dispose();
        flyAtlas.dispose();
    }
}
