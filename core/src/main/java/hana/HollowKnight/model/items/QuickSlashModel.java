package hana.HollowKnight.model.items;

import hana.HollowKnight.model.entities.PlayerModel;

public class QuickSlashModel extends Charm {
    protected QuickSlashModel(PlayerModel player) {
        super(CharmType.QUICK_SLASH, player);
        this.unlocked = true;
        this.picPath = "charms/Quick Slash - _0003_charm_nail_slash_speed_up.png";
    }

    @Override
    public void applyCharm() {
        PlayerModel.ATTACK_DURATION = 0.1f;
        PlayerModel.ATTACK_COOLDOWN = 0.22f;

    }

    @Override
    public void cancelCharm() {
        PlayerModel.ATTACK_DURATION = 0.5f;
        PlayerModel.ATTACK_COOLDOWN = 0.5f;
    }
}
