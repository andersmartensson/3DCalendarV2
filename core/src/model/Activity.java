package model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.google.api.services.calendar.model.Event;

import java.util.Date;

import data.Statics;
import model.GFX.GFXObject;

/**
 * Created by Anders on 2016-04-06.
 */
public class Activity extends GFXObject{
    Date date;
    long from;
    long to;
    private long yOrigin;
    public long height;
    public Event event;

    public Activity(Color c, long from, long to) {
        this.color = c;
        //this.date = d;
        this.from = from;
        this.to = to;
        scale = 10f;
    }

//    public void createActivity(Color c, Date d, long from, long to){
//        this.color = c;
//        this.date = d;
//        this.from = from;
//        this.to = to;
//    }

    public Model getModel(){
        if(model == null){
            model = createActivityModel();
        }
        return model;
    }

    private Model createActivityModel() {
        ModelBuilder mb = new ModelBuilder();
        Material m = new Material();
        //Creates diffuse texture
        m.set(ColorAttribute.createDiffuse(color));
        //Create Specular texture
        m.set(ColorAttribute.createSpecular(Color.WHITE));
        //Create bump if mobile can handle it.
        height = to - from;
        height = height / Statics.HEIGHT_DIVIDER;
        //System.out.println("Height: " + height );
        Model mod = mb.createBox(Statics.ACTIVITY_WIDTH,(float) height,Statics.ACTIVITY_DEPTH, m
                , VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
        //return mb.createSphere(scale,scale,scale,30,30,m, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
        yOrigin = height / 2;
        return mod;

    }

    public float getYOrigin() {
        //Half the height
        return yOrigin;
    }


}