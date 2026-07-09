package hana.HollowKnight.model.items;

/**
 * فهرست چارم‌های قابل‌کسب در بازی. هر چارم هزینه‌ی ناچ (notch) و توضیح کوتاهی دارد
 * که در منوی Inventory و Guide نمایش داده می‌شود.
 */
public enum CharmType {
    WORN_MASKS(1, "Increases the number of health masks."),
    STALWART_SHELL(2, "Extends the invulnerability window after taking damage."),
    QUICK_SLASH(3, "Increases the speed of the nail's swing animation."),
    SOUL_CATCHER(2, "Increases the amount of soul gained per hit."),
    SHAMAN_STONE(2, "Increases the power of spells."),
    FRAGILE_STRENGTH(2, "Increases attack damage, but breaks after taking heavy damage."),
    DASHMASTER(2, "Increases dash distance and speed."),
    VOID_HEART(0, "A unique charm hidden in a secret room; grants no notch cost.");

    private final int notchCost;
    private final String description;

    CharmType(int notchCost, String description) {
        this.notchCost = notchCost;
        this.description = description;
    }

    public int getNotchCost() {
        return notchCost;
    }

    public String getDescription() {
        return description;
    }
}
