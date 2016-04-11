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
import model.GFX.TextTexture;

/**
 * Created by Anders on 2016-04-06.
 */
public class Activity extends GFXObject{
    Date date;
    long from;
    long to;
    private float yOrigin;
    public float height;
    public Event event;
    public Date3d d3d;

    public Activity(Color c, Date3d d, Event e) {
        event = e;
        this.color = c;
        scale = 10f;
        d3d = d;
    }

    public Model getModel(){
        if(model == null){
            model = createActivityModel();
            disposables.add(model);
        }
        return model;
    }

    private Model createActivityModel() {
        ModelBuilder mb = new ModelBuilder();

        yOrigin = d3d.startHour;
        yOrigin += d3d.startMin / 60f;
        height = d3d.stopHour + (d3d.stopMin / 60f);
        height -= yOrigin;
        yOrigin += height/2f;
        Material m = new Material();
        //Creates diffuse texture

        TextTexture tt = new TextTexture();
        disposables.add(tt);
        m.set(ColorAttribute.createDiffuse(color));
        //Create Specular texture
        m.set(ColorAttribute.createSpecular(Color.WHITE));
        //yOrigin -= height /2;
        Model mod = mb.createBox(Statics.ACTIVITY_WIDTH, height,Statics.ACTIVITY_DEPTH, m
                , VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);

        return mod;

    }

    public String getDetails(){
        StringBuilder sb = new StringBuilder("Details:\n");
        sb.append(event.getSummary() + "\n");
        sb.append("Start: " + event.getStart());
        sb.append("\n");
        sb.append("Stop: " + event.getEnd());

        return sb.toString();
    }

    public float getYOrigin() {
        //Half the height
        return yOrigin;
    }

    public String toString(){
        return "Event: " + event.getSummary() + " x: " + position.x + " y: " + position.y + " h: " + height + " " +  d3d.toString();
    }

}
