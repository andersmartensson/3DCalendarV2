package model.GFX;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

import model.NineMenMorrisRules;

/**
 * Created by anders on 2015-11-26.
 */
public class Ball extends GFXObject{

    public int boardPositionNumber;

    public Ball(Color color) {
        this.scale = 0.5f;
        this.color = color;
    }

    public Model getModel(){
        if(model == null){
            model = createBallModel();
        }
        return model;
    }

    public int getMorrisColor(){
        return (color == Color.RED) ? NineMenMorrisRules.RED_MARKER : NineMenMorrisRules.BLUE_MARKER;
    }

    public static Color setMorrisColor(int c){
        return (c == 5) ? Color.RED : Color.BLUE;
    }

    private Model createBallModel() {
        ModelBuilder mb = new ModelBuilder();
        Material m = new Material();
        //Creates diffuse texture
        m.set(ColorAttribute.createDiffuse(color));
        //Create Specular texture
        m.set(ColorAttribute.createSpecular(Color.WHITE));
        //Create bump if mobile can handle it.
        return mb.createSphere(scale,scale,scale,30,30,m, Usage.Position | Usage.Normal | Usage.TextureCoordinates);
    }


}
