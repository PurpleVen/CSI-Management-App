package in.dbit.csiapp.mActivityManager;

import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import in.dbit.csiapp.SharedPreferenceConfig;
import in.dbit.csiapp.Prompts.Manager;
import in.dbit.csiapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class proposal_desc extends AppCompatActivity {

    String extra="";
    EditText comment_e;
    TextView comment_t;

    private SharedPreferenceConfig preferenceConfig;
    String eid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proposal_desc);
        getSupportActionBar().setTitle("Proposal Description");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Button ap =findViewById(R.id.approve_pd);
        Button rej =findViewById(R.id.reject_pd);
        Button edit = findViewById(R.id.edit_pd);
         comment_e = findViewById(R.id.comment_e);
         comment_t = findViewById(R.id.comment_t);


        preferenceConfig = new SharedPreferenceConfig(getApplicationContext());
        String urole1=preferenceConfig.readRoleStatus();
//      Toast.makeText(proposal_desc.this,urole1,Toast.LENGTH_SHORT).show();

        eid = getIntent().getStringExtra(praposal_recycler.eid);
        String st = getIntent().getStringExtra(praposal_recycler.st);

        Log.i("volleyABC" ,"123");
//        Toast.makeText(proposal_desc.this,eid , Toast.LENGTH_SHORT).show();
        get_data(getApplicationContext().getResources().getString(R.string.server_url) + "/proposal/viewproposal","0","0");


        if(urole1.equals("HOD") && st.equals("2")){
            ap.setVisibility(View.VISIBLE);
            rej.setVisibility(View.VISIBLE);
            comment_e.setVisibility(View.VISIBLE);
            comment_t.setVisibility(View.GONE);
        }
        else if(urole1.equals("SBC") && st.equals("1")){
            ap.setVisibility(View.VISIBLE);
            rej.setVisibility(View.VISIBLE);
            comment_e.setVisibility(View.VISIBLE);
            comment_t.setVisibility(View.GONE);
        }
        else if(urole1.equals("Chairperson") && st.equals("0")){
            ap.setVisibility(View.VISIBLE);
            rej.setVisibility(View.VISIBLE);
            comment_e.setVisibility(View.VISIBLE);
            comment_t.setVisibility(View.GONE);
        }
        else if(urole1.equals("Tech Head") || urole1.equals("Event Head") || urole1.equals("Vice Chairperson") ){
            if((st.equals("0") || st.equals("-1") || st.equals("-2"))){
                edit.setVisibility(View.VISIBLE);
            }

            comment_t.setVisibility(View.VISIBLE);
        }

        ap.setOnClickListener(new View.OnClickListener() {
                                  @Override
                                  public void onClick(View v) {
                                      if(urole1.equals("HOD")) {
                                          customDialog("The Proposal is Approved","3");

//                                          //if sbc then 2 if hod 3
                                      }
                                      else if(urole1.equals("SBC")) customDialog("The Proposal will be Forwarded to HOD","2");
//                                          //if sbc then 2 if hod 3
                                      else if(urole1.equals("Chairperson")) customDialog("The Proposal will be Forwarded to SBC","1");

                                  }
                              });


        rej.setOnClickListener(v -> {
                    if(urole1.equals("HOD")) customDialog("The Proposal is Rejected by HOD","-3");
//                        //if sbc then 1 if hod 2
                    else if(urole1.equals("SBC")) customDialog("The Proposal is Rejected by SBC, but can be edited","-2");
//                        //if sbc then 1 if hod 2
                    else if(urole1.equals("Chairperson")) customDialog("The Proposal is Rejected by Chairperson, but can be edited","-1");
//            finish();
        }
        );

        edit.setOnClickListener(v -> {
            Intent edit_proposal = new Intent(proposal_desc.this,edit_proposal.class);
            edit_proposal.putExtra("data",extra);
            edit_proposal.putExtra("cpm_id",eid);
            edit_proposal.putExtra("status" , st);
            startActivity(edit_proposal);
        }
        );
    }
    void get_data(String url , String flag , String status) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("cpm_id",eid); //actual value shud be id_s
            if(flag.equals("1")){
                jsonObject.put("proposals_status",status);
                jsonObject.put("proposals_comment",comment_e.getText().toString());

            }

        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        final String requestBody = jsonObject.toString();
        Log.i("volleyABC ", requestBody);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.i("volleyABC response", response);
//                Toast.makeText(proposal_desc.this,response, Toast.LENGTH_SHORT).show();
                if(flag.equals("0")){
                try {
                    extra=response;
                    set(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }}

                else {
                    Toast.makeText(proposal_desc.this,"Response Recorded", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try{
                    //String statusCode = String.valueOf(error.networkResponse.statusCode);
                    Log.i("volleyABC" ,Integer.toString(error.networkResponse.statusCode));
                    Toast.makeText(proposal_desc.this,error.networkResponse.statusCode,Toast.LENGTH_SHORT).show();//it will not occur as authenticating at start
                    error.printStackTrace();}
                catch (Exception e)
                {
                    Log.i("volleyABC" ,"exception");
                    Toast.makeText(proposal_desc.this,"Check Network",Toast.LENGTH_SHORT).show();} //occur if connection not get estabilished
            }

        }){
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
        RequestQueue requestQueue= Volley.newRequestQueue(proposal_desc.this);
        requestQueue.add(stringRequest);
    }

    void set(String response) throws JSONException {
        TextView n = findViewById(R.id.name_pd);
        TextView t = findViewById(R.id.theme_pd);
        TextView d = findViewById(R.id.desc_pd);
        TextView ed = findViewById(R.id.ed_pd);
        TextView threetrack = findViewById(R.id.pd_threetrack);
        TextView c = findViewById(R.id.cb_pd);
        TextView p = findViewById(R.id.pb_pd);
        TextView g = findViewById(R.id.gb_pd);
        TextView tot = findViewById(R.id.tb_pd);

        JSONObject res = new JSONObject(response);

        n.setText(res.getString("proposals_event_name"));
        t.setText(res.getString("proposals_event_category"));
        String date=res.getString("proposals_event_date");
        date = date.substring(8,10) + "/" + date.substring(5,7) + "/" + date.substring(0,4);
        ed.setText(date);
        String num_three_track = res.getString("proposals_three_track");
        if(num_three_track.equals("1")){
            threetrack.setText("Academics");
        }
        if(num_three_track.equals("2")){
            threetrack.setText("Aspiration");
        }
        if(num_three_track.equals("3")){
            threetrack.setText("Wellness");
        }
//        threetrack.setText(res.getString("proposals_three_track"));
        d.setText(res.getString("proposals_desc"));
        c.setText(res.getString("proposals_creative_budget"));
        p.setText(res.getString("proposals_publicity_budget"));
        g.setText(res.getString("proposals_guest_budget"));
        tot.setText(res.getString("proposals_total_budget"));
        comment_t.setText(res.getString("proposals_comment"));
        getSupportActionBar().setTitle(res.getString("proposals_event_name"));

    }
    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        //Refresh your stuff here
        get_data(getApplicationContext().getResources().getString(R.string.server_url) + "/proposal/viewproposal","0","0");
    }

    public void customDialog(String message , String st){
        final androidx.appcompat.app.AlertDialog.Builder builderSingle = new androidx.appcompat.app.AlertDialog.Builder(proposal_desc.this);
        //builderSingle.setIcon(R.drawable.ic_notification);
        builderSingle.setTitle("NOTE");
        builderSingle.setMessage(message);

        builderSingle.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        builderSingle.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        get_data(getApplicationContext().getResources().getString(R.string.server_url) + "/proposal/status","1",st );

//                        finish();
                        Intent manager = new Intent(proposal_desc.this, Manager.class);
                        startActivity(manager);
                        finish();

                    }
                });

        builderSingle.show();
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
}
