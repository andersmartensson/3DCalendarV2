package data;

/**
 * Created by anders on 2015-11-26.
 */
public class Statics {
    public static final String  skyboxDiffPath = "textures/skybox.png";

    public final static String G_KEY = "{\"installed\":{\"client_id\":\"53035057420-se1v5o64qkd72cmcqi1je5i9vopdeoo7.apps.googleusercontent.com\",\"project_id\":\"weighty-card-124409\",\"auth_uri\":\"https://accounts.google.com/o/oauth2/auth\",\"token_uri\":\"https://accounts.google.com/o/oauth2/token\",\"auth_provider_x509_cert_url\":\"https://www.googleapis.com/oauth2/v1/certs\",\"client_secret\":\"2FpOI5-XUiu6FfTNhJKEb_Uo\",\"redirect_uris\":[\"urn:ietf:wg:oauth:2.0:oob\",\"http://localhost\"]}}";


    public static final float ACTIVITY_WIDTH = 3.0f;
    public static final float ACTIVITY_DEPTH = 3.0f;


    public static final float CAM_FAR = 1000f; //How far the camera can see.
    public static final float CAM_NEAR = 0.1f; //Camera cut-off.


    public static final float SKYBOX_SIZE = CAM_FAR / 4;
    public static final float DATEPILLAR_WIDTH = 0.1f;
    public static final float DATEPILLAR_HEIGHT = 24f;
    public static final float DATEPILLAR_DEPTH = 0.2f;
    public static final float DATEPILLAR_X_ORIGN = -15f;
    public static final float DATEPILLAR_Y_ORIGIN = DATEPILLAR_HEIGHT /2;
    public static final float DATEPILLAR_Z_ORIGIN = -ACTIVITY_DEPTH /2;
    public static final float ACTIVITY_DUMP_X_POSTION = -100f;


    public static long HEIGHT_DIVIDER = 1000000;
    public static float DISTANCE_FROM_CAMERA = -40f;
}
