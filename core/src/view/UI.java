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
    public boolean detailsVisible;
    private Stage optionsStage;
    public Stage detailsStage;
    private InputMultiplexer inputMultiplexer;
    private MainView main;
    Array<Disposable> disposables;
    private Label detailsLabel;
    //Label reportDialogDetailsLabel;

    private boolean reportDialogVisible;
    public Stage reportDialogStage;
    private Label reportDialogDetailsContents;
    private Label reportDialogDescriptionContents;
    private Label reportDialogStatusContents;
    private Label reportDialogFromContents;
    private Label reportDialogToContents;
    private Label reportDialogDurationContents;
    private TextButton reportDialogChangeButton;

    public Stage getOptionsStage() {
        return optionsStage;
    }

    public Stage getUiStage() {
        return uiStage;
    }

    public Stage uiStage;

    private Label labelMessage;

    float density;

    public void createUI(InputMultiplexer inputMultiplexer, final MainView m) {
        disposables = new Array<Disposable>();
        this.main = m;
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
        optionsButton.getLabel().setFontScale(1.0f * density, 1.0f * density);
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
        toggleTheme.getLabel().setFontScale(1.0f * density, 1.0f * density);
        toggleTheme.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float screenX, float screenY, int pointer, int button) {
                main.toggleTheme();
                return false;
            }
        });

        final TextButton toggleDownload = new TextButton(Statics.SwitchToDownloadAll,skin);
        optionsTable.add(toggleDownload).minWidth(100f * density).minHeight(50f * density);
        toggleDownload.getLabel().setFontScale(1.0f * density, 1.0f * density);
        toggleDownload.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float screenX, float screenY, int pointer, int button) {
                Statics.downloadPrimary = !Statics.downloadPrimary;
                if (!Statics.downloadPrimary) {
                    toggleDownload.setText(Statics.switchToDownloadPrimary);
                } else {
                    toggleDownload.setText(Statics.SwitchToDownloadAll);
                }
                return false;
            }
        });

        uiTable.row();
        Pixmap detailsBackground = new Pixmap(1, 1, Pixmap.Format.RGB565);
        detailsBackground.setColor(Statics.DETAILS_BACKGROUND_COLOR);
        detailsBackground.fill();
        detailsVisible = false;

        //Create details label
        Table detailsTable = new Table();
        //detailsTable.setDebug(true);
        //Set background
        detailsTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(detailsBackground))));
        detailsTable.align(Align.bottomLeft);
        detailsStage.addActor(detailsTable);
        detailsLabel = new Label("",skin);
        detailsTable.add(detailsLabel).align(Align.left);
        detailsTable.row();
        final TextButton reportButton = new TextButton("Report", skin);
        detailsTable.add(reportButton).minWidth(100f * density).minHeight(50f * density);
        reportButton.getLabel().setFontScale(1.0f * density, 1.0f * density);
        reportButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float screenX, float screenY, int pointer, int button) {
                reportDialogVisible = true;
                updateReportDialogText();
                System.out.println("Report Clicked");
                return false;
            }
        });

        /*
        Report Dialog
         */
        reportDialogVisible = false;

        reportDialogStage = new Stage();
        disposables.add(reportDialogStage);

        //reportDialogStage.setDebugAll(true);
        Table reportDialogTable = new Table();
        //reportDialogTable.setDebug(true);
        reportDialogTable.align(Align.center);
        reportDialogTable.setFillParent(true);
        //reportDialogTable.debug();

        reportDialogStage.addActor(reportDialogTable);
        //Set background
        reportDialogTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(detailsBackground))));
        Label reportDialogLabel = new Label("Report\n", skin);
        reportDialogTable.add(reportDialogLabel);
        reportDialogTable.row();

        //Details(getSummary() ) text
        Label reportDialogDetailsLabel = new Label("Details: \n",skin);
        reportDialogTable.add(reportDialogDetailsLabel).align(Align.topLeft);
        reportDialogDetailsContents = new Label("",skin);
        reportDialogTable.add(reportDialogDetailsContents).align(Align.topLeft);;
        reportDialogTable.row();

        //Description
        Label reportDialogDescriptionLabel = new Label("Description: \n",skin);
        reportDialogTable.add(reportDialogDescriptionLabel).align(Align.topLeft);;
        reportDialogDescriptionContents = new Label("", skin);
        reportDialogTable.add(reportDialogDescriptionContents).align(Align.topLeft);;
        reportDialogTable.row();

        //Status
        Label reportDialogStatusLabel = new Label("Status: \n",skin);
        reportDialogTable.add(reportDialogStatusLabel).align(Align.topLeft);
        reportDialogStatusContents = new Label("",skin);
        reportDialogTable.add(reportDialogStatusContents).align(Align.topLeft);;
        reportDialogTable.row();

        //From
        Label reportDialogFromLabel = new Label("From: \n",skin);
        reportDialogTable.add(reportDialogFromLabel).align(Align.topLeft);
        reportDialogFromContents = new Label("",skin);
        reportDialogTable.add(reportDialogFromContents).align(Align.topLeft);;
        reportDialogTable.row();

        //To
        Label reportDialogToLabel = new Label("To: \n",skin);
        reportDialogTable.add(reportDialogToLabel).align(Align.topLeft);
        reportDialogToContents = new Label("",skin);
        reportDialogTable.add(reportDialogToContents).align(Align.topLeft);;
        reportDialogTable.row();

        //Duration
        Label reportDialogDurationLabel = new Label("Duration: \n",skin);
        reportDialogTable.add(reportDialogDurationLabel).align(Align.topLeft);
//        reportDialogDurationContents = new Label("",skin);
//        reportDialogTable.add(reportDialogDurationContents).align(Align.topLeft);;
        //Duration button
        reportDialogChangeButton = new TextButton("", skin);
        reportDialogTable.add(reportDialogChangeButton).align(Align.topLeft);
        reportDialogChangeButton.getLabel().setFontScale(1.0f * density, 1.0f * density);
        reportDialogChangeButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float screenX, float screenY, int pointer, int button) {
                System.out.println("Change Clicked");
                return false;
            }
        });
        reportDialogTable.row();

        /*
        Buttons
         */

        //Report button
        TextButton reportReportButton = new TextButton("Report", skin);
        reportReportButton.getLabel().setFontScale(1.0f * density, 1.0f * density);
        reportReportButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float screenX, float screenY, int pointer, int button) {
                reportDialogVisible = false;
                System.out.println("Ok Clicked");
                return false;
            }
        });
        reportDialogTable.add(reportReportButton).minWidth(100f * density).minHeight(50f * density);
        //Cancel button
        TextButton reportCancelButton = new TextButton("Cancel", skin);
        reportCancelButton.getLabel().setFontScale(1.0f * density, 1.0f * density);
        reportCancelButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float screenX, float screenY, int pointer, int button) {
                reportDialogVisible = false;
                System.out.println("Cancel Clicked");
                return false;
            }
        });
        reportDialogTable.add(reportCancelButton).minWidth(100f * density).minHeight(50f * density);
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

    private void updateReportDialogText(){
        if(main.currentActivity != null){
            //Get details(Summary)
            reportDialogDetailsContents.setText(main.currentActivity.event.getSummary()
                    + "\n" + main.currentActivity.d3d.getDateName());
            //Get Description
            reportDialogDescriptionContents.setText(main.currentActivity.event.getDescription());
            //Get Status
            reportDialogStatusContents.setText(main.currentActivity.event.getStatus());
            //Get From
            reportDialogFromContents.setText((main.currentActivity.d3d.getStartTime()));
            //Get To
            reportDialogToContents.setText((main.currentActivity.d3d.getStopTime()));
            //Get Duration
            reportDialogChangeButton.setText((main.currentActivity.d3d.getDuration()));
        }
        else {
            System.out.println("Current activity was NULL!!!");
        }
    }


    public void drawUI() {
//        Gdx.gl.glEnable(GL20.GL_BLEND);
//        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        uiBatch.enableBlending();
        uiBatch.begin();
        uiStage.draw();
        if(detailsVisible){
            detailsStage.draw();
        }
        if (optionsMenuVisible) {
            optionsStage.draw();
        }
        if(reportDialogVisible){
            reportDialogStage.draw();
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
