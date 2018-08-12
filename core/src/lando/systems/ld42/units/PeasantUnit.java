package lando.systems.ld42.units;

import lando.systems.ld42.Assets;

public class PeasantUnit extends Unit {
    public PeasantUnit(Assets assets) {
        super(assets.unitAnimationPeasant);
        actionPoint = 1;
        attackPower = 1;
        defensePower = 1;
    }
}
