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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
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
import com.google.api.services.calendar.model.EventDateTime;

import java.util.Calendar;
import java.util.Date;

import controller.CalendarController;
import controller.TouchController;
import data.Statics;
import model.Activity;
import model.Date3d;
import model.GFX.Alphabet;
import model.GFX.BackPlate;
import model.GFX.DatePillar;
import model.GFX.GFXObject;
import model.GFX.Ground;
import model.GFX.InsertEvent;
import model.GFX.Skybox;
import model.GFX.Sun;
import postprocessing.PostProcessor;
import postprocessing.ShaderLoader;
import postprocessing.effects.Fxaa;
import postprocessing.effects.Nfaa;
import shaders.WaterShader;

public class MainView extends InputAdapter implements ApplicationListener{

	//Render stuff
	Environment environment;
	PerspectiveCamera cam;
	CameraInputController camController;
	ModelBatch modelBatch;
	TextureRegion fboRegion;
	private DirectionalLight dirLight;
	private SpriteBatch spriteBatch;
	//Ui
	UI ui;
	//Render Layers
	private Array<ModelInstance> firstShadedLayer;
	private Array<ModelInstance> firstNonShadedLayer;
	private Array<ModelInstance> secondShadedLayer;
	private Array<ModelInstance> activityLayer;

	int screenWidth;
	int screenHeigth;
	int currentWeek;
	private ShaderProgram bgShader;
	private float appTime;
	private ModelInstance waterInstance;
	private Vector3 position;
	private Ground ground;
	public int theme;
	private Skybox skybox;
	private Sun sun;
	private Array<Disposable> disposables;
	private Array<Disposable> dynamicDisposables;
	private Array<Activity> activities;
	//private Vector3 finalPosition;
	private boolean camIsMoving;
	private Array<DatePillar> datePillars;
	private CalendarController calCont;
	private PostProcessor postProcessor;
	private Alphabet alphabet;
	private boolean updateActivities;
	public long from;
	public long to;
	public Activity currentActivity;
	private boolean downloadDone;
	private Vector3 finalCameraPosition;
    private InsertEvent insertEvent;

    public boolean isDownloadDone() {
		return downloadDone;
	}

	public void setDownloadDone(boolean downloadDone) {
		this.downloadDone = downloadDone;
	}

	private void createPostProcesses() {
		ShaderLoader.BasePath = Statics.SHADER_BASE_PATH;
		postProcessor = new PostProcessor( true, false, true );
		disposables.add(postProcessor);
//		Bloom bloom = createBloom();
//		if(Statics.RENDER_BLOOM)postProcessor.addEffect(bloom);
//		disposables.add(bloom);
		if(!Statics.isAndroid){
			Fxaa fxaa = new Fxaa(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
			disposables.add(fxaa);
			postProcessor.addEffect(fxaa);
			Nfaa nfaa = new Nfaa(Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
			disposables.add(nfaa);
			postProcessor.addEffect(nfaa);
		}
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

	//public static boolean isAndroid;
	public MainView(boolean isAndroid){
		//this.isAndroid = isAndroid;
		Statics.isAndroid = isAndroid;
	}

	public MainView(){
		//isAndroid = false;
		Statics.isAndroid = false;
	}

	@Override
	public void create () {
		updateActivities = false;
		//create calender
		Statics.calendar = Calendar.getInstance();
		//Settings for OpenGL
		Gdx.gl.glClearDepthf(1.0f);
		//Gdx.gl.glEnable(GL20.GL_DEPTH_TEST); // This conflicts with texture filtering
		Gdx.gl.glDepthFunc(GL20.GL_LESS);
		Gdx.gl.glDepthRangef(0f, 1f);
		Gdx.gl.glEnable(GL20.GL_TEXTURE_2D);

		camIsMoving = false; //For camera interpolation.
		disposables = new Array<Disposable>();
		dynamicDisposables = new Array<Disposable>();
		//Create text render
		spriteBatch = new SpriteBatch();
		disposables.add(spriteBatch);
		theme = Gdx.app.getPreferences("My Preferences").getInteger("theme", 0);
		appTime = 1.0f;
		screenWidth = Gdx.graphics.getWidth();
		screenHeigth = Gdx.graphics.getHeight();

//create render arrays:
		firstShadedLayer = new Array<ModelInstance>();
		firstNonShadedLayer = new Array<ModelInstance>();
		secondShadedLayer = new Array<ModelInstance>();
		activityLayer = new Array<ModelInstance>();

//create environment
		environment = new Environment();

//Create camera and calCont
		cam = new PerspectiveCamera(Statics.CAMERA_FOV, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		finalCameraPosition = Statics.CAM_START_POSITION.cpy();
		cam.position.set(finalCameraPosition);
		//Set camera aspect
		cam.viewportHeight = 9f;
		cam.viewportWidth = 16f;
		cam.far = Statics.CAM_FAR;
		cam.near = Statics.CAM_NEAR;
		cam.update();
		camController = new CameraInputController(cam);
		camController.translateUnits = Statics.CAMERA_CONTROL_TRANSLATE_UNITS;
		camController.target.set(Statics.CAM_FOCUS_POSITION.cpy());
		//Set input
		InputMultiplexer multiplexer = new InputMultiplexer();
		ui = new UI();
		disposables.add(ui);
		ui.createUI(multiplexer, this);
		multiplexer.addProcessor(ui.getUiStage());
		multiplexer.addProcessor(ui.detailsStage);
		multiplexer.addProcessor(ui.reportDialogStage);
		multiplexer.addProcessor(this);
		multiplexer.addProcessor(new TouchController(new TouchController.DirectionListener() {

            @Override
            public void flingLeft() {
                if (Statics.isAndroid) {
                    System.out.println("Swipe left");
                    //Go one week backwards
                    from -= calCont.milliSecondsInADay() * 7;
                    to -= calCont.milliSecondsInADay() * 7;
                    currentActivity = null;
                    calCont.update(from, to);
                    //System.out.println("Calendar update took: " + (System.currentTimeMillis() - time));
                    updateActivities = true;
                    clearedAndMoved = false;
                }
            }

            @Override
            public void flingRight() {
                if (Statics.isAndroid) {
                    System.out.println("Swipe right");
                    //Go one week forward
                    from += calCont.milliSecondsInADay() * 7;
                    to += calCont.milliSecondsInADay() * 7;
                    currentActivity = null;
                    calCont.update(from, to);
                    //System.out.println("Calendar update took: " + (System.currentTimeMillis() - time));
                    updateActivities = true;
                    clearedAndMoved = false;
                }
            }

            @Override
            public void flingUp() {
                System.out.println("SWIPE UP");

            }

            @Override
            public void flingDown() {
                System.out.println("Swipe down");

            }

            @Override
            public void dragLeft() {
                System.out.println("drag left");

            }

            @Override
            public void dragRight() {
                System.out.println("drag right");
            }


        }));
		multiplexer.addProcessor(camController);
		Gdx.input.setInputProcessor(multiplexer);
        //Create InsertEvent model
        insertEvent = new InsertEvent();
        //insertEvent.getModel();
		//Create text
		alphabet = new Alphabet();
		disposables.add(alphabet);
//Create Post Processing effects
		createPostProcesses();
		//=============Create models =================
		//Create sun - Not visible but is going to be needed to get a position for the lens flare
		sun = new Sun();
		//Create model and modelInstance and add it to render array
		sun.setModelInstance(new ModelInstance(sun.getModel()));
		sun.setPosition(new Vector3(115.0f, 150.0f, 200.0f));
		//firstNonShadedLayer.add(sun.getModelInstance());
		disposables.add(sun);

		//Create sky box sphere
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
		//Create calendar and download events
		calCont = new CalendarController(this);
		calCont.initialDownload();
		activities = new Array<Activity>();
		long time = System.currentTimeMillis();
		currentWeek = findWeek(time);
		//Date3d d = new Date3d(calCont.lastUpdate);

		createDatePillars(calCont.lastUpdate, true);
		createActivities(calCont.events, datePillars);
		System.out.println("CreatingDays and activities took " + (System.currentTimeMillis() - time));

		//Center camera on current date
		getCameraFocusOnDate(System.currentTimeMillis());
		//Test Text generator
		//firstShadedLayer.addAll(alphabet.load3DText("WWW123 WEKW PLEW",new Vector3(0,30f,0),1f));
	}

	private int findWeek(long time) {
		Calendar c = Statics.calendar;
		c.setTime(new Date(time));
		return c.get(Calendar.WEEK_OF_YEAR);
	}

	private void createDatePillars(long from, boolean initial) {
		//Date should start on a monday
		datePillars = new Array<DatePillar>();
		//Draw pillars
		Vector3 origin = new Vector3(Statics.DATEPILLAR_X_ORIGN,
				Statics.DATEPILLAR_Y_ORIGIN,
				Statics.DATEPILLAR_Z_ORIGIN);
		float step = Statics.ACTIVITY_WIDTH + Statics.ACTIVITY_SPACING;
		//Create datePillar
		Date3d d = new Date3d(from);

		for(int i=0;i<Statics.NUM_OF_DAYS_TO_DRAW;i++){
			DatePillar dt = new DatePillar(d.clone(true));
			d = dt.d3d;
			dt.setModelInstance(new ModelInstance(dt.getModel()));
            dt.calculateBoundingBox();

			origin.x += step;
			//If is end of week then add step and back plate
			if(i % 7 == 0){
				insertWeek(origin, step, d, initial);
			}

			Vector3 tv = origin.cpy();
			//tv.x += step;
			dt.setPosition(tv);
			firstShadedLayer.add(dt.getModelInstance());
			datePillars.add(dt);
			//Insert Month
			//Check if it is the first week of a month or
			// if this is the first instance
			if(i == 0 || d.day ==1 ){
				//Type out month as well.
			firstShadedLayer.addAll(alphabet.load3DText(Date3d.Month.values()[d.month].name()
					, new Vector3(origin.x
					, Statics.MONTH_ORIGIN_Y
					, Statics.MONTH_ORIGIN_Z)
					, 1f,
                    false));
			}

			//Type date
			firstShadedLayer.addAll(alphabet.load3DText(dt.d3d.getDayString()
					, new Vector3(origin.x + Statics.DATEPILLAR_DATE_NUM_MOD
					, Statics.DATEPILLAR_HEIGHT + 1f
					, Statics.DATEPILLAR_Z_ORIGIN)
					, 0.5f
                    ,false));
			d.date += calCont.milliSecondsInADay();
		}
	}

	private void insertWeek(Vector3 origin, float step, Date3d d, boolean initial) {
		Calendar c = Statics.calendar;
		//Type out week number
		//Check if e + days are
		c.setTime(new Date(d.date));
		//weekLayer.addAll(
		firstShadedLayer.addAll(
				alphabet.load3DText("WEEK " + c.get(Calendar.WEEK_OF_YEAR)
						, new Vector3(origin.x + Statics.WEEK_NUM_MODIFIER_X
						, Statics.WEEK_NUM_ORIGIN_Y
						, Statics.WEEK_NUM_ORIGIN_Z)
						, Statics.WEEK_NUMBER_SCALE
                        ,false)
                );

		//add extra step
		origin.x += step;
		if(initial){
			//Add back plate
			BackPlate bPlate= new BackPlate();
			disposables.add(bPlate);
			bPlate.setModelInstance(new ModelInstance(bPlate.getModel()));
			bPlate.setPosition(Statics.WEEK_BACKPLATE_POSITION.cpy());
			bPlate.fixPosition(origin.x);
			secondShadedLayer.add(bPlate.getModelInstance());
		}
	}

//	@Override
//	public boolean touchDown(float screenX, float screenY, int pointer, int button) {
//		System.out.println("Touched!!!!!!!!!!!==========");
//		//Get clicked activity
//		int result = getActivity(screenX, screenY);
//		if(result != -1){
//			ui.detailsVisible = true;
//			System.out.println("=============================");
//			currentActivity = activities.get(result);
//			//Update text
//			ui.updateDetails(currentActivity.getDetails());
//			System.out.println("Clicked: " + currentActivity.toString());
//			//Check if same week, else update calender
//			if(currentWeek != findWeek(currentActivity.d3d.date)){
//				from = calCont.getAdjustedDay(currentActivity.d3d.date);
//				to = from + calCont.milliSecondsInADay() * (Statics.NUM_OF_WEEKS_BEFORE_AND_AFTER *2 +1) * 7;
//				long time = System.currentTimeMillis();
//				calCont.update(from, to);
//				System.out.println("Calendar update took: " + (System.currentTimeMillis() - time));
//				updateActivities = true;
//				clearedAndMoved = false;
//			}
//			else {
//				//Focus and move camera to activity
//				finalPosition = new Vector3(currentActivity.position.x
//						, currentActivity.position.y
//						, currentActivity.position.z - Statics.CAMERA_DISTANCE_FROM);
//				cam.position.set(finalPosition);
//				cam.update();
//				//Fix pitch
//				fixPitch(finalPosition);
//				//Focus
//				cam.lookAt(currentActivity.position);
//				camController.target = currentActivity.position;
//				cam.update();
//			}
//		}
//		else {
//			System.out.println("NO result!!!!!!!!");
//		}
//		return false;
//	}

	private void createActivities(Array<Event> events, Array<DatePillar> pillars) {
		//Create Activity test
		Material m;
		Color c = null;
		float x = 0;
		for(Event e: events){
			//Get Color:
			if(e.getColorId() == null){
				//If no color, take default color
				m = GFXObject.translateColor(Statics.ACTIVITY_DEFAULT_COLOR);
			}
			else {
				m = GFXObject.translateColor(e.getColorId());
			}
			//Get startTime
			if(e.getStart().getDateTime() != null
					&& e.getEnd().getDateTime() != null){
				Date3d d3d = new Date3d(e);
				x = d3d.matchXValue(pillars);
				Activity a = new Activity(c, d3d,e, m);
				a.setModelInstance(new ModelInstance(a.getModel()));
				//Match it to corresponding datePillar
				a.setPosition(new Vector3(x, a.getYOrigin(), 0));
				a.calculateBoundingBox();
				activityLayer.add(a.getModelInstance());
				activities.add(a);
				//Add text
				firstShadedLayer.addAll(a.generateSummaryText(alphabet));
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
		if(camIsMoving){
			updateCamera();
		}
		if(updateActivities){
            updateActivities();
        }

		Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		postProcessor.capture();

		modelBatch.begin(cam);
		modelBatch.render(firstNonShadedLayer);
		modelBatch.render(skybox.getModelInstance());
		modelBatch.render(ground.getModelInstance(), environment);
		modelBatch.render(firstShadedLayer, environment);
		modelBatch.render(activityLayer, environment);
		modelBatch.render(secondShadedLayer, environment);
		modelBatch.end();

		postProcessor.render();
		ui.drawUI();
	}

    /*
    Called if a week is changed
     */
	private void updateCamera() {
        Vector3 v = cam.position.cpy();
        //Check if we can update camera position yet
        if(camIsMoving && !updateActivities){
            //update camera position and focus
            //Update camera position ( not used since it's not ready )
            if(false){
                //if(v.x != finalCameraPosition.x && v.y != finalCameraPosition.y && v.z != finalCameraPosition.z){
                smoothCameraMovement(v);
            }
            else {
                camIsMoving = false;
                cam.position.set(currentActivity.position.x
                        , currentActivity.position.y
                        //, currentActivity.position.z - Statics.CAMERA_DISTANCE_FROM);
                        ,cam.position.z);
                //Fix pitch
                fixPitch(finalCameraPosition);
                //Fix focus
                if(currentActivity != null){
                    cam.lookAt(currentActivity.position.cpy());
                    //Controller
                    camController.target = currentActivity.position.cpy();
                    currentWeek = findWeek(currentActivity.d3d.date);
                }
                else {
                    cam.lookAt(Statics.CAM_FOCUS_POSITION.cpy());
                    camController.target = Statics.CAM_FOCUS_POSITION.cpy();
                    double cDate = (from + to) / 2;
                    currentWeek = findWeek((long) cDate);
                }
            }
            cam.update();
        }
	}

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        //Get clicked activity
        int result = getActivity(screenX, screenY);
        if(result != -1){
            ui.detailsVisible = true;
            System.out.println("=============================");
            currentActivity = activities.get(result);
            //Update text
            ui.updateDetails(currentActivity.getDetails());
            System.out.println("Clicked: " + currentActivity.toString());
            //Check if same week, else update calender
            if(currentWeek != findWeek(currentActivity.d3d.date)){
                from = calCont.getAdjustedDay(currentActivity.d3d.date);
                to = from + calCont.milliSecondsInADay() * (Statics.NUM_OF_WEEKS_BEFORE_AND_AFTER *2 +1) * 7;
                long time = System.currentTimeMillis();
                calCont.update(from, to);
                System.out.println("Calendar update took: " + (System.currentTimeMillis() - time));
                updateActivities = true;
                clearedAndMoved = false;
                //Save old camera position to be able to determine how far to move models
                oldCameraPosition = cam.position.cpy().x;
                //Reset animateWeekSwitchTimer
                animateWeekSwitchTimer = 0;
                //Set direction
                if(currentWeek < findWeek(currentActivity.d3d.date)){
                    //move right
                    animateRight = false;
                }
                else {
                    animateRight = true;
                }
            }
            else {
                //Else, if then we can switch focus of camera directly
                //Focus and move camera to activity
                    finalCameraPosition = new Vector3(currentActivity.position.x
                            , currentActivity.position.y
                            , currentActivity.position.z - Statics.CAMERA_DISTANCE_FROM);
            }
            camIsMoving = true;
        }
        else {
            //Check if we hit the InsertEvent
            if(insertEventModelInstance != null && insertEvent.checkHit(screenX,screenY,cam)){
                System.out.println("HIT insert event");
                //Insert event
                Event e = new Event().setSummary("TEST UPLOAD")
                        .setLocation("800 Howard St., San Francisco, CA 94103")
                        .setDescription("TEST TEST TEST.");

                DateTime startDateTime = new DateTime(insertEvent.datePillar.d3d.date);
                EventDateTime start = new EventDateTime()
                        .setDateTime(startDateTime)
                        .setTimeZone("Europe/Stockholm");
                e.setStart(start);

                DateTime endDateTime = new DateTime(insertEvent.datePillar.d3d.date + 60000*60);
                EventDateTime end = new EventDateTime()
                        .setDateTime(endDateTime)
                        .setTimeZone("Europe/Stockholm");
                e.setEnd(end);

                calCont.InsertEvent(e);
            }
            //Check if we hit a date pillar
            result = getDatePillar(screenX,screenY);
            if(result != -1){
                //remove old one

                System.out.println("DatePillar result = " + result);
                //Place insertEvent model there.
                if(insertEventModelInstance == null){
                    insertEventModelInstance = new ModelInstance(insertEvent.getModel());
                    insertEvent.modelInstance = insertEventModelInstance;
                }
                insertEventModelInstance.transform.setTranslation(datePillars.get(result).getPosition());
                insertEvent.datePillar = datePillars.get(result);
                firstShadedLayer.add(insertEventModelInstance);
            }

        }
        return false;
    }
    ModelInstance insertEventModelInstance;

    float oldCameraPosition;
    int animateWeekSwitchTimer;
    boolean animateRight;

    private void updateActivities(){
        //Clearing
        if(!clearedAndMoved){
            if(animateWeekSwitchTimer < Statics.WEEK_SWITCH_ANIMATE_TIME){
                if(animateRight){
                    //move activities and date pillars right
                    moveActivitiesPillarsText(true);
                }
                else {
                    //move activities and date pillars left
                    moveActivitiesPillarsText(false);
                }
                animateWeekSwitchTimer ++;
            }
            else {
                long time = System.currentTimeMillis();
                datePillars.clear();
                firstShadedLayer.clear();
                createDatePillars(from, false);
                System.out.println("Pillars creation took : " + (System.currentTimeMillis() - time));
                clearedAndMoved = true;
            }
        }
        if(clearedAndMoved && downloadDone){
            //Clear OpenGL objects from memory
            System.out.println("Clearing acvities");
            long time2 = System.currentTimeMillis();
            Array<Disposable> dispose = new Array<Disposable>();
            dispose.addAll(activities);
            for(Disposable d: dispose){
                d.dispose();
            }
            activities.clear();
            activityLayer.clear();
            time2 = System.currentTimeMillis();
            createActivities(calCont.events, datePillars);
            System.out.println("Activities creation took : " + (System.currentTimeMillis() - time2));
            //Set current week
            //cam.update();

            //Find and replace current Activity
            currentActivity = findAndReplaceCurrentActivity(currentActivity, activities);
            updateActivities = false;
        }
    }

    private void moveActivitiesPillarsText(boolean right) {
        //Determine how far to move
        float xMove = 0;
        if(currentActivity != null){
            xMove = Math.abs(cam.position.x - currentActivity.position.x);
        }
        //Else it's a swipe, which means its one week wide
        else {
            xMove = Statics.WEEK_BACKPLATE_WIDTH;
        }
        xMove = xMove / Statics.WEEK_SWITCH_ANIMATE_TIME;
        xMove = (right) ? xMove : -xMove;
        //float xMove = (right) ? Statics.WEEK_ANIMATE_MOVE : -Statics.WEEK_ANIMATE_MOVE;
        for(ModelInstance mi :firstShadedLayer){
            Vector3 v3 = new Vector3();
            v3 = mi.transform.getTranslation(v3);
            mi.transform.setTranslation(v3.x + xMove
                    ,v3.y
                    ,v3.z);
        }
        for(ModelInstance mi: activityLayer){
            Vector3 v3 = new Vector3();
            v3 = mi.transform.getTranslation(v3);
            mi.transform.setTranslation(v3.x + xMove
                    ,v3.y
                    ,v3.z);
        }
    }

    private void smoothCameraMovement(Vector3 v) {
        //
        //CheckCameraPosition
        //float step = 0.2f;
        float steps = 60f;
        float error = 0.1f;

        System.out.println("UPDATING CAMERA POSITION!!!!!!!!");
        //move x
        if(isClose(v.x, finalCameraPosition.x)){
            System.out.println("X was close");
            v.x = finalCameraPosition.x;
        }
        else {
            if(v.x < finalCameraPosition.x ){
                v.x += finalCameraPosition.x - v.x / steps;
            }
            else {
                v.x -= v.x - finalCameraPosition.x / steps;
            }
        }
        //move y
        if(isClose(v.y, finalCameraPosition.y)){
            System.out.println("Y was close");
            v.y = finalCameraPosition.y;
        }
        else {
            if(v.y < finalCameraPosition.y ){
                v.y += finalCameraPosition.y - v.y / steps;
            }
            else {
                v.y -= v.y - finalCameraPosition.y / steps;
            }
        }
        //move z
        if(isClose(v.z, finalCameraPosition.z)){
            System.out.println("Z was close");
            v.z = finalCameraPosition.z;
        }
        else {
            if(v.z < finalCameraPosition.z ){
                v.z += finalCameraPosition.z - v.z / steps;
            }
            else {
                v.z -= v.z - finalCameraPosition.z / steps;
            }
        }

        cam.position.set(v.cpy());
        //cam.position.set(finalCameraPosition.cpy());
    }

    private boolean isClose(float x, float x1) {
		return isBetween(x,x1,Statics.CAMERA_IS_CLOSE_DISTANCE);
	}

	private boolean isBetween(float current, float destination, float error) {
		System.out.println("current: " + current + " dest: " + destination);
		if(current > destination ){
			if(error <= current - destination){
				System.out.println("error: " + error + " <= c -d : " + (current - destination));
				return true;
			}
			else {
				return false;
			}
		}
		else {
			if(error <= destination - current){
				System.out.println("error: " + error + " <=  d -c : " + (destination - current));

				return true;
			}
			else {
				return false;
			}
		}
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
		disposables.clear();
		disposables = null;
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

	/*
	* Rotates camera so it's level with horizon
	 */
	private void fixPitch(Vector3 pos) {
		//Rotate x
		while(cam.up.x > 0.05f || cam.up.x < - 0.05f){
			cam.rotateAround(pos, new Vector3(0, 1f, 0), 3f);
		}
		while(cam.up.y < 0.95f ){
			cam.rotateAround(pos,new Vector3(1f,0,0),3f);
		}
	}

	boolean clearedAndMoved;



    private Activity findAndReplaceCurrentActivity(Activity currentActivity, Array<Activity> activities) {
        for(Activity a: activities){
            if(currentActivity.d3d.date == a.d3d.date){
                return a;
            }
        }
        return null;
    }

    private void getCameraFocusOnDate(long date) {
		Date3d d = new Date3d(date);
		//Vector3 camPos = new Vector3(d.matchXValue(datePillars),finalPosition.y,finalPosition.z);
        Vector3 camPos = new Vector3(d.matchXValue(datePillars),finalCameraPosition.y,finalCameraPosition.z);
//        fixPitch(camPos.cpy());
//		cam.update();
		finalCameraPosition = camPos;
//		cam.position.set(camPos);
//		Vector3 camLookAt = new Vector3(camPos.x, finalPosition.y,0);
//		cam.lookAt(camLookAt);
//		camController.target = camLookAt.cpy();
//		cam.update();

//		Vector3 camLookAt = new Vector3(finalCameraPosition.x, finalPosition.y,0);
//		cam.lookAt(camLookAt);
//		camController.target = camLookAt.cpy();
//		cam.update();
	}

	public int getActivity (float screenX, float screenY) {
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

    public int getDatePillar (float screenX, float screenY) {
        position = new Vector3();
        Ray ray = cam.getPickRay(screenX, screenY);
        int result = -1;
        float distance = -1;
        for (int i = 0; i < datePillars.size; ++i) {
            DatePillar d = datePillars.get(i);
            d.getModelInstance().transform.getTranslation(position);
            position.add(d.center);
            float dist2 = ray.origin.dst2(position);
            if (distance >= 0f && dist2 > distance) continue;
            if (Intersector.intersectRayBoundsFast(ray,position,d.dimensions)){
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
