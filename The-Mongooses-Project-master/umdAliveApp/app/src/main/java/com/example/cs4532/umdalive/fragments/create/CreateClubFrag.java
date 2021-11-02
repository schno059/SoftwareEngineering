package com.example.cs4532.umdalive.fragments.create;


import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.cs4532.umdalive.R;
import com.example.cs4532.umdalive.RestSingleton;
import com.example.cs4532.umdalive.UserSingleton;
import com.example.cs4532.umdalive.fragments.base.ClubFrag;
import com.example.cs4532.umdalive.fragments.base.MapFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;


/**
 * A class that creates the page for creating a club
 *
 * @author Josh Senst
 *
 * 4/26/2018
 *
 */
public class CreateClubFrag extends Fragment {
    View view;

    private EditText ClubName;
    private TextView ClubNamePreview;
    private EditText ClubDescription;
    private EditText ClubLocation;
    private EditText ClubTime;
    private EditText ClubImageURL;
    private TextView ClubImagePrompt;
    private ImageView ClubImage;
    private Button save;
    private String functionalURL;
    private Spinner urlSpinner, locationSpinner;
    private String[] urlArray;
    private CheckBox customURL, offCampus;

    /**
     * Creates the create club page view
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return The view of the create club page
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Create View
        view = inflater.inflate(R.layout.create_club_layout, container, false);

        getLayoutComponents();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClubName.getText().length() > 0 && ClubDescription.getText().length() > 0) {
                    try {
                        createClub();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        loadClubImage();

        //makes club image clickable
        ClubImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                loadClubImage();
                loadClubName();
                return false;
            }
        });

        //makes club name preview refresh when tapped
        ClubNamePreview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                loadClubName();
                return false;
            }
        });

        customURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customURL.isChecked()) {
                    ClubImageURL.setVisibility(View.VISIBLE);
                    ClubImagePrompt.setVisibility(View.VISIBLE);
                    urlSpinner.setVisibility(View.INVISIBLE);
                } else {
                    ClubImageURL.setVisibility(View.INVISIBLE);
                    ClubImagePrompt.setVisibility(View.INVISIBLE);
                    urlSpinner.setVisibility(View.VISIBLE);
                }
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity().getBaseContext(),
                R.array.options_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        urlSpinner.setAdapter(adapter);

        urlSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0 && position<urlArray.length && urlArray[position]!=null && !urlArray[position].equals("")) {
                    ClubImageURL.setText(urlArray[position]);
                    loadClubImage();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do Nothing.
            }
        });

        offCampus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(offCampus.isChecked()){
                    ClubLocation.setVisibility(View.VISIBLE);
                    locationSpinner.setVisibility(View.INVISIBLE);
                    ClubLocation.setText("");
                } else {
                    ClubLocation.setVisibility(View.INVISIBLE);
                    locationSpinner.setVisibility(View.VISIBLE);
                }
            }
        });

        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MapFragment map = new MapFragment();
                Log.d("CreateEventFrag", "onItemSelected: Called.");
                Log.d("CreateEventFrag", "onItemSelected: " + map.getAddressFromArray(locationSpinner.getSelectedItem().toString()));
                ClubLocation.setText(map.getAddressFromArray(locationSpinner.getSelectedItem().toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do Nothing.
            }
        });

        return view;
    }



    /**
     * Gets the layout componenets from create_club_layout.xml
     * @return nothing
     */
    private void getLayoutComponents() {
        ClubName = view.findViewById(R.id.createClubName);
        ClubNamePreview = view.findViewById(R.id.createClubNamePreview);
        ClubDescription = view.findViewById(R.id.createClubDescription);
        ClubLocation = view.findViewById(R.id.createClubLocation);
        ClubTime = view.findViewById(R.id.createClubTime);
        ClubImageURL = view.findViewById(R.id.createClubImageURL);
        ClubImagePrompt = view.findViewById(R.id.createClubImagePrompt);
        ClubImage = view.findViewById(R.id.createClubImageDisplay);
        save = view.findViewById(R.id.createClubSave);
        urlSpinner = view.findViewById(R.id.createClubSpinner);
        urlArray = getResources().getStringArray(R.array.url_array);
        customURL = view.findViewById(R.id.createClubCheckbox);
        functionalURL = getString(R.string.url_home_depot);
        locationSpinner = view.findViewById(R.id.createClubLocationSpinner);
        offCampus = view.findViewById(R.id.offCampusCheckbox);

        ClubImageURL.setVisibility(View.INVISIBLE);
        ClubImagePrompt.setVisibility(View.INVISIBLE);
        urlSpinner.setVisibility(View.VISIBLE);
        locationSpinner.setVisibility(View.VISIBLE);
        ClubLocation.setVisibility(View.INVISIBLE);

        MapFragment map = new MapFragment();
        ArrayAdapter<String> buildingSpinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, map.getBuildingsArray());
        buildingSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(buildingSpinnerAdapter);
    }

    /**
     * Called whenever a user wishes to create a club, and makes the that user and admin
     * @throws JSONException Error in JSON processing
     * @see JSONException
     * @return nothing
     */
    private void createClub() throws JSONException {
        JSONObject members = new JSONObject();
        JSONArray regulars = new JSONArray();
        members.put("admin",UserSingleton.getInstance().getUserID());
        members.put("regular",regulars);

        JSONArray events = new JSONArray();

        JSONObject newClubData = new JSONObject();

        newClubData.put("name", ClubName.getText());
        newClubData.put("description", ClubDescription.getText());
        newClubData.put("location", ClubLocation.getText());
        newClubData.put("time", ClubTime.getText());
        newClubData.put("profilePic", functionalURL);

        newClubData.put("events", events);
        newClubData.put("members", members);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, RestSingleton.getInstance(view.getContext()).getUrl() + "createClub", newClubData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ClubFrag frag = new ClubFrag();
                        Bundle data = new Bundle();
                        try {
                            data.putString("clubID", response.get("_id").toString());
                            System.out.println(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
    private void loadClubImage() {
        if (ClubImageURL.getText().toString().length()>0) {
            Glide.with(this)
                    .load(ClubImageURL.getText().toString())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            //on load failed
                            ClubImageURL.setText(functionalURL);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            //on load success
                            functionalURL = ClubImageURL.getText().toString();
                            return false;
                        }
                    })
                    .error(
                            Glide.with(this)
                                    .load(functionalURL)
                                    .apply(RequestOptions.circleCropTransform())
                    )
                    .apply(RequestOptions.circleCropTransform())
                    .into(ClubImage);
        }
        else
        {
            functionalURL = getString(R.string.url_home_depot);
            Glide.with(this)
                    .load(functionalURL)
                    .apply(RequestOptions.circleCropTransform())
                    .into(ClubImage);
            ClubImageURL.setText(functionalURL);
        }
    }
    private void loadClubName() {
        if(ClubName.getText().toString().equals(""))
        {
            ClubNamePreview.setText("Creating Club");
        }
        else
        {
            ClubNamePreview.setText(ClubName.getText().toString());
        }
    }
    
}
