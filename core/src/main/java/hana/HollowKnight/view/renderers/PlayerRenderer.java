package hana.HollowKnight.view.renderers;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import hana.HollowKnight.model.entities.PlayerModel;

public class PlayerRenderer {

    private final Texture idleTexture;
    // بعداً می‌تونی texture‌های running/jumping/attacking رو هم اضافه کنی

    public PlayerRenderer() {
        idleTexture = new Texture("player/idle.png");
    }

    public void render(SpriteBatch batch, PlayerModel player) {
        TextureRegion region = new TextureRegion(idleTexture);

        if (!player.isFacingRight() && !region.isFlipX()) {
            region.flip(true, false);
        } else if (player.isFacingRight() && region.isFlipX()) {
            region.flip(true, false);
        }

        batch.draw(region, player.getX(), player.getY(), player.getWidth(), player.getHeight());
    }

    public void dispose() {
        idleTexture.dispose();
    }
}
