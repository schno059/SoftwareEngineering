package com.example.cs4532.umdalive.fragments.base;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Paul Sipper
 *
 * 4/26/2018
 * Version 1.0
 *
 * Class that creates the All Clubs Page
 */
public class AllClubsFrag extends Fragment implements View.OnClickListener {

    //View
    View view;

    //Layout Components
    private LinearLayout allClubsLinearLayout;

    /**
     * Creates the page whenever All Clubs is clicked in the app
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view The view of All Clubs
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Create View
        view =  inflater.inflate(R.layout.all_club_layout, container, false);

        //Get Layout Components
        getLayoutComponents();

        //Use Volley Singleton to Update Page UI
        RestSingleton restSingleton = RestSingleton.getInstance(view.getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, restSingleton.getUrl() + "getAllClubs",
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

        //Return the View
        return view;
    }

    /**
     * Allows a user to click on a club name to go to that club's page
     * @param clickedView The club name clicked
     * @return nothing
     */
    @Override
    public void onClick(View clickedView) {
        String TAG = (String) clickedView.getTag();

        ClubFrag frag = new ClubFrag();
        Bundle data = new Bundle();
        data.putString("clubID", TAG);
        frag.setArguments(data);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,frag).commit();

    }

    /**
     * Retrieves all layout components from all_club_layout.xml
     * @return nothing
     */
    private void getLayoutComponents () {
        allClubsLinearLayout = (LinearLayout) view.findViewById(R.id.AllClubsLayout);
    }

    /**
     * Adds club names stored in the database
     * @param res The response from the database
     * @return nothing
     * @exception JSONException Error in JSON processing
     * @see JSONException
     */
    private void updateUI(JSONObject res) throws JSONException {
        getActivity().findViewById(R.id.PageLoading).setVisibility(View.GONE);
        JSONArray allClubs = res.getJSONArray("clubs");
        for (int i=0;i<allClubs.length();i++) {
            //Linear layout
            LinearLayout linearClub = new LinearLayout(view.getContext());
            linearClub.setOrientation(LinearLayout.HORIZONTAL);
            linearClub.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams layoutParams;
            String id = allClubs.getJSONObject(i).getString("_id");



            //Club image
            final ImageView clubImage = new ImageView(view.getContext());
            if (allClubs.getJSONObject(i).getString("profilePic")!=null) {
                Glide.with(this)
                        .load(allClubs.getJSONObject(i).getString("profilePic"))
                        .error(
                                Glide.with(this)
                                        .load(getString(R.string.url_home_depot))
                                        .apply(RequestOptions.circleCropTransform())
                        )
                        .apply(RequestOptions.circleCropTransform())
                        .into(clubImage);
            }
            layoutParams = new LinearLayout.LayoutParams(200, 200);
            clubImage.setLayoutParams(layoutParams);
            clubImage.setTag(id);
            clubImage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    String TAG = (String) clubImage.getTag();

                    ClubFrag frag = new ClubFrag();
                    Bundle data = new Bundle();
                    data.putString("clubID", TAG);
                    frag.setArguments(data);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,frag).commit();
                    return false;
                }
            });
            linearClub.addView(clubImage);



            //Space for formatting
            Space clubSpace = new Space(view.getContext());
            layoutParams = new LinearLayout.LayoutParams(32, 32);
            clubSpace.setLayoutParams(layoutParams);
            linearClub.addView(clubSpace);



            //Club name
            String name = allClubs.getJSONObject(i).getString("name");
            TextView clubName = new TextView(view.getContext());
            clubName.setText(name);
            clubName.setTextSize(24);
            clubName.setOnClickListener(this);
            clubName.setTag(id);
            clubName.setGravity(Gravity.CENTER_VERTICAL);
            linearClub.addView(clubName);

            allClubsLinearLayout.addView(linearClub);

            //Vertical space for formatting
            clubSpace = new Space(view.getContext());
            layoutParams = new LinearLayout.LayoutParams(32, 32);
            clubSpace.setLayoutParams(layoutParams);
            allClubsLinearLayout.addView(clubSpace);
        }
    }
}
