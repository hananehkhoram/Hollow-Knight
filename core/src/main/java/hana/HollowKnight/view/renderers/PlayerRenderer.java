package hana.HollowKnight.view.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import hana.HollowKnight.model.entities.PlayerModel;

import java.util.EnumMap;
import java.util.Map;

public class PlayerRenderer {

    private enum Status { IDLE, HURT, RUN, JUMP, DOUBLE_JUMP, FALL, ATTACK, FOCUS, DASH, MANTIS, DEATH, ATTACK2 }

    private final String baseDir = "Animations/knight/";

    private final Map<Status, TextureAtlas> atlases = new EnumMap<>(Status.class);
    private final Map<Status, Animation<TextureAtlas.AtlasRegion>> animations = new EnumMap<>(Status.class);

    private final Map<Status, TextureAtlas> effectAtlases = new EnumMap<>(Status.class);
    private final Map<Status, Animation<TextureAtlas.AtlasRegion>> effectAnimations = new EnumMap<>(Status.class);


    private final float SPRITE_WIDTH = 349f;
    private final float SPRITE_HEIGHT = 186f;

    private float stateTime = 0f;
    private Status currentStatus = Status.IDLE;

    public PlayerRenderer() {
        load(Status.IDLE, "idle.atlas", "Idle", 0.1f, Animation.PlayMode.LOOP);
        load(Status.HURT, "idleHurt.atlas", "Idle Hurt", 0.1f, Animation.PlayMode.LOOP);
        load(Status.RUN, "run.atlas", "Run", 0.08f, Animation.PlayMode.LOOP);
        load(Status.JUMP, "jumo.atlas", "Airborne", 0.1f, Animation.PlayMode.LOOP);
        load(Status.DOUBLE_JUMP, "doubljump.atlas", "Double Jump", 0.08f, Animation.PlayMode.NORMAL);
        load(Status.FALL, "fall.atlas", "Fall", 0.1f, Animation.PlayMode.LOOP);
        load(Status.ATTACK, "slash.atlas", "SlashAlt", 0.08f, Animation.PlayMode.NORMAL);
        load(Status.FOCUS, "focus.atlas", "Focus", 0.1f, Animation.PlayMode.NORMAL);
        load(Status.DASH, "dash.atlas", "Dash", 0.05f, Animation.PlayMode.NORMAL);
        load(Status.MANTIS, "wallSlide.atlas", "Wall Slide", 0.1f, Animation.PlayMode.NORMAL);
        load(Status.DEATH, "death.atlas", "Death", 0.1f, Animation.PlayMode.NORMAL);

        loadEffect(Status.ATTACK, "slashE.atlas", "SlashEffect", 0.02f, Animation.PlayMode.NORMAL);
        loadEffect(Status.DASH, "dashE.atlas", "Dash Effect", 0.1f, Animation.PlayMode.NORMAL);
        loadEffect(Status.DEATH, "soulScreaming.atlas", "SoulScream", 0.1f, Animation.PlayMode.NORMAL);
    }

    private void load(Status status, String fileName, String regionName, float frameDuration,
                      Animation.PlayMode mode) {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(baseDir + fileName));
        atlases.put(status, atlas);
        animations.put(status, new Animation<>(frameDuration, atlas.findRegions(regionName), mode));
    }

    private void loadEffect(Status status, String fileName, String regionName, float frameDuration,
                            Animation.PlayMode mode) {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(baseDir + fileName));
        effectAtlases.put(status, atlas);
        effectAnimations.put(status, new Animation<>(frameDuration, atlas.findRegions(regionName), mode));
    }

    public void render(SpriteBatch batch, PlayerModel player) {
        float deltaTime = Gdx.graphics.getDeltaTime();
        stateTime += deltaTime;

        Status nextStatus = resolveStatus(player);

        if (currentStatus != nextStatus) {
            stateTime = 0f;
            currentStatus = nextStatus;
        }

        TextureRegion region = animations.get(currentStatus).getKeyFrame(stateTime);

        if (currentStatus == Status.DEATH) {
            if (animations.get(Status.DEATH).isAnimationFinished(stateTime)) {
                player.setJustDead(true);
            }
        }

        float offsetX = (SPRITE_WIDTH - player.getWidth()) / 2f;
        float offsetY = 0f;

        if (!player.isFacingRight() && region.isFlipX()) {
            region.flip(true, false);
        } else if (player.isFacingRight() && !region.isFlipX()) {
            region.flip(true, false);
        }


        float knightX = player.getX() - offsetX;
        float knightY = player.getY() - offsetY;

        batch.draw(
            region,
            knightX,
            knightY,
            SPRITE_WIDTH,
            SPRITE_HEIGHT
        );

        Animation<TextureAtlas.AtlasRegion> effectAnimation = effectAnimations.get(currentStatus);
        if (effectAnimation != null) {
            TextureRegion region2 = effectAnimation.getKeyFrame(stateTime);

            renderEffect(batch, player, region2, knightX, knightY);
        }

    }

    private Status resolveStatus(PlayerModel player) {
        if (!player.isAlive()) {
            return Status.DEATH;
        }
        if (player.isMantis()) {
            return Status.MANTIS;
        }
        if (player.isDashing()) {
            return Status.DASH;
        }
        if (player.isAttacking()) {
            return Status.ATTACK;
        }
        if (player.isFocusing()) {
            return Status.FOCUS;
        }
        if (!player.isOnGround()) {
            if (player.getVelocityY() > 0) {
                return player.isDoubleJumpUsed() ? Status.DOUBLE_JUMP : Status.JUMP;
            }
            return Status.FALL;
        }
        if (Math.abs(player.getVelocityX()) > 0.1f) {
            return Status.RUN;
        }
        return (player.getHealth() == 1) ? Status.HURT : Status.IDLE;
    }

    private void renderEffect(SpriteBatch batch, PlayerModel player, TextureRegion region2, float knightX, float knightY) {
        if (!player.isFacingRight() && !region2.isFlipX()) {
            region2.flip(true, false);
        } else if (player.isFacingRight() && region2.isFlipX()) {
            region2.flip(true, false);
        }

        float effectX = knightX;
        float effectY = knightY;

        if (currentStatus == Status.DASH) {
            if (!player.isFacingRight()) {
                effectX += 200f;
            } else {
                effectX -= 200f;
            }
            effectY -= 80f;
        }

        if (currentStatus == Status.ATTACK) {
            effectX = player.isFacingRight() ? effectX + player.getWidth() : effectX - player.getWidth();
            region2.flip(true, false);
        }

        batch.draw(region2, effectX, effectY);
    }

    public void dispose() {
        for (TextureAtlas atlas : atlases.values()) {
            atlas.dispose();
        }
        for (TextureAtlas atlas : effectAtlases.values()) {
            atlas.dispose();
        }
    }
}
