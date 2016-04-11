package data;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by anders on 2015-11-26.
 */
public class Statics {
    /*
    Paths
     */
    public static final String  skyboxDiffPath = "textures/skybox.png";
    public static final String backPlateTextPath = "textures/weekBG.png";

    public final static String G_KEY = "{\"installed\":{\"client_id\":\"53035057420-se1v5o64qkd72cmcqi1je5i9vopdeoo7.apps.googleusercontent.com\",\"project_id\":\"weighty-card-124409\",\"auth_uri\":\"https://accounts.google.com/o/oauth2/auth\",\"token_uri\":\"https://accounts.google.com/o/oauth2/token\",\"auth_provider_x509_cert_url\":\"https://www.googleapis.com/oauth2/v1/certs\",\"client_secret\":\"2FpOI5-XUiu6FfTNhJKEb_Uo\",\"redirect_uris\":[\"urn:ietf:wg:oauth:2.0:oob\",\"http://localhost\"]}}";
    //ublic static long HEIGHT_DIVIDER = 1000000;

    /*
    Activity Settings
     */
    public static final float ACTIVITY_WIDTH = 3.0f;
    public static final float ACTIVITY_DEPTH = 1.0f;
    public static final float ACTIVITY_DUMP_X_POSTION = -100f;
    public static final float ACTIVITY_TEXTURE_WIDTH_MODIFIER = 100f;
    public static final float ACTIVITY_TEXTURE_HEIGHT_MODIFIER = 100f;

    /*
    Date pillar settings
     */
    public static final float DATEPILLAR_WIDTH = 0.1f;
    public static final float DATEPILLAR_HEIGHT = 24f;
    public static final float DATEPILLAR_DEPTH = 0.2f;
    public static final float DATEPILLAR_X_ORIGN = -15f;
    public static final float DATEPILLAR_Y_ORIGIN = DATEPILLAR_HEIGHT /2f;
    public static final float DATEPILLAR_Z_ORIGIN = -ACTIVITY_DEPTH /2f;
    public static final float DATEPILLAR_DATE_NUM_MOD = - ACTIVITY_WIDTH /2f ;

    /*
    Backplate settings
     */
    public static final float WEEK_BACKPLATE_WIDTH = 1f + 7f * (ACTIVITY_WIDTH + 1f);
    public static final float WEEK_BACKPLATE_HEIGHT = 25f;
    public static final float WEEK_BACKPLATE_DEPTH = 0.1f;
    public static final Vector3 WEEK_BACKPLATE_POSITION = new Vector3(
        (WEEK_BACKPLATE_WIDTH / 2f)
                + DATEPILLAR_X_ORIGN
                +  (ACTIVITY_WIDTH * 4) ,
        WEEK_BACKPLATE_HEIGHT/2f,
        DATEPILLAR_Z_ORIGIN -0.1f);
    public static final float WEEK_NUMBER_SCALE = 0.8f;

    /*
    CAMERA settings
    */
    public static final float CAMERA_FOV = 75f;
    public static final float CAM_FAR = 1000f; //How far the camera can see.
    public static final float CAM_NEAR = 0.1f; //Camera cut-off.
    public static final Color DETAILS_BACKGROUND_COLOR = new Color(0.2f,0.2f,0.2f,0.5f);

    public static float DISTANCE_FROM_CAMERA = -30f;
    public static Vector3 CAM_START_POSITION = new Vector3(WEEK_BACKPLATE_WIDTH / 2f,
            WEEK_BACKPLATE_HEIGHT,
            - DISTANCE_FROM_CAMERA);
    /*
    Skybox settings
     */
    public static final float SKYBOX_SIZE = CAM_FAR / 4;
    /*
    Ground settings
     */
    public static final Vector3 GROUND_POSITION = new Vector3(0, -15.0f, 0.0f);
    /*
    Post Proccess settings
     */
    public static final String SHADER_BASE_PATH = "cShaders/";


    public static final String  ALPHA_A_PATH = "models/alphabet/A.g3dj";
    public static final String  ALPHA_B_PATH = "models/alphabet/B.g3dj" ;
    public static final String  ALPHA_C_PATH = "models/alphabet/C.g3dj" ;
    public static final String  ALPHA_PATH = "models/alphabet/" ;



}
