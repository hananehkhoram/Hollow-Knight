package hana.HollowKnight.view.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import hana.HollowKnight.controller.GameController;
import hana.HollowKnight.model.entities.PlayerModel;
import hana.HollowKnight.model.entities.ProjectileModel;
import hana.HollowKnight.model.entities.ProjectileType;

public class ProjectileRenderer {
    private float requiredTime = 2f;
    private float timePassed = 0f;

    private String baseDir = "";
    private final ShapeRenderer shapeRenderer;
    private String name;
    private String baseDir2 = "Animations/venegful/venegful2.atlas";
    private String name2 = "ShadowBall";

    private TextureAtlas simpleAtlas;
    private Animation<TextureAtlas.AtlasRegion> simpleAnimation;
    private TextureAtlas simpleAtlas2;
    private Animation<TextureAtlas.AtlasRegion> simpleAnimation2;

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
        simpleAtlas2 = new TextureAtlas(Gdx.files.internal(baseDir2));
        simpleAnimation2 = new Animation<>(0.05f, simpleAtlas2.findRegions(name2), Animation.PlayMode.LOOP);


    }

    public void render(SpriteBatch batch, ProjectileModel projectile, boolean isFacingRight, GameController controller) {
        if (!projectile.isActive()) return;

        timePassed += Gdx.graphics.getDeltaTime();
        if (timePassed > requiredTime) {
            timePassed = 0;
        }

        TextureRegion region = simpleAnimation.getKeyFrame(timePassed);
        if (controller.getModel().getPlayer().isVoidHeart && projectile.getType() == ProjectileType.VENEGFUL){
            region = simpleAnimation2.getKeyFrame(timePassed);
        }

        if (!isFacingRight && !region.isFlipX()) {
            region.flip(true, false);
        } else if (isFacingRight && region.isFlipX()) {
            region.flip(true, false);
        }

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
