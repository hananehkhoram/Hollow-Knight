package hana.HollowKnight.model.items;

import hana.HollowKnight.model.entities.PlayerModel;

public class VoidHeartModel extends Charm {

    protected VoidHeartModel(PlayerModel playerModel) {
        super(CharmType.VOID_HEART, playerModel);
        this.unlocked = false;
        this.picPath = "charms/Void Heart - charm_black.png";
    }

    @Override
    public void applyCharm() {
        player.setDamagePerHit((player.getDamagePerHit() * 10));
        player.isVoidHeart = true;
        System.out.println(player.isVoidHeart);
    }

    @Override
    public void cancelCharm() {
        player.setDamagePerHit((player.getDamagePerHit() / 10));
        player.isVoidHeart = false;
    }
}
