package hana.HollowKnight.view.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import hana.HollowKnight.model.entities.BossModel;

import java.util.EnumMap;
import java.util.Map;

public class BossRenderer {

    private final String baseDir = "Animations/falseknight/";

    private final Map<BossModel.State, TextureAtlas> atlases = new EnumMap<>(BossModel.State.class);
    private final Map<BossModel.State, Animation<TextureAtlas.AtlasRegion>> animations = new EnumMap<>(BossModel.State.class);

    private final float SPRITE_WIDTH = 1095;
    private final float SPRITE_HEIGHT = 635;

    private float stateTime = 0f;
    private BossModel.State currentStatus = BossModel.State.IDLE;

    public BossRenderer() {
        load(BossModel.State.IDLE, "idle.atlas", "Idle", 0.15f, Animation.PlayMode.LOOP);
        load(BossModel.State.TURN, "turn.atlas", "Turn", 0.08f, Animation.PlayMode.NORMAL);
        load(BossModel.State.RUN_ANTIC, "runantic.atlas", "Run Antic", 0.08f, Animation.PlayMode.NORMAL);
        load(BossModel.State.RUN, "run.atlas", "Run", 0.06f, Animation.PlayMode.LOOP);
        load(BossModel.State.ATTACK_ANTIC, "attackantic.atlas", "Attack Antic", 0.08f, Animation.PlayMode.NORMAL);
        load(BossModel.State.ATTACK, "attack.atlas", "Attack", 0.06f, Animation.PlayMode.NORMAL);
        load(BossModel.State.ATTACK_RECOVER, "attackrecover.atlas", "Attack Recover", 0.1f, Animation.PlayMode.NORMAL);
        load(BossModel.State.JUMP, "jump.atlas", "Jump", 0.1f, Animation.PlayMode.NORMAL);
        load(BossModel.State.JUMP_ATTACK, "jumpAttack.atlas", "Jump Attack", 0.08f, Animation.PlayMode.NORMAL);
        load(BossModel.State.LAND, "land.atlas", "Land", 0.1f, Animation.PlayMode.NORMAL);
        load(BossModel.State.BODY, "body.atlas", "Body", 0.1f, Animation.PlayMode.NORMAL);
        load(BossModel.State.BODY_STUN, "body.atlas", "Body", 0.15f, Animation.PlayMode.LOOP);
        load(BossModel.State.STUN_RECOVER, "stunrecover.atlas", "Stun Recover", 0.1f, Animation.PlayMode.NORMAL);
        load(BossModel.State.DEATH_FALL, "deathfall.atlas", "DeathFall", 0.1f, Animation.PlayMode.NORMAL);
        load(BossModel.State.DEATH_HIT, "deathhit.atlas", "DeathHit", 0.1f, Animation.PlayMode.NORMAL);
        load(BossModel.State.DEATH_LAND, "deathland.atlas", "DeathLand", 0.15f, Animation.PlayMode.NORMAL);
    }

    private void load(BossModel.State state, String fileName, String regionName, float frameDuration,
                      Animation.PlayMode mode) {
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(baseDir + fileName));
        atlases.put(state, atlas);
        animations.put(state, new Animation<>(frameDuration, atlas.findRegions(regionName), mode));
    }

    public void render(SpriteBatch batch, BossModel boss) {
        float deltaTime = Gdx.graphics.getDeltaTime();
        stateTime += deltaTime;

        BossModel.State nextStatus = boss.getState();
        if (currentStatus != nextStatus) {
            stateTime = 0f;
            currentStatus = nextStatus;
        }

        Animation<TextureAtlas.AtlasRegion> animation = animations.get(currentStatus);
        TextureRegion region = animation.getKeyFrame(stateTime);

        if (!boss.isFacingRight() && region.isFlipX()) {
            region.flip(true, false);
        } else if (boss.isFacingRight() && !region.isFlipX()) {
            region.flip(true, false);
        }

        float offsetX = (SPRITE_WIDTH - boss.getWidth()) / 2f;

        batch.draw(
            region,
            boss.getX() - offsetX,
            boss.getY(),
            SPRITE_WIDTH,
            SPRITE_HEIGHT
        );
    }

    public void dispose() {
        for (TextureAtlas atlas : atlases.values()) {
            atlas.dispose();
        }
    }
}
