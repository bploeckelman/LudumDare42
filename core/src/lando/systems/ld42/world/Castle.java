package lando.systems.ld42.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld42.Assets;
import lando.systems.ld42.Config;
import lando.systems.ld42.teams.Team;

public class Castle {

    public Tile tile;
    public Team.Type type;
    public TextureRegion castleOverlay;
    public TextureRegion hexBase;
    public TextureRegion hexOverlay;

    public Castle(Assets assets, Team.Type teamType, Tile baseTile) {
        this.type = teamType;
        this.castleOverlay = (type == Team.Type.player) ? assets.castleYellow : assets.castlePurple;
        this.hexBase = assets.whiteHex;
        this.hexOverlay = assets.emptyHex;
        this.tile = baseTile;
    }

    public void update(float dt) {
        // ...
    }

    public void render(SpriteBatch batch) {
        batch.setColor((type == Team.Type.player) ? Config.player_color : Config.enemy_color);
        batch.draw(hexBase, tile.position.x, tile.position.y, Tile.tileWidth, Tile.tileHeight);

        batch.setColor(1f, 1f, 1f, 1f);
        batch.draw(hexOverlay, tile.position.x, tile.position.y, Tile.tileWidth, Tile.tileHeight);

        batch.draw(castleOverlay, tile.position.x, tile.position.y, Tile.tileWidth, Tile.tileHeight);
    }

}
