package model.GFX;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

/**
 * Created by Datacom on 2015-12-01.
 */
public class MarkerBall extends GFXObject{

    public MarkerBall() {
        this.scale = 0.5f;
        this.color = Color.BLACK;

    }


    public Model getModel(){
        if(model == null){
            model = createBallModel();
        }

        return model;
    }

    private Model createBallModel() {

        ModelBuilder mb = new ModelBuilder();
        Material m = new Material();
        //Creates diffuse texture
        m.set(ColorAttribute.createDiffuse(color));
        //Create Specular texture
        m.set(ColorAttribute.createSpecular(Color.WHITE));
        //Create bump if mobile can handle it.

        return mb.createSphere(scale,scale/10f,scale,30,30,m, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
    }
}
