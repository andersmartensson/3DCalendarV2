package view;


import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DefaultTextureBinder;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;

import java.io.IOException;
import java.util.List;

import controller.GoogleCalendarDownload;
import data.Statics;
import model.Activity;
import model.GFX.GFXObject;
import model.GFX.Ground;
import model.GFX.Skybox;
import model.GFX.Sun;
import operations.SaveManager;
import shaders.WaterShader;

public class MainView extends InputAdapter implements ApplicationListener {

	//private ModelInstance waterInstance;


	//Render stuff
	Environment environment;
	PerspectiveCamera cam;
	CameraInputController camController;
	ModelBatch modelBatch;
	//ShapeRenderer shapeRender;
	TextureRegion fboRegion;
	private DirectionalLight dirLight;
	private SpriteBatch spriteBatch;
	private Texture blackTexture;
	//Ui
	UI ui;
	//Render Layers
	private Array<ModelInstance> firstShadedLayer;
	private Array<ModelInstance> firstNonShadedLayer;
	private Array<ModelInstance> secondShadedLayer;

	int screenWidth;
	int screenHeigth;
	private ShaderProgram bgShader;
	private float appTime;
	private ModelInstance waterInstance;

	private Vector3 position;

	private Ground ground;
	private SaveManager saveManager;
	public int theme;
	private Skybox skybox;
	private Sun sun;
	private Array<Disposable> disposables;
	private Array<Activity> activities;
	private Vector3 finalPosition;
	private boolean camIsMoving;

	@Override
	public void create () {

		Gdx.gl.glClearDepthf(1.0f);
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glDepthFunc(GL20.GL_LESS);
		Gdx.gl.glDepthRangef(0f, 1f);
		Gdx.gl.glEnable(GL20.GL_TEXTURE_2D);

		camIsMoving = false;
		disposables = new Array<Disposable>();
		spriteBatch = new SpriteBatch();
		blackTexture = new Texture(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Pixmap.Format.RGBA8888);
		disposables.add(blackTexture);
		theme = Gdx.app.getPreferences("My Preferences").getInteger("theme", 0);
		//Create save manager
		saveManager = new SaveManager();
		appTime = 1.0f;
		screenWidth = Gdx.graphics.getWidth();
		screenHeigth = Gdx.graphics.getHeight();

//create render arrays:
		firstShadedLayer = new Array<ModelInstance>();
		firstNonShadedLayer = new Array<ModelInstance>();
		secondShadedLayer = new Array<ModelInstance>();

//create environment
		environment = new Environment();

//Create camera and controller
		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		finalPosition = new Vector3(10f, 10f, 10f);
		cam.position.set(finalPosition);
		cam.viewportHeight = 720f;
		cam.viewportWidth = 1280f;
		cam.lookAt(0,0,0);
		cam.far = Statics.CAM_FAR;
		cam.near = Statics.CAM_NEAR;
		cam.update();
		camController = new CameraInputController(cam);

		InputMultiplexer multiplexer = new InputMultiplexer();
		ui = new UI();
		ui.createUI(multiplexer, this);
		multiplexer.addProcessor(ui.getUiStage());
		multiplexer.addProcessor(this);
		multiplexer.addProcessor(camController);
		Gdx.input.setInputProcessor(multiplexer);
//Create models

		//Create sun - Not visible but is going to be needed to get a position for the lens flare
		sun = new Sun();
		//Create model and modelInstance and add it to render array
		sun.setModelInstance(new ModelInstance(sun.getModel()));
		sun.setPosition(new Vector3(115.0f, 150.0f, 200.0f));
		sun.modelInstance.transform.setTranslation(sun.getPosition());
		//firstNonShadedLayer.add(sun.getModelInstance());

		//Create skybox sphere
		skybox = new Skybox(Statics.SKYBOX_SIZE);
		skybox.getModel();

		//Create ground
		ground = new Ground();
		ground.setModelInstance(new ModelInstance(ground.getModel()));
		ground.setPosition(new Vector3(0, -15.0f, 0.0f));
		ground.getModelInstance().transform.setTranslation(ground.getPosition());

//Create model batch and texture region
		modelBatch = new ModelBatch();
		fboRegion = new TextureRegion();
		setLight();
		createWaterShader();
		updateTheme();
		GoogleCalendarDownload gcd = new GoogleCalendarDownload();
		//System.out.println(CalendarMain.class.getResource("/").getPath());
		List<Event> events = null;
		try {
			events = gcd.execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("From render main:");
		if (events == null || events.size() == 0) {
			System.out.println("No upcoming events found.");
		} else {
			System.out.println("Upcoming events");
			for (Event event : events) {
				DateTime start = event.getStart().getDateTime();
				if (start == null) {
					start = event.getStart().getDate();
				}
				System.out.printf("%s (%s)\n", event.getSummary(), start);
			}
		}
		activities = new Array<Activity>(events.size());
		createActivities(events);
	}

	private void createActivities(List<Event> events) {
		//Create Activity test
		Activity a;
		Color c = null;
//		EventDateTime sTime;
//		DateTime sTime2;
		float x = 0;
		for(Event e: events){
			//Get Color:
			if(e.getColorId() == null){
				c = Color.RED;
			}
			else {
				System.out.println("color:" + e.getSummary() + " ->" + e.getColorId() + "<-");
				c = GFXObject.translateColor(e.getColorId());
			}
			//System.out.println("START: ");
			//System.out.println("star time: " + e.getStart().getDateTime().getValue());
//			System.out.println("start time: " + e.getStart().getDate().getValue());
//			System.out.println("endtime: " + e.getEnd().getDate().getValue());

			//Get startTim
			if(e.getStart().getDateTime() != null
					&& e.getEnd().getDateTime() != null){
				a = new Activity(c,e.getStart().getDateTime().getValue()
						,e.getEnd().getDateTime().getValue());
				a.setModelInstance(new ModelInstance(a.getModel()));
				a.setPosition(new Vector3(x, a.getYOrigin(), 0));
				a.getModelInstance().transform.setTranslation(a.getPosition());

				firstShadedLayer.add(a.getModelInstance());
				System.out.println("");
				//a = new Activity()
				x += Statics.ACTIVITY_WIDTH + 1f;
				a.event = e;
				activities.add(a);

			}

		}
	}

	Renderable renderable;
	Shader waterShader;
	RenderContext renderContext;
	ShaderProgram waterShaderProgram;

	private void createWaterShader(){
		ShaderProgram.pedantic = false;
		final String VERT = Gdx.files.internal("shaders/sea_vert.glsl").readString();
		final String FRAG = Gdx.files.internal("shaders/sea_frag.glsl").readString();
		bgShader = new ShaderProgram(VERT, FRAG);
		/**
		 * This renders directly
		 */
		//NodePart blockPart = water.getModel().nodes.get(0).parts.get(0);
		NodePart blockPart = skybox.getModel().nodes.get(0).parts.get(0);
		renderable = new Renderable();
		blockPart.setRenderable(renderable);
		renderable.environment = environment;
		renderable.worldTransform.idt();
		renderContext = new RenderContext(new DefaultTextureBinder(DefaultTextureBinder.WEIGHTED, 1));
		waterShader = new WaterShader();
		waterShader.init();

//		//Set values
//		bgShader.begin();
//		bgShader.setUniformf("resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//		bgShader.setUniformf("time", appTime);
//		bgShader.setUniformf("mouse",new Vector2(Gdx.Input.getX(), Gdx.Input.getY()));
//		bgShader.end();
	}

	private void updateWaterShader(){
		bgShader.begin();
		bgShader.setUniformf("time", appTime);
		bgShader.setUniformf("mouse", new Vector2(Gdx.input.getX(), Gdx.input.getY()));
		bgShader.end();
	}

	private void renderWaterShader(){
		/**
		 * This render directly
		 */
		renderContext.begin();
		waterShader.begin(cam, renderContext);
		waterShader.render(renderable);
		waterShader.end();
		renderContext.end();

		/**
		 * This renders to texture
		 */
//		waterBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
//		waterShaderProgram.begin();
//		waterShaderProgram.setUniformf("resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//		waterShaderProgram.setUniformf("time", (float) gameTime);
//		waterShaderProgram.setUniformMatrix(u_projTrans, cam.combined);
//		waterShaderProgram.end();
//
//		batch.setShader(waterShaderProgram);
//		waterBuffer.begin();
//		batch.begin();
//		batch.draw(fboRegion, 0, 0);
//		batch.end();
//		Material m = new Material();
//		m.set(TextureAttribute.createDiffuse(waterBuffer.getColorBufferTexture()));
//		water.getModel().materials.insert(0, m);
//		waterInstance = new ModelInstance(water.getModel());

	}

	@Override
	public void render (){
		appTime += 1 %1000000;
		updateCamera();
		//Gdx.gl.glClearColor(0.0f, 0.0f, 1.0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		//Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		modelBatch.begin(cam);
		modelBatch.render(firstNonShadedLayer);
		modelBatch.render(firstShadedLayer, environment);
		modelBatch.render(skybox.getModelInstance());
		modelBatch.render(ground.getModelInstance(), environment);
		modelBatch.render(secondShadedLayer, environment);
		modelBatch.end();

		ui.drawUI();
	}

	private void updateCamera() {
		//CheckCameraPosition
		float step = 0.5f;
		Vector3 v = cam.position;
		if(camIsMoving){
			if(finalPosition.x < v.x){
				cam.position.x -= step;
			}
			else if (finalPosition.x > v.x){
				cam.position.x += step;
			}

			if(finalPosition.y < v.y){
				cam.position.y -= step;
			}
			else if (finalPosition.y > v.y){
				cam.position.y += step;
			}

			if(finalPosition.z < v.z){
				cam.position.z -= step;
			}
			else if (finalPosition.z > v.z){
				cam.position.z += step;
			}
			cam.update();
		}
		if(!cameraNotInFinalPosition()){
			camIsMoving = false;
		}
//		//Check x

	}

//	public void updatePosition(Camera cam){
//		//time += 1;
//		float step = 0.1f;
//		float error = 0.001f;
//
//		Vector3 v = new Vector3();
//		cam.transform.getTranslation(v);
//		if(isBetween(v.x, position.x, error)){
//			v.x += (v.x < position.x) ? step : -step;
//		}
//		if(isBetween(v.y, position.y, error)){
//			v.y += (v.y < position.y) ? step : -step;
//		}
//		if(isBetween(v.z, position.z, error)){
//			v.z += (v.z < position.z) ? step : -step;
//		}
//
//		modelInstance.transform.setTranslation(v);
//	}

	private boolean isBetween(float current, float destination, float error) {
		if(current > destination + error){
			return true;
		}
		else if(current < destination - error){
			return true;
		}
		return false;
	}

	private void fixFloat(Vector3 position) {
		position.x = ((int) position.x * 10) / 10f;
		position.y = ((int) position.y * 10) / 10f;
		position.z = ((int) position.z * 10) / 10f;
	}

	private boolean cameraNotInFinalPosition() {
		Vector3 v = cam.position;
		if(v.x != finalPosition.x
				&& v.y != finalPosition.y
				&& v.z != finalPosition.z){
			return false;
		}
		return true;
	}


	@Override
	public void pause() {
		//saveManager.saveThisGame(morris);
		//saveManager.writeSaves();
	}

	@Override
	public void resume() {

	}

	public void resize(int width, int height) {

	}

	public void dispose() {
		modelBatch.dispose();
		disposeAndClearRenderLayers();
		ui.dispose();
		ground.dispose();
		skybox.dispose();
	}


	public void disposeRenderLayer(Array<ModelInstance> al){
		for(ModelInstance mi : al){
			mi.model.dispose();
		}
		al.clear();
	}
	/**
	 * Disposes of all models in render layers
	 */
	public void disposeAndClearRenderLayers() {
		disposeRenderLayer(firstShadedLayer);
		disposeRenderLayer(firstNonShadedLayer);
		disposeRenderLayer(firstNonShadedLayer);
	}

	protected boolean isVisible ( Camera cam, Activity a) {
		a.getModelInstance().transform.getTranslation(position);
		position.add(a.center);
		//return cam.frustum.sphereInFrustum(position, a.radius);
		return cam.frustum.boundsInFrustum(a.position.x,a.position.y,a.position.z,
				Statics.ACTIVITY_WIDTH / 2,
				a.height / 2,
				Statics.ACTIVITY_DEPTH /2 );
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		//System.out.println("X: " + screenX + " y: " + screenY);

		//Get clicked activity
		int result = getActivity(screenX, screenY);
		if(result != -1){
			Activity ca = activities.get(result);
			//Focus and move camera to activity
			//Focus
			cam.lookAt(ca.position);
			//move
			finalPosition = new Vector3(ca.position.x, ca.position.y, ca.position.z - Statics.DISTANCE_FROM_CAMERA);
			//Vector3 v = cam.position;
			//cam.position.set(finalPosition);
			//Focus
			cam.lookAt(ca.position);
			camController.target = ca.position;
			camController.update();
			//camController.target
			cam.update();
			camIsMoving = true;
		}


		return false;
	}


	public int getActivity (int screenX, int screenY) {
		position = new Vector3();
		Ray ray = cam.getPickRay(screenX, screenY);
		int result = -1;
		float distance = -1;
		for (int i = 0; i < activities.size; ++i) {
			Activity a = activities.get(i);
			a.getModelInstance().transform.getTranslation(position);
			position.add(a.center);
			float dist2 = ray.origin.dst2(position);
			if (distance >= 0f && dist2 > distance) continue;
			if (Intersector.intersectRayBoundsFast(ray,position,a.dimensions)){
				System.out.println("Hit: " + a.event.getSummary());
				result = i;
				distance = dist2;
			}

		}
		return result;
	}

	public void toggleTheme() {
		theme = (theme == 0) ? 1 : 0;
		Preferences pref= Gdx.app.getPreferences("My Preferences");
		pref.putInteger("theme", theme);
		pref.flush();
		updateTheme();
	}

	private void updateTheme() {
		setLight();
		ground.setGroundTexture(theme);
		skybox.setSkyBoxTexture(theme);
	}

	private void setLight() {
		if(theme == 0){
			environment.clear();
			environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.2f, 0.2f, 0.2f, 1f));
			//directional light
			dirLight = new DirectionalLight().set(0.8f, 0.8f, 0.8f, -115f, -150.0f, -200.0f);
			sun.setPosition(new Vector3(115.0f, 150.0f, 200.0f));
			sun.modelInstance.transform.setTranslation(sun.getPosition());
			environment.add(dirLight);
		}else{
			environment.clear();
			environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.2f, 0.1f, 0.1f, 1f));
			sun.setPosition(new Vector3(-30.0f, 50.0f, -100.0f));
			sun.modelInstance.transform.setTranslation(sun.getPosition());
			//directional light
			dirLight = new DirectionalLight().set(0.8f, 0.75f, 0.75f, 30f, -50.0f, 100.0f);
			environment.add(dirLight);
		}
	}
}
