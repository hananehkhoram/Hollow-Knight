package hana.HollowKnight.model.entities;

import hana.HollowKnight.view.audio.AudioManager;

public class ZoteModel extends Entity {
    public static final String[] talking = {
        "Hoy! Watch where you're going, you soggy vagabond! I don't want you splashing me. I've had a miserable time trying to keep dry.\n" +
            "I am Zote the Mighty, a knight of great renown. If I decide that I'm going to stay dry, then that's what's going to happen!\n" +
            "Curse this wretched city! Water and monsters constantly fall from above! It's no wonder this blasted kingdom fell to ruin...",
        "Have you seen them? The guards who still patrol this city, even after dying?\n" +
            "Hmph. Being overly devoted to one's duties is no virtue.",
        "Why does the air weep? Is this kingdom trying to mock me? I shed my last tear long ago..."
    };

    public static final String[] rules = {
        "Losing a battle earns you nothing and teaches you nothing. Win your battles, or don't engage in them at all!",
        "Fools laugh at everything, even at their superiors. But beware, laughter isn't harmless! Laughter spreads like a disease, and soon everyone is laughing at you.\n" +
            "You need to strike at the source of this perverse merriment quickly to stop it from spreading.",
        "Fighting and adventuring take their toll on your body. When you rest, your body strengthens and repairs itself. The longer you rest, the stronger you become.",
        "The past is painful, and thinking about your past can only bring you misery. Think about something else instead, such as the future, or some food.",
        "Is your opponent strong? No matter! Simply overcome their strength with even more strength, and they'll soon be defeated.",
        "Our elders teach that our fate is chosen for us before we are even born. I disagree."
    };

    private final int rulesNumber = rules.length;
    private static final float DEFAULT_WIDTH = 100;
    private static final float DEFAULT_HEIGHT = 100;
    private States state = States.IDLE;
    private boolean isTalking = false;
    private boolean hasEverTalked = false;
    private int talkingId = 0;
    private int rulesId = 0;
    private float stateTimer = 0f;
    private float attackCooldown = 0f;
    private float attackDuration = 0.5f;

    public ZoteModel() {
        this(100f, 100f);
    }

    public float getStateTimer() {
        return stateTimer;
    }


    public ZoteModel(float startX, float startY) {
        super(startX,
            startY,
            DEFAULT_WIDTH,
            DEFAULT_HEIGHT);
    }


    public int getTalkingId() {
        return talkingId;
    }

    public int getRulesId() {
        return rulesId;
    }

    public States getState() {
        return state;
    }

    public void setState(States state) {
        this.state = state;
    }

    public void advanceTalkingId() {
        this.talkingId++;
    }

    public boolean hasMoreIntroLines() {
        return talkingId < talking.length;
    }

    public void advanceRulesId() {
        this.rulesId = (this.rulesId + 1) % rulesNumber;
    }

    public String talk() {
        if (this.state != States.TALK) {
            this.state = States.TALK;
        }
        this.stateTimer = 0f;


        isTalking = true;
        hasEverTalked = true;

        if (talkingId < talking.length) {
            String speech = talking[talkingId];
            advanceTalkingId();
            return speech;
        } else {
            String rule = rules[rulesId];
            advanceRulesId();
            return rule;
        }
    }
    public void attack() {
        if (state == States.IDLE || state == States.TALK) {
            state = States.ATTACK;
            stateTimer = 0f;
            this.velocityX = 150f;
            AudioManager.getInstance().playZoteAttack();
        }
    }

    @Override
    public void update(float delta) {
        stateTimer += delta;

        switch (state) {
            case ATTACK:
                if (stateTimer >= 0.5f) {
                    state = States.ROLL;
                    stateTimer = 0f;
                    velocityX = this.isFacingRight() ? -100f : 100f;
                }
                break;

            case ROLL:
                if (stateTimer > 0.5f) {
                    state = States.GETUP;
                    stateTimer = 0f;
                    this.setVelocityX(0f);
                }
                break;

            case GETUP:
                if (stateTimer > 0.3f) {
                    state = States.IDLE;
                    isTalking = false;
                    stateTimer = 0f;
                }
                break;

            case FALL:
                if (isOnGround()){
                    state = States.IDLE;
                }
                break;

            case TALK:
                this.setVelocityX(0f); // 🟢 مطمئن می‌شویم زوت موقع حرف زدن راه نمی‌رود
                break;

            default:
                this.setVelocityX(0f);
                break;
        }

        super.update(delta);
    }

    public void setTalking(boolean talking) {
        isTalking = talking;
    }

    public boolean hasEverTalked() {
        return hasEverTalked;
    }

    public enum States {IDLE, TALK, ATTACK, FALL, ROLL, GETUP}
}
