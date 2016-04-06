package operations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Created by anders on 2015-11-26.
 */
public class BlurBitMap {



    //blur shader
    final static String blurVERT =
            "attribute vec4 "+ ShaderProgram.POSITION_ATTRIBUTE+";\n" +
                    "attribute vec4 "+ ShaderProgram.COLOR_ATTRIBUTE+";\n" +
                    "attribute vec2 "+ ShaderProgram.TEXCOORD_ATTRIBUTE+"0;\n" +

                    "uniform mat4 u_projTrans;\n" +
                    " \n" +
                    "varying vec4 vColor;\n" +
                    "varying vec2 vTexCoord;\n" +

                    "void main() {\n" +
                    "	vColor = "+ ShaderProgram.COLOR_ATTRIBUTE+";\n" +
                    "	vTexCoord = "+ ShaderProgram.TEXCOORD_ATTRIBUTE+"0;\n" +
                    "	gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
                    "}";

    final static String blurFRAG =
            "#ifdef GL_ES\n" +
                    "#define LOWP lowp\n" +
                    "precision mediump float;\n" +
                    "#else\n" +
                    "#define LOWP \n" +
                    "#endif\n" +
                    "varying LOWP vec4 vColor;\n" +
                    "varying vec2 vTexCoord;\n" +
                    "\n" +
                    "uniform sampler2D u_texture;\n" +
                    "uniform float resolution;\n" +
                    "uniform float radius;\n" +
                    "uniform vec2 dir;\n" +
                    "\n" +
                    "void main() {\n" +
                    "	vec4 sum = vec4(0.0);\n" +
                    "	vec2 tc = vTexCoord;\n" +
                    "	float blur = radius/resolution; \n" +
                    "    \n" +
                    "    float hstep = dir.x;\n" +
                    "    float vstep = dir.y;\n" +
                    "    \n" +
                    "	sum += texture2D(u_texture, vec2(tc.x - 4.0*blur*hstep, tc.y - 4.0*blur*vstep)) * 0.05;\n" +
                    "	sum += texture2D(u_texture, vec2(tc.x - 3.0*blur*hstep, tc.y - 3.0*blur*vstep)) * 0.09;\n" +
                    "	sum += texture2D(u_texture, vec2(tc.x - 2.0*blur*hstep, tc.y - 2.0*blur*vstep)) * 0.12;\n" +
                    "	sum += texture2D(u_texture, vec2(tc.x - 1.0*blur*hstep, tc.y - 1.0*blur*vstep)) * 0.15;\n" +
                    "	\n" +
                    "	sum += texture2D(u_texture, vec2(tc.x, tc.y)) * 0.16;\n" +
                    "	\n" +
                    "	sum += texture2D(u_texture, vec2(tc.x + 1.0*blur*hstep, tc.y + 1.0*blur*vstep)) * 0.15;\n" +
                    "	sum += texture2D(u_texture, vec2(tc.x + 2.0*blur*hstep, tc.y + 2.0*blur*vstep)) * 0.12;\n" +
                    "	sum += texture2D(u_texture, vec2(tc.x + 3.0*blur*hstep, tc.y + 3.0*blur*vstep)) * 0.09;\n" +
                    "	sum += texture2D(u_texture, vec2(tc.x + 4.0*blur*hstep, tc.y + 4.0*blur*vstep)) * 0.05;\n" +
                    "\n" +
                    "	gl_FragColor = vColor * vec4(sum.rgb, 1.0);\n" +
                    "}";

    private Texture blurPixmap(Pixmap map, float blurA) {
        //blur it
        Texture text = new Texture(map);
        return blurTexture(text, blurA);

    }

    int w;
    int h;
    ShaderProgram blurShader;
    SpriteBatch batch;
    FrameBuffer blurTargetA;
    FrameBuffer blurTargetB;

    public BlurBitMap(){
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();
        batch = new SpriteBatch();
        blurShader = new ShaderProgram(blurVERT, blurFRAG);

        if (!blurShader.isCompiled()) {
            System.err.println(blurShader.getLog());
            System.exit(0);
        }
        if (blurShader.getLog().length()!=0) System.out.println(blurShader.getLog());
    }


    public Texture blurTexture(Texture text, float blurA) {

        blurShader.begin();
        blurShader.setUniformf("dir", 0f, 0f);
        blurShader.setUniformf("resolution", w);
        blurShader.setUniformf("radius", blurA);
        blurShader.end();

        blurTargetA = new FrameBuffer(Format.RGBA8888, w, h, false);
        blurTargetB = new FrameBuffer(Format.RGBA8888, w, h, false);

        TextureRegion fboRegion = new TextureRegion(text);
        fboRegion.flip(false, true);

        SpriteBatch batch = new SpriteBatch();
        //Start rendering to an offscreen color buffer

        batch.setShader(blurShader);
        //First layer
        blurShader.begin();
        blurShader.setUniformf("dir", 1f, 0f);
        blurShader.end();

        blurTargetB.begin();
        batch.begin();

        batch.draw(fboRegion,0,0);
        batch.end();
        blurTargetB.end();

        //second
        fboRegion.setTexture(blurTargetB.getColorBufferTexture());

        blurShader.begin();
        blurShader.setUniformf("dir", 0f, 1f);
        blurShader.end();

        blurTargetA.begin();
        batch.begin();
        batch.draw(fboRegion, 0, 0);
        batch.end();
        blurTargetA.end();



        return blurTargetA.getColorBufferTexture();
    }

    public void disposeBlurTargets(){
        blurTargetA.dispose();
        blurTargetB.dispose();
    }

    public void dispose(){
        blurShader.dispose();
        batch.dispose();
    }

//Capture first layer
// TOOO SLOW BOOOO!!!!
//DON'T MAKE ME DELETE THIS CODE, IT'S SO BEAUTIFUL!!!
//		firstLayerBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, screenWidth,screenHeigth, false);
//		firstLayerBuffer.begin();

//Render first layer
//		modelBatch.begin(cam);
//		modelBatch.render(firstNonShadedLayer);
//		modelBatch.render(firstShadedLayer, environment);
//		modelBatch.end();

//Finish capture
//		firstLayerBuffer.end();
//		//blur the buffer
//		fboRegion.setRegion(firstLayerBuffer.getColorBufferTexture());
//		fboRegion.setRegion(blur.blurTexture(fboRegion.getTexture(), 1.0f));
//		//Flip texture cause of reasons
//		fboRegion.flip(false, true);
//		//Render framebuffer to screen
//		batch.begin();
//		batch.draw(fboRegion,0,0);
//		batch.end();

//Render Second layer
		/*


				//clear buffer from memory
//		firstLayerBuffer.dispose();
//		blur.disposeBlurTargets();
		 */
}
