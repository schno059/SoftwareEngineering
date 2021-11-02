package com.example.cs4532.umdalive.fragments.base;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.cs4532.umdalive.R;
import com.example.cs4532.umdalive.RestSingleton;
import com.example.cs4532.umdalive.UserSingleton;
import com.example.cs4532.umdalive.fragments.create.CreateEventFrag;
import com.example.cs4532.umdalive.fragments.edit.EditClubFrag;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Requires argument with key of clubID to be passed into it before it is added to the frame layout
 */

/**
 * @author Josh Senst
 *
 * 4/26/2018
 *
 * Class that creates the club page
 */
public class ClubFrag extends Fragment {

    //View
    View view;

    //Layout Components
    private ImageView clubImage;
    private TextView clubName;

    private Button joinLeave;
    private Button showLocation;
    private TextView clubDescription;

    private TextView clubLocation;
    private String clubLocationString;
    private TextView clubTime;

    private LinearLayout members;
    private LinearLayout eventsList;

    private FloatingActionButton editClub;
    private FloatingActionButton addEvent;

    private JSONObject joinLeaveObj;

    /**
     * Creates the page whenever the club page is clicked on
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view The view of the club page
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

         //Create View
        view = inflater.inflate(R.layout.club_layout, container, false);
        view.setVisibility(GONE);

        joinLeaveObj = new JSONObject();

        try {
            joinLeaveObj.put("clubID", getArguments().getString("clubID"));
            joinLeaveObj.put("userID", UserSingleton.getInstance().getUserID());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Get Layout Components
        getLayoutComponents();

        //On Click
        editClub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditClubFrag frag = new EditClubFrag();
                Bundle data = new Bundle();
                data.putString("clubID", clubName.getTag().toString());
                frag.setArguments(data);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,frag).commit();
            }
        });

        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateEventFrag frag = new CreateEventFrag();
                Bundle data = new Bundle();
                data.putString("clubID", clubName.getTag().toString());
                frag.setArguments(data);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,frag).commit();
            }
        });

        //Use Volley Singleton to Update Page UI
        RestSingleton restSingleton = RestSingleton.getInstance(view.getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, restSingleton.getUrl() + "getClub/" + getArguments().getString("clubID"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            updateUI(new JSONObject(response));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error connecting", String.valueOf(error));
            }
        });
        restSingleton.addToRequestQueue(stringRequest);

        //Return View
        return view;
    }


    /**
     * Gets the layout components from club_layout.xml
     * @return nothing
     */
    private void getLayoutComponents() {
        clubImage = view.findViewById(R.id.clubImage);
        clubName = (TextView) view.findViewById(R.id.ClubNameView);
        clubDescription = (TextView) view.findViewById(R.id.DescriptionView);
        clubLocation = (TextView) view.findViewById(R.id.LocationView);
        clubLocationString = "Location not provided";
        clubTime = (TextView) view.findViewById(R.id.TimeView);
        joinLeave= view.findViewById(R.id.ClubJoinLeave);
        showLocation = view.findViewById(R.id.showLocation);
        members = (LinearLayout) view.findViewById(R.id.memberList);
        eventsList = (LinearLayout) view.findViewById(R.id.eventsList);
        editClub = (FloatingActionButton) view.findViewById(R.id.EditClub);
        addEvent = (FloatingActionButton) view.findViewById(R.id.AddEvent);
    }

    /**
     * {"name":"",
     * "description":"",
     * "members":{
     * "admin":{"name":"","userID":""},
     * "regular":[]
     * },
     * "events":[]}
     */

    /**
     * Adds the club information to the page depending on which club was clicked. Inside there are several onClicks relevant to different items in lists being
     * clicked within the club layout.
     * @param res The response from the database
     * @throws JSONException Error in JSON processing
     * @see JSONException
     */
    private void updateUI(JSONObject res) throws JSONException{
        view.setVisibility(VISIBLE);
        getActivity().findViewById(R.id.PageLoading).setVisibility(GONE);

        clubName.setText(res.getString("name"));
        clubName.setTag(res.getString("_id"));

        if(res.getString("description").length()>0) {
            clubDescription.setText("What: "+res.getString("description"));
        } else {
            clubDescription.setText("No information given");
        }

        if(res.getString("location").length()>0) {
            clubLocation.setVisibility(VISIBLE);
            showLocation.setVisibility(VISIBLE);
            clubLocationString = res.getString("location");
            clubLocation.setText("Where: "+clubLocationString);
        } else {
            clubLocation.setVisibility(GONE);
            showLocation.setVisibility(GONE);
            clubLocationString = "Location not provided";
            clubLocation.setText(clubLocationString);
        }

        if(res.getString("time").length()>0) {
            clubTime.setVisibility(VISIBLE);
            clubTime.setText("When: "+res.getString("time"));
        } else {
            clubTime.setVisibility(GONE);
            clubTime.setText("Meeting time not provided");
        }

        if (res.getString("profilePic")!=null) {
            Glide.with(this)
                    .load(res.getString("profilePic"))
                    .error(
                            Glide.with(this)
                                    .load(getString(R.string.url_home_depot))
                                    .apply(RequestOptions.circleCropTransform())
                    )
                    .apply(RequestOptions.circleCropTransform())
                    .into(clubImage);
        }

        JSONObject memberJson = res.getJSONObject("members");

        JSONArray regulars = memberJson.getJSONArray("regular");
        final JSONObject admins = memberJson.getJSONObject("admin");

        JSONArray events = res.getJSONArray("events");

        String userID = UserSingleton.getInstance().getUserID();

        showLocation.setText("Show Building on Map");
        showLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });

        joinLeave.setText("Join CLub");
        joinLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinClub();
            }
        });

            for (int i = 0; i < events.length(); i++) {
                final JSONObject event = events.getJSONObject(i);

                String name = event.getString("name");
                String day = event.getString("date");

                TextView eventText = new TextView(view.getContext());

                eventText.setText(name + " : " +day);
                eventText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                eventText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventFrag frag = new EventFrag();
                        Bundle data = new Bundle();
                        try {
                            data.putString("eventID", event.get("_id").toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        frag.setArguments(data);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,frag).commit();
                    }       });

                eventsList.addView(eventText);
            }



        if(admins.getString("userID").equals(userID)){
            editClub.show();
            addEvent.show();
            joinLeave.setVisibility(GONE);
        }

        //------Image Section------

        //Admin linear layout
        LinearLayout linearMember = new LinearLayout(view.getContext());
        linearMember.setOrientation(LinearLayout.HORIZONTAL);
        linearMember.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams layoutParams;
        String id = admins.getString("userID");
        Space memberSpace;



        //Vertical space for formatting
        memberSpace = new Space(view.getContext());
        layoutParams = new LinearLayout.LayoutParams(16, 16);
        memberSpace.setLayoutParams(layoutParams);
        members.addView(memberSpace);



        //Admin image
        final ImageView adminImage = new ImageView(view.getContext());
        if (admins.getString("profilePic")!=null) {
            Glide.with(this)
                    .load(admins.getString("profilePic"))
                    .error(
                            Glide.with(this)
                                    .load(getString(R.string.url_home_depot))
                                    .apply(RequestOptions.circleCropTransform())
                    )
                    .apply(RequestOptions.circleCropTransform())
                    .into(adminImage);
        }
        layoutParams = new LinearLayout.LayoutParams(100, 100);
        adminImage.setLayoutParams(layoutParams);
        adminImage.setTag(id);
        adminImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ProfileFrag frag = new ProfileFrag();
                Bundle data = new Bundle();
                try {
                    data.putString("userID", admins.get("userID").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                frag.setArguments(data);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,frag).commit();
                return false;
            }
        });
        linearMember.addView(adminImage);



        //Horizontal space for formatting
        memberSpace = new Space(view.getContext());
        layoutParams = new LinearLayout.LayoutParams(8, 8);
        memberSpace.setLayoutParams(layoutParams);
        linearMember.addView(memberSpace);



        //Admin name
        TextView adminText = new TextView(view.getContext());
        adminText.setText(admins.get("name").toString() + " " + getEmojiByUnicode(0x1F451));
        adminText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        adminText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfileFrag frag = new ProfileFrag();
                Bundle data = new Bundle();
                try {
                    data.putString("userID", admins.get("userID").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                frag.setArguments(data);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,frag).commit();
            }
        });
        adminText.setTag(id);
        adminText.setGravity(Gravity.CENTER_VERTICAL);
        linearMember.addView(adminText);

        members.addView(linearMember);



        //Regular Members
        for (int i=0;i<regulars.length();i++){
            final JSONObject curMember = regulars.getJSONObject(i);
            linearMember = new LinearLayout(view.getContext());
            linearMember.setOrientation(LinearLayout.HORIZONTAL);
            linearMember.setGravity(Gravity.CENTER_VERTICAL);
            id = curMember.getString("userID");



            //Vertical space for formatting
            memberSpace = new Space(view.getContext());
            layoutParams = new LinearLayout.LayoutParams(16, 16);
            memberSpace.setLayoutParams(layoutParams);
            members.addView(memberSpace);



            //Member image
            final ImageView memberImage = new ImageView(view.getContext());
            if (curMember.getString("profilePic")!=null) {
                Glide.with(this)
                        .load(curMember.getString("profilePic"))
                        .error(
                                Glide.with(this)
                                        .load(getString(R.string.url_home_depot))
                                        .apply(RequestOptions.circleCropTransform())
                        )
                        .apply(RequestOptions.circleCropTransform())
                        .into(memberImage);
            }
            layoutParams = new LinearLayout.LayoutParams(100, 100);
            memberImage.setLayoutParams(layoutParams);
            memberImage.setTag(id);
            memberImage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    ProfileFrag frag = new ProfileFrag();
                    Bundle data = new Bundle();
                    try {
                        data.putString("userID", curMember.get("userID").toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    frag.setArguments(data);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,frag).commit();
                    return false;
                }
            });
            linearMember.addView(memberImage);



            //Horizontal space for formatting
            memberSpace = new Space(view.getContext());
            layoutParams = new LinearLayout.LayoutParams(8, 8);
            memberSpace.setLayoutParams(layoutParams);
            linearMember.addView(memberSpace);



            //Member name
            String name = curMember.getString("name");
            TextView memberName = new TextView(view.getContext());
            memberName.setText(name);
            memberName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            memberName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProfileFrag frag = new ProfileFrag();
                    Bundle data = new Bundle();
                    try {
                        data.putString("userID", curMember.get("userID").toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    frag.setArguments(data);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,frag).commit();
                }
            });
            memberName.setTag(id);
            memberName.setGravity(Gravity.CENTER_VERTICAL);
            linearMember.addView(memberName);

            members.addView(linearMember);
        }
    }

    /**
     * Checks if a club has a location set before starting a Google Maps intent.
     * Shows toast notification if no location has been set by the club's admin.
     */
    private void openMap() {
        if (clubLocation.getText() != "Location not provided") {
            mapStartIntent();
        }
        else {
            Toast.makeText(getView().getContext(), "Club location not provided.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Opens club location in Google Maps app. Converts campus room locations
     * to what building they are located in
     *
     */
    public void mapStartIntent() {
        Uri gmmIntentUri;   // the intent used for the Google Maps intent
        if (clubLocationString.contains("ABAH") || clubLocationString.contains("A. B.")) {
            gmmIntentUri = Uri.parse("geo:46.8185,-92.0841?q=A.+B.+Anderson+Hall");
        }
        else if (clubLocationString.contains("BagC") || clubLocationString.contains("Bagley")) {
            gmmIntentUri = Uri.parse("geo:46.8185,-92.0841?q=BagC");
        }
        else if (clubLocationString.contains("BohH") || clubLocationString.contains("Bohannon")) {
            gmmIntentUri = Uri.parse("geo:46.8185,-92.0841?q=BohH");
        }
        else if (clubLocationString.contains("HCAMS") || clubLocationString.contains("Heikkila")) {
            gmmIntentUri = Uri.parse("geo:46.8185,-92.0841?q=HCAMS");
        }
        else if (clubLocationString.contains("Chem")) {
            gmmIntentUri = Uri.parse("geo:46.8185,-92.0841?q=Chemistry");
        }
        else if (clubLocationString.contains("ChPk") || clubLocationString.contains("Chester Park")) {
            gmmIntentUri = Uri.parse("geo:46.8185,-92.0841?q=ChPk");
        }
        else if (clubLocationString.contains("Cina")) {
            gmmIntentUri = Uri.parse("geo:46.8185,-92.0841?q=Cina");
        }
        else if (clubLocationString.contains("Darland") || clubLocationString.contains("DAdB")) {
            gmmIntentUri = Uri.parse("geo:46.8185,-92.0841?q=Darland");
        }
        else if (clubLocationString.contains("Edu")) {
            gmmIntentUri = Uri.parse("geo:46.8185,-92.0841?q=EduE");
        }
        else if (clubLocationString.contains("Civ")) {
            gmmIntentUri = Uri.parse("geo:46.8185,-92.0841?q=Swenson+Civil");
        }
        else if (clubLocationString.contains("Eng")) {
            gmmIntentUri = Uri.parse("geo:46.8185,-92.0841?q=UMD+Engineering");
        }
        else if (clubLocationString.contains("GFMS") || clubLocationString.contains("Malosky")) {
            gmmIntentUri = Uri.parse("geo:46.8185,-92.0841?q=Malosky+Stadium");
        }
        else if (clubLocationString.contains("HH") || clubLocationString.contains("Heller")) {
            gmmIntentUri = Uri.parse("geo:46.8185,-92.0841?q=Heller+Hall");
        }
        else if (clubLocationString.contains("Hum")) {
            gmmIntentUri = Uri.parse("geo:46.8185,-92.0841?q=Humanities");
        }
        else if (clubLocationString.contains("KAML") || clubLocationString.contains("Library") && !clubLocationString.contains("Drive")) {
            gmmIntentUri = Uri.parse("geo:46.8185,-92.0841?q=KAML");
        }
        else if (clubLocationString.contains("Plaza")) {
            gmmIntentUri = Uri.parse("geo:46.8185,-92.0841?q=Kirby+Plaza");
        }
        else if (clubLocationString.contains("KSC") || clubLocationString.contains("Kirby") && !clubLocationString.contains("Drive")) {
            gmmIntentUri = Uri.parse("geo:46.8185,-92.0841?q=Kirby+Student+Center");
        }
        else if (clubLocationString.contains("LSBE") || clubLocationString.contains("Labovitz")) {
            gmmIntentUri = Uri.parse("geo:46.8185,-92.0841?q=LSBE");
        }
        else if (clubLocationString.contains("LSci") || clubLocationString.contains("Life")) {
            gmmIntentUri = Uri.parse("geo:46.8185,-92.0841?q=LSci");
        }
        else if (clubLocationString.contains("MPAC") || clubLocationString.contains("Performing")) {
            gmmIntentUri = Uri.parse("geo:46.8185,-92.0841?q=MPAC");
        }
        else if (clubLocationString.contains("MWAH")) {
            gmmIntentUri = Uri.parse("geo:46.8185,-92.0841?q=MWAH");
        }
        else if (clubLocationString.contains("MWAP")) {
            gmmIntentUri = Uri.parse("geo:46.8185,-92.0841?q=MWAP");
        }
        else if (clubLocationString.contains("Mon")) {
            gmmIntentUri = Uri.parse("geo:46.8185,-92.0841?q=MonH");
        }
        else if (clubLocationString.contains("DC") || clubLocationString.contains("Dining Center")) {
            gmmIntentUri = Uri.parse("geo:46.8185,-92.0841?q=RDC");
        }
        else if (clubLocationString.contains("Med")) {
            gmmIntentUri = Uri.parse("geo:46.8185,-92.0841?q=SMed");
        }
        else if (clubLocationString.contains("SCC") || clubLocationString.contains("Solon")) {
            gmmIntentUri = Uri.parse("geo:46.8185,-92.0841?q=Solon+Campus+Center");
        }
        else if (clubLocationString.contains("SpHC") || clubLocationString.contains("Sports")) {
            gmmIntentUri = Uri.parse("geo:46.8185,-92.0841?q=SpHC");
        }
        else if (clubLocationString.contains("SSB") || clubLocationString.contains("Swenson")) {
            gmmIntentUri = Uri.parse("geo:46.8185,-92.0841?q=Swenson+Science");
        }
        else if (clubLocationString.contains("TMA") || clubLocationString.contains("Tweed")) {
            gmmIntentUri = Uri.parse("geo:46.8185,-92.0841?q=Tweed");
        }
        else if (clubLocationString.contains("VKH") || clubLocationString.contains("Voss-Kovach")) {
            gmmIntentUri = Uri.parse("geo:46.8185,-92.0841?q=VKH");
        }
        else if (clubLocationString.contains("WWFH") || clubLocationString.contains("Field House") || clubLocationString.contains("Fieldhouse")) {
            gmmIntentUri = Uri.parse("geo:46.8185,-92.0841?q=Wards+Field+House");
        }
        else if (clubLocationString.contains("WMH") || clubLocationString.contains("Weber")) {
            gmmIntentUri = Uri.parse("geo:46.8185,-92.0841?q=Weber");
        }
        else if (clubLocationString.contains("LSH") || clubLocationString.contains("Lake Superior Hall")) {
            gmmIntentUri = Uri.parse("geo:46.8185,-92.0841?q=LSH");
        }
        else if (clubLocationString.contains("Ianni")) {
            gmmIntentUri = Uri.parse("geo:46.8185,-92.0841?q=Ianni");
        }
        else if (clubLocationString.contains("Heaney") && !clubLocationString.contains("Service")) {
            gmmIntentUri = Uri.parse("geo:46.8185,-92.0841?q=Heaney+Hall");
        }
        else if (clubLocationString.contains("Heaney") && clubLocationString.contains("Service")) {
            gmmIntentUri = Uri.parse("geo:46.8185,-92.0841?q=Heaney+Service+Center");
        }
        else if (clubLocationString.contains("Griggs")) {
            gmmIntentUri = Uri.parse("geo:46.8185,-92.0841?q=Griggs+Hall");
        }
        else {
            gmmIntentUri = Uri.parse("geo:46.8185,-92.0841?q=" + clubLocationString);
        }

        Intent locationIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        locationIntent.setPackage("com.google.android.apps.maps");
        startActivity(locationIntent);
    }

    private void joinClub() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, RestSingleton.getInstance(view.getContext()).getUrl() + "joinClub", joinLeaveObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        joinLeave.setText("Leave Club");
                        joinLeave.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                leaveClub();
                            }
                        });

                        TextView userText = new TextView(view.getContext());
                        userText.setText(UserSingleton.getInstance().getName());
                        userText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

                        members.addView(userText);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error connecting", String.valueOf(error));
            }
        });

        RestSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void leaveClub() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, RestSingleton.getInstance(view.getContext()).getUrl() + "leaveClub", joinLeaveObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ProfileFrag frag = new ProfileFrag();
                        Bundle data = new Bundle();
                        data.putString("userID", UserSingleton.getInstance().getUserID());
                        frag.setArguments(data);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,frag).commit();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Error connecting", String.valueOf(error));
            }
        });

        RestSingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }
}