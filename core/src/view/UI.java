package view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by Datacom on 2015-12-03.
 */
public class UI implements Disposable{

    public SpriteBatch uiBatch;
    public Skin skin;
    private boolean optionsMenuVisible;

    private Stage optionsStage;
    private InputMultiplexer inputMultiplexer;
    private MainView game;
    Array<Disposable> disposables;

    public Stage getOptionsStage() {
        return optionsStage;
    }

    public Stage getUiStage() {
        return uiStage;
    }

    public Stage uiStage;

    private Label labelMessage;

    float density;

    public void createUI(InputMultiplexer inputMultiplexer, final MainView game) {
        disposables = new Array<Disposable>();
        this.game = game;
        this.inputMultiplexer = inputMultiplexer;
        density = Gdx.graphics.getDensity();
        uiBatch = new SpriteBatch();
        disposables.add(uiBatch);
        uiStage = new Stage();
        disposables.add(uiStage);
        //create uiStage units
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        disposables.add(skin);
        Table uiTable = new Table();
        //optionsTable.setDebug(true);
        uiTable.setFillParent(true);
        uiTable.align(Align.topLeft);

        uiStage.addActor(uiTable);

        final TextButton optionsButton = new TextButton("Menu", skin);
        optionsButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float screenX, float screenY, int pointer, int button) {
                toggleOptions();
                return false;
            }
        });
        optionsButton.getLabel().setFontScale(2.0f * density, 2.0f * density);
        uiTable.add(optionsButton).minWidth(100f * density).minHeight(100f * density);
        labelMessage = new Label("                                          ", skin);
        Table turnTable = new Table();
        turnTable.setDebug(true);
        turnTable.setFillParent(true);
        //turnTable.
        turnTable.align(Align.top);
        uiStage.addActor(turnTable);
        turnTable.add(labelMessage).align(Align.topLeft).minWidth(100f * density);
        labelMessage.setFontScale(2.0f * density, 2.0f * density);

        /*
         * Create Options buttons
         */
        optionsStage = new Stage();
        disposables.add(optionsStage);
        Table optionsTable = new Table();
        optionsTable.setFillParent(true);
        optionsStage.addActor(optionsTable);

        //Create newgameSpot1 button
        final TextButton newGameSpot1 = new TextButton("New MainView", skin);
        optionsTable.add(newGameSpot1).minWidth(100f * density).minHeight(50f * density);
        newGameSpot1.getLabel().setFontScale(2.0f * density, 2.0f * density);
//        newGameSpot1.addListener(new InputListener() {
//            @Override
//            public boolean touchDown(InputEvent event, float screenX, float screenY, int pointer, int button) {
//                game.startNewGame(0);
//                toggleOptions();
//                return false;
//            }
//        });
        //create load gameSpot 1 button
        final TextButton loadGameSpot1 = new TextButton("Load MainView spot 1", skin);
        optionsTable.add(loadGameSpot1).minWidth(100f * density).minHeight(50f * density);
        loadGameSpot1.getLabel().setFontScale(2.0f * density, 2.0f * density);
//        loadGameSpot1.addListener(new InputListener() {
//            @Override
//            public boolean touchDown(InputEvent event, float screenX, float screenY, int pointer, int button) {
//                game.loadNewGame(0);
//                toggleOptions();
//                return false;
//            }
//        });

        optionsTable.row();

        //Create gameSpot button 2
        final TextButton newGameSpot2 = new TextButton("New MainView", skin);
        optionsTable.add(newGameSpot2).minWidth(100f * density).minHeight(50f * density);
        newGameSpot2.getLabel().setFontScale(2.0f * density, 2.0f * density);
//        newGameSpot2.addListener(new InputListener() {
//            @Override
//            public boolean touchDown(InputEvent event, float screenX, float screenY, int pointer, int button) {
//                game.startNewGame(1);
//                ;
//                toggleOptions();
//                return false;
//            }
//        });
        //create load gameSpot 1 button
        final TextButton loadGameSpot2 = new TextButton("Load MainView spot 2", skin);
        optionsTable.add(loadGameSpot2).minWidth(100f * density).minHeight(50f * density);
        loadGameSpot2.getLabel().setFontScale(2.0f * density, 2.0f * density);
//        loadGameSpot2.addListener(new InputListener() {
//            @Override
//            public boolean touchDown(InputEvent event, float screenX, float screenY, int pointer, int button) {
//                game.loadNewGame(1);
//                toggleOptions();
//                return false;
//            }
//        });
        optionsTable.row();

        //Create gameSpot button 3
        final TextButton newGameSpot3 = new TextButton("New MainView", skin);
        optionsTable.add(newGameSpot3).minWidth(100f * density).minHeight(50f * density);
        newGameSpot3.getLabel().setFontScale(2.0f * density, 2.0f * density);
//        newGameSpot3.addListener(new InputListener() {
//            @Override
//            public boolean touchDown(InputEvent event, float screenX, float screenY, int pointer, int button) {
//                game.startNewGame(2);
//                toggleOptions();
//                return false;
//            }
//        });
        //create load gameSpot 3 button
        final TextButton loadGameSpot3 = new TextButton("Load MainView spot 3", skin);
        optionsTable.add(loadGameSpot3).minWidth(100f * density).minHeight(50f * density);
        loadGameSpot3.getLabel().setFontScale(2.0f * density, 2.0f * density);
//        loadGameSpot3.addListener(new InputListener() {
//            @Override
//            public boolean touchDown(InputEvent event, float screenX, float screenY, int pointer, int button) {
//                game.loadNewGame(2);
//                toggleOptions();
//                return false;
//            }
//        });

        optionsTable.row();

        final TextButton toggleTheme = new TextButton("Switch theme (1)", skin);
        optionsTable.add(toggleTheme).minWidth(100f * density).minHeight(50f * density);
        toggleTheme.getLabel().setFontScale(2.0f * density, 2.0f * density);
        toggleTheme.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float screenX, float screenY, int pointer, int button) {
                game.toggleTheme();
                return false;
            }
        });


    }

    private void toggleOptions() {

        if (optionsMenuVisible) {
            inputMultiplexer.removeProcessor(optionsStage);
        } else {
            inputMultiplexer.addProcessor(0, optionsStage);
        }
        optionsMenuVisible = !optionsMenuVisible;
    }


    public void drawUI() {
        //updateTurnlabel();
        uiBatch.begin();
        uiStage.draw();
        if (optionsMenuVisible) {
            optionsStage.draw();
        }
        uiBatch.end();
    }

//    private void updateTurnlabel() {
//
//        if (game.morris == null) {
//            labelMessage.setText("");
//        } else if (game.morris.gameOver) {
//            labelMessage.setText("You win!");
//        } else {
//            String s = (game.morris.getTurn() == SavedState.RED_MOVES) ? "Red turn" : "Blue turn";
//            s += (game.morris.turnType == SavedState.TurnType.REMOVE_BALL) ? " to remove" : "";
//            labelMessage.setText(s);
//        }
//
//    }

    public void dispose() {
        for(Disposable d: disposables){
            d.dispose();
        }

    }

    public void resize() {
        uiStage.getViewport().update(1024, 576, true);
    }

}
