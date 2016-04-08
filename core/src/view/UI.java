package view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import data.Statics;

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
    private Label detailsLabel;

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
        uiTable.add(optionsButton).minWidth(100f * density)
                .minHeight(100f * density).align(Align.left);

        /*
         * Create Options buttons
         */
        optionsStage = new Stage();
        disposables.add(optionsStage);
        Table optionsTable = new Table();
        optionsTable.setFillParent(true);
        optionsStage.addActor(optionsTable);

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
        uiTable.row();
        Pixmap gray = new Pixmap(1, 1, Pixmap.Format.RGB565);
        gray.setColor(Statics.DETAILS_BACKGROUND_COLOR);
        gray.fill();
        //Create details label
        Table detailsTable = new Table();
        Texture grayText = new Texture( gray);
        //grayText.
        detailsTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(gray))));
        uiTable.add(detailsTable);
        detailsLabel = new Label("",skin);

        detailsTable.add(detailsLabel).align(Align.left);
//        detailsLabel.setText("Details: \n " +
//                "HEJ HEJ HEJ \n" +
//                "HEJ HEJ HEJ");
        //Create time Report

        //Create edit


    }

    public void updateDetails(String text){
        detailsLabel.setText(text);
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
//        Gdx.gl.glEnable(GL20.GL_BLEND);
//        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        //updateTurnlabel();
        uiBatch.enableBlending();
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
