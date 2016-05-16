package controller;

import com.badlogic.gdx.input.GestureDetector;

import data.Statics;

public class TouchController extends GestureDetector {
    public interface DirectionListener {
        void flingLeft();

        void flingRight();

        void  flingUp();

        void  flingDown();

        void dragLeft();

        void dragRight();
    }

    public TouchController(DirectionListener directionListener) {
        super(new DirectionGestureListener(directionListener));
    }

    private static class DirectionGestureListener extends GestureAdapter{
        DirectionListener directionListener;

        public DirectionGestureListener(DirectionListener directionListener){
            this.directionListener = directionListener;
        }

        @Override
        public boolean fling(float velocityX, float velocityY, int button) {

            if(Math.abs(velocityX)>Math.abs(velocityY)){
                //If at dragging speed
                if(Math.abs(velocityX) < Statics.TOUCH_DRAG_SPEED){
                    if(velocityX>0){
                        directionListener.dragLeft();
                    }else{
                        directionListener.dragRight();
                    }
                }
                else {
                    if(velocityX>0){
                        directionListener.flingLeft();
                    }else{
                        directionListener.flingRight();
                    }
                }

            }
            else{
                if(velocityY>0){
                    directionListener.flingDown();
                }else{
                    directionListener.flingUp();
                }
            }
            //return super.fling(velocityX, velocityY, button);
            return false;
        }


        @Override
        public boolean pan (float x, float y, float deltaX, float deltaY) {
            // Gdx.app.log("GestureDetectorTest", "pan at " + x + ", " + y);
//            if( Gdx.input.isTouched(1)){
//                System.out.println("TWo fingers touched");
//            }
//            else if (Gdx.input.isTouched(0)){
//                System.out.println("One finger touched");
//            }
//            else if (Gdx.input.isTouched(2)){
//                System.out.println("Three finger touched");
//            }
            return false;
        }

    }


}
