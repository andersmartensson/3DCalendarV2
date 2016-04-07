package model.GFX;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

/**
 * Created by anders on 2015-11-26.
 */
public class Sun extends GFXObject {
    public Sun() {
        super();
        scale = 5.0f;
        color = Color.WHITE;
    }



    public Model getModel(){
        if(model == null){
            model = createSunModel();
            //disposables = new Array<Disposable>();
            disposables.add(model);
        }

        return model;
    }

    private Model createSunModel() {

        ModelBuilder mb = new ModelBuilder();
        Material m = new Material();
        //Creates diffuse texture
        m.set(ColorAttribute.createDiffuse(color));

        return mb.createSphere(scale,scale,scale,10,10,m, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
    }
}
