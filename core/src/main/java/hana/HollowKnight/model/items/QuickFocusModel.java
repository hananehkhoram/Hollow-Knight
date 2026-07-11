package hana.HollowKnight.model.items;

import hana.HollowKnight.model.entities.PlayerModel;

public class QuickFocusModel extends Charm {
    protected QuickFocusModel(PlayerModel player) {
        super(CharmType.QUICK_FOCUS, player);
        this.unlocked = true;
        this.picPath = "charms/Quick Focus - _0005_charm_fast_focus.png";
    }

    @Override
    public void applyCharm() {
        PlayerModel.FOCUS_DURATION = 0.1f;
    }

    @Override
    public void cancelCharm() {
        PlayerModel.FOCUS_DURATION = 1.5f;
    }
}
