package hana.HollowKnight.model.items;

import hana.HollowKnight.model.entities.EnemyModel;
import hana.HollowKnight.model.entities.PlayerModel;

public class HeavyBlowModel extends Charm {
    protected HeavyBlowModel(PlayerModel player) {
        super(CharmType.HEAVY_BLOW, player);
        this.unlocked = true;
        this.picPath = "charms/Heavy Blow - _0008_charm_nail_damage_up.png";
    }

    @Override
    public void applyCharm() {
        EnemyModel.setKnockBackForceX(EnemyModel.KNOCKBACK_FORCE_X * 2);
        EnemyModel.setKnockBackForceY(EnemyModel.KNOCKBACK_FORCE_Y * 2);

    }

    @Override
    public void cancelCharm() {
        EnemyModel.setKnockBackForceX(EnemyModel.KNOCKBACK_FORCE_X);
        EnemyModel.setKnockBackForceY(EnemyModel.KNOCKBACK_FORCE_Y);
    }
}
