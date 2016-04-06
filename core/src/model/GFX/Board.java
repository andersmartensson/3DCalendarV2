package model.GFX;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by anders on 2015-11-26.
 */
public class Board extends GFXObject{

    public Board(Color color) {
        this.scale = 8.0f;
        this.color = color;
        this.position = new Vector3(0+scale/2,0.95f,0+scale/2);
        position.x -= scale/2.0f;
        position.z -= scale/2.0f;
    }


    public Model getModel(){
        if(model == null){
            model = createBoardModel();
        }

        return model;
    }

    private Model createBoardModel() {
        ModelBuilder mb = new ModelBuilder();
        Material m = new Material();
        //Creates diffuse texture
        m.set(TextureAttribute.createDiffuse(loadTexture(diff, "textures/nineMenBoardTexture2.png")));
        //m.set(ColorAttribute.createDiffuse(color));
        //Create Specular texture
        m.set(ColorAttribute.createSpecular(Color.WHITE));
        //Create bump if mobile can handle it.
        //
        return mb.createBox(scale,scale/20.0f,scale,m, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
    }
}

