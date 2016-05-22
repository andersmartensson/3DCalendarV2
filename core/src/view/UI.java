package view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
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
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import data.Statics;
import model.Activity;
import model.Date3d;

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
    private Label reportDialogSummaryContents;
    private Label reportDialogDescriptionContents;
    private Label reportDialogStatusContents;
    private Label reportDialogFromContents;
    private Label reportDialogToContents;
    private Label reportDialogDateContents;

    private Label reportDialogDurationContents;
    private TextButton reportDialogChangeButton;
    private Table detailsTable;
    private TextButton undoButton;

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
        if(Statics.isAndroid){
            density *= 0.4;
        }
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

        final TextButton optionsButton = new TextButton(Statics.UI_MENU_NAME, skin);
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
        uiTable.row();
        //Update button
        TextButton updateButton = new TextButton(Statics.UI_UPDATE_NAME, skin);
        updateButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float screenX, float screenY, int pointer, int button) {
                updateCalender();
                return false;
            }
        });
        updateButton.getLabel().setFontScale(1.0f * density, 1.0f * density);
        uiTable.add(updateButton).minWidth(100f * density)
                .minHeight(100f * density).align(Align.left);
        uiTable.row();

        //Undo button
        undoButton = new TextButton(" x ", skin);
        undoButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float screenX, float screenY, int pointer, int button) {
                if(main.deletedActivity != null){
                    undoLastDelete();
                }
                return false;
            }
        });
        undoButton.getLabel().setFontScale(1.0f * density, 1.0f * density);
        uiTable.add(undoButton).minWidth(100f * density)
                .minHeight(100f * density).align(Align.left);
        /*
         * Create Options buttons
         */
        optionsStage = new Stage();
        disposables.add(optionsStage);
        Table optionsTable = new Table();
        optionsTable.setFillParent(true);
        optionsStage.addActor(optionsTable);

        final TextButton toggleTheme = new TextButton(Statics.UI_SWITCH_THEME_NAME, skin);
        optionsTable.add(toggleTheme).minWidth(100f * density).minHeight(50f * density);
        toggleTheme.getLabel().setFontScale(1.0f * density, 1.0f * density);
        toggleTheme.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float screenX, float screenY, int pointer, int button) {
                main.toggleTheme();
                return false;
            }
        });

        final TextButton toggleDownload = new TextButton(Statics.UI_SWITCH_TO_DOWNLOAD_ALL,skin);
        optionsTable.add(toggleDownload).minWidth(100f * density).minHeight(50f * density);
        toggleDownload.getLabel().setFontScale(1.0f * density, 1.0f * density);
        toggleDownload.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float screenX, float screenY, int pointer, int button) {
                Statics.downloadPrimary = !Statics.downloadPrimary;
                if (!Statics.downloadPrimary) {
                    toggleDownload.setText(Statics.UI_SWITCH_TO_DOWNLOAD_PRIMARY);
                } else {
                    toggleDownload.setText(Statics.UI_SWITCH_TO_DOWNLOAD_ALL);
                }
                return false;
            }
        });

        uiTable.row();
        Pixmap detailsBackground = new Pixmap(1, 1, Pixmap.Format.RGB565);
        detailsBackground.setColor(Statics.DETAILS_BACKGROUND_COLOR);
        detailsBackground.fill();
        detailsVisible = false;

        //Create details Table
        detailsTable = new Table();
        //Set background
        detailsTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(detailsBackground))));
        detailsTable.setBounds(0, 0, Statics.DETAILS_DEFAULT_WIDTH, Statics.DETAILS_DEFAULT_HEIGHT);
        detailsTable.align(Align.bottomLeft);
        detailsStage.addActor(detailsTable);
        //Add Close button
        TextButton closeButton = new TextButton(" x ", skin);
        detailsTable.add(closeButton).minWidth(100f * density).minHeight(50f * density)
                .align(Align.right)
                .pad(0, 0, 0, Statics.DATEPILLAR_WIDTH / 2f);
        closeButton.getLabel().setFontScale(1.0f * density, 1.0f * density);
        closeButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float screenX, float screenY, int pointer, int button) {
                if (main.currentActivity != null && detailsVisible) {
                    main.currentActivity = null;
                    detailsVisible = false;
                }
                return false;
            }
        });
        detailsTable.row();


        detailsLabel = new Label("",skin);
        detailsTable.add(detailsLabel).align(Align.left);
        detailsTable.row();

        //insert space for delete and edit buttons
        detailsTable.row();
        detailsTable.row();
        Label emptyRowLabel = new Label("  ",skin);
        detailsTable.add(emptyRowLabel);
        Table detailsButtonTable = new Table();
        //detailsButtonTable.setDebug(true);
        //detailsButtonTable.pad(0f,500f,0f,500f);
        detailsTable.addActor(detailsButtonTable);
        detailsButtonTable.setFillParent(true);
        detailsButtonTable.align(Align.bottom);
        //Delete button
        final TextButton deleteButton = new TextButton(Statics.UI_DELETE_NAME, skin);
        detailsButtonTable.add(deleteButton).minWidth(100f * density).minHeight(50f * density)
                .align(Align.left)
                .pad(0, 0, 0, Statics.DATEPILLAR_WIDTH /2f);
        deleteButton.getLabel().setFontScale(1.0f * density, 1.0f * density);
        deleteButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float screenX, float screenY, int pointer, int button) {
                deleteEvent(main.currentActivity);
                return false;
            }
        });

        //Edit button
        final TextButton reportButton = new TextButton(Statics.UI_EDIT_NAME, skin);
        detailsButtonTable.add(reportButton).minWidth(100f * density).minHeight(50f * density)
                .align(Align.right)
                .pad(0, Statics.DETAILS_DEFAULT_WIDTH / 2f, 0f, 0);
        reportButton.getLabel().setFontScale(1.0f * density, 1.0f * density);
        reportButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float screenX, float screenY, int pointer, int button) {
                reportDialogVisible = true;
                updateReportDialogText();
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
        Label reportDialogDetailsLabel = new Label("Summary: \n",skin);
        reportDialogTable.add(reportDialogDetailsLabel).align(Align.topLeft);
        reportDialogSummaryContents = new Label("",skin);
        reportDialogTable.add(reportDialogSummaryContents).align(Align.topLeft);
        //Edit button
        TextButton editSummary = new TextButton("Edit Summary", skin);
        editSummary.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float screenX, float screenY, int pointer, int button) {
                if(reportDialogVisible){
                    EditSummary es = new EditSummary();
                    Gdx.input.getTextInput(es, "Edit Summary:","","");
                }
                return false;
            }
        });
        reportDialogTable.add(editSummary).align(Align.left);
        reportDialogTable.row();

        //Date
        Label reportDialogDateLabel = new Label("Date: ", skin);
        reportDialogTable.add(reportDialogDateLabel).align(Align.topLeft);
        reportDialogDateContents = new Label("",skin);
        reportDialogTable.add(reportDialogDateContents).align(Align.topLeft);
        //Edit button
        TextButton editDate = new TextButton("Edit Date   ", skin);
        editDate.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float screenX, float screenY, int pointer, int button) {
                if(reportDialogVisible){
                    EditDate ed = new EditDate();
                    Gdx.input.getTextInput(ed, "Edit Date:", "", "year-month-day");
                }
                return false;
            }
        });
        reportDialogTable.add(editDate).align(Align.left);
        reportDialogTable.row();


//        //Details
//        Label reportDialogDescriptionLabel = new Label("Description:",skin);
//        reportDialogTable.add(reportDialogDescriptionLabel).align(Align.topLeft);;
//        reportDialogDescriptionContents = new Label("", skin);
//        reportDialogTable.add(reportDialogDescriptionContents).align(Align.topLeft);;
//        reportDialogTable.row();


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
        //Edit button
        TextButton editFrom = new TextButton("Edit From", skin);
        editFrom.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float screenX, float screenY, int pointer, int button) {
                if(reportDialogVisible){
                    EditFrom ef = new EditFrom();
                    Gdx.input.getTextInput(ef, "From:", "", "hour:min");
                }
                return false;
            }
        });
        reportDialogTable.add(editFrom).align(Align.left);
        reportDialogTable.row();

        //To
        Label reportDialogToLabel = new Label("To: \n",skin);
        reportDialogTable.add(reportDialogToLabel).align(Align.topLeft);
        reportDialogToContents = new Label("",skin);
        reportDialogTable.add(reportDialogToContents).align(Align.topLeft);
        //Edit button
        TextButton editTo = new TextButton("Edit To  ", skin);
        editTo.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float screenX, float screenY, int pointer, int button) {
                if(reportDialogVisible){
                    EditTo eT = new EditTo();
                    Gdx.input.getTextInput(eT, "From:","","hour:min");
                }
                return false;
            }
        });

        reportDialogTable.add(editTo).align(Align.left);
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
        TextButton reportOkButton = new TextButton("Ok", skin);
        reportOkButton.getLabel().setFontScale(1.0f * density, 1.0f * density);
        reportOkButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float screenX, float screenY, int pointer, int button) {
                if(reportDialogVisible){
                    reportDialogVisible = false;
                    //Update events
                    System.out.println("Ok Clicked");
                }
                return false;
            }
        });
        reportDialogTable.add(reportOkButton).minWidth(100f * density).minHeight(50f * density);
        //Cancel button
        TextButton reportCancelButton = new TextButton("Cancel", skin);
        reportCancelButton.getLabel().setFontScale(1.0f * density, 1.0f * density);
        reportCancelButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float screenX, float screenY, int pointer, int button) {
                if(reportDialogVisible){
                    reportDialogVisible = false;
                    System.out.println("Cancel Clicked");
                }
                return false;
            }
        });
        reportDialogTable.add(reportCancelButton).minWidth(100f * density).minHeight(50f * density);
    }

    private void updateCalender() {
        main.updateActivities = true;
        main.clearedAndMoved = false;
        main.setDownloadDone(false);
        main.calCont.update(main.from,main.to);
    }

    private void undoLastDelete() {
        //main.deletedActivity.event.setId(main.deletedActivity.event.getId() + "2");
        Event old = main.deletedActivity.event;
        Event e = new Event().setSummary(old.getSummary());
        e.setStart(old.getStart());
        e.setEnd(old.getEnd());
        main.calCont.InsertEvent(e);
        main.deletedActivity = null;
    }

    private void deleteEvent(Activity a) {
        if(a != null){
            main.deletedActivity = a;
            main.calCont.deleteEvent(a.event);
        }
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
            reportDialogSummaryContents.setText(main.currentActivity.event.getSummary());
            //Get Date
            reportDialogDateContents.setText(main.currentActivity.d3d.getDateName());
            //Get Description
            //reportDialogDescriptionContents.setText(main.currentActivity.event.getDescription());
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
        uiBatch.enableBlending();
        uiBatch.begin();
        uiStage.draw();
        if(detailsVisible && main.currentActivity != null){
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

    public class EditSummary implements Input.TextInputListener {
        @Override
        public void input (String text) {
            main.currentActivity.event.setSummary(text);
            reportDialogSummaryContents.setText(main.currentActivity.event.getSummary()
                    + "\n" + main.currentActivity.d3d.getDateName());

            updateEvent(main.currentActivity.event);
        }
        @Override
        public void canceled () {
        }
    }

    public void update(PerspectiveCamera cam){
        //update details position
        if(main.currentActivity != null){
            Vector3 pos = main.currentActivity.getModelInstance().transform.getTranslation(new Vector3());
            cam.project(pos);
            detailsTable.setBounds(pos.x,pos.y,Statics.DETAILS_DEFAULT_WIDTH,Statics.DETAILS_DEFAULT_HEIGHT);
        }

        //Update undo
        if(main.deletedActivity != null){
            undoButton.setText(Statics.UI_UNDO_DELETE_NAME);
        }
        else {
            undoButton.setText(Statics.UNDO_NOT_DELETEABLE_NAME);
        }
    }

    private void updateEvent(Event event) {
        main.calCont.updateEvent(event);
    }

    public  class EditFrom implements Input.TextInputListener {
        @Override
        public void input (String text) {
            if(main.currentActivity.d3d.parseStartTime(text)){
                EventDateTime edt = new EventDateTime().setDateTime(createStartTime(main.currentActivity.d3d));//.setTimeZone("Europe/Stockholm");
                main.currentActivity.event.setStart(edt);
                reportDialogFromContents.setText((main.currentActivity.d3d.getStartTime()));

                updateEvent(main.currentActivity.event);
            }
            else {
                System.out.println("ERROR PARSING TIME");
            }
        }

        @Override
        public void canceled () {

        }
    }

    public  class EditTo implements Input.TextInputListener {
        @Override
        public void input (String text) {
            if(main.currentActivity.d3d.parseStopTime(text)){
                EventDateTime edt = new EventDateTime().setDateTime(createStopTime(main.currentActivity.d3d));//.setTimeZone("Europe/Stockholm");
                main.currentActivity.event.setEnd(edt);
                reportDialogToContents.setText((main.currentActivity.d3d.getStopTime()));
                updateEvent(main.currentActivity.event);
            }
            else {
                System.out.println("ERROR PARSING TIME");
            }
        }

        @Override
        public void canceled () {
        }
    }

    private class EditDate implements Input.TextInputListener{
        @Override
        public void input (String text) {
//            if(main.currentActivity.d3d.parseStopTime(text)){
//                EventDateTime edt = new EventDateTime().setDateTime(createStopTime(main.currentActivity.d3d));//.setTimeZone("Europe/Stockholm");
//                main.currentActivity.event.setEnd(edt);
//                reportDialogToContents.setText((main.currentActivity.d3d.getStopTime()));
//
//                updateEvent(main.currentActivity.event);
//            }
//            else {
//                System.out.println("ERROR PARSING TIME");
//            }

        }

        @Override
        public void canceled () {
        }
    }

    private DateTime createStopTime(Date3d d3d) {
        return createDateTime(d3d, false);
    }

    private DateTime createStartTime(Date3d d3d){
        return createDateTime(d3d, true);
    }
    private DateTime createDateTime(Date3d d3d, boolean start) {
        //Create date in the following format ->2015-05-28T17:00:00+02:00<-
        String time = (start) ? d3d.getStartTime(true) : d3d.getStopTime(true);
        String date = "" + d3d.year
                + "-" + d3d.getMonth(true)
                + "-" + d3d.getDay(true)
                + "T" + time
                + ":00.000+02:00";
        return new DateTime(date);
    }


}
