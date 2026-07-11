package hana.HollowKnight.model.items;

public enum CharmType {
    QUICK_SLASH( "Increases slash"),
    SOUL_CATCHER( "Increases soul gained per hit."),
    DASHMASTER("Increases dash distance and speed."),
    VOID_HEART("hidden in a secret room;"),
    HEAVY_BLOW("Increases knockback force"),
    QUICK_FOCUS ("Increases Focus Duration"),
    UNBREAKABLE_STRENGTH ("Increase Nail slash power"),
    SHARP_SHADOW("Dash works as a projectile.");

    private final String description;

    CharmType(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
}
