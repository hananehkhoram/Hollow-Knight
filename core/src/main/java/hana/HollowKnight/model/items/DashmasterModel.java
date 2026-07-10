package hana.HollowKnight.model.items;

import hana.HollowKnight.model.entities.PlayerModel;

public class DashmasterModel extends Charm {
    protected DashmasterModel(PlayerModel player) {
        super(CharmType.DASHMASTER, player);
        this.equipped = true;
    }

    @Override
    public void applyCharm() {
        if (player.getUsedNotches() < 3) {
            PlayerModel.DASH_COOLDOWN = 0.5f;
            setPicPath("");
        }
    }

    @Override
    public void cancelCharm() {
        if (this.equipped) {
            this.equipped = false;
            PlayerModel.DASH_COOLDOWN = 1.5f;
            setPicPath("");

        }
    }
}
