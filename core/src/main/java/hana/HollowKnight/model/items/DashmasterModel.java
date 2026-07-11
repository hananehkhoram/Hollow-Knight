package hana.HollowKnight.model.items;

import hana.HollowKnight.model.entities.PlayerModel;

public class DashmasterModel extends Charm {
    protected DashmasterModel(PlayerModel player) {
        super(CharmType.DASHMASTER, player);
        this.unlocked = true;
        this.picPath = "charms/Dashmaster - _0011_charm_generic_03.png";
    }

    @Override
    public void applyCharm() {
        PlayerModel.DASH_COOLDOWN = 0.1f;
    }

    @Override
    public void cancelCharm() {
        PlayerModel.DASH_COOLDOWN = 1.5f;
    }
}
