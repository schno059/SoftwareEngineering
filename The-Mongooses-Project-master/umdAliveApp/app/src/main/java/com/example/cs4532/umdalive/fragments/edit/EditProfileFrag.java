
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.cs4532.umdalive.R;
import com.example.cs4532.umdalive.RestSingleton;
import com.example.cs4532.umdalive.UserSingleton;
import com.example.cs4532.umdalive.fragments.base.ProfileFrag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Ross Schultz
 *
 * 4/26/2018
 *
 * Class that creates the Edit Profile page
 */
public class EditProfileFrag extends Fragment {

    View view;

    //Layout Components
    private ImageView image;
    private TextView name;
    private EditText major;
    private EditText about;
    private EditText url;
    private TextView urlPrompt;
    private Button save;
    private JSONObject userData;
    private String functionalUrl;
    private Spinner urlSpinner;
    private String[] urlArray;
    private CheckBox customURL;

    /**
     * Creates the edit profile  view
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view The view of the edit profile view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Create View
        view = inflater.inflate(R.layout.edit_profile_layout, container, false);

        //Get Layout Components
        getLayoutComponents();

        name.setText(UserSingleton.getInstance().getName());
        //Set User Data
        try {
            userData = new JSONObject(getArguments().getString("data"));
//            System.out.println(userData);
            JSONArray clubs = (JSONArray) userData.get("clubs");
//            System.out.println(clubs);
            for(int i=0; i<clubs.length(); i++)
            {
                JSONObject club = clubs.getJSONObject(i);
                JSONObject members = (JSONObject) club.get("members");
                String admin = (String) members.get("admin");
                clubs.put(i, club.get("_id"));
                if(admin.equals(userData.get("userID").toString()))
                {
                    clubs.put(i, club.get("_id"));
                }
            }

            major.setText(userData.getString("major"));
            about.setText(userData.getString("description"));
            url.setText(userData.getString("profilePic"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        loadProfileImage();

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
                if (major.getText().length() > 0 && about.getText().length() > 0) {
                    try {
                        editUser();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        //makes profile pic clickable
        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                loadProfileImage();
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
                    loadProfileImage();
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
     * Gets the layout components from edit_profile_layout.xml
     *
     * @return nothing
     */
    private void getLayoutComponents() {
        image = view.findViewById(R.id.editProfileImageDisplay);
        name = view.findViewById(R.id.editProfileNamePreview);
        major = view.findViewById(R.id.editProfileMajor);
        about = view.findViewById(R.id.editProfileDescription);
        save = view.findViewById(R.id.editProfileSave);
        url = view.findViewById(R.id.editProfileImageURL);
        urlPrompt = view.findViewById(R.id.editProfileImagePrompt);
        functionalUrl = getString(R.string.url_home_depot);
        urlSpinner = view.findViewById(R.id.editProfileSpinner);
        urlArray = getResources().getStringArray(R.array.url_array);
        customURL = view.findViewById(R.id.editProfileCheckbox);

        url.setVisibility(View.INVISIBLE);
        urlPrompt.setVisibility(View.INVISIBLE);
        urlSpinner.setVisibility(View.VISIBLE);
    }

    /**
     * Loads the image for the user's profile if they have one, otherwise their profile image
     * will default to an image of a wagon
     * @return nothing
     */
    private void loadProfileImage() {
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

    /**
     * Enables the editing of the user page's major and description
     * @throws JSONException Error in JSON processing
     * @see JSONException
     * @return nothing
     */
    private void editUser() throws JSONException {
        userData.put("major", major.getText().toString());
        userData.put("description", about.getText().toString());
        userData.put("profilePic", functionalUrl);

        System.out.println(userData);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, RestSingleton.getInstance(view.getContext()).getUrl() + "editUser", userData,
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