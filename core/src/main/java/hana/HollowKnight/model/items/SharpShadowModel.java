package hana.HollowKnight.model.items;

import hana.HollowKnight.model.entities.EnemyModel;
import hana.HollowKnight.model.entities.PlayerModel;

public class SharpShadowModel extends Charm {
    protected SharpShadowModel(PlayerModel player) {
        super(CharmType.SHARP_SHADOW, player);
        this.unlocked = true;
    }

    @Override
    public void applyCharm() {
        if (player.getUsedNotches() < 3) {
            PlayerModel.DASH_DURATION = PlayerModel.DASH_DURATION * 1.2f;
            setPicPath("");

        }
    }

    @Override
    public void cancelCharm() {
            PlayerModel.DASH_DURATION = PlayerModel.DASH_DURATION / 1.2f;
            setPicPath("");

    }
}
