package in.dbit.csiapp.Prompts;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;

import in.dbit.csiapp.R;

import in.dbit.csiapp.SharedPreferenceConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity implements Animation.AnimationListener {

    private static final int NOTIFICATION_PERMISSION_REQUEST = 100;
    private SharedPreferenceConfig preferenceConfig; //.....6/6/2019

    public static final String EXTRA_UID = "com.example.csimanagementsystem.EXTRA_UID";
    public static final String EXTRA_UNAME = "com.example.csimanagementsystem.EXTRA_UNAME";
    public static final String EXTRA_UROLE = "com.example.csimanagementsystem.EXTRA_UROLE";
    public static final String EXTRA_URL = "com.example.csimanagementsystem.EXTRA_URL";

    String server_url;
     //Main Server URL
    private SharedPreferences mpref; //asdfg
    private static final String pref_name="";


    String uid=" ",pstring=" ";
    ImageView logo;
    Animation splash,fadein,fadeout;
    View lay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestNotificationPermission();
        server_url = getApplicationContext().getResources().getString(R.string.server_url) + "/login";
//        Toast.makeText(this, "this is server_rl from string " + server_url , Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_main);
        animation();
        //.....6/6/2019
        preferenceConfig = new SharedPreferenceConfig(getApplicationContext());
        if(preferenceConfig.readLoginStatus()!=""){
            Intent manager = new Intent(MainActivity.this, Manager.class);
            manager.putExtra(EXTRA_UID, preferenceConfig.readLoginStatus());
            manager.putExtra(EXTRA_UROLE, preferenceConfig.readRoleStatus());
            manager.putExtra(EXTRA_UNAME,preferenceConfig.readNameStatus());
            //Log.i("New Error", preferenceConfig.readUrlStatus());
            manager.putExtra(EXTRA_URL, preferenceConfig.readUrlStatus());
//            Intent manager = new Intent(this, Manager.class);
            startActivity(manager);
            finish();
        }
        //.....6/6/2019

        Button Login =(Button) findViewById(R.id.Login_button);
        final EditText usrid =(EditText) findViewById(R.id.userid);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("abcdef" ,"Reached Listner" + server_url);

                uid =usrid.getText().toString();
                Log.i("something","User ID is"+uid);

                TextInputEditText pword =findViewById(R.id.password);
                pstring =pword.getText().toString();

                //validation part starts
                //if(uid.length()==10 ) { // && pstring.length()>=7


                    insertSrv();  //this method contains json part of the login page
               // }
               // else {

                    //Log.i("" ,"Not satisfied condition");
                    //Toast.makeText(MainActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    //This method is used to show pop-up on the screen if user gives wrong uid
                //}
                //validation part ends
            }
        });

        mpref=getSharedPreferences(pref_name,MODE_PRIVATE);
        String stored_usrid=mpref.getString("username","");
        usrid.setText(stored_usrid);

        getfcmtoken();
    }

    // Method to request notification permission
    private void requestNotificationPermission() {
        // Check if the permission has already been granted
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            // Check if the version of Android is Marshmallow or above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Show an explanation to the user
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                        // Display a dialog explaining why the permission is needed
                        new AlertDialog.Builder(this)
                                .setTitle("Notification Permission")
                                .setMessage("This app needs permission to send you notifications.")
                                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Request the permission
                                        ActivityCompat.requestPermissions(MainActivity.this,
                                                new String[]{Manifest.permission.POST_NOTIFICATIONS},
                                                NOTIFICATION_PERMISSION_REQUEST);
                                    }
                                })
                                .setNegativeButton("Cancel", null)
                                .create()
                                .show();
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.POST_NOTIFICATIONS},
                                NOTIFICATION_PERMISSION_REQUEST);
                    }
                }
            } else {
                // Request the permission without explanation (for devices running versions < Marshmallow)
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY},
                        NOTIFICATION_PERMISSION_REQUEST);
            }
        }
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permission denied
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getfcmtoken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String token = task.getResult();
                Log.i("My fcm token", token);
            }
        });
    }

    private void insertSrv()
    {
        //creating jsonobject starts
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", uid);
            jsonObject.put("password", pstring);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        //creating jsonobject ends

        //checking data inserted into json object
        final String requestBody = jsonObject.toString();
        Log.i("volleyABC", requestBody);

        //getting response from server starts
        StringRequest stringRequest = new StringRequest(Request.Method.POST,server_url,new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {

                Log.i("volleyABC" ,"got response    "+response);
                Toast.makeText(MainActivity.this, "Logged IN", Toast.LENGTH_SHORT).show();

                Intent manager = new Intent(MainActivity.this, Manager.class);
                String UROLE="", USERNAME="", ProfileURL="";

                try {
                    JSONObject jsonObject1 = new JSONObject(response);
                    // Log.i("tracking uid","main Activity "+UID);
                    Log.i("Profile response" , String.valueOf(jsonObject1));
                    USERNAME = jsonObject1.getString("name");
                    UROLE = jsonObject1.getString("role");
                    ProfileURL = jsonObject1.getString("dp");

                    //sharedPreference.... 6/6/2019
                    preferenceConfig.writeLoginStatus(true,uid,pstring,UROLE,USERNAME,ProfileURL);
                    //sharedPreference.... 6/6/2019


                    SharedPreferences.Editor editor=mpref.edit();
                    editor.putString("username",uid);
                    editor.putString("password",pstring);
                    editor.putString("urole",UROLE);
                    editor.apply();
                    finish();

                    //Send data to Manager.java starts
                    // Call manager.java file i.e. Activity with navigation drawer activity
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                //startActivity(manager);
                manager.putExtra(EXTRA_UID, uid);
                manager.putExtra(EXTRA_UNAME, USERNAME);
                manager.putExtra(EXTRA_UROLE, UROLE);
                manager.putExtra(EXTRA_URL, ProfileURL);
                //Send data to Manager.java ends
                startActivity(manager);
                //
            }
        },new Response.ErrorListener()  {

            @Override
            public void onErrorResponse(VolleyError error) {

                try{
                    Log.i("volleyABC" ,Integer.toString(error.networkResponse.statusCode));
                    Toast.makeText(MainActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show(); //This method is used to show pop-up on the screen if user gives wrong uid


                    error.printStackTrace();}
                catch (Exception e)
                {
                    Toast.makeText(MainActivity.this,"Check Network " + server_url,Toast.LENGTH_SHORT).show();}
            }
        }){
            //sending JSONOBJECT String to server starts
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };
        //sending JSONOBJECT String to server ends

        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest); // get response from server
    }

    //Afif's Work
    public void animation(){
        logo = findViewById(R.id.csilogo);
        splash = AnimationUtils.loadAnimation(this,R.anim.splashlogo);
        fadein = AnimationUtils.loadAnimation(this,R.anim.fade_in);
        splash.setAnimationListener(this);
        lay = findViewById(R.id.main);
        logo.setAnimation(splash);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        lay.setAnimation(fadein);
        lay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
