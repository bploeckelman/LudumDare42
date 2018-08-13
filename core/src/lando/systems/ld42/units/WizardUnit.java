package lando.systems.ld42.units;

import lando.systems.ld42.Assets;

public class WizardUnit extends Unit {
    public WizardUnit(Assets assets) {
        super(assets.unitAnimationWizard);
        actionPoint = 2;
        attackPower = 3;
        defensePower = 1;
    }
}
