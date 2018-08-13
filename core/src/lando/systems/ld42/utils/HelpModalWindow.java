package lando.systems.ld42.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld42.LudumDare42;

public class HelpModalWindow extends ModalWindow {
    private Rectangle helpRect;
    private String[] introTutorial = {
            "Welcome to Kingdoms Fall! Explosive Strategy Action Medieval Battle Simulator."
    };
    private String[] tutorial = {
            "There are two phases for you and your enemy.",
            " Recruitment Phase",
            " -  Recruit a unit by clicking on the tile next to your castle.",
            " -  Peasants are best for scouting",
            " -  Archers are best for defense",
            " -  Wizards are best for offence",
            " -  Soldiers are all-rounder unit",
            " Action Phase",
            " - Move a unit to an empty tile or move onto an enemy unit to attack",
            " - You can swap unit's tile by moving onto friendly unit",
            " Victory Condition",
            " - Capture enemy castle or hold all tiles except enemy castle"
    };

    public HelpModalWindow(OrthographicCamera camera) {
        super(camera);
        this.helpRect = new Rectangle();

    }

    @Override
    protected void renderWindowContents(SpriteBatch batch) {
        if (!showText) return;
        final float MARGIN_MULTIPLIER = 2.25f;
        batch.setColor(Color.WHITE);
        batch.setShader(LudumDare42.game.assets.fontShader);
        {
            final float title_text_scale = 0.6f;
            final float target_width = modalRect.width;
            LudumDare42.game.assets.font.getData().setScale(title_text_scale);
            LudumDare42.game.assets.fontShader.setUniformf("u_scale", title_text_scale);
            LudumDare42.game.assets.layout.setText(LudumDare42.game.assets.font, "Kingdoms Fall Tutorial",
                    Color.WHITE, target_width, Align.center, true);
            LudumDare42.game.assets.font.draw(batch, LudumDare42.game.assets.layout,
                    modalRect.x + margin_left,
                    modalRect.y + modalRect.height - margin_top);
            LudumDare42.game.assets.font.setColor(Color.WHITE);
            LudumDare42.game.assets.font.getData().setScale(1f);
            LudumDare42.game.assets.fontShader.setUniformf("u_scale", 1f);
        }

        for (int i=0;i<introTutorial.length;i++) {
            batch.setShader(LudumDare42.game.assets.fontShader);
            {
                final float title_text_scale = 0.3f;
                final float target_width = modalRect.width;
                LudumDare42.game.assets.font.getData().setScale(title_text_scale);
                LudumDare42.game.assets.fontShader.setUniformf("u_scale", title_text_scale);
                LudumDare42.game.assets.layout.setText(LudumDare42.game.assets.font, introTutorial[i],
                        Color.WHITE, target_width, Align.left, true);
                LudumDare42.game.assets.font.draw(batch, LudumDare42.game.assets.layout,
                        modalRect.x + margin_left,
                        modalRect.y + modalRect.height - margin_top * MARGIN_MULTIPLIER * (i + 2));
                LudumDare42.game.assets.font.setColor(Color.WHITE);
                LudumDare42.game.assets.font.getData().setScale(1f);
                LudumDare42.game.assets.fontShader.setUniformf("u_scale", 1f);
            }
        }

        for (int i=0;i<tutorial.length;i++) {
            batch.setShader(LudumDare42.game.assets.fontShader);
            {
                final float title_text_scale = 0.3f;
                final float target_width = modalRect.width;
                LudumDare42.game.assets.font.getData().setScale(title_text_scale);
                LudumDare42.game.assets.fontShader.setUniformf("u_scale", title_text_scale);
                LudumDare42.game.assets.layout.setText(LudumDare42.game.assets.font, tutorial[i],
                        Color.WHITE, target_width, Align.left, true);
                LudumDare42.game.assets.font.draw(batch, LudumDare42.game.assets.layout,
                        modalRect.x + margin_left,
                        modalRect.y + modalRect.height - margin_top * MARGIN_MULTIPLIER * (i+introTutorial.length+2));
                LudumDare42.game.assets.font.setColor(Color.WHITE);
                LudumDare42.game.assets.font.getData().setScale(1f);
                LudumDare42.game.assets.fontShader.setUniformf("u_scale", 1f);
            }
        }
        batch.setShader(null);

        batch.draw(LudumDare42.game.assets.unitAnimationPeasant.getKeyFrame(0),
                modalRect.x + margin_left * 1.5f,
                modalRect.y + modalRect.height - margin_top * 2f - margin_top * MARGIN_MULTIPLIER * (introTutorial.length+5),
                LudumDare42.game.assets.unitAnimationPeasant.getKeyFrame(0).getRegionWidth() * 1.5f,
                LudumDare42.game.assets.unitAnimationPeasant.getKeyFrame(0).getRegionHeight() * 1.5f);

        batch.draw(LudumDare42.game.assets.unitAnimationArcher.getKeyFrame(0),
                modalRect.x + margin_left * 1.5f,
                modalRect.y + modalRect.height - margin_top * 2f - margin_top * MARGIN_MULTIPLIER * (introTutorial.length+6),
                LudumDare42.game.assets.unitAnimationArcher.getKeyFrame(0).getRegionWidth() * 1.5f,
                LudumDare42.game.assets.unitAnimationArcher.getKeyFrame(0).getRegionHeight() * 1.5f);

        batch.draw(LudumDare42.game.assets.unitAnimationWizard.getKeyFrame(0),
                modalRect.x + margin_left * 1.5f,
                modalRect.y + modalRect.height - margin_top * 2f - margin_top * MARGIN_MULTIPLIER * (introTutorial.length+7),
                LudumDare42.game.assets.unitAnimationWizard.getKeyFrame(0).getRegionWidth() * 1.5f,
                LudumDare42.game.assets.unitAnimationWizard.getKeyFrame(0).getRegionHeight() * 1.5f);

        batch.draw(LudumDare42.game.assets.unitAnimationSoldier.getKeyFrame(0),
                modalRect.x + margin_left * 1.5f,
                modalRect.y + modalRect.height - margin_top * 2f - margin_top * MARGIN_MULTIPLIER * (introTutorial.length+8),
                LudumDare42.game.assets.unitAnimationSoldier.getKeyFrame(0).getRegionWidth() * 1.5f,
                LudumDare42.game.assets.unitAnimationSoldier.getKeyFrame(0).getRegionHeight() * 1.5f);
    }

}
