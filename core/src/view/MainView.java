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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;

import model.GFX.Ball;
import model.GFX.Board;
import model.GFX.Ground;
import model.GFX.MarkerBall;
import model.GFX.Skybox;
import model.GFX.Sun;
import model.NineMenMorrisRules;
import operations.SaveManager;


public class MainView extends InputAdapter implements ApplicationListener {

	//Render stuff
	Environment environment;
	PerspectiveCamera cam;
	CameraInputController camController;
	ModelBatch modelBatch;
	//ShapeRenderer shapeRender;
	TextureRegion fboRegion;
	private DirectionalLight dirLight;

	//Ui
	UI ui;
	//Render Layers
	private Array<ModelInstance> firstShadedLayer;
	private Array<ModelInstance> firstNonShadedLayer;
	private Array<ModelInstance> secondShadedLayer;
	private Array<ModelInstance> markerBallsLayer;
	private Array<ModelInstance> ballsLayer;

	int screenWidth;
	int screenHeigth;
	private ShaderProgram bgShader;
	private float gameTime;
	private ModelInstance waterInstance;
	private Array<Vector3> boardLayout;
	private Array<MarkerBall> markerBalls;
	private Vector3 position;
	private Board board;
	private Array<Ball> balls;
	private int selectedBallNumber = -1;



	public NineMenMorrisRules morris;
	private Ground ground;
	private SaveManager saveManager;
	public int theme;
	private Skybox skybox;
	private Sun sun;
	@Override
	public void create () {
		theme = Gdx.app.getPreferences("My Preferences").getInteger("theme", 0);
		//Create savem anager
		saveManager = new SaveManager();
		//Create morris game
		//morris = new NineMenMorrisRules();

		//markerBalls = new Array<MarkerBall>();
		//balls = new Array<Ball>();
		gameTime = 1.0f;
		screenWidth = Gdx.graphics.getWidth();
		screenHeigth = Gdx.graphics.getHeight();

//create render arrays:
		firstShadedLayer = new Array<ModelInstance>();
		firstNonShadedLayer = new Array<ModelInstance>();
		secondShadedLayer = new Array<ModelInstance>();
		//markerBallsLayer = new Array<ModelInstance>();
		//ballsLayer = new Array<ModelInstance>();
//create environment
		environment = new Environment();

//Create camera and controller
		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(10f, 10f, 10f);
		cam.viewportHeight = 720f;
		cam.viewportWidth = 1280f;
		cam.lookAt(0,0,0);
		cam.far = 550f;
		cam.near = 1f;
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
		//create board
		//board = new Board(Color.BROWN);
		//boardLayout = new BoardLayout().getBoardPositions();
		//secondShadedLayer.add(new ModelInstance(board.getModel()));
		//secondShadedLayer.get(secondShadedLayer.size-1).transform.setTranslation(board.getPosition());

		//Create sun - Not visible but is going to be needed to get a position for the lensflare
		sun = new Sun();
		//Create model and modelInstance and add it to render array
		sun.setModelInstance(new ModelInstance(sun.getModel()));
		sun.setPosition(new Vector3(115.0f, 150.0f, 200.0f));
		sun.modelInstance.transform.setTranslation(sun.getPosition());
		//firstNonShadedLayer.add(sun.getModelInstance());
		//Create skybox sphere
		skybox = new Skybox(cam.far);
		skybox.getModel();

		//Create ground
		ground = new Ground();
		ground.setModelInstance(new ModelInstance(ground.getModel()));
		ground.setPosition(new Vector3(0, -15.0f, 0.0f));
		ground.getModelInstance().transform.setTranslation(ground.getPosition());
		//Create markerBalls
		//reateMarkerBalls();

//Create model batch and texture region
		modelBatch = new ModelBatch();
		fboRegion = new TextureRegion();
		setLight();
		createWaterShader();
		updateTheme();
	}

	public synchronized void createBallsFromMorrisArray (){
		Ball ball;
		ModelInstance mi;
		Vector3 vRed = new Vector3(-board.getScale()/2f - 2f,1.1f,-board.getScale()/2f);
		Vector3 vBlue = new Vector3(board.getScale()/2f + 2f,1.1f,-board.getScale()/2f);
		//First check how many side balls
			//first create blue side balls
		disposeRenderLayer(ballsLayer);
		balls.clear();

		for(int i=0;i<morris.getBluemarker();i++){
			ball = new Ball(Color.BLUE);
			ball.setPosition(vBlue.cpy());
			vBlue.z += 1f;
			mi = new ModelInstance(ball.getModel());
			mi.transform.setTranslation(ball.getPosition());
			ball.setModelInstance(mi);
			ballsLayer.add(ball.getModelInstance());
			balls.add(ball);
		}
			//Create red side balls
		for(int i=0;i<morris.getRedmarker();i++){
			ball = new Ball(Color.RED);
			ball.setPosition(vRed.cpy());
			vRed.z += 1f;
			mi = new ModelInstance(ball.getModel());
			mi.transform.setTranslation(ball.getPosition());
			ball.setModelInstance(mi);
			ballsLayer.add(ball.getModelInstance());
			balls.add(ball);
		}
		//Create balls on board
		for(int i=1;i<morris.gameplan.length;i++){
			if(morris.gameplan[i]==NineMenMorrisRules.BLUE_MARKER){
				//create blue ball
				ball = new Ball(Color.BLUE);
				ball.setPosition(markerBalls.get(i).getPosition().cpy());
				ball.boardPositionNumber = i;
				mi = new ModelInstance(ball.getModel());
				mi.transform.setTranslation(ball.getPosition());
				ball.setModelInstance(mi);
				ballsLayer.add(ball.getModelInstance());
				balls.add(ball);
			}
			else if(morris.gameplan[i]==NineMenMorrisRules.RED_MARKER){
				//create red ball
				ball = new Ball(Color.RED);
				ball.setPosition(markerBalls.get(i).getPosition().cpy());
				ball.boardPositionNumber = i;
				mi = new ModelInstance(ball.getModel());
				mi.transform.setTranslation(ball.getPosition());
				ball.setModelInstance(mi);
				ballsLayer.add(ball.getModelInstance());
				balls.add(ball);
			}
		}
	}

	private void createMarkerBalls() {
		MarkerBall ball;
		ModelInstance mi;
		//For now create 25 markerBalls
		for(int i=0;i<25;i++){
			ball = new MarkerBall();
			ball.setPosition(boardLayout.get(i));
			//Create model and modelInstnace and add it to render array
			mi = new ModelInstance(ball.getModel());
			mi.transform.setTranslation(ball.getPosition());
			ball.setModelInstance(mi);
			markerBallsLayer.add(ball.getModelInstance());
			markerBalls.add(ball);
		}

	}

	private void createWaterShader(){
		ShaderProgram.pedantic = false;
		final String VERT = Gdx.files.internal("shaders/sea_vert.glsl").readString();
		final String FRAG = Gdx.files.internal("shaders/sea_frag.glsl").readString();
		bgShader = new ShaderProgram(VERT, FRAG);

		//Set values
		bgShader.begin();
		bgShader.setUniformf("resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		bgShader.setUniformf("time", gameTime);
		bgShader.setUniformf("mouse",new Vector2(Gdx.input.getX(), Gdx.input.getY()));
		bgShader.end();
	}

	private void updateWaterShader(){
		bgShader.begin();
		bgShader.setUniformf("time", gameTime);
		bgShader.setUniformf("mouse", new Vector2(Gdx.input.getX(), Gdx.input.getY()));
		bgShader.end();
	}

	@Override
	public void render (){
		//updateBalls();
		gameTime += 1 %1000000;

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		modelBatch.begin(cam);
		modelBatch.render(firstNonShadedLayer);
		modelBatch.render(firstShadedLayer, environment);
		modelBatch.render(skybox.getModelInstance());
		modelBatch.render(ground.getModelInstance(),environment);
		modelBatch.render(secondShadedLayer, environment);
		//modelBatch.render(markerBallsLayer, environment);
		//modelBatch.render(ballsLayer, environment);
		modelBatch.end();

		ui.drawUI();
	}

	private void updateBalls() {
		//Check balls array against morris balls array
		for(int i=0;i<balls.size;i++){
			if(balls.get(i).boardPositionNumber >= 1){
				if(morris.gameplan[balls.get(i).boardPositionNumber]==0){
					//set postion faaaar away
					balls.get(i).setPosition(new Vector3(100f,100f,100f));
					balls.get(i).boardPositionNumber = -1;
				}
			}
		}
		//check morris array and add any missing balls.

		//UpdatePosition of all markerBalls
		for(Ball b : balls){
			b.updatePosition();
		}
	}

	private boolean checkIfInBallArray(int postion) {

		for(int i=0;i<balls.size;i++){
			if(balls.get(i).boardPositionNumber == postion ){
				return true;
			}
		}
		return false;
	}

	@Override
	public void pause() {
		saveManager.saveThisGame(morris);
		saveManager.writeSaves();
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
	 * Disposes of all models in renderlayers
	 */
	public void disposeAndClearRenderLayers() {
		disposeRenderLayer(firstShadedLayer);
		disposeRenderLayer(firstNonShadedLayer);
		disposeRenderLayer(firstNonShadedLayer);
		disposeRenderLayer(ballsLayer);
		disposeRenderLayer(markerBallsLayer);
	}

	protected boolean isVisible (final Camera cam, final Ball b) {
		b.getModelInstance().transform.getTranslation(position);
		position.add(b.center);
		return cam.frustum.sphereInFrustum(position, b.radius);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
//		if(morris.turnType == NineMenMorrisRules.TurnType.TAKE_TURNS){
//			System.out.println("Take turns!");
//			takeTurns(screenX, screenY);
//		}
//		else{
//			System.out.println("REMOVE TURN!!!");
//			removeTurn(screenX,screenY);
//		}
		//System.out.println("TURN: " + morris.turn + " \n");
		return false;
	}

	private void removeTurn(int screenX, int screenY) {
		//Get number
		int removeBallNumber = getBallObject(screenX, screenY);
		//remove
		if(removeBallNumber != -1
				&& morris.remove(balls.get(removeBallNumber).boardPositionNumber,
				balls.get(removeBallNumber).getMorrisColor())){
			//successfully removed ball
			System.out.println("REMOVED!!!");
			if(morris.getTurn()== NineMenMorrisRules.RED_MOVES){
				morris.win(NineMenMorrisRules.RED_MARKER);
			}
			else{
				morris.win(NineMenMorrisRules.BLUE_MARKER);
			}


			morris.switchTurn();
			morris.turnType = NineMenMorrisRules.TurnType.TAKE_TURNS;
			//Check win

		}
		else {
			//Do Nothing
		}
	}

	private void takeTurns(int screenX, int screenY) {
		Vector3 v = new Vector3();
		if(selectedBallNumber == -1){
			selectedBallNumber = getBallObject(screenX, screenY);
			if(selectedBallNumber >= 0){
				System.out.println("Selected a ball: " + selectedBallNumber);
				v = balls.get(selectedBallNumber).getPosition();
				v.y += 1.0f;
				balls.get(selectedBallNumber).setPosition(v);
			}
		}
		else {
			int temp = getMarkerObject(screenX, screenY);
			if(temp >= 0){
				//System.out.println("Selected a marker: " + temp);
				//Log.i("Selection", "Selected: " + selecting);
				//System.out.println("selected ball board number: " + balls.get(selectedBallNumber).boardPositionNumber);
				boolean legal = morris.legalMove(temp,
						balls.get(selectedBallNumber).boardPositionNumber, getBallColor(selectedBallNumber));
				if(legal){
					markerBalls.get(temp).getModelInstance().transform.getTranslation(v);
					v.y +=0.1f;
					balls.get(selectedBallNumber).setPosition(v);
					balls.get(selectedBallNumber).boardPositionNumber = temp;
					selectedBallNumber = -1;
					//check if three in a row
					if(morris.isMill(temp)){
						System.out.println("IS MILL!!!!!!");
						//set turn back to same
						morris.switchTurn();
						//remove a ball
						morris.turnType = NineMenMorrisRules.TurnType.REMOVE_BALL;
					}
				}else{
					v = balls.get(selectedBallNumber).getPosition();
					v.y -= 1.0f;
					balls.get(selectedBallNumber).setPosition(v);
					selectedBallNumber = -1;
				}
				//morris.printArray();
			}
		}
	}

	private int getBallColor(int ballPos){
		Color c = balls.get(ballPos).getColor();
		if(c == Color.RED)  return NineMenMorrisRules.RED_MOVES;
		else 				return NineMenMorrisRules.BLUE_MOVES;
	}
	public int getBallObject (int screenX, int screenY) {
		position = new Vector3();
		Ray ray = cam.getPickRay(screenX, screenY);
		int result = -1;
		float distance = -1;
		for (int i = 0; i < balls.size; ++i) {
			final Ball ball = balls.get(i);
			ball.getModelInstance().transform.getTranslation(position);
			position.add(ball.center);
			float dist2 = ray.origin.dst2(position);
			if (distance >= 0f && dist2 > distance) continue;
			if (Intersector.intersectRaySphere(ray, position, ball.radius, null)) {
				result = i;
				distance = dist2;
			}
		}
		return result;
	}

	public int getMarkerObject (int screenX, int screenY) {
		position = new Vector3();
		Ray ray = cam.getPickRay(screenX, screenY);
		int result = -1;
		float distance = -1;
		for (int i = 0; i < markerBalls.size; ++i) {
			final MarkerBall ball = markerBalls.get(i);
			ball.getModelInstance().transform.getTranslation(position);
			position.add(ball.center);
			float dist2 = ray.origin.dst2(position);
			if (distance >= 0f && dist2 > distance) continue;
			if (Intersector.intersectRaySphere(ray, position, ball.radius, null)) {
				result = i;
				distance = dist2;
			}
		}
		return result;
	}

	public void startNewGame(int slot) {
		saveManager.saveThisGame(morris);
		morris = new NineMenMorrisRules();
		saveManager.setSlot(slot);

		createBallsFromMorrisArray();

	}
	public void loadNewGame(int i) {
		saveManager.saveThisGame(morris);
		saveManager.setSlot(i);
		NineMenMorrisRules n = saveManager.getGame(i);
		morris = (n != null) ? n : new NineMenMorrisRules();
		createBallsFromMorrisArray();

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
