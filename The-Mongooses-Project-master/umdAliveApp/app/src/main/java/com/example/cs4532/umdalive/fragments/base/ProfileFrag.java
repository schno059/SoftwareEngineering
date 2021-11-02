package com.example.cs4532.umdalive.fragments.base;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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
import com.example.cs4532.umdalive.fragments.create.CreateProfileFrag;
import com.example.cs4532.umdalive.fragments.edit.EditProfileFrag;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Requires argument with key of userID to be passed into it before it is added to the frame layout
 */

/**
 * @author Ross Schultz
 *
 * Class that creates the profile page
 */
public class ProfileFrag extends Fragment {

    //View
    View view;

    //Layout Components
    private TextView profileName;
    private TextView profileMajor;
    private TextView profileAbout;
    private LinearLayout profileClubs;
    private ImageView profileImage;
    private FloatingActionButton profileEdit;

    private Bundle profileData;

    /**
     * Creates the view of the profile when navigating to it
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view The view of the profile page
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Create View
        view = inflater.inflate(R.layout.profile_layout, container, false);
        view.setVisibility(View.GONE);
        //Get Layout Components
        getLayoutComponents();

        if (getArguments().getString("userID") == UserSingleton.getInstance().getUserID()) {
            profileEdit.show();
        } else {
            profileEdit.hide();
        }

        profileData = new Bundle();

        profileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditProfileFrag frag = new EditProfileFrag();
                frag.setArguments(profileData);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, frag).commit();
            }
        });

        //Use Volley Singleton to Update Page UI
        RestSingleton restSingleton = RestSingleton.getInstance(view.getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, restSingleton.getUrl() + "getUser/" + getArguments().getString("userID"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.isEmpty()){
                            //Add All Clubs Fragment
                            CreateProfileFrag frag = new CreateProfileFrag();
                            Bundle data = new Bundle();
                            frag.setArguments(data);
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,frag).commit();
                        } else {
                            try {
                                updateUI(new JSONObject(response));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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
     * Gets the layout components from profile_layout.xml
     * @return nothing
     */
    //Sets the Text views of the profile layout
    private void getLayoutComponents() {
        profileName = view.findViewById(R.id.profileName);
        profileMajor = view.findViewById(R.id.profileMajor);
        profileAbout = view.findViewById(R.id.profileAbout);
        profileImage = view.findViewById(R.id.profileImage);
        profileClubs = view.findViewById(R.id.profileClubs);
        profileEdit = view.findViewById(R.id.profileEdit);
    }

    /**
     * Adds the name, major, and about of the profile of the member
     * @param res The response from the database
     * @throws JSONException error in JSON processing
     * @see JSONException
     */
    //Updates the layout so current information is visible
    private void updateUI(JSONObject res) throws JSONException{
        view.setVisibility(View.VISIBLE);

        profileData.putString("data", res.toString());

        getActivity().findViewById(R.id.PageLoading).setVisibility(View.GONE);

        if (res.getString("profilePic")!=null) {
            Glide.with(this)
                    .load(res.getString("profilePic"))
                    .error(
                            Glide.with(this)
                                    .load(getString(R.string.url_home_depot))
                                    .apply(RequestOptions.circleCropTransform())
                    )
                    .apply(RequestOptions.circleCropTransform())
                    .into(profileImage);
        }

        profileName.setText(res.getString("name"));
        profileMajor.setText(res.getString("major"));
        profileAbout.setText(res.getString("description"));

        JSONArray clubArray = res.getJSONArray("clubs");

        for(int i = 0; i < clubArray.length(); i++){
            final JSONObject curClub = (JSONObject) clubArray.get(i);
            LinearLayout linearClub = new LinearLayout(view.getContext());
            linearClub.setOrientation(LinearLayout.HORIZONTAL);
            linearClub.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams layoutParams;
            String id = curClub.getString("_id");
            Space clubSpace;



            //Vertical space for formatting
            clubSpace = new Space(view.getContext());
            layoutParams = new LinearLayout.LayoutParams(16, 16);
            clubSpace.setLayoutParams(layoutParams);
            profileClubs.addView(clubSpace);



            //Club image
            final ImageView clubImage = new ImageView(view.getContext());
            if (curClub.getString("profilePic")!=null) {
                Glide.with(this)
                        .load(curClub.getString("profilePic"))
                        .error(
                                Glide.with(this)
                                        .load(getString(R.string.url_home_depot))
                                        .apply(RequestOptions.circleCropTransform())
                        )
                        .apply(RequestOptions.circleCropTransform())
                        .into(clubImage);
            }
            layoutParams = new LinearLayout.LayoutParams(100, 100);
            clubImage.setLayoutParams(layoutParams);
            clubImage.setTag(id);
            clubImage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    ClubFrag frag = new ClubFrag();
                    Bundle data = new Bundle();
                    try {
                        data.putString("clubID", curClub.get("_id").toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    frag.setArguments(data);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,frag).commit();
                    return false;
                }
            });
            linearClub.addView(clubImage);



            //Horizontal space for formatting
            clubSpace = new Space(view.getContext());
            layoutParams = new LinearLayout.LayoutParams(8, 8);
            clubSpace.setLayoutParams(layoutParams);
            linearClub.addView(clubSpace);



            //Club name
            String name = curClub.getString("name");
            TextView clubName = new TextView(view.getContext());
            clubName.setText(name);
            clubName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            clubName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClubFrag frag = new ClubFrag();
                    Bundle data = new Bundle();
                    try {
                        data.putString("clubID", curClub.get("_id").toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    frag.setArguments(data);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,frag).commit();
                }
            });
            clubName.setTag(id);
            clubName.setGravity(Gravity.CENTER_VERTICAL);
            linearClub.addView(clubName);

            profileClubs.addView(linearClub);
        }



    }
}
