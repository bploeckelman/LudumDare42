package lando.systems.ld42.ui;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Quint;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import lando.systems.ld42.Assets;
import lando.systems.ld42.LudumDare42;
import lando.systems.ld42.accessors.Scene2dWindowAccessor;
import lando.systems.ld42.teams.Team;
import lando.systems.ld42.turns.TurnAction;
import lando.systems.ld42.units.ArcherUnit;
import lando.systems.ld42.units.PeasantUnit;
import lando.systems.ld42.units.SoldierUnit;
import lando.systems.ld42.units.WizardUnit;
import lando.systems.ld42.world.Tile;
import lando.systems.ld42.world.World;

public class RecruitmentUI extends UserInterface {

    public static final float width = 150f;
    public static final float height = 80f;
    public static final float margin = 10f;


    public Team team;
    public Camera camera;

    public Window window;
    private TextureRegion peasantHead;
    private TextureRegion soldierHead;
    private TextureRegion archerHead;
    private TextureRegion wizardHead;

    TextButton recruitPeasantButton;
    TextButton recruitSoldierButton;
    TextButton recruitArcherButton;
    TextButton recruitWizardButton;

    public RecruitmentUI(Assets assets) {
        this.skin = new Skin(Gdx.files.internal("skins/cloud-form-ui.json"));
        this.window = new Window("Recruit", skin);
        this.window.setVisible(false);
        this.root = window;
        this.peasantHead = assets.unitHeadPeasant;
        this.soldierHead = assets.unitHeadSoldier;
        this.archerHead = assets.unitHeadArcher;
        this.wizardHead = assets.unitHeadWizard;
    }

    @Override
    public void update(float dt) {
        if (recruitPeasantButton != null ) {
            recruitPeasantButton.setText("Peasant " + team.getUnitCountPeasant() + "/" + team.getMaxPeasant());
            disableButton(recruitPeasantButton, team.canBuildPeasant());
        }
        if (recruitSoldierButton != null ) {
            recruitSoldierButton.setText("Soldier " + team.getUnitCountSoldier() + "/" + team.getTileTypeCount(Tile.Type.mountain));
            disableButton(recruitSoldierButton, team.canBuildSoldier());
        }
        if (recruitArcherButton != null ) {
            recruitArcherButton.setText("Archer " + team.getUnitCountArcher() + "/" + team.getTileTypeCount(Tile.Type.forest));
            disableButton(recruitArcherButton, team.canBuildArcher());
        }
        if (recruitWizardButton != null ) {
            recruitWizardButton.setText("Wizard " + team.getUnitCountWizard() + "/" + team.getTileTypeCount(Tile.Type.crystal));
            disableButton(recruitWizardButton, team.canBuildWizard());
        }
        // ...
    }

    private void disableButton(TextButton button, boolean enabled){
        if (button != null ) {
            if (enabled) {
                button.setTouchable(Touchable.enabled);
                button.setDisabled(false);
            } else {
                button.setTouchable(Touchable.disabled);
                button.setDisabled(true);
            }
        }
    }

    public void rebuild(final Team team, final Tile tile, Camera camera) {
        this.team = team;
        this.camera = camera;

        window.clear();
        window.setSize(width, height);
        window.setVisible(true);

        Image peasantIcon = new Image(peasantHead);
        Image soldierIcon = new Image(soldierHead);
        Image archerIcon = new Image(archerHead);
        Image wizardIcon = new Image(wizardHead);

        recruitPeasantButton = new TextButton("Peasant", skin);
        recruitSoldierButton = new TextButton("Soldier", skin);
        recruitArcherButton  = new TextButton("Archer",  skin);
        recruitWizardButton  = new TextButton("Wizard",  skin);

        // TODO: add disabled style, enable or disable as appropriate, set text according to controlled territory
        // TODO: only move to 'next turn' when done recruiting

        recruitPeasantButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                team.addUnit(new PeasantUnit(LudumDare42.game.assets), tile);
                RecruitmentUI.this.hide();
            }
        });
        recruitSoldierButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                team.addUnit(new SoldierUnit(LudumDare42.game.assets), tile);
                RecruitmentUI.this.hide();
            }
        });
        recruitArcherButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                team.addUnit(new ArcherUnit(LudumDare42.game.assets), tile);
                RecruitmentUI.this.hide();
            }
        });
        recruitWizardButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                team.addUnit(new WizardUnit(LudumDare42.game.assets), tile);
                RecruitmentUI.this.hide();
            }
        });

        Table buttonGroup = new Table(skin);
        buttonGroup.setSize(width, height);
        buttonGroup.row();
        buttonGroup.add(peasantIcon).size((1 / 4f) * width, (1 / 4f) * height).left();
        buttonGroup.add(recruitPeasantButton).size((3 / 4f) * width, (1 / 4f) * height).left();
        buttonGroup.row();
        buttonGroup.add(soldierIcon).size((1 / 4f) * width, (1 / 4f) * height).left();
        buttonGroup.add(recruitSoldierButton).size((3 / 4f) * width, (1 / 4f) * height).left();
        buttonGroup.row();
        buttonGroup.add(archerIcon).size((1 / 4f) * width, (1 / 4f) * height).left();
        buttonGroup.add(recruitArcherButton).size((3 / 4f) * width, (1 / 4f) * height).left();
        buttonGroup.row();
        buttonGroup.add(wizardIcon).size((1 / 4f) * width, (1 / 4f) * height).left();
        buttonGroup.add(recruitWizardButton).size((3 / 4f) * width, (1 / 4f) * height).left();

        window.top();
        window.add(buttonGroup).fill().expand();
        window.pack();
        window.setPosition(camera.viewportWidth, margin);
        window.setKeepWithinStage(false);

        root = window;
    }

    @Override
    public void show() {
        window.setVisible(true);
        window.setPosition(camera.viewportWidth, margin);
        Tween.to(window, Scene2dWindowAccessor.POS_X, 1f)
             .target(camera.viewportWidth - width - 2f * margin).ease(Bounce.OUT)
             .start(LudumDare42.game.tween);
    }

    @Override
    public void hide() {
        World.THE_WORLD.screen.endRecruitment(false);

        window.setPosition(camera.viewportWidth - width - 2f * margin, margin);
        Tween.to(window, Scene2dWindowAccessor.POS_X, 1f)
             .target(camera.viewportWidth).ease(Quint.OUT)
             .setCallback(new TweenCallback() {
                 @Override
                 public void onEvent(int i, BaseTween<?> baseTween) {
                     window.setVisible(false);
                 }
             })
             .start(LudumDare42.game.tween);
    }

}
