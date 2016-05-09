package controller;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by Anders on 2016-05-09.
 */
public class CalendarCameraInputController extends CameraInputController implements GestureDetector.GestureListener{
    protected CalendarCameraInputController(CameraGestureListener gestureListener, Camera camera) {
        super(gestureListener, camera);
    }

    public CalendarCameraInputController(Camera camera) {
        super(camera);
    }




    @Override
    public boolean tap(float x, float y, int count, int button) {
        System.out.println("HELLO!!!!");
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX,
                         float velocityY,
                         int button){
        System.out.println("Fling!!!!!");

        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

}
