package model.GFX;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

import data.Statics;
import model.Date3d;

/**
 * Created by Anders on 2016-04-07.
 */
public class DatePillar extends GFXObject{
    public Date3d d3d;

    public DatePillar(Date3d d){
        super();
        color = Color.BLACK;
        d3d = d;
    }

    public DatePillar(long date) {
        super();
        color = Color.BLACK;
        d3d = new Date3d(date);
    }

    public Model getModel(){
        if(model == null){
            model = createDatePillar();
            disposables.add(model);
        }
        return model;
    }

    private Model createDatePillar() {
        ModelBuilder mb = new ModelBuilder();
        Material m = new Material();
        //Creates diffuse texture
        m.set(ColorAttribute.createDiffuse(color));
        Model mod = mb.createBox(Statics.DATEPILLAR_WIDTH,
                Statics.DATEPILLAR_HEIGHT,
                Statics.DATEPILLAR_DEPTH,
                m,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
        return mod;
    }

    public String toString(){
        return " D: " + d3d.day + " M: " + d3d.month + " Y: " + d3d.year;
    }
}
