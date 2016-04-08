package model.GFX;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

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
        disposables = new Array<Disposable>();
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
        initGFXOBject();
    }

    /**
     * Should genererate a cloud texture from a shader
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
        for(Disposable d: disposables){
            d.dispose();
        }
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

    public static Color translateColor(String colorId) {

        //int s = Integer.getInteger(colorId);
        int s = Integer.decode(colorId);
        switch (s){
            case 2:
                //Light green
                return new Color(0.5f,1f,0.5f,1f);
            case 3:
                return Color.PURPLE;
            case 4:
                //Light Red
                return new Color(1f,0.5f,0.5f,1f);
            case 5:
                return Color.YELLOW;
            case 6:
                return Color.ORANGE;
            case 7:
                //Turquoise
                return Color.TEAL;
            case 8:
                return Color.GRAY;
            case 9:
                return Color.BLUE;
            case 10:
                return Color.GREEN;
            case 11:
                return Color.RED;



        }

        return Color.BLUE;
    }
}
