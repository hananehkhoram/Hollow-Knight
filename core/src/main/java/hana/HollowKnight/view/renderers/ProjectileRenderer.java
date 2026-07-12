package hana.HollowKnight.view.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import hana.HollowKnight.model.entities.ProjectileModel;

public class ProjectileRenderer {

    private String baseDir = "";
    private final ShapeRenderer shapeRenderer;

    private TextureAtlas simpleAtlas;
    private Animation<TextureAtlas.AtlasRegion> simpleAnimation;

    public ProjectileRenderer(ProjectileModel projectile) {
        shapeRenderer = new ShapeRenderer();

        switch (projectile.getType()) {
            case POGO:
                baseDir = "Animations/pogo/";
                break;
            case ORB_ATTACK:
                baseDir = "Animations/";
                break;
        }

        simpleAtlas = new TextureAtlas(Gdx.files.internal(baseDir + "pogo.atlas"));
        simpleAnimation = new Animation<>(0.05f, simpleAtlas.findRegions("DownSlashEffect"), Animation.PlayMode.LOOP);
    }

    public void render(SpriteBatch batch, ProjectileModel projectile) {
        if (!projectile.isActive()) return;

        TextureRegion currentFrame = null;
        currentFrame = simpleAnimation.getKeyFrame(Gdx.graphics.getDeltaTime(), false);

        if (currentFrame != null) {
            batch.draw(
                currentFrame,
                projectile.getX(),
                projectile.getY(),
                projectile.getBounds().width,
                projectile.getBounds().height
            );
        }
    }

    public void renderDebug(ProjectileModel projectile) {
        if (!projectile.isActive()) return;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 0, 0, 1);
        shapeRenderer.rect(projectile.getX(), projectile.getY(), projectile.getBounds().width, projectile.getBounds().height);
        shapeRenderer.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
        if (simpleAtlas != null) simpleAtlas.dispose();
        if (onStartAtlas != null) onStartAtlas.dispose();
        if (onCollisionAtlas != null) onCollisionAtlas.dispose();
    }
}
