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
    private final Sound heroDeath;
    private final Sound jump;
    private final Sound getDamage;
    private final Sound focusHealthHeal;
    private final Sound focus;
    private final Sound breakWall1;
    private final Sound breakWall2;
    private final Sound breakWall3;
    private final Sound moving;
    private final Sound sword;
    private final Music GreenpathSound;

    private float sfxVolume = 5f;
    private float bgmVolume = 0.5f;
    private Music currentBgm;

    private AudioManager() {
        backgroundSound = Gdx.audio.newMusic(Gdx.files.internal("01. Enter Hallownest.mp3"));
        clickedSound = Gdx.audio.newSound(Gdx.files.internal("mainButtonConfirm.wav"));
        crossroadsSound = Gdx.audio.newMusic(Gdx.files.internal("03. Crossroads.mp3"));
        cityOfTearsSound = Gdx.audio.newMusic(Gdx.files.internal("09. City of Tears.mp3"));
        heroDamage = Gdx.audio.newSound(Gdx.files.internal("hero_damage.wav"));
        heroDash = Gdx.audio.newSound(Gdx.files.internal("hero_dash.wav"));
        jump = Gdx.audio.newSound(Gdx.files.internal("hero_jump.wav"));
        getDamage = Gdx.audio.newSound(Gdx.files.internal("hero_damage.wav"));
        focusHealthHeal = Gdx.audio.newSound(Gdx.files.internal("focus_health_heal.wav"));
        focus = Gdx.audio.newSound(Gdx.files.internal("focus_health_charging.wav"));
        breakWall1 = Gdx.audio.newSound(Gdx.files.internal("breakable_wall_hit_1.wav"));
        breakWall2 = Gdx.audio.newSound(Gdx.files.internal("breakable_wall_hit_2.wav"));
        breakWall3 = Gdx.audio.newSound(Gdx.files.internal("breakable_wall_death.wav"));
        heroDeath = Gdx.audio.newSound(Gdx.files.internal("hero_death_extra_details.wav"));
        moving = Gdx.audio.newSound(Gdx.files.internal("hero_run_footsteps_stone.wav"));
        sword = Gdx.audio.newSound(Gdx.files.internal("sword_4.wav"));
        GreenpathSound = Gdx.audio.newMusic(Gdx.files.internal("05. Greenpath.mp3"));
    }

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    public void playGreenpathSound() {
        switchBgm(GreenpathSound);
    }

    public void stopGreenpathSound() {
        GreenpathSound.stop();
    }

    public void playHeroDeathSound() {
        stopCityofTears();
        heroDeath.play(sfxVolume);
    }

    public void stopCityofTears() {
        cityOfTearsSound.stop();
    }

    public void playFocusSound() {
        focus.play(sfxVolume);
    }

    public void playSwordSound() {
        sword.play(sfxVolume);
    }

    public void stopFocusSound() {
        focus.stop();
    }

    public void playMovingSound() {
        moving.play(sfxVolume);
    }

    public void playBreakWall1() {
        breakWall1.play(sfxVolume);
    }

    public void playBreakWall2() {
        breakWall2.play(sfxVolume);
    }

    public void playBreakWall3() {
        breakWall3.play(sfxVolume);
    }


    public void playFocusHealSound() {
        focusHealthHeal.play(sfxVolume);
    }

    public void playJumpSound() {
        jump.play(sfxVolume);
    }

    public void playGetDamageSound() {
        getDamage.play(sfxVolume);
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
