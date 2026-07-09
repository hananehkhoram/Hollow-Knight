package hana.HollowKnight.view.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

public class RainManager {

    private static class RainDrop {
        float x, y;
        float speed;
        float stateTime;
        float scale;
    }

    private final Animation<TextureRegion> rainAnimation;
    private final Texture rainTexture;
    private final Array<RainDrop> rainDrops;
    private final int maxDrops = 60;
    public RainManager(String pngPath) {
        rainTexture = new Texture(Gdx.files.internal(pngPath));

        TextureRegion[] rainFrames = new TextureRegion[6];

        rainFrames[0] = new TextureRegion(rainTexture, 270, 260, 265, 265);

        rainFrames[1] = new TextureRegion(rainTexture, 550, 225, 265, 265);

        rainFrames[2] = new TextureRegion(rainTexture, 20, 415, 255, 255);

        rainFrames[3] = new TextureRegion(rainTexture, 540, 525, 265, 265);

        rainFrames[4] = new TextureRegion(rainTexture, 20, 710, 255, 255);


        rainFrames[5] = new TextureRegion(rainTexture, 295, 710, 265, 265);


        rainAnimation = new Animation<>(0.07f, rainFrames);
        rainAnimation.setPlayMode(Animation.PlayMode.LOOP);

        rainDrops = new Array<>();
    }

    public void updateAndRender(SpriteBatch batch, Viewport viewport, float delta) {
        float screenWidth = viewport.getWorldWidth();
        float screenHeight = viewport.getWorldHeight();

        if (rainDrops.size < maxDrops) {
            RainDrop drop = new RainDrop();
            drop.x = MathUtils.random(-200f, screenWidth);
            drop.y = screenHeight + MathUtils.random(50f, 300f);
            drop.speed = MathUtils.random(400f, 700f);
            drop.stateTime = MathUtils.random(0f, 1f);
            drop.scale = MathUtils.random(0.5f, 1.2f);
            rainDrops.add(drop);
        }

        for (int i = rainDrops.size - 1; i >= 0; i--) {
            RainDrop drop = rainDrops.get(i);

            drop.y -= drop.speed * delta;
            drop.x -= (drop.speed * 0.15f) * delta;
            drop.stateTime += delta;

            if (drop.y < -300) {
                rainDrops.removeIndex(i);
                continue;
            }

            TextureRegion currentFrame = rainAnimation.getKeyFrame(drop.stateTime);

            float width = currentFrame.getRegionWidth() * drop.scale;
            float height = currentFrame.getRegionHeight() * drop.scale;

            batch.draw(currentFrame, drop.x, drop.y, width, height);
        }
    }

    public void dispose() {
        if (rainTexture != null) rainTexture.dispose();
    }
}
