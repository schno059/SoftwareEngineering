package com.example.cs4532.umdalive.fragments.edit;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.cs4532.umdalive.R;
import com.example.cs4532.umdalive.RestSingleton;
import com.example.cs4532.umdalive.fragments.base.AllClubsFrag;
import com.example.cs4532.umdalive.fragments.base.EventFrag;
import com.example.cs4532.umdalive.fragments.base.MapFragment;

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
 * Class that allows for the editing of events on the edit events page
 */

public class EditEventFrag  extends Fragment implements View.OnClickListener {
    //View
    View view;

    //Layout Components
    private TextView EditingEvent;
    private EditText NewEventName;
    private EditText NewEventDescription;
    private EditText NewEventTime;
    private EditText NewEventDate;
    private EditText NewEventLocation;
    private Button SaveButton;
    private Button DeleteEvent;
    private Spinner editEventLocationSpinner;
    private CheckBox offCampusCheckbox;

    private JSONObject eventData;

    /**
     * Creates the page for Editing Events when the edit events button is pressed
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return view The View of the edit events page
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Create View
        view = inflater.inflate(R.layout.edit_event_layout, container, false);

        //Get Layout Components
        getLayoutComponents();

        //Use Volley Singleton to Update Page UI
        RestSingleton restSingleton = RestSingleton.getInstance(view.getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, restSingleton.getUrl() + "getEvent/" + getArguments().getString("eventID"),
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

        DeleteEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestSingleton restSingleton = RestSingleton.getInstance(view.getContext());
                StringRequest stringRequest = null;

                try {
                    Log.d("stringRequest", restSingleton.getUrl() + "deleteEvent/" + eventData.getString("_id"));
                    stringRequest = new StringRequest(Request.Method.DELETE, restSingleton.getUrl() + "deleteEvent/" + eventData.getString("_id"),
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        Log.d("IDChecking", eventData.getString("_id"));
                                        updateUI(new JSONObject(response));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("Debugging connecting", String.valueOf(error));
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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, map.getBuildingsArray());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editEventLocationSpinner.setAdapter(adapter);

        editEventLocationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("CreateEventFrag", "onItemSelected: Called.");
                Log.d("CreateEventFrag", "onItemSelected: " + map.getAddressFromArray(editEventLocationSpinner.getSelectedItem().toString()));
                NewEventLocation.setText(map.getAddressFromArray(editEventLocationSpinner.getSelectedItem().toString()));
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
                    NewEventLocation.setVisibility(View.VISIBLE);
                    editEventLocationSpinner.setVisibility(View.INVISIBLE);
                    NewEventLocation.setText("");
                } else {
                    NewEventLocation.setVisibility(View.INVISIBLE);
                    editEventLocationSpinner.setVisibility(View.VISIBLE);
                }
            }
        });

        //Return View
        return view;
    }

    /**
     * Allows for the clicked to edit on the text box
     * @param v The textView clicked
     * @return nothing
     */
    @Override
    public void onClick(View v) {
        /*
        if(v.getTag()=="DELETE"){

            
        } else {

         */
            if (NewEventName.getText().toString().trim().length() != 0) {
                try {
                    eventData.put("name", NewEventName.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (NewEventDescription.getText().toString().trim().length() != 0) {
                try {
                    eventData.put("description", NewEventDescription.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (NewEventDate.getText().toString().trim().length() != 0) {
                try {
                    eventData.put("date", NewEventDate.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (NewEventTime.getText().toString().trim().length() != 0) {
                try {
                    eventData.put("time", NewEventTime.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            RestSingleton restSingleton = RestSingleton.getInstance(view.getContext());
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, restSingleton.getUrl() + "editEvent/", eventData,
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
            EventFrag frag = new EventFrag();
            Bundle data = new Bundle();
            data.putString("eventID", EditingEvent.getTag().toString());
            frag.setArguments(data);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, frag).commit();


    }



    /**
     * Gets the layout components from edit_event_layout.xml
     * @return nothing
     */
    private void getLayoutComponents() {
        EditingEvent = view.findViewById(R.id.EventEditing);
        NewEventName = view.findViewById(R.id.editEventName);
        NewEventDescription = view.findViewById(R.id.editEventDescription);
        NewEventDate = view.findViewById(R.id.editEventDate);
        NewEventTime = view.findViewById(R.id.editEventTime);
        NewEventLocation = view.findViewById(R.id.EventLocation);
        DeleteEvent = view.findViewById(R.id.DeleteEvent);
        SaveButton = view.findViewById(R.id.SaveEvent);
        SaveButton.setOnClickListener(this);
        //DeleteEvent.setOnClickListener(this);
        DeleteEvent.setTag("DELETE");
        offCampusCheckbox = view.findViewById(R.id.editEventOffCampus);
        editEventLocationSpinner = view.findViewById(R.id.editEventLocationSpinner);
        NewEventLocation = view.findViewById(R.id.editEventLocation);
        NewEventLocation.setVisibility(View.INVISIBLE);
    }

    /**
     * Adds the textView boxes from the database into the page
     * @param res The response from the database
     * @throws JSONException Error in JSON processing
     * @see JSONException
     */
    private void updateUI(JSONObject res) throws JSONException{
        Log.d("EventString", res.getString("name"));
        EditingEvent.setText("Editing Event:\n"+res.getString("name"));
        EditingEvent.setTag(res.getString("_id"));
        NewEventName.setText(res.getString("name"));
        NewEventDescription.setText(res.getString("description"));
        NewEventTime.setText(res.getString("time"));
        NewEventDate.setText(res.getString("date"));
        eventData = res;
    }
}