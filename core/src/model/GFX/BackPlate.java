package model.GFX;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

import data.Statics;

/**
 * Created by Anders on 2016-04-07.
 */
public class BackPlate extends GFXObject{
    static Texture backPlateText;

    public BackPlate(){
        super();

    }

    public Model getModel(){
        if(model == null){
            model = createBackPlate();
            modelInstance = new ModelInstance(model);
            disposables.add(model);
        }
        return model;
    }

    private Model createBackPlate() {
        ModelBuilder mb = new ModelBuilder();
        Material m = new Material();
        //Creates diffuse texture
        if(backPlateText == null) {
            backPlateText = new Texture(Gdx.files.internal(Statics.backPlateTextPath) ,true);
            backPlateText.setFilter(Texture.TextureFilter.MipMap,Texture.TextureFilter.MipMap);
            // = loadTexture(backPlateText, );
            disposables.add(backPlateText);
        }
        //backPlateText.getTextureData();
        //backPlateText.setFilter(GL_LINEAR_MIPMAP_LINEAR,);



        m.set(TextureAttribute.createDiffuse(backPlateText));

        m.set(new BlendingAttribute(GL20.GL_ONE, GL20.GL_ONE)); //set so that its transparent
        //Create bump if mobile can handle it.
        float width = Statics.WEEK_BACKPLATE_WIDTH;
        float height = Statics.WEEK_BACKPLATE_HEIGHT;
        float depth = Statics.WEEK_BACKPLATE_DEPTH;
        return mb.createBox(width, height, depth, m, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);

    }

    /**
     * Adjusts the x position depending on what week it is
     * @param x
     */
    public void fixPosition(float x) {
        position.x += x;
        this.setPosition(position);
    }
}
