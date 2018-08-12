package lando.systems.ld42.units;

import lando.systems.ld42.Assets;

public class SoldierUnit extends Unit {
    public SoldierUnit(Assets assets) {
        super(assets.unitAnimationSoldier);
        actionPoint = 1;
        attackPower = 3;
        defensePower = 1;
    }
}
