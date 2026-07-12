package hana.HollowKnight.view.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import hana.HollowKnight.model.entities.PlayerModel;
import hana.HollowKnight.model.entities.ProjectileModel;
import hana.HollowKnight.model.entities.ProjectileType;

public class ProjectileRenderer {
    private float requiredTime = 2f;
    private float timePassed = 0f;

    private String baseDir = "";
    private final ShapeRenderer shapeRenderer;
    private String name;

    private TextureAtlas simpleAtlas;
    private Animation<TextureAtlas.AtlasRegion> simpleAnimation;

    public ProjectileRenderer(ProjectileType projectile) {
        shapeRenderer = new ShapeRenderer();

        switch (projectile) {
            case POGO:
                baseDir = "Animations/pogo/pogo.atlas";
                name = "DownSlashEffect";
                break;
            case VENEGFUL:
                baseDir = "Animations/venegful/venegful.atlas";
                name = "SoulBall";
                break;
        }

        simpleAtlas = new TextureAtlas(Gdx.files.internal(baseDir));
        simpleAnimation = new Animation<>(0.05f, simpleAtlas.findRegions(name), Animation.PlayMode.LOOP);
    }

    public void render(SpriteBatch batch, ProjectileModel projectile) {
        if (!projectile.isActive()) return;

        timePassed += Gdx.graphics.getDeltaTime();
        if (timePassed > requiredTime) {
            timePassed = 0;
        }

        TextureRegion region = simpleAnimation.getKeyFrame(timePassed);
        batch.draw(
            region,
            projectile.getX(),
            projectile.getY(),
            projectile.getBounds().width,
            projectile.getBounds().height
        );
    }


    public void dispose() {
        shapeRenderer.dispose();
        if (simpleAtlas != null) simpleAtlas.dispose();
    }
}
