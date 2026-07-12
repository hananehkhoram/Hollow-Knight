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
    private final Music bossFight;
    private final Music gameOver;
    private final Sound bossDefeat;
    private final Sound zote1;
    private final Sound zote2;
    private final Sound zote3;
    private final Sound zote4;
    private final Sound zote5;
    private final Sound zoteAttack;
    private int zoteIndex;

    private float sfxVolume = 1.0f;
    private float bgmVolume = 0.5f;
    private Music currentBgm;

    private Music musicToFadeOut = null;
    private float fadeDuration = 0f;
    private float fadeTimer = 0f;
    private float fadeStartVolume = 0f;
    private boolean isFading = false;

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
        bossDefeat = Gdx.audio.newSound(Gdx.files.internal("Boss Defeat.wav"));
        bossFight = Gdx.audio.newMusic(Gdx.files.internal("04. False Knight.mp3"));
        gameOver = Gdx.audio.newMusic(Gdx.files.internal("PersianGameOfThrones.mp3"));
        zote1 = Gdx.audio.newSound(Gdx.files.internal("Animations/zote/Zote_01.wav"));
        zote2 = Gdx.audio.newSound(Gdx.files.internal("Animations/zote/Zote_02.wav"));
        zote3 = Gdx.audio.newSound(Gdx.files.internal("Animations/zote/Zote_03.wav"));
        zote4 = Gdx.audio.newSound(Gdx.files.internal("Animations/zote/Zote_04.wav"));
        zote5 = Gdx.audio.newSound(Gdx.files.internal("Animations/zote/Zote_05.wav"));
        zoteAttack = Gdx.audio.newSound(Gdx.files.internal("Animations/zote/Zote_battle_defeat_end.wav"));
        zoteIndex = 1;

    }

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    public void playZote() {
        zoteIndex = (zoteIndex + 1) % 6;
        if (zoteIndex == 0) {
            zoteIndex = 1;
        }

        switch (zoteIndex) {
            case 1:
                zote1.play(sfxVolume);
                break;
            case 2:
                zote2.play(sfxVolume);
                break;
            case 3:
                zote3.play(sfxVolume);
                break;
            case 4:
                zote4.play(sfxVolume);
                break;
            case 5:
                zote5.play(sfxVolume);
        }
    }

    public void playZoteAttack(){
        zoteAttack.play(sfxVolume);
    }

    public void update(float delta) {
        if (!isFading || musicToFadeOut == null) return;

        fadeTimer += delta;
        float progress = fadeTimer / fadeDuration;

        if (progress >= 1f) {
            musicToFadeOut.setVolume(0f);
            musicToFadeOut.stop();
            musicToFadeOut = null;
            isFading = false;
        } else {
            float newVolume = fadeStartVolume * (1f - progress);
            musicToFadeOut.setVolume(newVolume);
        }
    }

    public void fadeOutCurrentMusic(float duration) {
        if (currentBgm == null || !currentBgm.isPlaying()) return;

        musicToFadeOut = currentBgm;
        currentBgm = null;
        fadeDuration = duration;
        fadeTimer = 0f;
        fadeStartVolume = musicToFadeOut.getVolume();
        isFading = true;
    }

    public void playGameOver() {
        switchBgm(gameOver);
    }

    public void stopGameOver() {
        gameOver.stop();
    }

    private void switchBgm(Music newBgm) {
        if (currentBgm == newBgm) return;

        if (currentBgm != null) {
            fadeOutCurrentMusic(1.5f);
        }

        currentBgm = newBgm;
        currentBgm.setLooping(true);
        currentBgm.setVolume(bgmVolume);
        currentBgm.play();
    }

    public void playGreenpathSound() {
        switchBgm(GreenpathSound);
    }

    public void stopGreenpathSound() {
        GreenpathSound.stop();
        if (currentBgm == GreenpathSound) currentBgm = null;
    }

    public void playCityOfTearsSound() {
        switchBgm(cityOfTearsSound);
    }

    public void playBossFightSound() {
        switchBgm(bossFight);
    }

    public void stopCityofTears() {
        cityOfTearsSound.stop();
        if (currentBgm == cityOfTearsSound) currentBgm = null;
    }

    public void stopBossFight() {
        bossFight.stop();
    }

    public void playMenuSound() {
        switchBgm(backgroundSound);
        stopGameOver();
        stopBossFight();
    }

    public void stopMenuSound() {
        if (currentBgm == backgroundSound) {
            backgroundSound.stop();
            currentBgm = null;
        }
    }


    public void playHeroDeathSound() {
        if (currentBgm != null) {
            fadeOutCurrentMusic(0.5f);
        }
        heroDeath.play(sfxVolume);
    }

    public void playFocusSound() {
        focus.play(sfxVolume);
    }

    public void stopFocusSound() {
        focus.stop();
    }

    public void playSwordSound() {
        sword.play(sfxVolume);
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

    public void clickMenuSound() {
        clickedSound.play(sfxVolume);
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
        heroDeath.dispose();
        jump.dispose();
        getDamage.dispose();
        focusHealthHeal.dispose();
        focus.dispose();
        breakWall1.dispose();
        breakWall2.dispose();
        breakWall3.dispose();
        moving.dispose();
        sword.dispose();
        GreenpathSound.dispose();
    }
}
