package hana.HollowKnight.model.items;

import hana.HollowKnight.model.entities.EnemyModel;
import hana.HollowKnight.model.entities.PlayerModel;

public class HeavyBlowModel extends Charm {
    protected HeavyBlowModel(PlayerModel player) {
        super(CharmType.HEAVY_BLOW, player);
        this.unlocked = true;
    }

    @Override
    public void applyCharm() {
        if (player.getUsedNotches() < 3) {
            EnemyModel.setKnockBackForceX(EnemyModel.KNOCKBACK_FORCE_X * 2);
            EnemyModel.setKnockBackForceY(EnemyModel.KNOCKBACK_FORCE_Y * 2);
            setPicPath("");

        }
    }

    @Override
    public void cancelCharm() {
            EnemyModel.setKnockBackForceX(EnemyModel.KNOCKBACK_FORCE_X);
            EnemyModel.setKnockBackForceY(EnemyModel.KNOCKBACK_FORCE_Y);
            setPicPath("");
    }
}
