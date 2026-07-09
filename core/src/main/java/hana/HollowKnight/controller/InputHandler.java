package hana.HollowKnight.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import hana.HollowKnight.model.entities.PlayerModel;

import java.util.EnumMap;
import java.util.Map;

public class InputHandler {

    public enum PlayerAction {
        MOVE_LEFT("Move Left"),
        MOVE_RIGHT("Move Right"),
        MOVE_UP("Move Up"),
        JUMP("Jump"),
        DASH("Dash"),
        ATTACK("Attack"),
        OPEN_INVENTORY("Inventory"),
        PAUSE("Pause"),
        FOCUS_SOUL("Focus soul");

        private final String displayName;
        PlayerAction(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    private final Map<PlayerAction, Integer> keyBindings = new EnumMap<>(PlayerAction.class);

    private boolean jumpWasDown = false;

    public InputHandler() {
        keyBindings.put(PlayerAction.MOVE_LEFT, Input.Keys.LEFT);
        keyBindings.put(PlayerAction.MOVE_RIGHT, Input.Keys.RIGHT);

        keyBindings.put(PlayerAction.JUMP, Input.Keys.Z);
        keyBindings.put(PlayerAction.ATTACK, Input.Keys.X);

        keyBindings.put(PlayerAction.FOCUS_SOUL, Input.Keys.A);
        keyBindings.put(PlayerAction.DASH, Input.Keys.C);

        keyBindings.put(PlayerAction.OPEN_INVENTORY, Input.Keys.TAB);
        keyBindings.put(PlayerAction.PAUSE, Input.Keys.ESCAPE);
    }

    public void update(PlayerModel player) {
        boolean left = isDown(PlayerAction.MOVE_LEFT);
        boolean right = isDown(PlayerAction.MOVE_RIGHT);

        if (left && !right) {
            player.moveLeft();
        } else if (right && !left) {
            player.moveRight();
        } else {
            player.stopMoving();
        }

        if (isJustPressed(PlayerAction.JUMP)) {
            player.jump();
        }
        if (isJustPressed(PlayerAction.DASH)) {
            player.dash();
        }
        if (isJustPressed(PlayerAction.ATTACK)) {
            player.attack();
        }

        boolean jumpDown = isDown(PlayerAction.JUMP);
        if (!jumpDown && jumpWasDown) {
            player.cutJumpShort();
        }
        jumpWasDown = jumpDown;
    }

    private boolean isDown(PlayerAction action) {
        return Gdx.input.isKeyPressed(keyBindings.get(action));
    }

    private boolean isJustPressed(PlayerAction action) {
        return Gdx.input.isKeyJustPressed(keyBindings.get(action));
    }


    public int getKeyFor(PlayerAction action) {
        return keyBindings.get(action);
    }

    public String getKeyNameFor(PlayerAction action) {
        return Input.Keys.toString(keyBindings.get(action));
    }

    public void rebind(PlayerAction action, int newKeyCode) {
        keyBindings.put(action, newKeyCode);
    }

    public Map<PlayerAction, Integer> getAllBindings() {
        return keyBindings;
    }

    public void reset (){
        keyBindings.clear();
        keyBindings.put(PlayerAction.MOVE_LEFT, Input.Keys.LEFT);
        keyBindings.put(PlayerAction.MOVE_RIGHT, Input.Keys.RIGHT);

        keyBindings.put(PlayerAction.JUMP, Input.Keys.Z);
        keyBindings.put(PlayerAction.ATTACK, Input.Keys.X);

        keyBindings.put(PlayerAction.FOCUS_SOUL, Input.Keys.A);
        keyBindings.put(PlayerAction.DASH, Input.Keys.C);

        keyBindings.put(PlayerAction.OPEN_INVENTORY, Input.Keys.TAB);
        keyBindings.put(PlayerAction.PAUSE, Input.Keys.ESCAPE);
    }
}
