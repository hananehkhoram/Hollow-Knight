package hana.HollowKnight.model.items;

import hana.HollowKnight.model.entities.PlayerModel;

public class QuickFocusModel extends Charm {
    protected QuickFocusModel(PlayerModel player) {
        super(CharmType.QUICK_FOCUS, player);
        this.unlocked = true;
    }

    @Override
    public void applyCharm() {
        if (player.getUsedNotches() < 3) {
            PlayerModel.FOCUS_DURATION = 1f;
            setPicPath("");

        }
    }

    @Override
    public void cancelCharm() {
            PlayerModel.FOCUS_DURATION = 1.5f;
            setPicPath("");
    }
}
