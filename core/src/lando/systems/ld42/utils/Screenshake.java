package lando.systems.ld42.utils;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by dsgraham on 4/27/17.
 */
public class Screenshake {

    private static final int default_frequency = 35;
    private static final float default_amplitude = .5f;

    float[] samples;
    float  internalTimer = 0;
    public float  shakeDuration = 0;

    int     duration  = 5; // In seconds, make longer if you want more variation
    int     frequency = default_frequency; // hertz

    public float   amplitude = default_amplitude; // how much you want to shake
    public boolean falloff   = true; // if the shake should decay as it expires

    int sampleCount;

    public Screenshake() {
        this(default_frequency, default_amplitude);
    }

    public Screenshake(int frequency, float amplitude) {
        this.frequency = frequency;
        this.amplitude = amplitude;

        sampleCount = duration * frequency;
        samples = new float[sampleCount];
        for (int i = 0; i < sampleCount; i++) {
            samples[i] = MathUtils.random(-1f, 1f);
        }
    }

    /**
     * Called every frame will shake the camera if it has a shake duration
     *
     * @param dt     Gdx.graphics.getDeltaTime() or your dt in seconds
     * @param camera your camera
     * @param center Where the camera should stay centered on
     */
    public void update(float dt, Camera camera, Vector2 center) {
        update(dt, camera, center.x, center.y);
    }

    public void update(float dt, Camera camera, float centerx, float centery) {
        internalTimer += dt;
        if (internalTimer > duration) internalTimer -= duration;
        if (shakeDuration > 0) {
            shakeDuration -= dt;
            float shakeTime = (internalTimer * frequency);
            int first = (int) shakeTime;
            int second = (first + 1) % sampleCount;
            int third = (first + 2) % sampleCount;
            float deltaT = shakeTime - (int) shakeTime;
            float deltaX = samples[first] * deltaT + samples[second] * (1f - deltaT);
            float deltaY = samples[second] * deltaT + samples[third] * (1f - deltaT);

            camera.position.x = centerx + deltaX * amplitude * (falloff ? Math.min(shakeDuration, 1f) : 1f);
            camera.position.y = centery + deltaY * amplitude * (falloff ? Math.min(shakeDuration, 1f) : 1f);
            camera.update();
        }
    }

    /**
     * Will make the camera shake for the duration passed in in seconds
     *
     * @param d duration of the shake in seconds
     */
    public void shake(float d) {
        shakeDuration = d;
    }
}
