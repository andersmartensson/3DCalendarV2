package model.GFX;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.google.api.client.util.DateTime;

import data.Statics;

/**
 * Created by Anders on 2016-04-07.
 */
public class DatePillar extends GFXObject{
    DateTime dateTime;
    public DatePillar(DateTime dt){
        super();
        color = Color.BLACK;
        dateTime = dt;
    }

    public DatePillar(){
        super();
        color = Color.BLACK;
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
}
