package com.example.cs4532.umdalive.fragments.base;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.Manifest;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.cs4532.umdalive.PermissionUtils;
import com.example.cs4532.umdalive.R;
import com.example.cs4532.umdalive.RestSingleton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MapFragment extends Fragment {

    public ArrayList<MarkerExtras> getMarkersExtrasList() {
        return markersExtrasList;
    }

    ArrayList<MarkerExtras> markersExtrasList = new ArrayList<>();


    final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    boolean permissionDenied = false;
    public String[][] campusBuildingsArray = {{"ABAH", "1121 University Dr, Duluth, MN 55812"},
            {"BagC", "703 Oakland Circle Duluth, MN 55812"}, {"BohH", "1207 Ordean Court Duluth, MN 55812"}, {"Chem", "1039 University Drive\n" +
            "Duluth, MN 55812-3020"}, {"ChPk", "31 West College Street\n" +
            "Duluth, MN 55812-1106"}, {"CinaH", "1123 University Drive\n" +
            "Duluth, MN 55812-3006"}, {"DAdB", "1049 University Drive\n" +
            "Duluth, MN 55812-3011"}, {"EduE", "412 Library Drive\n" +
            "Duluth, MN 55812-3029"}, {"Engr", "1303 Ordean Court\n" +
            "Duluth, MN 55812-3025"}, {"GFMS", "1336 University Drive\n" +
            "Duluth, MN 55812-3045"}, {"HS", "615 Niagara Court\n" +
            "Duluth, MN 55812-3065"}, {"HCAMS", "1038 University Drive\n" +
            "Duluth, MN 55812"}, {"HH", "1114 Kirby Drive\n" +
            "Duluth, MN 55812-3036"}, {"H", "1201 Ordean Court\n" +
            "Duluth, MN 55812-3041"}, {"KAML", "416 Library Drive\n" +
            "Duluth, MN 55812-3001"}, {"KPlz", "1208 Kirby Drive\n" +
            "Duluth, MN 55812-3095"}, {"KSC", "1120 Kirby Drive\n" +
            "Duluth, MN 55812-3085"}, {"LSBE", "1318 Kirby Drive\n" +
            "Duluth, MN 55812-3002"}, {"LAnnx", "1314 Kirby Drive\n" +
            "Duluth, MN 55812-3002"}, {"LSci", "1110 Kirby Drive\n" +
            "Duluth, MN 55812-3003"}, {"MPAC", "1215 Ordean Court\n" +
            "Duluth, MN 55812-3008"}, {"MWAH", "1023 University Drive\n" +
            "Duluth, MN 55812-3009"}, {"MWAP", "1023 University Drive\n" +
            "Duluth, MN 55812-3009"}, {"MonH", "1211 Ordean Court\n" +
            "Duluth, MN 55812-3012"}, {"RDC", "1120 Kirby Drive\n" +
            "Duluth, MN 55812-3085"}, {"SMed", "1035 University Drive\n" +
            "Duluth, MN 55812-3031"}, {"SCC", "1117 University Drive\n" +
            "Duluth, MN 55812-3000"}, {"SpHC", "1216 Ordean Court\n" +
            "Duluth, MN 55812-3032"}, {"SCiv", "1405 University Drive\n" +
            "Duluth, MN 55812-3007"}, {"SSB", "1035 Kirby Drive\n" +
            "Duluth, MN 55812-3004"}, {"TMA", "1201 Ordean Court\n" +
            "Duluth, MN 55812-3041"}, {"VKH", "1305 Ordean Court\n" +
            "Duluth, MN 55812-3042"}, {"WWFH", "1228 University Drive\n" +
            "Duluth, MN 55812-3043"}, {"WMH", "1151 University Drive\n" +
            "Duluth, MN 55812-3048"}, {"BH", "1320 Maplewood Court\n" +
            "Duluth, MN 55812-3015"}, {"GoldH - A", "1205 Village Lane\n" +
            "Duluth, MN 55812-3040"}, {"GoldH - B", "1215 Village Lane\n" +
            "Duluth, MN 55812-3016"}, {"GoldH - C", "1225 Village Lane\n" +
            "Duluth, MN 55812-3021"}, {"GH A-D", "509 Niagara Court\n" +
            "Duluth, MN 55812-3050"}, {"GH E-F", "508 Niagara Court\n" +
            "Duluth, MN 55812-3055"}, {"GH K-N", "1220 Maplewood Court\n" +
            "Duluth, MN 55812-3060"}, {"GH P-S", "1224 Maplewood Court\n" +
            "Duluth, MN 55812-3026"}, {"HHa", "1220 Village Lane\n" +
            "Duluth, MN 55812-3070"}, {"HHSC", "1315 Maplewood Court\n" +
            "Duluth, MN 55812-3075"}, {"JApts - Cuyuna", "1115 Junction Avenue\n" +
            "Duluth, MN 55812-2408"}, {"JApts - Mesabi", "1135 Junction Avenue\n" +
            "Duluth, MN 55812-2413"}, {"LSH", "513 Niagara Court\n" +
            "Duluth, MN 55812-3046"}, {"LAIH", "506 Niagara Court\n" +
            "Duluth, MN 55812"}, {"OApts - Aspen", "616 Oakland Circle\n" +
            "Duluth, MN 55812-3014"}, {"OApts - Balsam", "623 Oakland Circle\n" +
            "Duluth, MN 55812-3017"}, {"OApts - Basswood", "621 Oakland Circle\n" +
            "Duluth, MN 55812-3018"}, {"OApts - Birch", "619 Oakland Circle\n" +
            "Duluth, MN 55812-3019"}, {"OApts - Oak", "618 Oakland Circle\n" +
            "Duluth, MN 55812-3022"}, {"VH", "1105 Kirby Drive\n" +
            "Duluth, MN 55812-3039"}};

    public String getAddressFromArray(String building) {
        String address = "";
        for (int i = 0; i < campusBuildingsArray.length; i++) {
            if (campusBuildingsArray[i][0] == building) {
                address = campusBuildingsArray[i][1];
                break;
            }
        }
        return address;
    }

    public ArrayList<String> getBuildingsArray() {
        ArrayList<String> array = new ArrayList<>();
        for (int i = 0; i < campusBuildingsArray.length; i++) {
            array.add(i, campusBuildingsArray[i][0]);
        }
        return array;
    }

    //for adding markers on the map from string address
    public LatLng getLocationFromAddress(Context context, String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;
        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return p1;
    }

    //for getting the string address from the marker position field which is in latlng
    public String getAddressFromLocation(Context context, LatLng latLng) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        String p1 = null;
        try {
            address = coder.getFromLocation(latLng.latitude, latLng.longitude, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            p1 = location.getAddressLine(0);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return p1;
    }

    private MarkerExtras retrieveMarkerExtras(Marker marker) {
        MarkerExtras markerExtras = new MarkerExtras();
        for (int i = 0; i < getMarkersExtrasList().size(); i++) {
            if (marker.getSnippet().equals(getMarkersExtrasList().get(i).getID())) {
                markerExtras = getMarkersExtrasList().get(i);
                Log.d("MarkerExtras", "retrieveMarkerExtras: Found Match!");
                break;
            }
        }
        return markerExtras;
    }

    // Creates the custom info boxes whenever a marker is pressed.
    private void createCustomInfoBoxes(final GoogleMap googleMap) {
        final GoogleMap mMap = googleMap;
        if (mMap != null) {
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    MarkerExtras markerExtras = retrieveMarkerExtras(marker);

                    View infoBox = getLayoutInflater().inflate(R.layout.custom_marker_info_box, null);
                    Log.d("infoBox Width", "getInfoContents: " + infoBox.getWidth());

                    TextView clubName = infoBox.findViewById(R.id.marker_name);

                    TextView eventName = infoBox.findViewById(R.id.marker_event_name);

                    TextView clubLocation = infoBox.findViewById(R.id.marker_location);


                    TextView clubTime = infoBox.findViewById(R.id.marker_time);

                    TextView clubDescription = infoBox.findViewById(R.id.marker_description);


                    ImageView clubPhoto = infoBox.findViewById(R.id.marker_photo);


                    clubName.setText(Html.fromHtml("<b>Club: </b>" + markerExtras.getClub()));
                    eventName.setText(Html.fromHtml("<b>Event: </b>" + markerExtras.getEventName()));
                    clubLocation.setText(Html.fromHtml("<b>Location: </b>" + getAddressFromLocation(getContext(), marker.getPosition())));
                    clubTime.setText(Html.fromHtml("<b>Time: </b>" + markerExtras.getTime()));
                    clubDescription.setText(Html.fromHtml("<b>Description: </b>" + markerExtras.getDescription()));

                    return infoBox;
                }
            });
        }
    }


    private StringBuffer testClubs(JSONArray clubs, String clubID) throws JSONException {
        StringBuffer clubName = new StringBuffer("Not Used Yet.");
        for(int i = 0; i < clubs.length(); i++){
            if(clubs.getJSONObject(i).getString("_id").equals(clubID)){
                clubName.delete(0, clubName.length());
                clubName.append(clubs.getJSONObject(i).getString("name"));
                Log.d("cLUB Name", "testClubs: " + clubs.getJSONObject(i).getString("name"));
                break;
            }
        }

        return clubName;
    }

    public StringBuffer getClub(final String clubID){
        final StringBuffer clubName = new StringBuffer("Not used yet.");
        RestSingleton restSingleton = RestSingleton.getInstance(Objects.requireNonNull(getView()).getContext());
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET,
                restSingleton.getUrl() + "getAllClubs",
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    JSONArray clubs = response.getJSONArray("clubs");
                    clubName.delete(0, clubName.length());
                    clubName.append(testClubs(clubs, clubID));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("getClub", "onErrorResponse: " + error);
            }
        });
        restSingleton.addToRequestQueue(objectRequest);

        return clubName;
    }


    private void addEventMarker(GoogleMap googleMap, JSONObject event) throws JSONException {
        if (event.has("location")) {
            if (!event.getString("location").isEmpty()) {
                MarkerOptions newEventOptions = new MarkerOptions();
                newEventOptions.title(event.getString("name"));
                newEventOptions.snippet(event.getString("_id"));
                Bitmap bulldogLogo = BitmapFactory.decodeResource(getResources(), R.drawable.bulldoglogonoborder);
                newEventOptions.icon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(bulldogLogo, 125, 125, false)));
                Log.d("LatLng null check", "addEventMarker: location:" + event.getString("location"));
                newEventOptions.position(getLocationFromAddress(Objects.requireNonNull(getView()).getContext(), event.getString("location")));
                googleMap.addMarker(newEventOptions);
                MarkerExtras markerExtras = new MarkerExtras(MapFragment.this, newEventOptions,
                        event.getString("_id"),
                        event.getString("description"),
                        event.getString("time"),
                        event.getString("date"),
                        event.getString("club"),
                        event.getString("name"));
                markersExtrasList.add(markerExtras);
            }
        }
    }

    private void addAllEventsToMap(final GoogleMap googleMap) {
        RestSingleton restSingleton = RestSingleton.getInstance(Objects.requireNonNull(getView()).getContext());
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET,
                restSingleton.getUrl() + "getAllEvents",
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray events = response.getJSONArray("events");
                    for (int i = 0; i < events.length(); i++) {
                        addEventMarker(googleMap, events.getJSONObject(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        restSingleton.getRequestQueue().add(objectRequest);    /*addToRequestQueue(arrayRequest);*/
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
//        Log.d("TestLocation", "getLocationFromAddress(): " + getLocationFromAddress(getContext(), campusBuildingsArray[30][1]));

        //Initialize view
        final View view = inflater.inflate(R.layout.fragment_map, container, false);


        //Initialize map fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);

        //Async map
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                addAllEventsToMap(googleMap);
                // Add all the clubs:
//                addEvents(googleMap);
                //Set starting location to be UMD Campus
                LatLng umdLatLng = new LatLng(46.8185, -92.0841);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(umdLatLng, 15));
                // Initialize the custom info boxes for the markers.
                createCustomInfoBoxes(googleMap);
                googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                    @Override
                    public boolean onMyLocationButtonClick() {
                        Toast.makeText(getView().getContext(), "MyLocation button clicked", Toast.LENGTH_SHORT).show();
                        // Return false so that we don't consume the event and the default behavior still occurs
                        // (the camera animates to the user's current position).
                        return false;
                    }
                });
                googleMap.setOnMyLocationClickListener(new GoogleMap.OnMyLocationClickListener() {
                    @Override
                    public void onMyLocationClick(@NonNull Location location) {
                        Toast.makeText(getView().getContext(), "Current location:\n" + location, Toast.LENGTH_LONG).show();
                    }
                });

                enableMyLocation(googleMap);

            }

            private void enableMyLocation(GoogleMap map) {
                // [START maps_check_location_permission]
                if (ContextCompat.checkSelfPermission(getView().getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    if (map != null) {
                        map.setMyLocationEnabled(true);
                    }
                } else {
                    // Permission to access the location is missing. Show rationale and request permission
                    PermissionUtils.requestPermission((AppCompatActivity) getActivity(), LOCATION_PERMISSION_REQUEST_CODE,
                            Manifest.permission.ACCESS_FINE_LOCATION, true);
                }
                // [END maps_check_location_permission]
            }

            ;


            protected void onResumeFragments() {
                //super.onResumeFragments();
                if (permissionDenied) {
                    // Permission was not granted, display error dialog.
                    showMissingPermissionError();
                    permissionDenied = false;
                }
            }

            ;

            /**
             * Displays a dialog with error message explaining that the location permission is missing.
             */
            private void showMissingPermissionError() {
                PermissionUtils.PermissionDeniedDialog
                        .newInstance(true).show(getFragmentManager(), "dialog");
            }
        });
        return view;
    }
}

class MarkerExtras {
    MapFragment mapFragment;
    MarkerOptions marker;
    String id, description, time, date, eventName;
    StringBuffer club;

    public String getID() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public StringBuffer getClub() {
        return club;
    }

    public String getEventName(){
        return eventName;
    }

    //Blank Constructor
    MarkerExtras() {

    }

    MarkerExtras(MapFragment mapFragment, MarkerOptions marker, String id, String description, String time, String date, String club, String eventName) {
        this.mapFragment = mapFragment;
        this.marker = marker;
        this.id = id;
        this.eventName = eventName;

        this.club = mapFragment.getClub(club);

        if (description == null || description.isEmpty()) {
            this.description = "No description set for this event.";
        } else {
            this.description = description;
        }

        if (time == "" || time.isEmpty()) {
            this.time = "No time set for this event.";
        } else {
            this.time = time;
        }

        if (date == null || date.isEmpty()) {
            this.date = "No date set for this event.";
        } else {
            this.date = date;
        }
    }
}


