package lando.systems.ld42;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.BitmapFontLoader;
import com.badlogic.gdx.assets.loaders.ShaderProgramLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class Assets implements Disposable {

    // Initialize descriptors for all assets
    private final AssetDescriptor<TextureAtlas> atlasAsset = new AssetDescriptor<TextureAtlas>("images/sprites.atlas", TextureAtlas.class);
    private final AssetDescriptor<Texture> titleTextureAsset = new AssetDescriptor<Texture>("images/title.png", Texture.class);
    private final AssetDescriptor<Texture> statsTextureAsset = new AssetDescriptor<Texture>("images/stats.png", Texture.class);

    private final AssetDescriptor<BitmapFont> distanceFieldFontAsset = new AssetDescriptor<BitmapFont>("fonts/ubuntu.fnt", BitmapFont.class,
            new BitmapFontLoader.BitmapFontParameter() {{
                genMipMaps = true;
                minFilter = Texture.TextureFilter.MipMapLinearLinear;
                magFilter = Texture.TextureFilter.Linear;
            }}
    );

    private final ShaderProgramLoader.ShaderProgramParameter defaultVertParam = new ShaderProgramLoader.ShaderProgramParameter() {{ vertexFile = "shaders/default.vert"; }};
    private final AssetDescriptor<ShaderProgram> distanceFieldShaderAsset = new AssetDescriptor<ShaderProgram>("shaders/dist.frag",       ShaderProgram.class);
    private final AssetDescriptor<ShaderProgram> shaderBlindsAsset        = new AssetDescriptor<ShaderProgram>("shaders/blinds.frag",     ShaderProgram.class, defaultVertParam);
    private final AssetDescriptor<ShaderProgram> shaderFadeAsset          = new AssetDescriptor<ShaderProgram>("shaders/dissolve.frag",   ShaderProgram.class, defaultVertParam);
    private final AssetDescriptor<ShaderProgram> shaderRadialAsset        = new AssetDescriptor<ShaderProgram>("shaders/radial.frag",     ShaderProgram.class, defaultVertParam);
    private final AssetDescriptor<ShaderProgram> shaderDoomAsset          = new AssetDescriptor<ShaderProgram>("shaders/doomdrip.frag",   ShaderProgram.class, defaultVertParam);
    private final AssetDescriptor<ShaderProgram> shaderPixelizeAsset      = new AssetDescriptor<ShaderProgram>("shaders/pixelize.frag",   ShaderProgram.class, defaultVertParam);
    private final AssetDescriptor<ShaderProgram> shaderDoorwayAsset       = new AssetDescriptor<ShaderProgram>("shaders/doorway.frag",    ShaderProgram.class, defaultVertParam);
    private final AssetDescriptor<ShaderProgram> shaderCrosshatchAsset    = new AssetDescriptor<ShaderProgram>("shaders/crosshatch.frag", ShaderProgram.class, defaultVertParam);
    private final AssetDescriptor<ShaderProgram> shaderRippleAsset        = new AssetDescriptor<ShaderProgram>("shaders/ripple.frag",     ShaderProgram.class, defaultVertParam);
    private final AssetDescriptor<ShaderProgram> shaderHeartAsset         = new AssetDescriptor<ShaderProgram>("shaders/heart.frag",      ShaderProgram.class, defaultVertParam);
    private final AssetDescriptor<ShaderProgram> shaderCircleCropAsset    = new AssetDescriptor<ShaderProgram>("shaders/circlecrop.frag", ShaderProgram.class, defaultVertParam);
    private final AssetDescriptor<ShaderProgram> shaderWaterAsset         = new AssetDescriptor<ShaderProgram>("shaders/water.frag",      ShaderProgram.class, defaultVertParam);
    private final AssetDescriptor<ShaderProgram> shaderCloudAsset         = new AssetDescriptor<ShaderProgram>("shaders/cloud.frag",      ShaderProgram.class, defaultVertParam);


    public enum Loading { SYNC, ASYNC }

    public SpriteBatch batch;
    public ShapeRenderer shapes;
    public GlyphLayout layout;

    public AssetManager mgr;

    public TextureAtlas atlas;
    public TextureRegion testTexture;
    public Texture titleTexture;
    public Texture statsTexture;

    public TextureRegion whitePixel;
    public TextureRegion whiteCircle;
    public TextureRegion defaultHex;
    public TextureRegion whiteHex;
    public TextureRegion highlightHex;
    public TextureRegion emptyHex;
    public TextureRegion blankTile;
    public TextureRegion blankTile1;
    public TextureRegion blankTile2;
    public TextureRegion blankTile3;
    public TextureRegion blankTile4;
    public TextureRegion blankTile5;
    public TextureRegion sparkle;
    public TextureRegion sparklePlayer;
    public TextureRegion sparkleEnemy;
    public TextureRegion sword;
    public TextureRegion shield;
    public TextureRegion pointer;

    public TextureRegion tree;
    public TextureRegion mountain;
    public TextureRegion gem;

    public TextureRegion unitHeadPeasant;
    public TextureRegion unitHeadSoldier;
    public TextureRegion unitHeadArcher;
    public TextureRegion unitHeadWizard;

    public TextureRegion boot;
    public TextureRegion smokeTexture;

    public Animation<TextureRegion> unitAnimationPeasant;
    public Animation<TextureRegion> unitAnimationSoldier;
    public Animation<TextureRegion> unitAnimationArcher;
    public Animation<TextureRegion> unitAnimationWizard;
    public Animation<TextureRegion> castleRaiseAnimationPlayer;
    public Animation<TextureRegion> castleRaiseAnimationEnemy;
    public Animation<TextureRegion> castleWaveAnimationPlayer;
    public Animation<TextureRegion> castleWaveAnimationEnemy;
    public Animation<TextureRegion> castleLowerAnimationPlayer;
    public Animation<TextureRegion> castleLowerAnimationEnemy;
    public Animation<TextureRegion> tileHighlightAnimation;

    public static BitmapFont font;
    public ShaderProgram fontShader;
    public BitmapFont titleFont;

    public Array<ShaderProgram> randomTransitions;
    public ShaderProgram blindsShader;
    public ShaderProgram fadeShader;
    public ShaderProgram radialShader;
    public ShaderProgram doomShader;
    public ShaderProgram crosshatchShader;
    public ShaderProgram pizelizeShader;
    public ShaderProgram doorwayShader;
    public ShaderProgram rippleShader;
    public ShaderProgram heartShader;
    public ShaderProgram circleCropShader;
    public ShaderProgram waterShader;

    public ShaderProgram cloudShader;
    public NinePatch ninePatchTooltip;
    public NinePatch ninePatchScrews;
    public NinePatch ninePatchStats;

    public boolean initialized;

    public Assets() {
        this(Loading.SYNC);
    }

    public Assets(Loading loading) {
        // Let us write shitty shader programs
        ShaderProgram.pedantic = false;

        batch = new SpriteBatch();
        shapes = new ShapeRenderer();
        layout = new GlyphLayout();

        initialized = false;

        mgr = new AssetManager();
        mgr.load(atlasAsset);
        mgr.load(titleTextureAsset);
        mgr.load(statsTextureAsset);
        mgr.load(distanceFieldFontAsset);
        mgr.load(distanceFieldShaderAsset);
        mgr.load(shaderBlindsAsset);
        mgr.load(shaderFadeAsset);
        mgr.load(shaderRadialAsset);
        mgr.load(shaderDoomAsset);
        mgr.load(shaderPixelizeAsset);
        mgr.load(shaderDoorwayAsset);
        mgr.load(shaderCrosshatchAsset);
        mgr.load(shaderRippleAsset);
        mgr.load(shaderHeartAsset);
        mgr.load(shaderCircleCropAsset);
        mgr.load(shaderWaterAsset);
        mgr.load(shaderCloudAsset);

        if (loading == Loading.SYNC) {
            mgr.finishLoading();
            updateLoading();
        }
    }

    public float updateLoading() {
        if (!mgr.update()) return mgr.getProgress();
        if (initialized) return 1f;
        initialized = true;

        // Cache TextureRegions from TextureAtlas in fields for quicker access
        atlas = mgr.get(atlasAsset);
        testTexture = atlas.findRegion("badlogic");
        whitePixel = atlas.findRegion("white-pixel");
        whiteCircle = atlas.findRegion("white-circle");
        defaultHex = atlas.findRegion("default-hex");
        whiteHex = atlas.findRegion("white-hex");
        //highlightHex = atlas.findRegion("tile-outline");
        emptyHex = atlas.findRegion("tile-empty");
        blankTile = atlas.findRegion("tile-blank");
        blankTile1 = atlas.findRegion("tile-blank1");
        blankTile2 = atlas.findRegion("tile-blank2");
        blankTile3 = atlas.findRegion("tile-blank3");
        blankTile4 = atlas.findRegion("tile-blank4");
        blankTile5 = atlas.findRegion("tile-blank5");
        sparkle = atlas.findRegion("sparkle");
        sparklePlayer = atlas.findRegion("sparkle-player");
        sparkleEnemy = atlas.findRegion("sparkle-enemy");
        boot = atlas.findRegion("icon-boot");
        sword = atlas.findRegion("icon-sword");
        shield = atlas.findRegion("icon-shield");
        pointer = atlas.findRegion("pointer");

        tree = atlas.findRegion("tree");
        mountain = atlas.findRegion("mountain");
        gem = atlas.findRegion("gem");

        ninePatchTooltip = new NinePatch(atlas.findRegion("ninepatch-tooltip"), 10, 10, 10, 10);
        ninePatchScrews = new NinePatch(atlas.findRegion("ninepatch-screws"), 6, 6, 6, 6);
        ninePatchStats = new NinePatch(atlas.findRegion("ninepatch-stats"), 10, 10, 10, 10);

        unitHeadPeasant = atlas.findRegion("unit-peasant-head");
        unitHeadSoldier = atlas.findRegion("unit-soldier-head");
        unitHeadArcher = atlas.findRegion("unit-archer-head");
        unitHeadWizard = atlas.findRegion("unit-wizard-head");
        smokeTexture = atlas.findRegion("smoke");

        Array<TextureAtlas.AtlasRegion> peasant = atlas.findRegions("unit-peasant");
        Array<TextureAtlas.AtlasRegion> soldier = atlas.findRegions("unit-soldier");
        Array<TextureAtlas.AtlasRegion> archer = atlas.findRegions("unit-archer");
        Array<TextureAtlas.AtlasRegion> wizard = atlas.findRegions("unit-wizard");
        unitAnimationPeasant = new Animation<TextureRegion>(0.33f, peasant, Animation.PlayMode.LOOP);
        unitAnimationSoldier = new Animation<TextureRegion>(0.33f, soldier, Animation.PlayMode.LOOP);
        unitAnimationArcher = new Animation<TextureRegion>(0.33f, archer, Animation.PlayMode.LOOP);
        unitAnimationWizard = new Animation<TextureRegion>(0.33f, wizard, Animation.PlayMode.LOOP);

        Array<TextureAtlas.AtlasRegion> castleRaisePlayer = atlas.findRegions("castle-blue-raise");
        Array<TextureAtlas.AtlasRegion> castleRaiseEnemy = atlas.findRegions("castle-red-raise");
        Array<TextureAtlas.AtlasRegion> castleWavePlayer = atlas.findRegions("castle-blue-wave");
        Array<TextureAtlas.AtlasRegion> castleWaveEnemy = atlas.findRegions("castle-red-wave");

        castleRaiseAnimationPlayer = new Animation<TextureRegion>(0.1f, castleRaisePlayer, Animation.PlayMode.NORMAL);
        castleRaiseAnimationEnemy = new Animation<TextureRegion>(0.1f, castleRaiseEnemy, Animation.PlayMode.NORMAL);
        castleWaveAnimationPlayer = new Animation<TextureRegion>(0.16f, castleWavePlayer, Animation.PlayMode.LOOP);
        castleWaveAnimationEnemy = new Animation<TextureRegion>(0.16f, castleWaveEnemy, Animation.PlayMode.LOOP);

        titleTexture = mgr.get(titleTextureAsset);
        statsTexture = mgr.get(statsTextureAsset);
        Array<TextureAtlas.AtlasRegion> textureHighlight = atlas.findRegions("tile-outline");
        tileHighlightAnimation = new Animation<TextureRegion>(0.1f, textureHighlight, Animation.PlayMode.LOOP_PINGPONG);


        // Initialize distance field font
        font = mgr.get(distanceFieldFontAsset);
        font.getData().setScale(.3f);
        font.setUseIntegerPositions(false);
        fontShader = mgr.get(distanceFieldShaderAsset);

        titleFont = mgr.get(distanceFieldFontAsset);
        titleFont.getData().setScale(1f);
        titleFont.setUseIntegerPositions(false);

        blindsShader     = mgr.get(shaderBlindsAsset);
        fadeShader       = mgr.get(shaderFadeAsset);
        radialShader     = mgr.get(shaderRadialAsset);
        doomShader       = mgr.get(shaderDoomAsset);
        pizelizeShader   = mgr.get(shaderPixelizeAsset);
        doorwayShader    = mgr.get(shaderDoorwayAsset);
        crosshatchShader = mgr.get(shaderCrosshatchAsset);
        rippleShader     = mgr.get(shaderRippleAsset);
        heartShader      = mgr.get(shaderHeartAsset);
        circleCropShader = mgr.get(shaderCircleCropAsset);
        waterShader      = mgr.get(shaderWaterAsset);
        cloudShader      = mgr.get(shaderCloudAsset);

        randomTransitions = new Array<ShaderProgram>();
        randomTransitions.addAll(
//                blindsShader,
//                fadeShader,
                radialShader,
//                doomShader,
                pizelizeShader,
//                doorwayShader,
//                crosshatchShader,
//                rippleShader,
//                heartShader,
                circleCropShader
        );

        return 1f;
    }

    @Override
    public void dispose() {
        mgr.clear();
        font.dispose();
        shapes.dispose();
        batch.dispose();
    }

    // ------------------------------------------------------------------------
    // Static helpers methods
    // ------------------------------------------------------------------------

    public static void drawString(SpriteBatch batch, String text,
                                  float x, float y, Color c, float scale, BitmapFont font) {
        batch.setShader(LudumDare42.game.assets.fontShader);
        LudumDare42.game.assets.fontShader.setUniformf("u_scale", scale);
        font.getData().setScale(scale);
        font.setColor(c);
        font.draw(batch, text, x, y);
        font.getData().setScale(1f);
        LudumDare42.game.assets.fontShader.setUniformf("u_scale", 1f);
        font.getData().setScale(scale);
        batch.setShader(null);
    }

    public static void drawString(SpriteBatch batch, String text,
                                  float x, float y, Color c, float scale,
                                  BitmapFont font, float targetWidth, int halign) {
        batch.setShader(LudumDare42.game.assets.fontShader);
        LudumDare42.game.assets.fontShader.setUniformf("u_scale", scale);
        font.getData().setScale(scale);
        font.setColor(c);
        font.draw(batch, text, x, y, targetWidth, halign, true);
        font.getData().setScale(1f);
        LudumDare42.game.assets.fontShader.setUniformf("u_scale", 1f);
        font.getData().setScale(scale);
        batch.setShader(null);
    }
}
