package hana.HollowKnight.model.items;

import hana.HollowKnight.model.entities.EnemyModel;
import hana.HollowKnight.model.entities.PlayerModel;

public class QuickSlashModel extends Charm{
    protected QuickSlashModel(PlayerModel player) {
        super(CharmType.QUICK_SLASH, player);
        this.unlocked = true;
    }

    @Override
    public void applyCharm() {
        if (player.getUsedNotches() < 3) {
            PlayerModel.ATTACK_DURATION = 0.1f;
            PlayerModel.ATTACK_COOLDOWN = 0.22f;
            setPicPath("");

        }
    }

    @Override
    public void cancelCharm() {
            PlayerModel.ATTACK_DURATION = 0.5f;
            PlayerModel.ATTACK_COOLDOWN = 0.5f;
            setPicPath("");
    }
}
