package model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.google.api.services.calendar.model.Event;

import java.util.Date;

import data.Statics;
import model.GFX.Alphabet;
import model.GFX.GFXObject;

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
    Material material;

    public Activity(Color c, Date3d d, Event e, Material m) {
        super();
        material = m;
        event = e;
        this.color = c;
        scale = 10f;
        d3d = d;
    }

    public Model getModel(){
        if(model == null){
            model = createActivityModel(material);
            disposables.add(model);
        }
        return model;
    }

    public Array<ModelInstance> generateSummaryText(Alphabet a){
        Vector3 v = new Vector3(position.x - Statics.ACTIVITY_WIDTH /2f
                ,(position.y + height / 2f) -(3f*Statics.ACTIVITY_TEXT_SCALE)
                ,position.z + Statics.ACTIVITY_TEXT_MODIFY_Z);
        String s = event.getSummary();
        //System.out.println("TEXT TO BE GENERATED:\n->" + event.getSummary() + "<-");
        s = autoNewLine(s, Statics.ACTIVITY_TEXT_SCALE);
        return a.load3DText(s, v, Statics.ACTIVITY_TEXT_SCALE,false);
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
        StringBuilder ns = new StringBuilder("");
        String[] ts = s.split(" ");
        for(int i=0;i<ts.length;i++){
            ns.append(ts[i] + "\n");
        }
        //System.out.println("text ->" + ns + "<-");
        return ns.toString();
    }

    private Model createActivityModel(Material m) {
        ModelBuilder mb = new ModelBuilder();

        yOrigin = d3d.startHour;
        yOrigin += d3d.startMin / 60f;
        yOrigin -= 1f;
        height = d3d.stopHour + (d3d.stopMin / 60f);
        height -= yOrigin +1f;
        yOrigin += height/2f;
        Model mod = mb.createBox(Statics.ACTIVITY_WIDTH, height,Statics.ACTIVITY_DEPTH, m
                , VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);

        return mod;

    }

    public String getDetails(){
        StringBuilder sb = new StringBuilder("Details:\n");
        sb.append(event.getSummary() + "\n");

        sb.append("\nStatus: " + event.getStatus() );
        if(event.getDescription() != null){
            sb.append("\nDescription: " + event.getDescription());
        }
        else {
            sb.append("\nDesciption: - ");
        }

        sb.append("\nStart: " + event.getStart());
        sb.append("\nStop: " + event.getEnd());

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
