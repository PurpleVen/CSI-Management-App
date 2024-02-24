package com.example.csi.mActivityManager;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.app.AlertDialog;
import android.content.DialogInterface;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.csi.Prompts.MainActivity;
import com.example.csi.Prompts.Manager;
import com.example.csi.R;
import com.example.csi.SharedPreferenceConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

public class Technical_form extends AppCompatActivity {
    private String urole1,eid , BoxStatus;
    LinearLayout tech_lay;
    private SharedPreferenceConfig preferenceConfig;
    private TextView name , theme , e_date,speaker,csi_f,ncsi_f,worth_prize , description, cr_budget, pub_budget, guest_budget , tech_req, techFileStatus;
    CheckBox question , internet , software;
    EditText comments;
    LinearLayout comments_layout;
    private LinearLayout checkboxContainer;
    private ArrayList<String> checkboxNames = new ArrayList<>();
    private List<CheckBox> checkBoxList = new ArrayList<>();
    private List<CheckBox> checkedboxes = new ArrayList<>();
    private Button techselectFileButton, techdeleteButton;
    private static final int REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technical_form);
        getSupportActionBar().setTitle("Technical");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        preferenceConfig = new SharedPreferenceConfig(getApplicationContext());
        urole1=preferenceConfig.readRoleStatus();
        tech_lay = findViewById(R.id.tech_pl);
//        linearLayout = (LinearLayout) findViewById(R.id.linear_layout);

//        name =findViewById(R.id.name_tf);
        name = (TextView) findViewById(R.id.name_tf);
        techFileStatus = findViewById(R.id.techFileStatus);
        theme  =findViewById(R.id.theme_tf);
        e_date =findViewById(R.id.ed_tf);
        speaker =findViewById(R.id.speaker_tf);
        csi_f =findViewById(R.id.fee_csi_tf);
        ncsi_f =findViewById(R.id.fee_non_csi_tf);
        worth_prize =findViewById(R.id.prize_tf);
        description =findViewById(R.id.desc_pd_tf);
        cr_budget=findViewById(R.id.cb);
        pub_budget=findViewById(R.id.pb);
        guest_budget=findViewById(R.id.gb);
        checkboxContainer = findViewById(R.id.checkbox_container);
        Bundle extras = getIntent().getExtras();
        if(extras == null) {

        } else {
            eid=extras.getString("EID");

        }

        question = findViewById(R.id.question);
        internet = findViewById(R.id.internet);
        software = findViewById(R.id.software);
        comments = findViewById(R.id.comment_t);
        comments_layout=findViewById(R.id.tf_comment_layout);


        Button edit = findViewById(R.id.updateTech);
        Button add_checkbox_button = findViewById(R.id.add_checkbox_button);


        if(urole1.equals("Tech Head" )){
            edit.setVisibility(View.VISIBLE);
            tech_lay.setVisibility(View.GONE);
        }else{
            tech_lay.setVisibility(View.VISIBLE);
            findViewById(R.id.tech_delete_button).setEnabled(false);


        }


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tech_lay.setVisibility(View.VISIBLE);
                add_checkbox_button.setVisibility(View.VISIBLE);
                comments_layout.setVisibility(View.VISIBLE);
                question.setEnabled(true);
                internet.setEnabled(true);
                software.setEnabled(true);
                edit.setVisibility(View.GONE);




                Log.i("volleyABC4985" ,"edit text");
            }
        });

        Button update = findViewById(R.id.Send_Tech_form);
        update.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                comments_layout.setVisibility(View.VISIBLE);
                question.setEnabled(false);
                internet.setEnabled(false);
                software.setEnabled(false);
                tech_lay.setVisibility(View.VISIBLE);

                volley_send();
            }
        });


        volley_get();



//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_technical_form);

        Button addCheckboxButton = findViewById(R.id.add_checkbox_button);
        addCheckboxButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNamePrompt();
            }
        });

        techselectFileButton = findViewById(R.id.tech_select_file_button);
        if (!urole1.equals("Tech Head")) {
            techselectFileButton.setVisibility(View.GONE);

        }
        techselectFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        techdeleteButton = findViewById(R.id.tech_delete_button);
        if (!urole1.equals("Tech Head")) {
            techdeleteButton.setVisibility(View.GONE);

        }
        techdeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUploadedFile();
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        // Check if a file has already been uploaded for this eid
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isFileUploaded = sharedPreferences.getBoolean(eid, false);
        if (isFileUploaded) {
            techFileStatus.setVisibility(View.GONE); // Hide the status message
            techselectFileButton.setEnabled(false); // Disable the select file button
            Button techdownloadButton = findViewById(R.id.tech_download_button);
            techdownloadButton.setVisibility(View.VISIBLE); // Show the download button
            techdeleteButton.setVisibility(View.VISIBLE);
            techdownloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Build the download request
                    OkHttpClient client = new OkHttpClient();
                    okhttp3.Request downloadRequest = new okhttp3.Request.Builder()
                            .url(getApplicationContext().getResources().getString(R.string.server_url) + "/technical/download?eid=" + eid)
                            .build();

                    // Execute the download request asynchronously
                    client.newCall(downloadRequest).enqueue(new okhttp3.Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            e.printStackTrace();
                            Toast.makeText(Technical_form.this, "Error downloading file", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(Call call, okhttp3.Response response) throws IOException {
                            if (!response.isSuccessful()) {
                                throw new IOException("Unexpected code " + response);
                            }

                            // Create a file with the downloaded content
                            byte[] bytes = response.body().bytes();
                            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                            String fileName = response.header("Content-Disposition").replaceAll("attachment; filename=", "");
                            File file = new File(downloadsDir, fileName);
                            FileOutputStream outputStream = new FileOutputStream(file);
                            outputStream.write(bytes);
                            outputStream.close();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Technical_form.this, "File downloaded successfully", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            });
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, try to download file again
            } else {
                // Permission denied
                Toast.makeText(this, "Permission denied to write to external storage", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void showUploadConfirmationDialog(final Uri fileUri) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("Confirm Upload");
        builder.setMessage("Do you want to upload the selected file?");

        builder.setPositiveButton("Upload", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Proceed with file upload
                uploadFile(fileUri);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void deleteUploadedFile() {
        // Build the delete request
        OkHttpClient client = new OkHttpClient();
        // Use FormBody to send eid as part of the request body
        RequestBody formBody = new FormBody.Builder()
                .add("eid", eid)
                .build();

        okhttp3.Request deleteRequest = new okhttp3.Request.Builder()
                .url(getApplicationContext().getResources().getString(R.string.server_url) + "/technical/delete")
                .post(formBody)
                .build();

        // Execute the delete request asynchronously
        client.newCall(deleteRequest).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Technical_form.this, "Error deleting file", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Technical_form.this, "File deleted successfully", Toast.LENGTH_SHORT).show();
                        techselectFileButton.setEnabled(true); // Enable the select file button
                        findViewById(R.id.tech_download_button).setVisibility(View.GONE); // Hide the download button
                        findViewById(R.id.tech_delete_button).setVisibility(View.GONE); // Hide the delete button

                        // Update SharedPreferences to reflect that the file has been deleted
                        SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
                        editor.putBoolean(eid, false);
                        editor.apply();
                    }
                });
            }
        });
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            showUploadConfirmationDialog(uri);
        }
    }

    private void uploadFile(Uri fileUri) {
        String eventNameText = name.getText().toString();
        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);

            // Check if a file has already been uploaded for this eid
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            boolean isFileUploaded = sharedPreferences.getBoolean(eid, false);
            if (isFileUploaded) {
                Toast.makeText(this, "You have already uploaded a file for this eid", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create OkHttp3 client and builder
            OkHttpClient client = new OkHttpClient();
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);

            // Add file to the builder
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            RequestBody requestBody = RequestBody.create(MediaType.parse(getContentResolver().getType(fileUri)), bytes);
            builder.addFormDataPart("pdfFile", "file.pdf", requestBody);
            builder.addFormDataPart("eid", eid);
            Log.i("eid for server upload",  eid);

            builder.addFormDataPart("eventname", eventNameText); // Make sure this matches the server's expected field name
            Log.i("event name",  eventNameText);

            // Build the request
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(getApplicationContext().getResources().getString(R.string.server_url) + "/technical/upload")
                    .post(builder.build())
                    .build();

            // Execute the request asynchronously
            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    Toast.makeText(Technical_form.this, "Error uploading file", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(Call call, okhttp3.Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }

                    // Save the eid in SharedPreferences to indicate that a file has been uploaded for this eid
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(eid, true);
                    editor.apply();


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            techselectFileButton.setEnabled(false); // Disable the select file button
                            Button techdownloadButton = findViewById(R.id.tech_download_button);
                            techdownloadButton.setVisibility(View.VISIBLE); // Show the download button
                            techdownloadButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // Build the download request
                                    okhttp3.Request downloadRequest = new okhttp3.Request.Builder()
                                            .url(getApplicationContext().getResources().getString(R.string.server_url) + "/technical/download?eid=" + eid)
                                            .build();

                                    // Execute the download request asynchronously
                                    client.newCall(downloadRequest).enqueue(new okhttp3.Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            e.printStackTrace();
                                            Toast.makeText(Technical_form.this, "Error downloading file", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onResponse(Call call, okhttp3.Response response) throws IOException {
                                            if (!response.isSuccessful()) {
                                                throw new IOException("Unexpected code " + response);
                                            }

                                            // Try to extract the file name from the Content-Disposition header
                                            String contentDisposition = response.header("Content-Disposition");
                                            String downloadedFileName = "downloaded_file.pdf"; // Default file name if header parsing fails
                                            if (contentDisposition != null && contentDisposition.contains("filename=")) {
                                                downloadedFileName = contentDisposition.split("filename=")[1].replaceAll("\"", "");
                                            }

                                            // Create a file with the downloaded content
                                            byte[] bytes = response.body().bytes();
                                            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                                            File file = new File(downloadsDir, downloadedFileName);
                                            FileOutputStream outputStream = new FileOutputStream(file);
                                            outputStream.write(bytes);
                                            outputStream.close();
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(Technical_form.this, "File downloaded successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Technical_form.this, "File uploaded successfully", Toast.LENGTH_SHORT).show();
                            techselectFileButton.setEnabled(false);
                            findViewById(R.id.tech_download_button).setVisibility(View.VISIBLE);
                            findViewById(R.id.tech_delete_button).setVisibility(View.VISIBLE);
                        }
                    });
                }
            });
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        // TODO Auto-generated method sub
        int id= item.getItemId();
        if (id == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }



    //    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//
//    }
    private void showNamePrompt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Task Name To Be Added:");

        final View inputView = getLayoutInflater().inflate(R.layout.name_prompt, null);
        builder.setView(inputView);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = ((EditText) inputView.findViewById(R.id.name_edit_text)).getText().toString();
                if (!TextUtils.isEmpty(name)) {
                    addCheckbox(name);
                    Log.i("checkboxx" , name);
                } else {
                    Toast.makeText(Technical_form.this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);

        builder.show();
    }

    public void addCheckbox(String name) {
//        LinearLayout checkboxContainer;


        CheckBox checkBox = new CheckBox(this);
        checkBox.setText(name);
        checkboxContainer.addView(checkBox);
        checkBoxList.add(checkBox);


        Log.i("listt",checkBoxList.get(0).getText().toString());
//        CheckBox checkBox = new CheckBox(this);
//        checkBox.setText(name);
//        checkboxContainer.addView(checkBox);
    }


    public void volley_get(){

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("eid", eid);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        final String requestBody = jsonObject.toString();
        Log.i("volleyABC ", requestBody);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,getApplicationContext().getResources().getString(R.string.server_url) + "/technical/viewEvents", new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onResponse(String response) {


//                ret[0]=response;
                Log.i("volleyABC4985" ,"got response    "+response);
                //Toast.makeText(Creative_form.this, "Logged IN", Toast.LENGTH_SHORT).show();
                Log.i("volleyABC4985" ,"got response    "+response);
                //Toast.makeText(Creative_form.this, "Logged IN", Toast.LENGTH_SHORT).show();
                Log.i("volleyABC" ,"reposnsde"+response);
                if(response != null){
                    try {
                        JSONObject jsonObject1 = new JSONObject(response);
                        // Log.i("tracking uid","main Activity "+UID);
                        name.setText(jsonObject1.getString("proposals_event_name"));
                        theme.setText(jsonObject1.getString("proposals_event_category"));
                        speaker.setText(jsonObject1.getString("speaker"));
                        csi_f.setText(jsonObject1.getString("proposals_reg_fee_csi"));
                        ncsi_f.setText(jsonObject1.getString("proposals_reg_fee_noncsi"));
                        worth_prize.setText(jsonObject1.getString("proposals_prize"));
                        description.setText(jsonObject1.getString("proposals_desc"));

                        cr_budget.setText(jsonObject1.getString("proposals_creative_budget"));
                        pub_budget.setText(jsonObject1.getString("proposals_publicity_budget"));
                        guest_budget.setText(jsonObject1.getString("proposals_guest_budget"));

//                        addCheckbox(jsonObject1.getString("tasks"));


                        LinearLayout tasksContainer = findViewById(R.id.tasks_container);
                        String tasksString = jsonObject1.getString("tasks");
                        String statusString = jsonObject1.getString("status");

                        // Split the comma-separated string into an array of individual status values
                        String[] statusValues = statusString.split(",");
//                        Log.i("status ki values", statusValues[1] );




                        JSONArray tasksArray = new JSONArray(tasksString);
                        JSONArray statusArray = new JSONArray(statusValues);
//                        Log.i("taskstatussss" , String.valueOf(tasksArray.length()));

                        for (int i = 0; i < tasksArray.length(); i++) {
                            String taskName = tasksArray.getString(i);
                            String taskstatus = statusArray.getString(i);
                            Log.i("server se aaya array", taskName );
                            CheckBox checkBox = new CheckBox(getApplicationContext());
                            checkBox.setText(taskName);
                            checkBox.setTextColor(getResources().getColor(R.color.DarkBlue));
                            checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

//                            if(!urole1.equals("Tech Head")){
//                                checkBox.setClickable(false);
//                            }
                            if(urole1.equals("Tech Head")){
                                checkBox.setEnabled(true);
                            }
                            else {
                                checkBox.setEnabled(false);
                            }
                            if(taskstatus.equals("1")){
                                checkBox.setChecked(true);
                                Log.i("checked" , taskstatus);
                            }
                            else{
                                checkBox.setChecked(false);
                            }
                            tasksContainer.addView(checkBox);

                        }


                        String eventDate=jsonObject1.getString("proposals_event_date");
                        if(eventDate!=null)
                            eventDate = eventDate.substring(8,10) + "/" + eventDate.substring(5,7) + "/" + eventDate.substring(0,4);
                        e_date.setText(eventDate);
                        getSupportActionBar().setTitle(jsonObject1.getString("proposals_event_name"));
                        //Send data to Manager.java starts
                        // Call manager.java file i.e. Activity with navigation drawer activity



                        if((int)jsonObject1.get("qs_set")==1){
                            question.setChecked(true);
                        }else{
                            question.setChecked(false);
                        }
                        if((int)jsonObject1.get("internet")==1){
                            internet.setChecked(true);
                        }else{
                            internet.setChecked(false);
                        }
                        if((int)jsonObject1.get("software_install")==1){
                            software.setChecked(true);
                        }else{
                            software.setChecked(false);
                        }




                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }


            }
        },new Response.ErrorListener()  {
            @Override
            public void onErrorResponse(VolleyError error) {

                try{
                    Log.i("volleyABC" ,Integer.toString(error.networkResponse.statusCode));
                    Toast.makeText(Technical_form.this, "Invalid request", Toast.LENGTH_SHORT).show(); //This method is used to show pop-up on the screen if user gives wrong uid
                    error.printStackTrace();}
                catch (Exception e)
                {
                    Toast.makeText(Technical_form.this,"Check Network",Toast.LENGTH_SHORT).show();

                }

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
//        return ret[0];


    }




    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void volley_send(){

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("eid", eid);
            jsonObject.put("tech_comment",comments.getText().toString());
            if(question.isChecked()){
                jsonObject.put("qs_set",1);
            }else{
                jsonObject.put("qs_set",0);
            }
            if(internet.isChecked()){
                jsonObject.put("internet",1);
            }else{
                jsonObject.put("internet",0);
            }
            if(software.isChecked()){
                jsonObject.put("software_install",1);
            }else{
                jsonObject.put("software_install",0);
            }


        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        final String requestBody = jsonObject.toString();
        Log.i("volleyABC ", requestBody);



        // Create a RequestQueue to handle the API request
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        // Create a string array to store the checked checkboxes
        ArrayList<String> checkedCheckboxes = new ArrayList<>();

        // Create a string array to store the status of checkboxes
        ArrayList<Integer> checkboxStatus = new ArrayList<>();

        // Add the checked checkboxes to the string array
        for (int i = 0; i < checkBoxList.size(); i++) {
            View view = checkBoxList.get(i);
            CheckBox checkBox = (CheckBox) view;
            checkedCheckboxes.add(checkBox.getText().toString());
            if(checkBox.isChecked()){
                checkboxStatus.add(1);
//                checkedCheckboxes.add("1");
            }
            else{
                checkboxStatus.add(0);
//                checkedCheckboxes.add("0");
//                checkedCheckboxes.add((checkBox.getText()+"0"));
            }

            Log.i("arrayss" , checkedCheckboxes.toString());
        }






        // Create a JSON object to store the data to be sent to the server
        JSONObject jsonObjectnew = new JSONObject();

        try {
            // Add the checkedCheckboxes list to the JSON object
            jsonObjectnew.put("checkedCheckboxes", new JSONArray(checkedCheckboxes));
            jsonObjectnew.put("eid", eid);
            jsonObjectnew.put("checkboxStatus", new JSONArray(checkboxStatus));
            Log.i("statusarray", String.valueOf(checkboxStatus));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create a POST request to the server with the data
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, getApplicationContext().getResources().getString(R.string.server_url) + "/technical/addcheckbox", jsonObjectnew,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Volley Response", response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Volley Error", error.toString());
            }
        });


        StringRequest stringRequest = new StringRequest(Request.Method.POST,getApplicationContext().getResources().getString(R.string.server_url) + "/technical/editEvents ", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
//                ret[0]=response;
                Log.i("volleyABC4985" ,"got response    "+response);

                Toast.makeText(Technical_form.this, "Updated", Toast.LENGTH_SHORT).show();
                comments_layout.setVisibility(View.GONE);
                Intent manager = new Intent(Technical_form.this, Manager.class);
                startActivity(manager);
                finish();


            }
        },new Response.ErrorListener()  {
            @Override
            public void onErrorResponse(VolleyError error) {

                try{
                    Log.i("volleyABC" ,Integer.toString(error.networkResponse.statusCode));
                    Toast.makeText(Technical_form.this, "Invalid request", Toast.LENGTH_SHORT).show(); //This method is used to show pop-up on the screen if user gives wrong uid
                    error.printStackTrace();}
                catch (Exception e)
                {
                    Toast.makeText(Technical_form.this,"Check Network",Toast.LENGTH_SHORT).show();

                }
//                finish();

            }
        })

        {
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

//        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest); // get response from server
        // Add the request to the RequestQueue
        requestQueue.add(jsonObjectRequest);
//        return ret[0];


    }
}