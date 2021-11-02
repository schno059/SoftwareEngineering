package com.example.cs4532.umdalive.fragments.edit;

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
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.cs4532.umdalive.R;
import com.example.cs4532.umdalive.RestSingleton;
import com.example.cs4532.umdalive.UserSingleton;
import com.example.cs4532.umdalive.fragments.base.AllClubsFrag;
import com.example.cs4532.umdalive.fragments.base.ClubFrag;
import com.example.cs4532.umdalive.fragments.base.MapFragment;
import com.example.cs4532.umdalive.fragments.base.ProfileFrag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Josh on 4/25/2018.
 */

/**
 * @author Josh Senst
 *
 * 4/26/2018
 *
 * Class that creates the Edit Club Page
 */
public class EditClubFrag extends Fragment implements View.OnClickListener {
    //View
    View view;

    //Layout Components
    private TextView EditingClub;
    private EditText NewClubName;
    private EditText NewClubDescription;
    private EditText NewClubLocation;
    private EditText NewClubTime;
    private EditText NewImageURL;
    private Button SaveButton;
    private Button DeleteButton;

    private ImageView image;
    private TextView namePreview;
    private EditText name;
    private EditText description;
    private EditText location;
    private EditText time;
    private EditText url;
    private TextView urlPrompt;
    private Button save;
    private CheckBox deleteCheckbox;
    private Button delete;
    private JSONObject userData;
    private String functionalUrl;
    private Spinner urlSpinner;
    private String[] urlArray;
    private CheckBox customURL;
    private CheckBox offCampusCheckbox;
    private Spinner editClubLocationSpinner;

    /**
     * Creates the page whenever the button for editing a club is pressed
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view The view of the edit club page
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Create View
        view = inflater.inflate(R.layout.edit_club_layou, container, false);

        //Get Layout Components
        getLayoutComponents();

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

        customURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customURL.isChecked()) {
                    url.setVisibility(View.VISIBLE);
                    urlPrompt.setVisibility(View.VISIBLE);
                    urlSpinner.setVisibility(View.INVISIBLE);
                } else {
                    url.setVisibility(View.INVISIBLE);
                    urlPrompt.setVisibility(View.INVISIBLE);
                    urlSpinner.setVisibility(View.VISIBLE);
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (name.getText().length() > 0 && description.getText().length() > 0) {
                    try {
                        editClub();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        deleteCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteCheckbox.isChecked()) {
                    delete.setVisibility(View.VISIBLE);
                    save.setVisibility(View.INVISIBLE);
                } else {
                    delete.setVisibility(View.INVISIBLE);
                    save.setVisibility(View.VISIBLE);
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestSingleton restSingleton = RestSingleton.getInstance(view.getContext());
                StringRequest stringRequest = null;
                try {
                    stringRequest = new StringRequest(Request.Method.DELETE, restSingleton.getUrl() + "deleteClub/" + userData.getString("_id"),
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                restSingleton.addToRequestQueue(stringRequest);
                AllClubsFrag frag = new AllClubsFrag();
                Bundle data = new Bundle();
                frag.setArguments(data);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,frag).commit();
            }
        });
        final MapFragment map = new MapFragment();
        ArrayAdapter<String> buildingAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, map.getBuildingsArray());
        buildingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editClubLocationSpinner.setAdapter(buildingAdapter);

        editClubLocationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("CreateEventFrag", "onItemSelected: Called.");
                Log.d("CreateEventFrag", "onItemSelected: " + map.getAddressFromArray(editClubLocationSpinner.getSelectedItem().toString()));
                location.setText(map.getAddressFromArray(editClubLocationSpinner.getSelectedItem().toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do Nothing.
            }
        });


        offCampusCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(offCampusCheckbox.isChecked()){
                    location.setVisibility(View.VISIBLE);
                    editClubLocationSpinner.setVisibility(View.INVISIBLE);
                    location.setText("");
                } else {
                    location.setVisibility(View.INVISIBLE);
                    editClubLocationSpinner.setVisibility(View.VISIBLE);
                }
            }
        });


        //makes profile pic clickable
        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                loadClubImage();
                loadClubName();
                return false;
            }
        });

        //makes club name preview refresh when tapped
        namePreview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                loadClubName();
                return false;
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
                    url.setText(urlArray[position]);
                    loadClubImage();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do Nothing.
            }
        });

        //Return View
        return view;
    }

    /**
     * Allows the clicking of the editText boxes
     * @param v The text editing area that has been clicked
     * @return nothing
     */
    @Override
    public void onClick(View v) {
        if (v.getTag().toString() == "DELETE") {
            // Moved to a standalone function.
        } else {
            if (name.getText().toString().trim().length() != 0) {
                try {
                    userData.put("name", name.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (description.getText().toString().trim().length() != 0) {
                try {
                    userData.put("description", description.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (location.getText().toString().trim().length() != 0) {
                try {
                    userData.put("location", location.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (time.getText().toString().trim().length() != 0) {
                try {
                    userData.put("time", time.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (url.getText().toString().trim().length() != 0) {
                try {
                    userData.put("profilePic", url.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            RestSingleton restSingleton = RestSingleton.getInstance(view.getContext());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, restSingleton.getUrl() + "editClub/", userData,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error connecting", String.valueOf(error));
                }
            });
            restSingleton.addToRequestQueue(jsonObjectRequest);
            ClubFrag frag = new ClubFrag();
            Bundle data = new Bundle();
            data.putString("clubID", namePreview.getTag().toString());
            frag.setArguments(data);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, frag).commit();
        }
    }

    /**
     * Gets the layout components from edit_club_layou.xml
     * @return nothing
     */
    private void getLayoutComponents() {
        image = view.findViewById(R.id.editClubImageDisplay);
        namePreview = view.findViewById(R.id.editClubNamePreview);
        name = view.findViewById(R.id.editClubName);
        description = view.findViewById(R.id.editClubDescription);
        time = view.findViewById(R.id.editClubTime);
        location = view.findViewById(R.id.editClubLocation);
        save = view.findViewById(R.id.editClubSave);
        deleteCheckbox = view.findViewById(R.id.editClubCheckbox);
        delete = view.findViewById(R.id.editClubDelete);
        url = view.findViewById(R.id.editClubImageURL);
        urlPrompt = view.findViewById(R.id.editClubImagePrompt);
        functionalUrl = getString(R.string.url_home_depot);
        urlSpinner = view.findViewById(R.id.editClubSpinner);
        urlArray = getResources().getStringArray(R.array.url_array);
        customURL = view.findViewById(R.id.editClubImageCheckbox);

        url.setVisibility(View.INVISIBLE);
        urlPrompt.setVisibility(View.INVISIBLE);
        delete.setVisibility(View.INVISIBLE);
        urlSpinner.setVisibility(View.VISIBLE);
        editClubLocationSpinner = view.findViewById(R.id.editClubLocationSpinner);
        offCampusCheckbox = view.findViewById(R.id.editClubOffCampus);
        location.setVisibility(View.INVISIBLE);
    }

    /**
     * Adds the layout components and sets them in editTexts
     * @param res The response from the database
     * @throws JSONException Error in JSON processing
     * @see JSONException
     */
    private void updateUI(JSONObject res) throws JSONException {
        name.setText(res.getString("name"));
        name.setTag(res.getString("_id"));
        delete.setTag("DELETE");
        name.setText(res.getString("name"));
        description.setText(res.getString("description"));
        location.setText(res.getString("location"));
        time.setText(res.getString("time"));
        url.setText(res.getString("profilePic"));
        userData = res;

        //converts getClub call into valid return data
        //vital to prevent member loss
        JSONObject members = (JSONObject) userData.get("members");
        JSONObject admin = (JSONObject) members.get("admin");
        members.put("admin", admin.get("userID"));
        JSONArray regular = (JSONArray) members.get("regular");
        for(int i=0; i<regular.length(); i++)
        {
            System.out.println(regular.getJSONObject(i).get("userID"));
            regular.put(i, regular.getJSONObject(i).get("userID"));
        }

        loadClubImage();
    }

    private void loadClubImage() {
        if (url.getText().toString().length()>0) {
            Glide.with(this)
                    .load(url.getText().toString())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            //on load failed
                            url.setText(functionalUrl);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            //on load success
                            functionalUrl = url.getText().toString();
                            return false;
                        }
                    })
                    .error(
                            Glide.with(this)
                                    .load(functionalUrl)
                                    .apply(RequestOptions.circleCropTransform())
                    )
                    .apply(RequestOptions.circleCropTransform())
                    .into(image);
        }
        else
        {
            functionalUrl = getString(R.string.url_home_depot);
            Glide.with(this)
                    .load(functionalUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(image);
            url.setText(functionalUrl);
        }
    }

    private void loadClubName() {
        if(name.getText().toString().equals(""))
        {
            namePreview.setText("Editing Club");
        }
        else
        {
            namePreview.setText(name.getText().toString());
        }
    }

    private void editClub() throws JSONException {
        userData.put("name", name.getText().toString());
        userData.put("description", description.getText().toString());
        userData.put("location", location.getText().toString());
        userData.put("time", time.getText().toString());
        userData.put("profilePic", functionalUrl);
//        userData.put("members", members);

        System.out.println(userData);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, RestSingleton.getInstance(view.getContext()).getUrl() + "editClub", userData,
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
}
