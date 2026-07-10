package hana.HollowKnight.model.items;

import hana.HollowKnight.model.entities.PlayerModel;

public abstract class Charm {
    protected final CharmType type;
    protected boolean unlocked;
    protected boolean equipped;
    protected PlayerModel player;
    protected String picPath;

    protected Charm(CharmType type, PlayerModel player) {
        this.type = type;
        this.equipped = false;
        this.player = player;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public abstract void applyCharm();

    public abstract void cancelCharm();

    public void toggleEquipped() {
        if (isEquipped()) {
            cancelCharm();
            player.decreaseUsedNotches();
            this.unlocked = false;
        } else {
            applyCharm();
            player.increaseUsedNotches();
            this.equipped = true;
        }
    }

    public CharmType getType() {
        return type;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
        if (!unlocked) equipped = false;
    }

    public boolean isEquipped() {
        return equipped;
    }

    public void setEquipped(boolean equipped) {
        if (equipped && !unlocked) return;
        this.equipped = equipped;
    }

    public String getDisplayName() {
        return type.name().replace('_', ' ');
    }
}
