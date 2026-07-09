package hana.HollowKnight.view.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class AudioManager {
    private static AudioManager instance;

    private final Sound backgroundSound;
    private final Sound clickedSound;
    private final Sound crossroadsSound;
    private final Sound cityOfTearsSound;
    private final Sound heroDamage;

    private float sfxVolume = 1f;
    private float bgmVolume = 0.75f;

    private long backgroundSoundId = -1;

    private AudioManager() {
        backgroundSound = Gdx.audio.newSound(Gdx.files.internal("01. Enter Hallownest.mp3"));
        clickedSound = Gdx.audio.newSound(Gdx.files.internal("mainButtonConfirm.wav"));
        crossroadsSound = Gdx.audio.newSound(Gdx.files.internal("03. Crossroads.mp3"));
        cityOfTearsSound = Gdx.audio.newSound(Gdx.files.internal("09. City of Tears.mp3"));
        heroDamage = Gdx.audio.newSound(Gdx.files.internal("hero_damage.wav"));
    }

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    public void playHeroDamageSound() {
        heroDamage.play(sfxVolume);
    }

    public void playMenuSound() {
        if (backgroundSoundId == -1) {
            backgroundSoundId = backgroundSound.loop(bgmVolume);
        }
    }

    public void playCrossroadsSound() {
        stopMenuSound();
        crossroadsSound.loop(bgmVolume);
    }

    public void playCityOfTearsSound() {
        stopMenuSound();
        cityOfTearsSound.loop(bgmVolume);
    }

    public void stopMenuSound() {
        backgroundSound.stop();
        backgroundSoundId = -1;
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
        if (backgroundSoundId != -1) {
            backgroundSound.setVolume(backgroundSoundId, bgmVolume);
        }
    }
}
