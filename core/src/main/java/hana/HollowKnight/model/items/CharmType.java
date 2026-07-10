package hana.HollowKnight.model.items;

public enum CharmType {
    QUICK_SLASH( "Increases the speed of the nail's swing animation."),
    SOUL_CATCHER( "Increases the amount of soul gained per hit."),
    DASHMASTER("Increases dash distance and speed."),
    VOID_HEART("A unique charm hidden in a secret room; grants no notch cost."),
    HEAVY_BLOW("Increases knockback force applied on enemies."),
    QUICK_FOCUS ("Increases time duration needed for healing through focusing."),
    UNBREAKABLE_STRENGTH ("Increase Nail slash power."),
    SHARP_SHADOW("Dash work as a projectile.");

    private final String description;

    CharmType(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
}
