package hana.HollowKnight.view.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import hana.HollowKnight.model.entities.PlayerModel;

public class PlayerRenderer {
    private final String baseDir = "Animations/knight/";

    private final TextureAtlas idleTexture;
    private final Animation<TextureAtlas.AtlasRegion> idleAnimation;
    private final TextureAtlas movingTexture;
    private final Animation<TextureAtlas.AtlasRegion> movingAnimation;
    private final TextureAtlas idleHurtTexture;
    private final Animation<TextureAtlas.AtlasRegion> idleHurtAnimation;
    private final TextureAtlas dashAtlas;
    private final Animation<TextureAtlas.AtlasRegion> dashAnimation;
    private final TextureAtlas deathAtlas;
    private final Animation<TextureAtlas.AtlasRegion> deathAnimation;
    private final TextureAtlas doubleJumpTexture;
    private final Animation<TextureAtlas.AtlasRegion> doubleJumpAnimation;
    private final TextureAtlas jumpTexture;
    private final Animation<TextureAtlas.AtlasRegion> jumpAnimation;
    private final TextureAtlas attackAtlas;
    private final Animation<TextureAtlas.AtlasRegion> attackAnimation;
    private final TextureAtlas fallTexture;
    private final Animation<TextureAtlas.AtlasRegion> fallAnimation;
    private final TextureAtlas focuTexture;
    private final Animation<TextureAtlas.AtlasRegion> focusAnimation;
    private final TextureAtlas dashEAtlas;
    private final Animation<TextureAtlas.AtlasRegion> dashEAnimation;
    private final TextureAtlas soulScreamingAtlas;
    private final Animation<TextureAtlas.AtlasRegion> soulScreamingAnimation;

    private final float SPRITE_WIDTH = 349f;
    private final float SPRITE_HEIGHT = 186f;

    private float stateTime;
    private String currentStatus = "IDLE";

    public PlayerRenderer() {
        stateTime = 0;

        idleTexture = new TextureAtlas(baseDir + "idle.atlas");
        idleAnimation = new Animation<>(0.1f, idleTexture.findRegions("Idle"), Animation.PlayMode.LOOP);

        movingTexture = new TextureAtlas(baseDir + "run.atlas");
        movingAnimation = new Animation<>(0.08f, movingTexture.findRegions("Run"), Animation.PlayMode.LOOP);

        idleHurtTexture = new TextureAtlas(baseDir + "idleHurt.atlas");
        idleHurtAnimation = new Animation<>(0.1f, idleHurtTexture.findRegions("Idle Hurt"), Animation.PlayMode.LOOP);

        dashAtlas = new TextureAtlas(Gdx.files.internal(baseDir + "dash.atlas"));
        dashAnimation = new Animation<>(0.05f, dashAtlas.findRegions("Dash"), Animation.PlayMode.NORMAL);

        deathAtlas = new TextureAtlas(Gdx.files.internal(baseDir + "death.atlas"));
        deathAnimation = new Animation<>(0.1f, deathAtlas.findRegions("Death"), Animation.PlayMode.NORMAL);

        doubleJumpTexture = new TextureAtlas(Gdx.files.internal(baseDir + "doubljump.atlas"));
        doubleJumpAnimation = new Animation<>(0.08f, doubleJumpTexture.findRegions("Double Jump"), Animation.PlayMode.NORMAL);

        jumpTexture = new TextureAtlas(Gdx.files.internal(baseDir + "jumo.atlas"));
        jumpAnimation = new Animation<>(0.1f, jumpTexture.findRegions("Airborne"), Animation.PlayMode.LOOP);

        attackAtlas = new TextureAtlas(Gdx.files.internal(baseDir + "slash.atlas"));
        attackAnimation = new Animation<>(0.05f, attackAtlas.findRegions("SlashAlt"), Animation.PlayMode.NORMAL);

        fallTexture = new TextureAtlas(Gdx.files.internal(baseDir + "fall.atlas"));
        fallAnimation = new Animation<>(0.1f, fallTexture.findRegions("Fall"), Animation.PlayMode.LOOP);

        focuTexture = new TextureAtlas(Gdx.files.internal(baseDir + "focus.atlas"));
        focusAnimation = new Animation<>(0.1f, focuTexture.findRegions("Focus"), Animation.PlayMode.NORMAL);

        soulScreamingAtlas = new TextureAtlas(Gdx.files.internal(baseDir + "soulScreaming.atlas"));
        soulScreamingAnimation = new Animation<>(0.1f, soulScreamingAtlas.findRegions("SoulScream"), Animation.PlayMode.NORMAL);

        dashEAtlas = new TextureAtlas(Gdx.files.internal(baseDir + "dashE.atlas"));
        dashEAnimation = new Animation<>(0.1f, dashEAtlas.findRegions("Dash Effect"), Animation.PlayMode.NORMAL);
    }

    public void render(SpriteBatch batch, PlayerModel player) {
        float deltaTime = Gdx.graphics.getDeltaTime();
        stateTime += deltaTime;

        TextureRegion region;
        TextureRegion region2 = null;
        String nextStatus = "IDLE";

        if (!player.isAlive()) {
            nextStatus = "DEATH";
            region = deathAnimation.getKeyFrame(stateTime);
            region2 = soulScreamingAnimation.getKeyFrame(stateTime);
            player.setJustDead(true);
        } else if (player.isDashing()) {
            nextStatus = "DASH";
            region = dashAnimation.getKeyFrame(stateTime);
            region2 = dashEAnimation.getKeyFrame(stateTime);
        } else if (player.isAttacking()) {
            nextStatus = "ATTACK";
            region = attackAnimation.getKeyFrame(stateTime);
        } else if (player.isFocusing()) {
            nextStatus = "FOCUS";
            region = focusAnimation.getKeyFrame(stateTime);
        } else if (!player.isOnGround()) {
            if (player.getVelocityY() > 0) {
                if (!player.isDoubleJumpUsed()) {
                    nextStatus = "JUMP";
                    region = jumpAnimation.getKeyFrame(stateTime);
                } else {
                    nextStatus = "DOUBLE_JUMP";
                    region = doubleJumpAnimation.getKeyFrame(stateTime);
                }
            } else {
                nextStatus = "FALL";
                region = fallAnimation.getKeyFrame(stateTime);
            }
        } else if (Math.abs(player.getVelocityX()) > 0.1f) {
            nextStatus = "RUN";
            region = movingAnimation.getKeyFrame(stateTime);
        } else {
            if (player.getHealth() == 1) {
                nextStatus = "HURT";
                region = idleHurtAnimation.getKeyFrame(stateTime);
            } else {
                nextStatus = "IDLE";
                region = idleAnimation.getKeyFrame(stateTime);
            }
        }

        if (!currentStatus.equals(nextStatus)) {
            stateTime = 0f;
            currentStatus = nextStatus;
            if (currentStatus.equals("DASH")) region = dashAnimation.getKeyFrame(0);
            if (currentStatus.equals("ATTACK")) region = attackAnimation.getKeyFrame(0);
        }

        if (!player.isFacingRight() && region.isFlipX()) {
            region.flip(true, false);
        } else if (player.isFacingRight() && !region.isFlipX()) {
            region.flip(true, false);
        }

        float offsetX = (SPRITE_WIDTH - player.getWidth()) / 2f;
        float offsetY = 0f;

        float knightX = player.getX() - offsetX;
        float knightY = player.getY() - offsetY;

        if (region2 != null) {
            if (!player.isFacingRight() && !region2.isFlipX()) {
                region2.flip(true, false);
            } else if (player.isFacingRight() && region2.isFlipX()) {
                region2.flip(true, false);
            }

            float effectX = knightX;
            float effectY = knightY;

            if (currentStatus.equals("DASH")) {
                if (!player.isFacingRight()) {
                    effectX += 200f;
                    effectY -= 80f;
                } else {
                    effectX -= 200f;
                    effectY -= 80f;}
            }

            batch.draw(region2, effectX, effectY);
        }

        batch.draw(
            region,
            knightX,
            knightY,
            SPRITE_WIDTH,
            SPRITE_HEIGHT
        );
    }

    public void dispose() {
        idleTexture.dispose();
        movingTexture.dispose();
        idleHurtTexture.dispose();
        dashAtlas.dispose();
        deathAtlas.dispose();
        doubleJumpTexture.dispose();
        jumpTexture.dispose();
        attackAtlas.dispose();
        fallTexture.dispose();
        focuTexture.dispose();
        dashEAtlas.dispose();
        soulScreamingAtlas.dispose();
    }
}
