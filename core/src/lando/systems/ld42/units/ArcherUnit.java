package lando.systems.ld42.units;

import lando.systems.ld42.Assets;

public class ArcherUnit extends Unit {
    public ArcherUnit(Assets assets) {
        super(assets.unitAnimationArcher);
        actionPoint = 1;
        attackPower = 2;
        defensePower = 2;
    }
}
