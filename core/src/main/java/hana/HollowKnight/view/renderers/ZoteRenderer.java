package hana.HollowKnight.view.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import hana.HollowKnight.model.entities.ZoteModel;

public class ZoteRenderer {
    private final String baseDir = "Animations/zote/";

    private final TextureAtlas idleAtlas;
    private final Animation<TextureAtlas.AtlasRegion> idleAnimation;
    private final TextureAtlas talkAtlas;
    private final Animation<TextureAtlas.AtlasRegion> talkAnimation;
    private final TextureAtlas attackAtlas;
    private final Animation<TextureAtlas.AtlasRegion> attackAnimation;
    private final TextureAtlas rollAtlas;
    private final Animation<TextureAtlas.AtlasRegion> rollAnimation;
    private final TextureAtlas getUpAtlas;
    private final Animation<TextureAtlas.AtlasRegion> getUpAnimaton;
    private final TextureAtlas fallAtlas;
    private final Animation<TextureAtlas.AtlasRegion> fallAnimation;

    private final float SPRITE_WIDTH = 349;
    private final float SPRITE_HEIGHT = 186;

    float stateTime = 0;

    public ZoteRenderer() {
        idleAtlas = new TextureAtlas(Gdx.files.internal(baseDir + "idle.atlas"));
        idleAnimation = new Animation<>(0.1f, idleAtlas.findRegions("Idle"), Animation.PlayMode.LOOP);

        talkAtlas = new TextureAtlas(Gdx.files.internal(baseDir + "talk.atlas"));
        talkAnimation = new Animation<>(0.1f, talkAtlas.findRegions("Talk"), Animation.PlayMode.LOOP);

        rollAtlas = new TextureAtlas(Gdx.files.internal(baseDir + "roll.atlas"));
        rollAnimation = new Animation<>(0.08f, rollAtlas.findRegions("Roll"), Animation.PlayMode.LOOP);

        attackAtlas = new TextureAtlas(Gdx.files.internal(baseDir + "attack.atlas"));
        attackAnimation = new Animation<>(0.08f, attackAtlas.findRegions("Attack"), Animation.PlayMode.LOOP);

        getUpAtlas = new TextureAtlas(Gdx.files.internal(baseDir + "getup.atlas"));
        getUpAnimaton = new Animation<>(0.05f, getUpAtlas.findRegions("Get Up"), Animation.PlayMode.NORMAL);

        fallAtlas = new TextureAtlas(Gdx.files.internal(baseDir + "fall.atlas"));
        fallAnimation = new Animation<>(0.1f, fallAtlas.findRegions("Fall"), Animation.PlayMode.LOOP);
    }

    public void render(SpriteBatch batch, ZoteModel zote) {
        float deltaTime = Gdx.graphics.getDeltaTime();
        stateTime += deltaTime;

        ZoteModel.States state = zote.getState();
        float currentAnimTime = zote.getStateTimer();

        TextureRegion region;
        switch (state) {
            case IDLE:
                region = idleAnimation.getKeyFrame(currentAnimTime);
                break;
            case TALK:
                region = talkAnimation.getKeyFrame(currentAnimTime);
                break;
            case ATTACK:
                region = attackAnimation.getKeyFrame(currentAnimTime);
                break;
            case ROLL:
                region = rollAnimation.getKeyFrame(currentAnimTime);
                break;
            case FALL:
                region = fallAnimation.getKeyFrame(currentAnimTime);
                break;
            case GETUP:
                region = getUpAnimaton.getKeyFrame(currentAnimTime);
                break;
            default:
                region = idleAnimation.getKeyFrame(currentAnimTime);
        }
        if (zote.isFacingRight() && !region.isFlipX() || !zote.isFacingRight() && region.isFlipX()){
            region.flip(true, false);
        }

        float offsetX = (SPRITE_WIDTH - zote.getWidth()) / 2f;
        float offsetY = 0f;
        batch.draw(
            region,
            zote.getX() - offsetX,
            zote.getY() - offsetY,
            SPRITE_WIDTH,
            SPRITE_HEIGHT
        );
    }
    public void dispose() {
        idleAtlas.dispose();
        rollAtlas.dispose();
        fallAtlas.dispose();
        talkAtlas.dispose();
        getUpAtlas.dispose();
        attackAtlas.dispose();
    }

}
