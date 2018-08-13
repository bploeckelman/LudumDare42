package lando.systems.ld42.utils;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Sine;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import lando.systems.ld42.LudumDare42;

import java.util.HashMap;

public class Audio implements Disposable {

    public static final float MUSIC_VOLUME = .4f;
    public static final float SOUND_VOLUME = .6f;
    public static final boolean shutUpYourFace = false;
    public static final boolean shutUpYourTunes = false;

    public enum Sounds {
        lose_level, peasant, soldier, archer, wizard, fight, click
    }

    public enum Musics {
        music1
    }

    public HashMap<Sounds, SoundContainer> sounds = new HashMap<Sounds, SoundContainer>();
    public HashMap<Musics, Music> musics = new HashMap<Musics, Music>();

    public Music currentMusic;
    public MutableFloat musicVolume;

    public Audio() {
        this(!shutUpYourTunes);
    }

    public Audio(boolean playMusic) {
//        putSound(Sounds.lose_level, Gdx.audio.newSound(Gdx.files.internal("audio/awww.mp3")));
//        putSound(Sounds.lose_level, Gdx.audio.newSound(Gdx.files.internal("audio/beer-bottle-pop.mp3")));

        putSound(Sounds.peasant, Gdx.audio.newSound(Gdx.files.internal("sounds/peasant.mp3")));
        putSound(Sounds.peasant, Gdx.audio.newSound(Gdx.files.internal("sounds/peasant1.mp3")));
        putSound(Sounds.soldier, Gdx.audio.newSound(Gdx.files.internal("sounds/army.mp3")));
        putSound(Sounds.archer, Gdx.audio.newSound(Gdx.files.internal("sounds/arrow.mp3")));
        putSound(Sounds.archer, Gdx.audio.newSound(Gdx.files.internal("sounds/arrow1.mp3")));
        putSound(Sounds.wizard, Gdx.audio.newSound(Gdx.files.internal("sounds/wizard.mp3")));

        putSound(Sounds.fight, Gdx.audio.newSound(Gdx.files.internal("sounds/swords.mp3")));

        putSound(Sounds.click, Gdx.audio.newSound(Gdx.files.internal("sounds/click.mp3")));
        putSound(Sounds.click, Gdx.audio.newSound(Gdx.files.internal("sounds/click1.mp3")));
        putSound(Sounds.click, Gdx.audio.newSound(Gdx.files.internal("sounds/click2.mp3")));


        musics.put(Musics.music1, Gdx.audio.newMusic(Gdx.files.internal("sounds/lute-harp.mp3")));
//        musics.put(Musics.music2, Gdx.audio.newMusic(Gdx.files.internal("audio/song2.mp3")));

        currentMusic = musics.get(Musics.music1);
        currentMusic.setLooping(true);
        currentMusic.setVolume(0);
        musicVolume = new MutableFloat(0);
        if (playMusic) {
            currentMusic.play();
            setMusicVolume(MUSIC_VOLUME, 5f);
        }
        currentMusic.setOnCompletionListener(nextSong);
    }

    public void update(float dt){
        if (currentMusic != null) {
            currentMusic.setVolume(musicVolume.floatValue());
        }
    }

    @Override
    public void dispose() {
        Sounds[] allSounds = Sounds.values();
        for (Sounds sound : allSounds) {
            if (sounds.get(sound) != null) {
                sounds.get(sound).dispose();
            }
        }
        Musics[] allMusics = Musics.values();
        for (Musics music : allMusics) {
            if (musics.get(music) != null) {
                musics.get(music).dispose();
            }
        }
        currentMusic = null;
    }

    public void putSound(Sounds soundType, Sound sound) {
        SoundContainer soundCont = sounds.get(soundType);
        //Array<Sound> soundArr = sounds.get(soundType);
        if (soundCont == null) {
            soundCont = new SoundContainer();
        }

        soundCont.addSound(sound);
        sounds.put(soundType, soundCont);
    }

    public long playSound(Sounds soundOption) {
        if (shutUpYourFace) return -1;

        SoundContainer soundCont = sounds.get(soundOption);
        Sound s = soundCont.getSound();
        return (s != null) ? s.play(SOUND_VOLUME) : 0;
    }

    public void playMusic(Musics musicOption) {
        // Stop currently running music
        if (currentMusic != null) currentMusic.stop();

        // Set specified music track as current and play it
        currentMusic = musics.get(musicOption);
        currentMusic.setLooping(true);
        currentMusic.play();
    }

    public void stopSound(Sounds soundOption) {
        SoundContainer soundCont = sounds.get(soundOption);
        if (soundCont != null) {
            soundCont.stopSound();
        }
    }

    public void stopAllSounds() {
        for (SoundContainer soundCont : sounds.values()) {
            if (soundCont != null) {
                soundCont.stopSound();
            }
        }
    }

    public void setMusicVolume(float level, float duration) {
//        LudumDare42.game.tween.killTarget(musicVolume);
        Tween.to(musicVolume, 1, duration)
                .target(level)
                .ease(Sine.IN)
                .start(LudumDare42.game.tween);
    }


    public Music.OnCompletionListener nextSong = new Music.OnCompletionListener() {
        @Override
        public void onCompletion(Music music) {
//            if (currentMusic == musics.get(Musics.music1)){
//                currentMusic = musics.get(Musics.music2);
//            } else {
//                currentMusic = musics.get(Musics.music1);
//            }
//            currentMusic.setVolume(musicVolume.floatValue());
//            currentMusic.play();
//            currentMusic.setOnCompletionListener(nextSong);
        }
    };
}

class SoundContainer {
    public Array<Sound> sounds;
    public Sound currentSound;

    public SoundContainer() {
        sounds = new Array<Sound>();
    }

    public void addSound(Sound s) {
        if (!sounds.contains(s, false)) {
            sounds.add(s);
        }
    }

    public Sound getSound() {
        if (sounds.size > 0) {
            int randIndex = MathUtils.random(0, sounds.size - 1);
            Sound s = sounds.get(randIndex);
            currentSound = s;
            return s;
        } else {
            System.out.println("No sounds found!");
            return null;
        }
    }

    public void stopSound() {
        if (currentSound != null) {
            currentSound.stop();
        }
    }

    public void dispose() {
        if (currentSound != null) {
            currentSound.dispose();
        }
    }
}
