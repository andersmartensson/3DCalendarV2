package model.GFX;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.utils.Array;

/**
 * Created by anders on 2015-11-26.
 */
public class Ground extends GFXObject {
    Array<Texture> diffTextures;
    Array<Texture> specTextures;
    Texture destertTexture;
    TextureRegion desertRegion;
    TextureRegion waterRegion;
    int diffFrame;
    int specFrame;
    public Ground() {
        this.scale = 1000;
        this.color = Color.BLUE;
    }

    public Model getModel(){
        if(model == null){
            model = createGroundModel();
        }
        return model;
    }

    private Model createGroundModel() {
        ModelBuilder mb = new ModelBuilder();
        Material m = new Material();
        //Creates diffuse texture
        //Load array with diffuse Textures
        //diffTextures = loadTextures(diffTextures, 1, "textures/waterDiffSeq/diffusion.");
        diff = loadTexture(diff, "textures/waterTexture.jpg");
        diff.setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
        waterRegion = new TextureRegion(diff);
        waterRegion.setRegion(0, 0, diff.getWidth() * 8, diff.getHeight() * 8);
        destertTexture = loadTexture(destertTexture, "textures/desertTexture.jpg");
        //destertTexture.setFilter(Texture.TextureFilter.MipMapLinearNearest,Texture.TextureFilter.MipMapLinearNearest );
        destertTexture.setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
        desertRegion = new TextureRegion(destertTexture);
        desertRegion.setRegion(0, 0, destertTexture.getWidth() * 8, destertTexture.getHeight() * 8);

        diffFrame = 0;
        m.set(TextureAttribute.createDiffuse(diff));
        //Create Specular texture
        specFrame = 0;
        //specTextures = loadTextures(specTextures,1,"textures/waterSpecSeq/specular.");
        //m.set(TextureAttribute.createSpecular(specTextures.get(specFrame)));

        m.set(ColorAttribute.createSpecular(Color.WHITE));
        return mb.createBox(scale,scale/1000.0f,scale,m, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
    }

    public void setGroundTexture(int theme){
        if(theme == 0){
            Material m = getModelInstance().materials.get(0);
            m.set(TextureAttribute.createDiffuse(waterRegion));
            getModelInstance().materials.set(0, m);
        }else{
            Material m = getModelInstance().materials.get(0);
            m.set(TextureAttribute.createDiffuse(desertRegion));
            getModelInstance().materials.set(0, m);
        }
    }

    private Array<Texture> loadTextures(Array<Texture> array, int totFrames, String s) {
        array = new Array<Texture>();
        Texture text = null;
        String num;
        for(int i=1;i<totFrames+1;i++){
            //num = "0" + 1;
            num = (i < 10) ? "0" + i: "" + i;
            //text = new Texture();
            array.add(loadTexture(text,s + num + ".jpg"));
        }
        return array;
    }

    public void updateWaterTexture() {
        //update diffuse texture first
        //if(diffFrame <= specFrame){
        if(true){
            //diffFrame = (diffFrame > 19) ? 0 : diffFrame +1;
            diffFrame += 1 %19;
            System.out.println(diffFrame);
            Material m = getModelInstance().materials.get(0);
            m.set(TextureAttribute.createDiffuse(diffTextures.get(diffFrame)));
            getModelInstance().materials.set(0, m);
        }

    }


    public void dispose(){
        diff.dispose();
        destertTexture.dispose();
        model.dispose();
        //modelInstance.model.dispose();
    }
}
