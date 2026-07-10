package hana.HollowKnight.model.items;

import hana.HollowKnight.model.entities.EnemyModel;
import hana.HollowKnight.model.entities.PlayerModel;

public class UnbreakableStrengthModel extends Charm{
    protected UnbreakableStrengthModel(PlayerModel playerModel) {
        super(CharmType.UNBREAKABLE_STRENGTH, playerModel);
        this.unlocked = true;
    }

    @Override
    public void applyCharm() {
        if (player.getUsedNotches() < 3) {
            player.setDamagePerHit((player.getDamagePerHit() * 10));
            setPicPath("");
        }
    }

    @Override
    public void cancelCharm() {
            player.setDamagePerHit((player.getDamagePerHit() / 10));
            setPicPath("");
    }
}
