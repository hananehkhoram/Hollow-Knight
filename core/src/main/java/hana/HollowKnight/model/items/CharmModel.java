package hana.HollowKnight.model.items;

/**
 * نمونه‌ی زمان‌اجرای یک چارم؛ فقط وضعیت (قفل/باز، سوارشده یا نه) را نگه می‌دارد.
 * منطق «اثر» هر چارم (مثلا افزایش سرعت دش) در PlayerModel هنگام equip/unequip اعمال می‌شود.
 */
public class CharmModel {
    private final CharmType type;
    private boolean unlocked;
    private boolean equipped;

    public CharmModel(CharmType type) {
        this.type = type;
    }

    public CharmType getType() { return type; }

    public boolean isUnlocked() { return unlocked; }
    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
        if (!unlocked) equipped = false; // چارم قفل‌شده نمی‌تواند سوار باشد
    }

    public boolean isEquipped() { return equipped; }
    public void setEquipped(boolean equipped) {
        if (equipped && !unlocked) return; // نمی‌شود چارم قفل را سوار کرد
        this.equipped = equipped;
    }

    public String getDisplayName() {
        return type.name().replace('_', ' ');
    }
}
