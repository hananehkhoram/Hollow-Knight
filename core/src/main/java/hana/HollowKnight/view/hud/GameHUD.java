package hana.HollowKnight.view.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameHUD {
    private final TextureAtlas hudAtlas;
    private final TextureAtlas hpAtlas;
    private final TextureAtlas waterSoulAtlas;
    private final TextureAtlas hpBreakAtlas;
    private final TextureAtlas hpRefillAtlas;
    private final Texture emptyHpRegion;
    private final Animation<TextureAtlas.AtlasRegion> healthBarAn;
    private final Animation<TextureAtlas.AtlasRegion> hpAn;
    private final Animation<TextureAtlas.AtlasRegion> waterSoulGrowAn;
    private final Animation<TextureAtlas.AtlasRegion> waterSoulIdleAn;
    private final Animation<TextureAtlas.AtlasRegion> waterSoulShrinkAn;
    private final Animation<TextureAtlas.AtlasRegion> hpBreakAn;
    private final Animation<TextureAtlas.AtlasRegion> hpRefillAn;
    private final Viewport hudViewport;
    private final ShapeRenderer shapeRenderer;
    public String soulState = "IDLE";
    private float stateTime;
    private float breakTime = 0;
    private float refillTime = 0;
    private boolean isBreaking = false;
    private boolean isRefilling = true;
    private int lastHp = -1;
    private float prevSoul = 0;

    public GameHUD() {
        hudViewport = new StretchViewport(1280, 720);
        stateTime = 0;
        shapeRenderer = new ShapeRenderer();

        String baseDir = "Animations/hud/";
        hudAtlas = new TextureAtlas(Gdx.files.internal(baseDir + "hud_animation.atlas"));
        healthBarAn = new Animation<>(0.1f, hudAtlas.findRegions("HealthBar"), Animation.PlayMode.NORMAL);

        hpAtlas = new TextureAtlas(Gdx.files.internal(baseDir + "filledHealth.atlas"));
        hpAn = new Animation<>(0.2f, hpAtlas.findRegions("FilledHealthShine"), Animation.PlayMode.LOOP);

        emptyHpRegion = new Texture(Gdx.files.internal(baseDir + "EmptyHealth.png"));

        waterSoulAtlas = new TextureAtlas(Gdx.files.internal(baseDir + "Soulorb.atlas"));

        waterSoulGrowAn = new Animation<>(0.2f,
            waterSoulAtlas.findRegions("HUD_Soulorb_fills_soul_grow"), Animation.PlayMode.LOOP);

        waterSoulIdleAn = new Animation<>(0.2f,
            waterSoulAtlas.findRegions("HUD_Soulorb_fills_soul_idle"), Animation.PlayMode.LOOP);

        waterSoulShrinkAn = new Animation<>(0.2f,
            waterSoulAtlas.findRegions("HUD_Soulorb_fills_soul_shrink"), Animation.PlayMode.LOOP);

        hpBreakAtlas = new TextureAtlas(Gdx.files.internal(baseDir + "breakHealth.atlas"));
        hpBreakAn = new Animation<>(0.1f, hpBreakAtlas.findRegions("BreakHealth"), Animation.PlayMode.NORMAL);

        hpRefillAtlas = new TextureAtlas(Gdx.files.internal(baseDir + "refillHealth.atlas"));
        hpRefillAn = new Animation<>(0.1f, hpRefillAtlas.findRegions("HealthRefill"), Animation.PlayMode.NORMAL);
    }

    public void render(SpriteBatch sb, int currentHp, int maxHp, int soul, int maxSoul) {
        float deltaTime = Gdx.graphics.getDeltaTime();
        stateTime += deltaTime;

        float startX = 100f;
        float hpY = 645;
        float hpSize = 60f;

        if (lastHp != -1 && currentHp < lastHp) {
            isBreaking = true;
            breakTime = 0;
        } else if (lastHp != -1 && currentHp > lastHp) {
            isRefilling = true;
            refillTime = 0;
        }
        lastHp = currentHp;

        if (prevSoul > soul) {
            soulState = "GROW";
        } else if (prevSoul < soul) {
            soulState = "SHRINK";
        } else {
            soulState = "IDLE";
        }

        prevSoul = soul;

        if (soulState.equals("GROW")) {
            if (waterSoulGrowAn != null && waterSoulGrowAn.isAnimationFinished(stateTime)) {
                soulState = "IDLE";
            }
        } else if (soulState.equals("SHRINK")) {
            if (waterSoulShrinkAn != null && waterSoulShrinkAn.isAnimationFinished(stateTime)) {
                soulState = "IDLE";
            }
        }

        TextureRegion hudRegion = healthBarAn.getKeyFrame(stateTime);
        TextureRegion hpRegion = hpAn.getKeyFrame(stateTime);

        TextureRegion waterSoulRegion = null;
        if (soulState.equals("GROW") && waterSoulGrowAn != null) {
            waterSoulRegion = waterSoulGrowAn.getKeyFrame(stateTime);
        } else if (soulState.equals("SHRINK") && waterSoulShrinkAn != null) {
            waterSoulRegion = waterSoulShrinkAn.getKeyFrame(stateTime);
        } else {
            if (waterSoulIdleAn != null) {
                waterSoulRegion = waterSoulIdleAn.getKeyFrame(stateTime);
            }
        }

        float soulX = 3f;
        float soulY = 600f;
        float soulW = 125f;
        float soulH = 125f;


        float circleX = soulX + 61;
        float circleY = soulY + 58;
        float radius = 30f;

        float minY = circleY - radius;
        float fillOffset = ((float) soul / maxSoul) * (radius * 2f);
        float waterSoulY = minY + fillOffset - soulH + 10;

        hudViewport.apply();
        sb.setProjectionMatrix(hudViewport.getCamera().combined);
        shapeRenderer.setProjectionMatrix(hudViewport.getCamera().combined);

        sb.begin();
        if (hudRegion != null) {
            sb.draw(hudRegion, 20, 620, 150, 90);
        }

        for (int i = 0; i < maxHp; i++) {
            float currentX = startX + (i * hpSize * 0.75f);
            if (i < currentHp) {
                if (hpRegion != null) {
                    sb.draw(hpRegion, currentX, hpY, hpSize, hpSize);
                }
                if (isRefilling && i == currentHp - 1 && hpRefillAn != null) {
                    refillTime += deltaTime;
                    TextureRegion refillFrame = hpRefillAn.getKeyFrame(refillTime);
                    if (refillFrame != null) {
                        sb.draw(refillFrame, currentX, hpY, hpSize, hpSize);
                    }
                    if (hpRefillAn.isAnimationFinished(refillTime)) {
                        isRefilling = false;
                    }
                }
            } else {
                if (emptyHpRegion != null) {
                    sb.draw(emptyHpRegion, currentX, hpY, hpSize, hpSize);
                }
            }
        }
        sb.end();

        if (waterSoulRegion != null) {
            Gdx.gl.glEnable(GL20.GL_STENCIL_TEST);
            Gdx.gl.glClear(GL20.GL_STENCIL_BUFFER_BIT);

            Gdx.gl.glStencilFunc(GL20.GL_ALWAYS, 1, 0xFF);
            Gdx.gl.glStencilOp(GL20.GL_KEEP, GL20.GL_KEEP, GL20.GL_REPLACE);
            Gdx.gl.glColorMask(false, false, false, false);

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.circle(circleX, circleY, radius);
            shapeRenderer.end();

            Gdx.gl.glColorMask(true, true, true, true);
            Gdx.gl.glStencilFunc(GL20.GL_EQUAL, 1, 0xFF);
            Gdx.gl.glStencilOp(GL20.GL_KEEP, GL20.GL_KEEP, GL20.GL_KEEP);

            sb.begin();
            sb.draw(waterSoulRegion, soulX, waterSoulY, soulW, soulH);
            sb.end();

            Gdx.gl.glDisable(GL20.GL_STENCIL_TEST);
        }

        if (isBreaking && hpBreakAn != null) {
            breakTime += deltaTime;
            TextureRegion breakFrame = hpBreakAn.getKeyFrame(breakTime);
            if (breakFrame != null) {
                sb.begin();
                sb.draw(breakFrame, startX + (currentHp * hpSize * 0.75f), hpY, hpSize, hpSize);
                sb.end();
            }
            if (hpBreakAn.isAnimationFinished(breakTime)) {
                isBreaking = false;
            }
        }
    }

    public void resize(int width, int height) {
        hudViewport.update(width, height, true);
    }

    public void dispose() {
        if (hudAtlas != null) hudAtlas.dispose();
        if (hpAtlas != null) hpAtlas.dispose();
        if (waterSoulAtlas != null) waterSoulAtlas.dispose();
        if (hpBreakAtlas != null) hpBreakAtlas.dispose();
        if (hpRefillAtlas != null) hpRefillAtlas.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
    }
}
