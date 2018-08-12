package lando.systems.ld42.units;

import lando.systems.ld42.Assets;

public class WizardUnit extends Unit {
    public WizardUnit(Assets assets) {
        super(assets.unitAnimationWizard);
        actionPoint = 1;
        attackPower = 3;
        defensePower = 1;
    }
}
