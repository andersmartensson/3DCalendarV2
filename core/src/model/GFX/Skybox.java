package model.GFX;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;

import data.Statics;

/**
 * Created by anders on 2015-11-26.
 */
public class Skybox extends GFXObject {

    Texture desertBG;
    public Skybox(float size){
        super();
        scale = size*4 -80;
        //scale = 590;
        color = Color.BLUE;
    }

    public Model getModel(){
        if(model == null){
            model = createSkyBoxModel();
            modelInstance = new ModelInstance(model);
            disposables.add(model);
        }
        return model;
    }

    public Model createSkyBoxModel() {
        ModelBuilder mb = new ModelBuilder();
        Material m = new Material();
        //Creates diffuse texture
        diff = loadTexture(diff, Statics.skyboxDiffPath);
        disposables.add(diff);
        desertBG = loadTexture(desertBG, "textures/desertSkybox.jpg");
        disposables.add(desertBG);
        m.set(TextureAttribute.createDiffuse(diff));
        //Create bump if mobile can handle it.
        return mb.createSphere(-scale,-scale,-scale,80,80,m, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
    }

    public void setSkyBoxTexture(int theme){
        if(theme == 0){
            Material m = getModelInstance().materials.get(0);
            m.set(TextureAttribute.createDiffuse(diff));
            getModelInstance().materials.set(0, m);
        }else{
            ModelInstance mi = getModelInstance();
            Array<Material> mats = mi.materials;
            Material m = mats.get(0);
            m.set(TextureAttribute.createDiffuse(desertBG));
            getModelInstance().materials.set(0, m);
        }
    }

}
