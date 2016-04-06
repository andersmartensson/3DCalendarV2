package shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;

/**
 * Created by anders on 2015-12-01.
 */
public class WaterShader implements Shader {

    ShaderProgram program;
    Camera cam;
    RenderContext context;
    double gameTime;
    int u_projTrans;
    int u_worldTrans;
    int u_colorU;
    int u_colorV;

    @Override
    public void init() {
        final String VERT = Gdx.files.internal("shaders/sea_vert.glsl").readString();
        final String FRAG = Gdx.files.internal("shaders/sea_frag.glsl").readString();
        //final String FRAG = Gdx.files.internal("shaders/starBack2Shader.fragment.glsl").readString();

        program = new ShaderProgram(VERT, FRAG);
        if(!program.isCompiled()){
            throw new GdxRuntimeException(program.getLog());
        }

        u_projTrans = program.getUniformLocation("u_projTrans");
        u_worldTrans = program.getUniformLocation("u_worldTrans");
        u_colorU = program.getUniformLocation("u_colorU");
        u_colorV = program.getUniformLocation("u_colorV");
    }

    @Override
    public int compareTo(Shader other) {
        return 0;
    }

    @Override
    public boolean canRender(Renderable instance) {
        return false;
    }

    @Override
    public void begin(Camera camera, RenderContext context) {
        gameTime +=0.01;
        this.cam = camera;
        this.context = context;
        program.begin();
        program.setUniformf("resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        program.setUniformf("time", (float) gameTime);
        Vector2 v2= new Vector2(Gdx.input.getX(), Gdx.input.getY());
        //program.setUniformf("mouse", v2);
        //Vector3 v = new Vector3();
        //cam.project(v);
        //program.setUniformf("camera", cam.direction);
        System.out.println("cam direction x: " + cam.direction.x + " y: " + cam.direction.y + " z: " + cam.direction.z);
        program.setUniformMatrix(u_projTrans, camera.combined);
        context.setDepthTest(GL20.GL_LEQUAL);
        context.setCullFace(GL20.GL_BACK);
        //program.setUniformf("mouse",new Vector2(Gdx.Input.getX(),Gdx.Input.getY()));
    }

    @Override
    public void render(Renderable renderable) {
        //program.setUniformMatrix(u_worldTrans, renderable.worldTransform);
        //program.setUniformMatrix(u_projTrans, cam.combined);
        //program.setUniformMatrix(u_projTrans, cam.combined);
        //if(program == null) System.out.println("NULL!!!!");
        //if(program != null) System.out.println("NOT NULL!!!!");

        renderable.meshPart.render(program);
    }

    @Override
    public void end() {
        program.end();
    }

    @Override
    public void dispose() {
        program.dispose();
    }
}
