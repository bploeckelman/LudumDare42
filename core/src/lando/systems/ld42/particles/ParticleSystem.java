package lando.systems.ld42.particles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import lando.systems.ld42.Assets;
import lando.systems.ld42.LudumDare42;
import lando.systems.ld42.world.Tile;

public class ParticleSystem {

    private final Array<Particle> activeParticles = new Array<Particle>(false, 1028);
    private final Pool<Particle> particlePool = Pools.get(Particle.class, 2000);

    private static float nearRadius = 100;

    public ParticleSystem() {
        for (int i =0; i < 800; i++){
            particlePool.free(new Particle());
        }
    }


    public void addDamageParticles(float x, float y, Vector2 direction, Color color){
        int particles = 20;
        for (int i = 0; i < particles; i++){
            Particle part = particlePool.obtain();

            float speed = MathUtils.random(400f);
            float dir = direction.angle() + MathUtils.random(-10f, 10f);
            float px = x + MathUtils.random(-3f, 3f);
            float py = y + MathUtils.random(-3f, 3f);
            float vx = MathUtils.cosDeg(dir) * speed;
            float vy = MathUtils.sinDeg(dir) * speed;
            float scale = MathUtils.random(1, 2f);
            float ttl = MathUtils.random(.05f, .1f);
            part.init(
                    px, py,
                    vx, vy,
                    -vx, -vy, .5f,
                    color.r, color.g, color.b, .5f,
                    1f, 1f, 1f, .3f,
                    scale, ttl,
                    LudumDare42.game.assets.whitePixel);

            activeParticles.add(part);
        }
    }

    public void addBattleCloud(Tile t1, Tile t2){
        int smokeParticles = 400;
        float vel = 40;
        float minScale = 10;
        float maxScale = 20;
        float minTTL = 1;
        float maxTTL = 2;
        float gray = .5f;
        for (int i = 0; i < smokeParticles; i++){
            Particle particle = particlePool.obtain();
            float scale = MathUtils.random(minScale, maxScale);
            float dir = MathUtils.random(360);
            float dist = MathUtils.random(Tile.tileHeight/2f);
            float posX = t1.position.x + Tile.tileWidth /2f - scale/2f + MathUtils.cosDeg(dir) * dist;
            float posY = t1.position.y + Tile.tileHeight /2f - scale/2f + MathUtils.sinDeg(dir) * dist;

            float velX = MathUtils.random(-vel, vel);
            float velY =  MathUtils.random(-vel, vel);
            float ttl = MathUtils.random(minTTL, maxTTL);
            float grayValue = MathUtils.random(gray) + (1f - gray);

            particle.init(posX, posY, velX, velY, -velX, -velY,
                    0.5f, grayValue, grayValue, grayValue, 1f,
                    grayValue, grayValue, grayValue, 0f, scale, ttl, LudumDare42.game.assets.smokeTexture);
            activeParticles.add(particle);



            particle = particlePool.obtain();
            scale = MathUtils.random(minScale, maxScale);
            dir = MathUtils.random(360);
            dist = MathUtils.random(Tile.tileHeight/2f);
            posX = t2.position.x + Tile.tileWidth /2f - scale/2f +MathUtils.cosDeg(dir) * dist;
            posY = t2.position.y + Tile.tileHeight /2f - scale/2f + MathUtils.sinDeg(dir) * dist;

            velX = MathUtils.random(-vel, vel);
            velY =  MathUtils.random(-vel, vel);
            ttl = MathUtils.random(minTTL, maxTTL);
            grayValue = MathUtils.random(gray) + (1f - gray);

            particle.init(posX, posY, velX, velY, -velX, -velY,
                    0.5f, grayValue, grayValue, grayValue, 1f,
                    grayValue, grayValue, grayValue, 0f, scale, ttl, LudumDare42.game.assets.smokeTexture);
            activeParticles.add(particle);
        }
    }

    public void addTileDestroyParticles(Tile t){
        int tiles = 10;
        float width = Tile.tileWidth/tiles;
        float height = Tile.tileHeight/tiles;

        TextureRegion blank = LudumDare42.game.assets.blankTile;
        int texW = blank.getRegionWidth() / tiles;
        int texH = blank.getRegionHeight() / tiles;

        for (int x = 0; x < tiles; x++){
            for (int y = 0; y < tiles; y++) {
                Particle part = particlePool.obtain();

                float speed = MathUtils.random(80, 120f);
                float dir = MathUtils.random(70f, 110f);
                float px = t.position.x + x * width;
                float py = t.position.y + y * height;
                float vx = MathUtils.cosDeg(dir) * speed;
                float vy = MathUtils.sinDeg(dir) * speed;
                float ttl = MathUtils.random(.5f, 1.5f);
                TextureRegion reg = new TextureRegion(blank, x * texW, y*texH, texW, texH);
                part.init(
                        px, py,
                        vx, vy,
                        0, -200, 1f,
                        1, 1, 1, 1f,
                        .5f, .5f, .5f, 0f,
                        width, -height, ttl,
                        reg);

                activeParticles.add(part);
            }
        }
    }


    public void update(float dt){
        int len = activeParticles.size;
//        Gdx.app.log("Particles", "Active Particles Size = " + len);
//        Gdx.app.log("Particles", "Pool Free Size = " + particlePool.getFree());
//        Gdx.app.log("Particles", "Pool Peak Size = " + particlePool.peak);

        for (int i = len -1; i >= 0; i--){
            Particle part = activeParticles.get(i);
            part.update(dt);
            if (part.timeToLive <= 0){
                activeParticles.removeIndex(i);
                particlePool.free(part);
            }
        }
    }

    public void render(SpriteBatch batch){
        for (Particle part : activeParticles){
            part.render(batch);
        }
    }


    public void clearParticles(){
        particlePool.freeAll(activeParticles);
        activeParticles.clear();
    }
}

