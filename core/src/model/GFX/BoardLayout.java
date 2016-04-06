package model.GFX;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * Sets positions of balls on board
 */
public class BoardLayout {

    /**
     * Board is composed of a 7 x 7 grid
     *
     * First is Upper left corner is 1 x 1
     * Second is in the middle top of the board
     * and bottom right is the last at 7 x7
     *
     * Board dimensions is 0x0 to 8x8
     */
    public static Array<Vector3> getBoardPositions() {

        Array<Vector3> boardPositions = new Array<Vector3>();
        float height = 1.2f;
        //Creates the vectors for all the positions into an array
        Vector3 v;
        //0
        v = new Vector3(100f,200f,100f);
        boardPositions.add(v);
        // 1st
        v = new Vector3(3.0f,height, 3.0f);
        boardPositions.add(v);
        // 2nd
        v = new Vector3(2.0f,height, 2.0f);
        boardPositions.add(v);
        //3rd
        v = new Vector3(1.0f,height, 1.0f);
        boardPositions.add(v);
        //4th
        v = new Vector3(4.0f,height, 3.0f);
        boardPositions.add(v);
        //5th
        v = new Vector3(4.0f,height, 2.0f);
        boardPositions.add(v);
        //6th
        v = new Vector3(4.0f,height, 1.0f);
        boardPositions.add(v);
        //7th
        v = new Vector3(5.0f,height, 3.0f);
        boardPositions.add(v);
        //8th
        v = new Vector3(6.0f,height, 2.0f);
        boardPositions.add(v);
        //9th
        v = new Vector3(7.0f,height, 1.0f);
        boardPositions.add(v);
        //10th
        v = new Vector3(5.0f,height, 4.0f);
        boardPositions.add(v);
        //11th
        v = new Vector3(6.0f,height, 4.0f);
        boardPositions.add(v);
        //12th
        v = new Vector3(7.0f,height, 4.0f);
        boardPositions.add(v);
        //13th
        v = new Vector3(5.0f,height, 5.0f);
        boardPositions.add(v);
        //14th
        v = new Vector3(6.0f,height, 6.0f);
        boardPositions.add(v);
        //15th
        v = new Vector3(7.0f,height, 7.0f);
        boardPositions.add(v);
        //16th
        v = new Vector3(4.0f,height, 5.0f);
        boardPositions.add(v);
        //17th
        v = new Vector3(4.0f,height, 6.0f);
        boardPositions.add(v);
        //18th
        v = new Vector3(4.0f,height, 7.0f);
        boardPositions.add(v);
        //19th
        v = new Vector3(3.0f,height, 5.0f);
        boardPositions.add(v);
        //20th
        v = new Vector3(2.0f,height, 6.0f);
        boardPositions.add(v);
        //21th
        v = new Vector3(1.0f,height, 7.0f);
        boardPositions.add(v);
        // 22th
        v = new Vector3(3.0f,height, 4.0f);
        boardPositions.add(v);
        // 23th
        v = new Vector3(2.0f,height, 4.0f);
        boardPositions.add(v);
        //24st
        v = new Vector3(1.0f,height, 4.0f);
        boardPositions.add(v);
        System.out.println("Board size: " + boardPositions.size);
        float boardWidth = 8.0f;
        //Correct positions so that they all center to 0
        for(Vector3 tv : boardPositions){
            tv.x -= boardWidth/2;
            tv.z -= boardWidth/2;
        }

        return boardPositions;
    }

}
