package lando.systems.ld42.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public abstract class UserInterface {

    public Table root;
    protected Skin skin;

    public abstract void update(float dt);

    public void show() {
        root.setVisible(true);
    }

    public void hide() {
        root.setVisible(false);
    }

}
