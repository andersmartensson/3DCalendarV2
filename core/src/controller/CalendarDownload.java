package controller;

//import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import java.io.IOException;

/**
 * Created by Anders on 2016-04-20.
 */
public class CalendarDownload{
    private GoogleAccountCredential credential;
//    public static HttpResponse executeGet(
//            HttpTransport transport, JsonFactory jsonFactory, String accessToken, GenericUrl url)
//            throws IOException {
//        accessToken = Statics.G_KEY;
//        //Credential
//        Credential credential = new Credential(BearerToken.authorizationHeaderAccessMethod()).setAccessToken(accessToken);
//        HttpRequestFactory requestFactory = transport.createRequestFactory(credential);
//        return requestFactory.buildGetRequest(url).execute();
//    }


//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws IOException {
//        // do stuff
//    }


//
//    public void connect(){
//        //LibGDX socket
//        //Socket socket = Gdx.net.newClientSocket(Protocol protocol, String host, int port, SocketHints hints);
//        Net.HttpResponseListener httpResponseListener = new Net.HttpResponseListener() {
//            @Override
//            public void handleHttpResponse(Net.HttpResponse httpResponse) {
//
//            }
//
//            @Override
//            public void failed(Throwable t) {
//
//            }
//
//            @Override
//            public void cancelled() {
//
//            }
//        };
//
//        //LibGdx http send request
//        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
//        Net.HttpRequest httpRequest = requestBuilder.newRequest().method(Net.HttpMethods.GET).url("http://www.google.de").build();
//        //Gdx.net.sendHttpRequest(httpRequest, httpResponseListener);
//
//        //LibGdx GET
//        HttpRequestBuilder requestBuilder2 = new HttpRequestBuilder();
//        Net.HttpRequest httpRequest2 = requestBuilder.newRequest().method(Net.HttpMethods.GET).url("http://www.google.de").content("q=libgdx&example=example").build();
//        Gdx.net.sendHttpRequest(httpRequest2, httpResponseListener);
//        //Gdx.net.sendHttpRequest();
//
//
//        // Google Accounts
////        credential = GoogleAccountCredential.usingOAuth2(Gdx.)
//////        credential =  GoogleAccountCredential.usingOAuth2(,
//////                , Collections.singleton(CalendarScopes.CALENDAR));
//
//
//    }

    public static Credential authorize() throws IOException {


        return null;
    }


}
