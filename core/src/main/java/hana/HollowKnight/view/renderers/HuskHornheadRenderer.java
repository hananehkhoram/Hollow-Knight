package hana.HollowKnight.view.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import hana.HollowKnight.model.entities.HuskHornheadModel;

public class HuskHornheadRenderer {
    private final String baseDir = "Animations/enemy/Husk/";

    private final TextureAtlas walkAtlas;
    private final Animation<TextureAtlas.AtlasRegion> walkAnimation;
    private final TextureAtlas idleAtlas;
    private final Animation<TextureAtlas.AtlasRegion> idleAnimation;
    private final TextureAtlas turnAtlas;
    private final Animation<TextureAtlas.AtlasRegion> turnAnimation;
    private final TextureAtlas anticipateAtlas;
    private final Animation<TextureAtlas.AtlasRegion> anticipateAnimation;
    private final TextureAtlas lungeAtlas;
    private final Animation<TextureAtlas.AtlasRegion> lungeAnimation;
    private final TextureAtlas deathAirAtlas;
    private final Animation<TextureAtlas.AtlasRegion> deathAirAnimation;
    private final TextureAtlas deathLandAtlas;
    private final Animation<TextureAtlas.AtlasRegion> deathLandAnimation;

    private static final float SPRITE_WIDTH = 200;
    private static final float SPRITE_HEIGHT = 200;

    public HuskHornheadRenderer() {
        walkAtlas = new TextureAtlas(Gdx.files.internal(baseDir + "walk.atlas"));
        walkAnimation = new Animation<>(0.08f, walkAtlas.findRegions("Walk"), Animation.PlayMode.LOOP);

        idleAtlas = new TextureAtlas(Gdx.files.internal(baseDir + "idle.atlas"));
        idleAnimation = new Animation<>(0.12f, idleAtlas.findRegions("Idle"), Animation.PlayMode.LOOP);

        turnAtlas = new TextureAtlas(Gdx.files.internal(baseDir + "turn.atlas"));
        turnAnimation = new Animation<>(0.08f, turnAtlas.findRegions("Turn"), Animation.PlayMode.NORMAL);

        anticipateAtlas = new TextureAtlas(Gdx.files.internal(baseDir + "attackanticipate.atlas"));
        anticipateAnimation = new Animation<>(0.08f, anticipateAtlas.findRegions("Attack Anticipate"), Animation.PlayMode.NORMAL);

        lungeAtlas = new TextureAtlas(Gdx.files.internal(baseDir + "attacklunge.atlas"));
        lungeAnimation = new Animation<>(0.06f, lungeAtlas.findRegions("Attack Lunge"), Animation.PlayMode.LOOP);

        deathAirAtlas = new TextureAtlas(Gdx.files.internal(baseDir + "deathair.atlas"));
        deathAirAnimation = new Animation<>(0.1f, deathAirAtlas.findRegions("Death Air"), Animation.PlayMode.NORMAL);

        deathLandAtlas = new TextureAtlas(Gdx.files.internal(baseDir + "deathland.atlas"));
        deathLandAnimation = new Animation<>(0.1f, deathLandAtlas.findRegions("Death Land"), Animation.PlayMode.NORMAL);
    }

    public void render(SpriteBatch batch, HuskHornheadModel husk) {
        float stateTime = husk.getStateTimer();

        TextureRegion region;
        switch (husk.getState()) {
            case IDLE:
                region = idleAnimation.getKeyFrame(stateTime);
                break;
            case TURN:
                region = turnAnimation.getKeyFrame(stateTime);
                break;
            case ATTACK_ANTICIPATE:
                region = anticipateAnimation.getKeyFrame(stateTime);
                break;
            case ATTACK_LUNGE:
                region = lungeAnimation.getKeyFrame(stateTime);
                break;
            case DEATH_AIR:
                region = deathAirAnimation.getKeyFrame(stateTime);
                break;
            case DEATH_LAND:
                region = deathLandAnimation.getKeyFrame(stateTime);
                break;
            case WALK:
            default:
                region = walkAnimation.getKeyFrame(stateTime);
                break;
        }

        if (!husk.isFacingRight() && region.isFlipX()) {
            region.flip(true, false);
        } else if (husk.isFacingRight() && !region.isFlipX()) {
            region.flip(true, false);
        }

        float offsetX = (SPRITE_WIDTH - husk.getWidth()) / 2f;
        float offsetY = 0f;

        batch.draw(
            region,
            husk.getX() - offsetX,
            husk.getY() - offsetY,
            SPRITE_WIDTH,
            SPRITE_HEIGHT
        );
    }

    public void dispose() {
        walkAtlas.dispose();
        idleAtlas.dispose();
        turnAtlas.dispose();
        anticipateAtlas.dispose();
        lungeAtlas.dispose();
        deathAirAtlas.dispose();
        deathLandAtlas.dispose();
    }
}
