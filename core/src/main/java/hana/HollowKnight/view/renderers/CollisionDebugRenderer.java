package hana.HollowKnight.view.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import hana.HollowKnight.model.entities.MosscreepModel;
import hana.HollowKnight.model.entities.PlayerModel;

import java.util.ArrayList;

public class CollisionDebugRenderer {

    private final ShapeRenderer shapeRenderer;

    public CollisionDebugRenderer() {
        this.shapeRenderer = new ShapeRenderer();
    }

    public void render(OrthographicCamera camera, PlayerModel player, Array<Rectangle> solidTiles, Array<Rectangle> hazards, ArrayList<MosscreepModel> crawls) {
        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        Gdx.gl.glLineWidth(3f);

        shapeRenderer.setColor(Color.RED);
        for (MosscreepModel mosscreepModel : crawls) {
            Rectangle rectangle = mosscreepModel.getBounds();
                shapeRenderer.rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        }

        shapeRenderer.setColor(Color.GREEN);
        for (Rectangle tile : solidTiles) {
            shapeRenderer.rect(tile.x, tile.y, tile.width, tile.height);
        }

        shapeRenderer.setColor(Color.YELLOW);
        for (Rectangle hazard : hazards) {
            shapeRenderer.rect(hazard.x, hazard.y, hazard.width, hazard.height);
        }

        Rectangle pBounds = player.getBounds();
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(pBounds.x, pBounds.y, pBounds.width, pBounds.height);

        if (player.isAttacking()) {
            shapeRenderer.setColor(Color.ORANGE);
            Rectangle attackBounds = player.getAttackHitbox();
            shapeRenderer.rect(attackBounds.x, attackBounds.y, attackBounds.width, attackBounds.height);
        }

        shapeRenderer.end();
        Gdx.gl.glLineWidth(1f);
    }

    public void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }
}
