package lando.systems.ld42.accessors;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

public class Scene2dWindowAccessor implements TweenAccessor<Window> {

    public static final int POS_X = 1;
    public static final int POS_Y = 2;
    public static final int POS_XY = 3;
    public static final int SIZE_W = 4;
    public static final int SIZE_H = 5;
    public static final int SIZE_WH = 6;

    public Scene2dWindowAccessor() {}

    @Override
    public int getValues(Window target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case 1:
                returnValues[0] = target.getX();
                return 1;
            case 2:
                returnValues[0] = target.getY();
                return 1;
            case 3:
                returnValues[0] = target.getX();
                returnValues[1] = target.getY();
                return 2;
            case 4:
                returnValues[0] = target.getWidth();
                return 1;
            case 5:
                returnValues[0] = target.getHeight();
                return 1;
            case 6:
                returnValues[0] = target.getWidth();
                returnValues[1] = target.getHeight();
                return 2;
            default:
                assert false;
                return -1;

        }
    }

    @Override
    public void setValues(Window target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case 1:
                target.setX(newValues[0]);
                break;
            case 2:
                target.setY(newValues[0]);
                break;
            case 3:
                target.setX(newValues[0]);
                target.setY(newValues[1]);
                break;
            case 4:
                target.setWidth(newValues[0]);
                break;
            case 5:
                target.setHeight(newValues[0]);
                break;
            case 6:
                target.setWidth(newValues[0]);
                target.setHeight(newValues[1]);
                break;
            default:
                assert false;
        }
    }

}
