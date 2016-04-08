package model.GFX;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import data.Statics;

/**
 * Created by Anders on 2016-04-08.
 */
public class TextTexture implements Disposable {

    String text;
    public Texture texture;


    SpriteBatch spriteBatch;
    Texture img;
    Skin skin;
    Pixmap bg;
    Texture texture2;

    public TextTexture(){
        //spriteBatch = new SpriteBatch();
        img = new Texture("badlogic.jpg");
        bg = new Pixmap(1820, 980, Pixmap.Format.RGBA8888);
        bg.setColor(Color.BLUE);
        bg.fill();
        texture2 = new Texture(bg);
    }

    /**
     * Creates a texture with the supplied text
     * @param sb - to save memory, pass a single spriteBatch to multiple objects
     * @param c - Color of the background
     * @param s - The text.
     * @param h -
     * @param w -
     * @return
     */
    public Texture createTextTexture(SpriteBatch sb, Skin skin, Color c, String s, float h, float w){

        Array<Disposable> trash = new Array<Disposable>(2);
//
        int width = (int) (Statics.ACTIVITY_TEXTURE_HEIGHT_MODIFIER * w);
        int height = (int) (Statics.ACTIVITY_TEXTURE_WIDTH_MODIFIER * h);
//        //Create label and stage
        Stage stage = new Stage();
        trash.add(stage);
        Label label = new Label(s,skin);
        Table table = new Table();
        stage.addActor(table);
        table.add(label);
        table.setFillParent(true);
        table.align(Align.topLeft);
//        //create colored texture
        Pixmap bg = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        bg.setColor(c);
        bg.fill();
        Texture bgText = new Texture( bg);
        trash.add(bgText);
////        TextureRegion fboRegion = new TextureRegion(bgText);
////        fboRegion.flip(false, true);
//        //Create framebuffer
//        //System.out.println("H: " + height + " W:" + width);
        FrameBuffer fb = new FrameBuffer(Pixmap.Format.RGBA8888
                ,width ,height , false);
        trash.add(fb);

        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT) ;
        //Capture to framebuffer
        fb.begin();
        //Render
        sb.begin();
        //draw color background
        sb.draw(texture2, 300, 300);
        sb.draw(bgText, 300, 300);

        //sb.draw(fboRegion, 0, 0);
        sb.draw(img, 0, 0);
        //draw text.
        stage.draw();
        sb.end();
        //save framebuffer and return
        fb.end();

        texture = fb.getColorBufferTexture();
        //Dispose of trash
        for(Disposable d: trash){
            d.dispose();
        }
        sb.begin();
        sb.draw(texture,0,0);
        sb.end();
        return texture;

        //        //Create label and stage
        //Stage stage = new Stage();

//        //create colored texture
//        Pixmap bg = new Pixmap(100, 100, Pixmap.Format.RGB565);
//        bg.setColor(Color.BROWN);
//        bg.fill();
//        Texture bgText = new Texture(bg);
//
//        FrameBuffer fb = new FrameBuffer(Pixmap.Format.RGBA8888
//                ,1000 ,1000 , false);
//        Gdx.gl.glClearColor(1, 0, 0, 1);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//
//        sb.begin();
//        sb.draw(img, 0, 0);
//        sb.draw(texture2, 100, 100);
//        sb.draw(bgText,200,200);
//        sb.end();
//
//        return img;
    }
    @Override
    public void dispose() {
        texture.dispose();
    }
}
