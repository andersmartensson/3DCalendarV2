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
import com.google.api.services.calendar.model.Event;

import java.util.List;

import controller.CalendarController;
import data.Statics;
import model.Activity;
import model.Date3d;
import model.GFX.BackPlate;
import model.GFX.DatePillar;
import model.GFX.GFXObject;
import model.GFX.Ground;
import model.GFX.Skybox;
import model.GFX.Sun;
import model.GFX.TextTexture;
import operations.SaveManager;
import postprocessing.PostProcessor;
import postprocessing.ShaderLoader;
import postprocessing.effects.Fxaa;
import shaders.WaterShader;

public class MainView extends InputAdapter implements ApplicationListener {

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
	private Array<DatePillar> datePillars;
	private CalendarController calCont;
	private PostProcessor postProcessor;


	private void createPostProcesses() {
		ShaderLoader.BasePath = Statics.SHADER_BASE_PATH;
		postProcessor = new PostProcessor( true, false, true );
		disposables.add(postProcessor);
//		Bloom bloom = createBloom();
//		if(Statics.RENDER_BLOOM)postProcessor.addEffect(bloom);
//		disposables.add(bloom);
		Fxaa fxaa = new Fxaa(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		disposables.add(fxaa);
		postProcessor.addEffect(fxaa);
//		Nfaa nfaa = new Nfaa(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
//		disposables.add(nfaa);
//		postProcessor.addEffect(nfaa);
		//Create lens flare
		//lens = new LensFlare(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//		createLensFlare();
//		disposables.add(lens);
//		postProcessor.addEffect(lens);
//		//create spaceship lens
//		spaceShipLens = new LensFlare(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//		disposables.add(spaceShipLens);
//		postProcessor.addEffect(spaceShipLens);
	}

	@Override
	public void create () {

		//Settings for OpenGL
		Gdx.gl.glClearDepthf(1.0f);
		//Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glDepthFunc(GL20.GL_LESS);
		Gdx.gl.glDepthRangef(0f, 1f);
		Gdx.gl.glEnable(GL20.GL_TEXTURE_2D);


		camIsMoving = false; //For camera interpolarisation.
		disposables = new Array<Disposable>();
		//Create text render
		spriteBatch = new SpriteBatch();
		disposables.add(spriteBatch);
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

//Create camera and calCont
		cam = new PerspectiveCamera(Statics.CAMERA_FOV, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		finalPosition = Statics.CAM_START_POSITION;
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
		disposables.add(ui);
		ui.createUI(multiplexer, this);
		multiplexer.addProcessor(ui.getUiStage());
		multiplexer.addProcessor(this);
		multiplexer.addProcessor(camController);
		Gdx.input.setInputProcessor(multiplexer);
//Create Post Proccesing effects
		createPostProcesses();
		//=============Create models =================
		//Create sun - Not visible but is going to be needed to get a position for the lens flare
		sun = new Sun();
		//Create model and modelInstance and add it to render array
		sun.setModelInstance(new ModelInstance(sun.getModel()));
		sun.setPosition(new Vector3(115.0f, 150.0f, 200.0f));
		//firstNonShadedLayer.add(sun.getModelInstance());
		disposables.add(sun);

		//Create skybox sphere
		skybox = new Skybox(Statics.SKYBOX_SIZE);
		skybox.getModel();
		disposables.add(skybox);

		//Create ground
		ground = new Ground();
		ground.setModelInstance(new ModelInstance(ground.getModel()));
		ground.setPosition(Statics.GROUND_POSITION);


//Create model batch and texture region
		modelBatch = new ModelBatch();
		disposables.add(modelBatch);
		fboRegion = new TextureRegion();
		setLight();
		//createWaterShader();
		updateTheme();
		//Create Calender and download events
		calCont = new CalendarController(this);
		calCont.update();
		activities = new Array<Activity>(calCont.events.size());
		createDays(calCont.events);
		createActivities(calCont.events, datePillars);


		//TEST
		//createTest();
		tt = new TextTexture();
	}
	TextTexture tt;
	private void createDays(List<Event> events) {
		//figure out what date the first event is at
		Event e = events.get(0);
		//takes first event and
		//LocalDateTime now = LocalDateTime.now();
		//Date should start on a monday
		datePillars = new Array<DatePillar>();
		//Draw two weeks of pillars
		DatePillar dt = null;
		ModelInstance mi = null;
		Vector3 origin = new Vector3(Statics.DATEPILLAR_X_ORIGN,
				Statics.DATEPILLAR_Y_ORIGIN,
				Statics.DATEPILLAR_Z_ORIGIN);
		float step = Statics.ACTIVITY_WIDTH + 1f;
		//Create datePillar
		Date3d d = new Date3d(e);
		for(int i=0;i<28;i++){
			dt = new DatePillar(d.clone());
			dt.setModelInstance(new ModelInstance(dt.getModel()));
			origin.x += step;
			//If is week then add step and back plate
			if(i % 7 == 0){
				origin.x += step;
				//Add backplate
				BackPlate bPlate= new BackPlate();
				disposables.add(bPlate);
				bPlate.setModelInstance(new ModelInstance(bPlate.getModel()));
				bPlate.setPosition(Statics.WEEK_BACKPLATE_POSITION.cpy());
				bPlate.fixPosition(origin.x);
				firstShadedLayer.add(bPlate.getModelInstance());
			}
			Vector3 tv = origin.cpy();
			//tv.x += step;
			dt.setPosition(tv);
			firstShadedLayer.add(dt.getModelInstance());
			datePillars.add(dt);
			//dt.getModelInstance().transform.setTranslation(dt.getPosition());
			d.day +=1;
		}

	}

	private void createActivities(List<Event> events, Array<DatePillar> pillars) {
		//Create Activity test
		Activity a;
		Color c = null;
//		EventDateTime sTime;
//		DateTime sTime2;
		float x = 0;
		SpriteBatch sb = new SpriteBatch();

		for(Event e: events){
			//Get Color:
			if(e.getColorId() == null){
				c = Color.RED;
			}
			else {
				//System.out.println("color:" + e.getSummary() + " ->" + e.getColorId() + "<-");
				c = GFXObject.translateColor(e.getColorId());
			}
			//Get startTime
			if(e.getStart().getDateTime() != null
					&& e.getEnd().getDateTime() != null){
				Date3d d3d = new Date3d(e);
				x = d3d.matchXValue(pillars);
				a = new Activity(c, d3d,e);
				a.setModelInstance(new ModelInstance(a.getModel(sb,ui.skin)));
				//Match it to corresponding datePillar
				a.setPosition(new Vector3(x, a.getYOrigin(), 0));
				//a.getModelInstance().transform.setTranslation(a.getPosition());
				firstShadedLayer.add(a.getModelInstance());
				//x += Statics.ACTIVITY_WIDTH + 1f;
				//a.event = e;
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
		Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		postProcessor.capture();
		//Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		//modelBatch.;
		modelBatch.begin(cam);
		modelBatch.render(firstNonShadedLayer);
		modelBatch.render(firstShadedLayer, environment);
		modelBatch.render(skybox.getModelInstance());
		modelBatch.render(ground.getModelInstance(), environment);
		modelBatch.render(secondShadedLayer, environment);
		modelBatch.end();

		postProcessor.render();
		ui.drawUI();


		//renderTest();
		//tt.createTextTexture(spriteBatch,ui.skin,Color.BROWN,"Hejsan HEHSAN \n Andra raden",10f,10f);

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

	}

	@Override
	public void resume() {

	}

	public void resize(int width, int height) {

	}

	public void dispose() {
		//modelBatch.dispose();

		for(Disposable d: disposables){
			d.dispose();
		}
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
		//Get clicked activity
		int result = getActivity(screenX, screenY);
		if(result != -1){
			System.out.println("=============================");
			Activity ca = activities.get(result);
			//Update text
			ui.updateDetails(ca.getDetails());
			System.out.println("Clicked: " + ca.toString());
			//Focus and move camera to activity
			//Gdx.input.
			//move
			finalPosition = new Vector3(ca.position.x, ca.position.y, ca.position.z - Statics.DISTANCE_FROM_CAMERA);
			cam.position.set(finalPosition);
			cam.update();

			//Fix pitch
			System.out.println("can UP: " + cam.up);
//			//Rotate x
			while(cam.up.x > 0.05f || cam.up.x < - 0.05f){
				cam.rotateAround(finalPosition, new Vector3(0, 1f, 0), 3f);
				//cam.update();
				//System.out.println("FIX x, can UP: " + cam.up);
			}
			while(cam.up.y < 0.95f ){
				cam.rotateAround(finalPosition,new Vector3(1f,0,0),3f);
				//cam.update();
				//System.out.println("Fix y, can UP: " + cam.up);
			}
			//Focus
			cam.lookAt(ca.position);
			camController.target = ca.position;
			cam.update();
			//Roate y

//			cam.lookAt(ca.position);
//			camController.target = ca.position;

			//camController.update();
			//camController.target


			//camIsMoving = true;
//			Vector3 t = new Vector3(1,0,0);
//			Vector3 cv = cam.direction;
//			System.out.println("cam direction: " + cv);
//
//			//cam.rotate(cam.up, cam.normalizeUp());
//			//cam.rotateAround(finalPosition, cam.up, 90f);
//			cam.
//					cam.update();
//			System.out.println("canmera up " + cam.up);
//			//cam.normalizeUp();
//			cam.update();
//			System.out.println("canmera after noirmal up " + cam.up);
//
//			cv = cam.direction;
//
//			System.out.println("cam direction: " + cv);
//			System.out.println("=============================");

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
