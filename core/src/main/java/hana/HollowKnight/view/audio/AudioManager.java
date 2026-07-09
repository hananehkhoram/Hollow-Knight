package hana.HollowKnight.view.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AudioManager {
    private static AudioManager instance;

    private final Music backgroundSound;
    private final Sound clickedSound;
    private final Music crossroadsSound;
    private final Music cityOfTearsSound;
    private final Sound heroDamage;
    private final Sound heroDash;
    private final Sound jump;
    private final Sound move;

    private float sfxVolume = 1f;
    private float bgmVolume = 0.75f;
    private Music currentBgm;

    private AudioManager() {
        backgroundSound = Gdx.audio.newMusic(Gdx.files.internal("01. Enter Hallownest.mp3"));
        clickedSound = Gdx.audio.newSound(Gdx.files.internal("mainButtonConfirm.wav"));
        crossroadsSound = Gdx.audio.newMusic(Gdx.files.internal("03. Crossroads.mp3"));
        cityOfTearsSound = Gdx.audio.newMusic(Gdx.files.internal("09. City of Tears.mp3"));
        heroDamage = Gdx.audio.newSound(Gdx.files.internal("hero_damage.wav"));
        heroDash = Gdx.audio.newSound(Gdx.files.internal("hero_dash.wav"));
        jump = Gdx.audio.newSound(Gdx.files.internal("hero_jump.wav"));
        move = Gdx.audio.newSound(Gdx.files.internal("grass_move_6.wav"));
    }

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    public void playJumpSound() {
        jump.play(sfxVolume);
    }
    public void playMoveSound() {
        move.play(sfxVolume);
    }

    public void playHeroDashSound() {
        heroDash.play(sfxVolume);
    }

    public void playHeroDamageSound() {
        heroDamage.play(sfxVolume);
    }

    private void switchBgm(Music newBgm) {
        if (currentBgm == newBgm) return;
        if (currentBgm != null) {
            currentBgm.stop();
        }
        currentBgm = newBgm;
        currentBgm.setLooping(true);
        currentBgm.setVolume(bgmVolume);
        currentBgm.play();
    }

    public void playMenuSound() {
        switchBgm(backgroundSound);
    }

    public void playCrossroadsSound() {
        switchBgm(crossroadsSound);
    }

    public void playCityOfTearsSound() {
        switchBgm(cityOfTearsSound);
    }

    public void stopMenuSound() {
        if (currentBgm == backgroundSound) {
            backgroundSound.stop();
            currentBgm = null;
        }
    }

    public void clickMenuSound() {
        clickedSound.play(sfxVolume);
    }

    public float getSfxVolume() {
        return sfxVolume;
    }

    public void setVolume(float volume) {
        this.sfxVolume = volume;
    }

    public float getBgmVolume() {
        return bgmVolume;
    }

    public void setBgmVolume(float bgmVolume) {
        this.bgmVolume = bgmVolume;
        if (currentBgm != null) {
            currentBgm.setVolume(bgmVolume);
        }
    }

    public void dispose() {
        backgroundSound.dispose();
        clickedSound.dispose();
        crossroadsSound.dispose();
        cityOfTearsSound.dispose();
        heroDamage.dispose();
        heroDash.dispose();
    }
}
