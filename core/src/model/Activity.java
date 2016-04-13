package model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.google.api.services.calendar.model.Event;

import java.util.Date;

import data.Statics;
import model.GFX.Alphabet;
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

    public Array<ModelInstance> generateSummaryText(Alphabet a){
        Vector3 v = new Vector3(position.x - Statics.ACTIVITY_WIDTH /2f
                ,position.y + height / 2f
                ,position.z + Statics.ACTIVITY_DEPTH);
        String s = event.getSummary();
        System.out.println("TEXT TO BE GENERATED:\n->" + event.getSummary() + "<-");
        s = autoNewLine(s, Statics.ACTIVITY_TEXT_SCALE);
        return a.load3DText(s, v, Statics.ACTIVITY_TEXT_SCALE);
    }
    /*
    Checks where it needs to create a new line
     */
    private String autoNewLine(String s, float scale) {
        //See if current text exceeds current activity width
        return checkLine(s, scale);

    }

    private String checkLine(String s, float scale) {
//        float l = s.length() * 3f * scale;
//        if(l > Statics.ACTIVITY_WIDTH ){
//            //Split last and check again
//            String ns = null;
//            String[] ts = s.split(" ");
//            for(int i=0;i<ts.length -1;i++){
//                ns += ts[1];
//            }
//            //Add last and new line
//            ns += "\n" + ts[ts.length-1];
//        }
//        return ns;
        String ns = "";
        String[] ts = s.split(" ");
        for(int i=0;i<ts.length;i++){
            System.out.println("line: " + i + " ->" + ts[i] + "<-");
            ns += ts[i] + "\n";
        }
        System.out.println("text ->" + ns + "<-");
        return ns;
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
