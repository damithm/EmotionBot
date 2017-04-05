package mobile.jkcs.com.emotionbot;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private static final String TAG = "+++Emotion Bot+++";
    private EditText eText;
    private TextView typeText;
    private AVLoadingIndicatorView loaderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        eText = (EditText)findViewById(R.id.enterText);
        typeText = (TextView)findViewById(R.id.typeTxt);
        loaderView = (AVLoadingIndicatorView)findViewById(R.id.avi);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void getEmotionValue(View view) throws JSONException {


        loaderView.show();

        String enterText = eText.getText().toString();
        String text4Test = null;

        if (enterText.isEmpty()){

            text4Test = "I'am happy...";

        }else {

            text4Test = enterText;
        }

        String tag_json_obj = "json_obj_req";
        String url = "https://westus.api.cognitive.microsoft.com/text/analytics/v2.0/sentiment";
        JSONObject obj = new JSONObject();
        obj.accumulate("language", "en");
        obj.accumulate("id", "text_01");
        obj.accumulate("text", text4Test);

        JSONArray jsonArray = new JSONArray();
        jsonArray.put(0,obj);
        Log.d(TAG, jsonArray.toString());


        JSONObject finalObj = new JSONObject();
        finalObj.accumulate("documents",jsonArray);

        /*JSONArray jsonArray = new JSONArray();
        HashMap<String,String> mapObj = new HashMap<>();
        mapObj.put("language","en");
        mapObj.put("id","text_01");
        mapObj.put("text","I'am happy");*/

        //finalObj.accumulate("documents", mapObj);

        Log.d(TAG,finalObj.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, finalObj,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());

                        try {
                            //JSONObject objResponse = response.getJSONObject("documents");
                            JSONArray resArayy = response.getJSONArray("documents");
                            JSONObject resJobj = resArayy.getJSONObject(0);
                            double score =  resJobj.getDouble("score");

                            loaderView.hide();

                            if (score > 0.7){

                                typeText.setTextColor(Color.GREEN);
                                typeText.setText("Happy");

                            }else if (score > 0.1){

                                typeText.setTextColor(Color.RED);
                                typeText.setText("Angry");

                            }else {

                                typeText.setTextColor(Color.YELLOW);
                                typeText.setText("UnHappy");

                            }

                            Log.d(TAG, resArayy.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                // headers.put("Content-Type", "application/json");
                headers.put("Ocp-Apim-Subscription-Key", "49227c33908f48bcb3bb2b123d7432c3");

                return headers;
            }

            /*@Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //  params.put("url", "https://cdnimg.fonts.net/CatalogImages/23/174783.png");
                params.put("language", "en");
                params.put("detectOrientation", "true");

                return params;
            }*/

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };


        AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);

    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://mobile.jkcs.com.emotionbot/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://mobile.jkcs.com.emotionbot/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
