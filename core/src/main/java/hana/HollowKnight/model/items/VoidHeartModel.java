package hana.HollowKnight.model.items;

import hana.HollowKnight.model.entities.PlayerModel;

public class VoidHeartModel extends Charm {

    protected VoidHeartModel(PlayerModel playerModel) {
        super(CharmType.VOID_HEART, playerModel);
        this.unlocked = false;
    }

    @Override
    public void applyCharm() {
        if (player.getUsedNotches() < 3 && this.unlocked) {
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
