package hana.HollowKnight.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import hana.HollowKnight.model.entities.PlayerModel;
import hana.HollowKnight.view.audio.AudioManager;

import java.util.EnumMap;
import java.util.Map;

public class InputHandler {
    private static InputHandler instance;
    private final StringBuilder inputBuffer = new StringBuilder();
    private float bufferTimer = 0f;
    private static final float BUFFER_TIMEOUT = 1.5f;

    public void updateCheat(float delta, GameController controller) {
        bufferTimer += delta;
        if (bufferTimer >= BUFFER_TIMEOUT){
            inputBuffer.setLength(0);
        }
        for (int keyCode = 0; keyCode < 256; keyCode++){
            if (Gdx.input.isKeyJustPressed(keyCode)){
                String keyStr = Input.Keys.toString(keyCode).toUpperCase();
                if (keyStr.length() == 1){
                    inputBuffer.append(keyStr);
                    bufferTimer = 0;
                    checkCheats(controller);
                }
            }
        }}

    private void checkCheats(GameController controller) {
        String currentInput = inputBuffer.toString();
        PlayerModel player = controller.getModel().getPlayer();

        if (currentInput.endsWith("EH")) { //Emergency Heal
            if (player.getHealth() < 3){
                player.setHealth(player.getHealth() + 1);
            }}

        if (currentInput.endsWith("GM")) { //God Mode
            player.setGodeMode(true);
        }

        if (currentInput.endsWith("RS")) { //Refill Soul
            player.setSoul(PlayerModel.DEFAULT_MAX_SOUL);
        }

        if (currentInput.endsWith("SM")) { //Spectator Mode
            PlayerModel.MOVE_SPEED *= 7;
            PlayerModel.GRAVITY = 0;
            PlayerModel.DASH_SPEED *= 5;
        }

        if (currentInput.endsWith("BK")) {//Boss Kill (only in greenpath)
            if (!controller.getBosses().isEmpty()) {
                controller.getBosses().getFirst().setAlive(false);
                player.addPlayerKillsCount();
            }
        }

        if (currentInput.endsWith("BT")) { //Boss Arena Teleport
            controller.teleportToBossArena("maps/Greenpath/Greenpath.tmx");
        }

        if (currentInput.endsWith("SW")) {
            controller.lunchPogo();
        }
        if (currentInput.endsWith("SD")) {
            controller.lunchVenegful();
        }

        if (inputBuffer.length() > 10) {
            inputBuffer.delete(0, 1);
        }
    }



    public static InputHandler getInstance() {
        if (instance == null) {
            instance = new InputHandler();
        }
        return instance;
    }

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

    private InputHandler() {
        keyBindings.put(PlayerAction.MOVE_LEFT, Input.Keys.LEFT);
        keyBindings.put(PlayerAction.MOVE_RIGHT, Input.Keys.RIGHT);

        keyBindings.put(PlayerAction.JUMP, Input.Keys.Z);
        keyBindings.put(PlayerAction.ATTACK, Input.Keys.X);

        keyBindings.put(PlayerAction.FOCUS_SOUL, Input.Keys.A);
        keyBindings.put(PlayerAction.DASH, Input.Keys.C);

        keyBindings.put(PlayerAction.OPEN_INVENTORY, Input.Keys.I);
        keyBindings.put(PlayerAction.PAUSE, Input.Keys.ESCAPE);
    }

    public void update(PlayerModel player, GameController controller) {
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
            AudioManager.getInstance().playJumpSound();
        }
        if (isJustPressed(PlayerAction.DASH)) {
            player.dash();
            if (player.isDashing()) {
                AudioManager.getInstance().playHeroDashSound();}

        }
        if (isJustPressed(PlayerAction.ATTACK)) {
            player.attack();
            AudioManager.getInstance().playSwordSound();
        }

        player.focus(Gdx.graphics.getDeltaTime());

        if (isJustPressed(PlayerAction.FOCUS_SOUL)) {
            if (player.isFocusing()) {
                AudioManager.getInstance().playFocusSound();
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) && !player.isPogo()) {
            AudioManager.getInstance().playPogo();
            controller.lunchPogo();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && !player.isVenegful()) {
            AudioManager.getInstance().playPogo();
            controller.lunchVenegful();
        }

        boolean jumpDown = isDown(PlayerAction.JUMP);
        if (!jumpDown && jumpWasDown) {
            player.cutJumpShort();
        }
        jumpWasDown = jumpDown;

    }

    public boolean isDown(PlayerAction action) {
        return Gdx.input.isKeyPressed(keyBindings.get(action));
    }

    public boolean isJustPressed(PlayerAction action) {
        return Gdx.input.isKeyJustPressed(keyBindings.get(action));
    }

    public String getKeyNameFor(PlayerAction action) {
        return Input.Keys.toString(keyBindings.get(action));
    }

    public void rebind(PlayerAction action, int newKeyCode) {
        keyBindings.put(action, newKeyCode);
    }

    public void reset (){
        keyBindings.clear();
        keyBindings.put(PlayerAction.MOVE_LEFT, Input.Keys.LEFT);
        keyBindings.put(PlayerAction.MOVE_RIGHT, Input.Keys.RIGHT);

        keyBindings.put(PlayerAction.JUMP, Input.Keys.Z);
        keyBindings.put(PlayerAction.ATTACK, Input.Keys.X);

        keyBindings.put(PlayerAction.FOCUS_SOUL, Input.Keys.A);
        keyBindings.put(PlayerAction.DASH, Input.Keys.C);

        keyBindings.put(PlayerAction.OPEN_INVENTORY, Input.Keys.I);
        keyBindings.put(PlayerAction.PAUSE, Input.Keys.ESCAPE);
    }
}
