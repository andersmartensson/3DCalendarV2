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
    private Stage detailsStage;
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
        detailsStage = new Stage();
        disposables.add(detailsStage);
        //create uiStage units
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        disposables.add(skin);
        Table uiTable = new Table();
        //optionsTable.setDebug(true);
        //uiTable.setDebug(true);
        uiTable.setFillParent(true);
        uiTable.align(Align.topLeft);
        //uiTable.setHeight(Gdx.graphics.getHeight());

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

        final TextButton toggleTheme = new TextButton("Switch theme", skin);
        optionsTable.add(toggleTheme).minWidth(100f * density).minHeight(50f * density);
        toggleTheme.getLabel().setFontScale(2.0f * density, 2.0f * density);
        toggleTheme.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float screenX, float screenY, int pointer, int button) {
                game.toggleTheme();
                return false;
            }
        });

        final TextButton toggleDownload = new TextButton(Statics.SwitchToDownloadAll,skin);
        optionsTable.add(toggleDownload).minWidth(100f * density).minHeight(50f * density);
        toggleDownload.getLabel().setFontScale(2.0f * density, 2.0f * density);
        toggleDownload.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float screenX, float screenY, int pointer, int button) {
                Statics.downloadPrimary = !Statics.downloadPrimary;
                if(!Statics.downloadPrimary){
                    toggleDownload.setText(Statics.switchToDownloadPrimary);
                }
                else {
                    toggleDownload.setText(Statics.SwitchToDownloadAll);
                }
                return false;
            }
        });

        uiTable.row();
        Pixmap gray = new Pixmap(1, 1, Pixmap.Format.RGB565);
        gray.setColor(Statics.DETAILS_BACKGROUND_COLOR);
        gray.fill();
        //Create details label
        Table detailsTable = new Table();
        //Texture grayText = new Texture( gray);
        //grayText.
        detailsTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(gray))));
        //detailsTable.setFillParent(true);
        detailsTable.align(Align.bottomLeft);
        detailsStage.addActor(detailsTable);
        //uiTable.add(detailsTable).align(Align.bottomLeft);
        detailsLabel = new Label("",skin);

        detailsTable.add(detailsLabel).align(Align.left);
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
        uiBatch.enableBlending();
        uiBatch.begin();
        uiStage.draw();
        detailsStage.draw();
        if (optionsMenuVisible) {
            optionsStage.draw();
        }
        uiBatch.end();
    }

    public void dispose() {
        for(Disposable d: disposables){
            d.dispose();
        }

    }

    public void resize() {
        uiStage.getViewport().update(1024, 576, true);
    }

}
