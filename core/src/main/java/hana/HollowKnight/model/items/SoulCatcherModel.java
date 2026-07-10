package hana.HollowKnight.model.items;

import hana.HollowKnight.model.entities.EnemyModel;
import hana.HollowKnight.model.entities.PlayerModel;

public class SoulCatcherModel extends Charm{
    protected SoulCatcherModel(PlayerModel playerModel) {
        super(CharmType.SOUL_CATCHER,  playerModel);
        this.unlocked = true;
    }

    @Override
    public void applyCharm() {
        if (player.getUsedNotches() < 3) {
            PlayerModel.SOUL_PER_HIT = PlayerModel.SOUL_PER_HIT * 2;
            setPicPath("");
        }
    }

    @Override
    public void cancelCharm() {
            PlayerModel.SOUL_PER_HIT = PlayerModel.SOUL_PER_HIT / 2;
            setPicPath("");

    }
}
