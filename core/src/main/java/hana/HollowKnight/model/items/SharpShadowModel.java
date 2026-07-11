package hana.HollowKnight.model.items;

import hana.HollowKnight.model.entities.EnemyModel;
import hana.HollowKnight.model.entities.PlayerModel;

public class SharpShadowModel extends Charm {
    protected SharpShadowModel(PlayerModel player) {
        super(CharmType.SHARP_SHADOW, player);
        this.unlocked = true;
        this.picPath = "charms/Sharp Shadow - charm_shade_impact.png";
    }


    @Override
    public void applyCharm() {
        if (player.getUsedNotches() < 3) {
            PlayerModel.DASH_DURATION = PlayerModel.DASH_DURATION * 1.2f;
        }
    }

    @Override
    public void cancelCharm() {
            PlayerModel.DASH_DURATION = PlayerModel.DASH_DURATION / 1.2f;
    }
}
