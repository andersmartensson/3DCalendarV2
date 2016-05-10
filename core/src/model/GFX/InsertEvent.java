package model.GFX;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.UBJsonReader;

import model.Date3d;

/**
 * Created by Anders on 2016-05-10.
 */
public class InsertEvent extends GFXObject{

    public Date3d d3d;
    public static Model model;
    public DatePillar datePillar;

    public InsertEvent(){
        super();
        color = Color.BLACK;
        //d3d = d;
    }


    public Model getModel(){
        if(model == null){
            model = createInsertEvent();
            disposables.add(model);
        }
        return model;
    }

    public boolean checkHit(int x, int y, Camera cam) {
        this.calculateBoundingBox();

        float distance = -1;

        position = new Vector3();
        Ray ray = cam.getPickRay(x, y);
        modelInstance.transform.getTranslation(position);
        position.add(center);
        float dist2 = ray.origin.dst2(position);
        //if (distance >= 0f && dist2 > distance) continue;
        if (Intersector.intersectRayBoundsFast(ray, position, dimensions)){
            return true;
        }
        else {
            return false;
        }

//        for (int i = 0; i < datePillars.size; ++i) {
//            DatePillar d = datePillars.get(i);
//            d.getModelInstance().transform.getTranslation(position);
//            position.add(d.center);
//            float dist2 = ray.origin.dst2(position);
//            if (distance >= 0f && dist2 > distance) continue;
//            if (Intersector.intersectRayBoundsFast(ray, position, d.dimensions)){
//                result = i;
//                distance = dist2;
//            }
//        }
//        return result;
    }

    private Model createInsertEvent() {

        ModelLoader modelLoader = new G3dModelLoader(new UBJsonReader());

        return  modelLoader.loadModel(Gdx.files.internal("models/insertEvent.g3db"));
    }

    public String toString(){
        return " D: " + d3d.day + " M: " + d3d.month + " Y: " + d3d.year;
    }


}
