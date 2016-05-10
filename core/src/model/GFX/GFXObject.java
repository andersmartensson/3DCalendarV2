package model.GFX;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import data.Statics;

/**
 * Created by anders on 2015-11-26.
 */
public abstract class GFXObject implements Disposable{
    public float scale;
    public Color color;
    public Model model;
    public Vector3 center;
    public Vector3 dimensions;
    public float radius;
    public ModelInstance modelInstance;
    public Vector3 position;
    public Texture diff; //Diffusion texture
    public Texture spec; //Specular texture
    public Texture bump;
    private int time;
    public Array<Disposable> disposables;

    public GFXObject(){
        disposables = new Array<Disposable>();
    }

    public void initGFXOBject(){
        calculateBoundingBox();
    }

    public void calculateBoundingBox(){
        center = new Vector3();
        dimensions = new Vector3();
        BoundingBox bounds = new BoundingBox();
        modelInstance.calculateBoundingBox(bounds);
        bounds.getCenter(center);
        bounds.getDimensions(dimensions);
        radius = dimensions.len() / 2f;
    }

    public ModelInstance getModelInstance() {
        return modelInstance;
    }

    public void setModelInstance(ModelInstance modelInstance) {
        this.modelInstance = modelInstance;
        //disposables = new Array<Disposable>();
        //calculateBoundingBox();
        //initGFXOBject();
    }

    /**
     * Should generate a cloud texture from a shader
     * @return  For now return white texture
     */
    protected Texture createCloudTexture() {
        return loadTexture(spec,"textures/spec_texture.jpg");
    }

    /**
     * Loads the texture into memory
     */
    protected Texture loadTexture(Texture text, String textPath) {
        text = new Texture(Gdx.files.internal(textPath));
        disposables.add(text);
        return text;
    }

    public void dispose(){
        //System.out.println("Disposing!!!");
        for(Disposable d: disposables){
            d.dispose();
        }
        disposables.clear();
        disposables = null;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Vector3 getPosition() {
        return (position == null) ? new Vector3(0,0,0) : position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
        if(modelInstance != null){
            modelInstance.transform.setTranslation(position);
        }
    }

    public void updatePosition(){
            //time += 1;
            float step = 0.1f;
            float error = 0.001f;

            Vector3 v = new Vector3();
            modelInstance.transform.getTranslation(v);
            if(isBetween(v.x, position.x, error)){
                v.x += (v.x < position.x) ? step : -step;
            }
            if(isBetween(v.y, position.y, error)){
                v.y += (v.y < position.y) ? step : -step;
            }
            if(isBetween(v.z, position.z, error)){
                v.z += (v.z < position.z) ? step : -step;
            }
            modelInstance.transform.setTranslation(v);
    }

    private boolean isBetween(float current, float destination, float error) {
        if(current > destination + error){
            return true;
        }
        else if(current < destination - error){
            return true;
        }
        return false;
    }

    private void fixFloat(Vector3 position) {
        position.x = ((int) position.x * 10) / 10f;
        position.y = ((int) position.y * 10) / 10f;
        position.z = ((int) position.z * 10) / 10f;
    }

    public static Material translateColor(String colorId) {

        //int s = Integer.getInteger(colorId);
        int s = Integer.decode(colorId);
        switch (s){
            case 2:
                //Light green
                if(Statics.MatLightGreen == null){
                    Statics.MatLightGreen = new Material();
                    Statics.MatLightGreen.set(ColorAttribute.createDiffuse(new Color(0.3f,1f,0.3f,1f)));
                    Statics.MatLightGreen.set(ColorAttribute.createSpecular(Color.WHITE));
                }
                return Statics.MatLightGreen;
            case 3:
                if(Statics.MatPurple == null){
                    Statics.MatPurple = new Material();
                    Statics.MatPurple.set(ColorAttribute.createDiffuse(Color.GREEN));
                    Statics.MatPurple.set(ColorAttribute.createSpecular(Color.WHITE));
                }
                return Statics.MatPurple;
            case 4:
                //Light Red
                if(Statics.MatLightRed == null){
                    Statics.MatLightRed = new Material();
                    Statics.MatLightRed.set(ColorAttribute.createDiffuse(new Color(1f,0.5f,0.5f,1f)));
                    Statics.MatLightRed.set(ColorAttribute.createSpecular(Color.WHITE));
                }
                return Statics.MatLightRed;
            case 5:
                //Yellow
                if(Statics.MatYellow == null){
                    Statics.MatYellow = new Material();
                    Statics.MatYellow.set(ColorAttribute.createDiffuse(Color.YELLOW));
                    Statics.MatYellow.set(ColorAttribute.createSpecular(Color.WHITE));
                }
                return Statics.MatYellow;
            case 6:
                //Orange
                if(Statics.MatOrange == null){
                    Statics.MatOrange = new Material();
                    Statics.MatOrange.set(ColorAttribute.createDiffuse(Color.ORANGE));
                    Statics.MatOrange.set(ColorAttribute.createSpecular(Color.WHITE));
                }
                return Statics.MatOrange;
            case 7:
                //Turquoise
                if(Statics.MatTurquoise == null){
                    Statics.MatTurquoise = new Material();
                    Statics.MatTurquoise.set(ColorAttribute.createDiffuse(new Color(1f,0,0.8f,1f)));
                    Statics.MatTurquoise.set(ColorAttribute.createSpecular(Color.WHITE));
                }
                return Statics.MatTurquoise;
            case 8:
                //Gray
                if(Statics.MatGray == null){
                    Statics.MatGray = new Material();
                    Statics.MatGray.set(ColorAttribute.createDiffuse(Color.GRAY));
                    Statics.MatGray.set(ColorAttribute.createSpecular(Color.WHITE));
                }
                return Statics.MatGray;
            case 9:
                //Blue
                if(Statics.MatBlue == null){
                    Statics.MatBlue = new Material();
                    Statics.MatBlue.set(ColorAttribute.createDiffuse(Color.BLUE));
                    Statics.MatBlue.set(ColorAttribute.createSpecular(Color.WHITE));
                }
                return Statics.MatBlue;
            case 10:
                if(Statics.MatGreen == null){
                    Statics.MatGreen = new Material();
                    Statics.MatGreen.set(ColorAttribute.createDiffuse(Color.GREEN));
                    Statics.MatGreen.set(ColorAttribute.createSpecular(Color.WHITE));
                }
                return Statics.MatGreen;
            case 11:
                //Red
                if(Statics.MatRed == null){
                    Statics.MatRed = new Material();
                    Statics.MatRed.set(ColorAttribute.createDiffuse(Color.RED));
                    Statics.MatRed.set(ColorAttribute.createSpecular(Color.WHITE));
                }
                return Statics.MatRed;
            default:
                //Blue
                if(Statics.MatBlue == null){
                    Statics.MatBlue = new Material();
                    Statics.MatBlue.set(ColorAttribute.createDiffuse(Color.BLUE));
                    Statics.MatBlue.set(ColorAttribute.createSpecular(Color.WHITE));
                }
                return Statics.MatBlue;

        }
    }


}
