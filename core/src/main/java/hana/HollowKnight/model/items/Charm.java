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

    public static Charm charmFactory (CharmType type, PlayerModel player) {
         switch (type) {
             case DASHMASTER : {
                 return new DashmasterModel(player);
             }
             case HEAVY_BLOW : {
                 return new HeavyBlowModel(player);
             }
             case QUICK_FOCUS : {
                 return new QuickFocusModel(player);
             }
             case QUICK_SLASH : {
                 return new QuickSlashModel(player);
             }
             case SHARP_SHADOW : {
                 return new SharpShadowModel(player);
             }
             case SOUL_CATCHER : {
                 return new SoulCatcherModel(player);
             }
             case UNBREAKABLE_STRENGTH : {
                 return new UnbreakableStrengthModel(player);
             }
             case VOID_HEART : {
                 return new VoidHeartModel(player);
             }
             default: return null;
         }
    }

    public abstract void applyCharm();

    public abstract void cancelCharm();

    public void toggleEquipped() {
        if (isEquipped()) {
            cancelCharm();
            player.decreaseUsedNotches();
            this.equipped = false;
        } else if (player.getUsedNotches() < 3) {
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

    public String getPicPath() {
        return picPath;
    }

    public String getDisplayName() {
        return type.name().replace('_', ' ');
    }
}
