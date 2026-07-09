package hana.HollowKnight.view.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import hana.HollowKnight.model.entities.PlayerModel;

public class CollisionDebugRenderer {

    private final ShapeRenderer shapeRenderer;

    public CollisionDebugRenderer() {
        this.shapeRenderer = new ShapeRenderer();
    }

    public void render(OrthographicCamera camera, PlayerModel player, Array<Rectangle> solidTiles, Array<Rectangle> hazards) {
        // حتماً مطمئن میشیم که این رندرر از ماتریس دوربین مپ شما استفاده میکنه
        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        Gdx.gl.glLineWidth(3f); // خطوط رو کمی ضخیم میکنیم تا واضح باشن

        // ۱. رسم تایل‌های جامد (سبز)
        shapeRenderer.setColor(Color.GREEN);
        for (Rectangle tile : solidTiles) {
            shapeRenderer.rect(tile.x, tile.y, tile.width, tile.height);
        }

        // ۲. رسم موانع (زرد)
        shapeRenderer.setColor(Color.YELLOW);
        for (Rectangle hazard : hazards) {
            shapeRenderer.rect(hazard.x, hazard.y, hazard.width, hazard.height);
        }

        // ۳. رسم هیت‌باکس واقعی شوالیه (قرمز)
        Rectangle pBounds = player.getBounds();
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(pBounds.x, pBounds.y, pBounds.width, pBounds.height);

        // ۴. رسم هیت‌باکس حمله شوالیه در صورت اتک زدن (نارنجی)
        if (player.isAttacking()) {
            shapeRenderer.setColor(Color.ORANGE);
            Rectangle attackBounds = player.getAttackHitbox();
            shapeRenderer.rect(attackBounds.x, attackBounds.y, attackBounds.width, attackBounds.height);
        }

        shapeRenderer.end();
        Gdx.gl.glLineWidth(1f); // برگرداندن ضخامت خط به حالت پیش‌فرض
    }

    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }
}
