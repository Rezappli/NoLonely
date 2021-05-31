package com.example.nalone.ui.evenements.display;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.android.volley.VolleyError;
import com.example.nalone.R;
import com.example.nalone.enumeration.Visibility;
import com.example.nalone.enumeration.VisibilityMap;
import com.example.nalone.json.JSONController;
import com.example.nalone.json.JSONObjectCrypt;
import com.example.nalone.listeners.JSONArrayListener;
import com.example.nalone.objects.CustomMarker;
import com.example.nalone.objects.CustomRenderer;
import com.example.nalone.objects.Evenement;
import com.example.nalone.ui.evenements.InfosEvenementsActivity;
import com.example.nalone.util.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.example.nalone.util.Constants.MAPVIEW_BUNDLE_KEY;
import static com.example.nalone.util.Constants.USER;
import static com.example.nalone.util.Constants.range;


public class EventMapFragment extends Fragment implements OnMapReadyCallback {

    private View rootView;

    private MapView mMapView;
    private GoogleMap mMap;
    private TextView textViewLocationPrive, textViewLocationAll, textViewLocationPublic, textViewLocationCreate, textViewLocationInscrit;
    private ImageView imageViewLocationPrive, imageViewLocationAll, imageViewLocationPublic, imageViewLocationCreate, imageViewLocationInscrit;

    private static VisibilityMap currentVisibilityMap = VisibilityMap.ALL;

    private List<Evenement> nearby_events;

    private CardView loading;

    private static CameraPosition posCam = null;
    private View viewGrey;
    private Circle circle = null;


    // Bottom sheet
    private BottomSheetBehavior bottomSheetBehaviorDetails;


    private TextView textViewDetailName, textViewDetailCity, textViewDetailDate, textViewDetailTime, textViewDetailNbMembers;
    private ImageView imageViewDetailCategory;

    private ClusterManager<CustomMarker> clusterManager;


    public EventMapFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = rootView.findViewById(R.id.mapView);
        if (USER != null) {
            initGoogleMap(savedInstanceState);
        }
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

        return rootView;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void createFragment() {

        CardView cardViewLocationCreate = rootView.findViewById(R.id.cardViewLocationCreate);
        CardView cardViewLocationPrive = rootView.findViewById(R.id.cardViewLocationPrivate);
        CardView cardViewLocationPublic = rootView.findViewById(R.id.cardViewLocationPublic);
        CardView cardViewLocationInscrit = rootView.findViewById(R.id.cardViewLocationInscrit);
        CardView cardViewLocationAll = rootView.findViewById(R.id.cardViewLocationAll);
        textViewLocationCreate = rootView.findViewById(R.id.textViewLocationCreate);
        textViewLocationPrive = rootView.findViewById(R.id.textViewLocationPrivate);
        textViewLocationPublic = rootView.findViewById(R.id.textViewLocationPublic);
        textViewLocationInscrit = rootView.findViewById(R.id.textViewLocationInscrit);
        textViewLocationAll = rootView.findViewById(R.id.textViewLocationAll);
        imageViewLocationInscrit = rootView.findViewById(R.id.imageViewLocationInscrit);
        imageViewLocationPrive = rootView.findViewById(R.id.imageViewLocationPrive);
        imageViewLocationPublic = rootView.findViewById(R.id.imageViewLocationPublic);
        imageViewLocationCreate = rootView.findViewById(R.id.imageViewLocationCreate);
        imageViewLocationAll = rootView.findViewById(R.id.imageViewLocationAll);
        loading = rootView.findViewById(R.id.loading);
        viewGrey = rootView.findViewById(R.id.viewGreyMap);
        imageViewDetailCategory = rootView.findViewById(R.id.imageViewDetailCategory);


        //Bottom sheet
        final View bottomSheetDetails = rootView.findViewById(R.id.sheetEvent);

        textViewDetailName = rootView.findViewById(R.id.textViewDetailName);
        textViewDetailCity = rootView.findViewById(R.id.textViewDetailCity);
        textViewDetailDate = rootView.findViewById(R.id.textViewDetailDate);
        textViewDetailTime = rootView.findViewById(R.id.textViewDetailTime);
        textViewDetailNbMembers = rootView.findViewById(R.id.textViewDetailNbMembers);

        bottomSheetBehaviorDetails = BottomSheetBehavior.from(bottomSheetDetails);

        bottomSheetBehaviorDetails.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onStateChanged(@NonNull View view, int state) {
                switch (state) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                    case BottomSheetBehavior.STATE_HIDDEN:
                        viewGrey.setVisibility(View.GONE);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        viewGrey.setVisibility(View.VISIBLE);
                        break;

                    case BottomSheetBehavior.STATE_DRAGGING:
                    case BottomSheetBehavior.STATE_SETTLING:
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        viewGrey.setOnClickListener(v -> bottomSheetBehaviorDetails.setState(BottomSheetBehavior.STATE_COLLAPSED));

        cardViewLocationAll.setOnClickListener(v -> {
            hiddeText(textViewLocationAll);
            imageViewLocationAll.setImageDrawable(getResources().getDrawable(R.drawable.location_all_30));
            updateMap(VisibilityMap.ALL);
            currentVisibilityMap = VisibilityMap.ALL;
        });

        cardViewLocationPrive.setOnClickListener(v -> {
            hiddeText(textViewLocationPrive);
            imageViewLocationPrive.setImageDrawable(getResources().getDrawable(R.drawable.location_private_30));
            updateMap(VisibilityMap.PRIVATE);
            currentVisibilityMap = VisibilityMap.PRIVATE;
        });

        cardViewLocationInscrit.setOnClickListener(v -> {
            hiddeText(textViewLocationInscrit);
            imageViewLocationInscrit.setImageDrawable(getResources().getDrawable(R.drawable.location_inscrit_30));
            updateMap(VisibilityMap.REGISTER);
            currentVisibilityMap = VisibilityMap.REGISTER;
        });

        cardViewLocationPublic.setOnClickListener(v -> {
            hiddeText(textViewLocationPublic);
            imageViewLocationPublic.setImageDrawable(getResources().getDrawable(R.drawable.location_public_30));
            updateMap(VisibilityMap.PUBLIC);
            currentVisibilityMap = VisibilityMap.PUBLIC;
        });

        cardViewLocationCreate.setOnClickListener(v -> {
            hiddeText(textViewLocationCreate);
            imageViewLocationCreate.setImageDrawable(getResources().getDrawable(R.drawable.location_create_30));
            updateMap(VisibilityMap.CREATE);
            currentVisibilityMap = VisibilityMap.CREATE;
        });


    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private void hiddeText(TextView tv) {
        textViewLocationInscrit.setVisibility(View.VISIBLE);
        textViewLocationPublic.setVisibility(View.VISIBLE);
        textViewLocationPrive.setVisibility(View.VISIBLE);
        textViewLocationCreate.setVisibility(View.VISIBLE);
        textViewLocationAll.setVisibility(View.VISIBLE);
        tv.setVisibility(View.GONE);
        imageViewLocationInscrit.setImageDrawable(getResources().getDrawable(R.drawable.location_inscrit_24));
        imageViewLocationPrive.setImageDrawable(getResources().getDrawable(R.drawable.location_private_24));
        imageViewLocationPublic.setImageDrawable(getResources().getDrawable(R.drawable.location_public_24));
        imageViewLocationCreate.setImageDrawable(getResources().getDrawable(R.drawable.location_create_24));
        imageViewLocationAll.setImageDrawable(getResources().getDrawable(R.drawable.location_all_24));
    }


    private void initGoogleMap(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        clusterManager = new ClusterManager<>(requireContext(), mMap);
        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);

        mMap.setOnCameraMoveListener(() -> posCam = mMap.getCameraPosition());

        if (posCam == null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(USER.getLatitude(), USER.getLongitude()), 10));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(posCam));
        }

        JSONObjectCrypt params = new JSONObjectCrypt();
        params.putCryptParameter("uid", USER.getUid());
        params.putCryptParameter("latitude", USER.getLatitude());
        params.putCryptParameter("longitude", USER.getLongitude());
        params.putCryptParameter("range", range);

        nearby_events = new ArrayList<>();

        JSONController.getJsonArrayFromUrl(Constants.URL_NEARBY_EVENTS, getContext(), params, new JSONArrayListener() {
            @Override
            public void onJSONReceived(JSONArray jsonArray) {
                Log.w("Response", "Value:" + jsonArray.toString());
                try {
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            nearby_events.add((Evenement) JSONController.convertJSONToObject(jsonArray.getJSONObject(i), Evenement.class));
                        }
                        updateMap(VisibilityMap.ALL);
                        updateCircle();
                    }
                } catch (JSONException e) {
                    Log.w("Response", "Erreur:" + e.getMessage());
                    Toast.makeText(getContext(), getResources().getString(R.string.error_event), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onJSONReceivedError(VolleyError volleyError) {
                Log.w("Response", "Erreur:" + volleyError.toString());
                Toast.makeText(getContext(), getResources().getString(R.string.error_event), Toast.LENGTH_SHORT).show();
            }
        });


        loading.setVisibility(View.GONE);


    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void updateMap(VisibilityMap visibilityMap) {
        mMap.clear();
        clusterManager.clearItems();
        switch (visibilityMap) {
            case ALL:
                for (Evenement e : nearby_events) {
                    addMarkerOnMap(e);
                }
                break;
            case PUBLIC:
                for (Evenement e : nearby_events) {
                    if (e.getVisibility().equals(Visibility.PUBLIC)) {
                        if (!e.getOwner_uid().equalsIgnoreCase(USER.getUid())) {
                            addMarkerOnMap(e);
                        }
                    }
                }
                break;
            case PRIVATE:
                for (Evenement e : nearby_events) {
                    if (e.getVisibility().equals(Visibility.PRIVATE)) {
                        if (!e.getOwner_uid().equalsIgnoreCase(USER.getUid())) {
                            addMarkerOnMap(e);
                        }
                    }
                }
                break;
            case CREATE:
                for (Evenement e : nearby_events) {
                    if (e.getOwner_uid().equalsIgnoreCase(USER.getUid())) {
                        addMarkerOnMap(e);
                    }
                }
                break;
            case REGISTER:
                break;
        }
    }

    private void updateCircle() {
        if (mMap != null) {
            if (circle != null) {
                circle.remove();
                circle = mMap.addCircle(new CircleOptions()
                        .center(new LatLng(USER.getLatitude(), USER.getLongitude()))
                        .radius(range * 1000)
                        .strokeWidth(3f)
                        .strokeColor(Color.BLUE));
            } else {
                if (USER != null) {
                    circle = mMap.addCircle(new CircleOptions()
                            .center(new LatLng(USER.getLatitude(), USER.getLongitude()))
                            .radius(range * 1000)
                            .strokeWidth(3f)
                            .strokeColor(Color.BLUE));
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void addMarkerOnMap(Evenement e) {

        CustomMarker m;

        if (e.getVisibility().equals(Visibility.PUBLIC)) {
            if (e.getOwner_uid().equalsIgnoreCase(USER.getUid())) {
                m = new CustomMarker(new LatLng(e.getLatitude(), e.getLongitude()), BitmapDescriptorFactory.HUE_GREEN, e);
            } else {
                m = new CustomMarker(new LatLng(e.getLatitude(), e.getLongitude()), BitmapDescriptorFactory.HUE_RED, e);
            }
        } else {
            if (e.getOwner_uid().equalsIgnoreCase(USER.getUid())) {
                m = new CustomMarker(new LatLng(e.getLatitude(), e.getLongitude()), BitmapDescriptorFactory.HUE_GREEN, e);
            } else {
                m = new CustomMarker(new LatLng(e.getLatitude(), e.getLongitude()), BitmapDescriptorFactory.HUE_AZURE, e);
            }
        }

        clusterManager.addItem(m);
        clusterManager.setRenderer(new CustomRenderer(getContext(), mMap, clusterManager));

        mMap.setOnMarkerClickListener(clusterManager);

        clusterManager.setOnClusterClickListener(cluster -> false);

        clusterManager.setOnClusterItemClickListener(item -> {
            Evenement e1 = item.getTag();

            if (e1 != null) {
                Button buttonDisplay = rootView.findViewById(R.id.buttonDisplay);
                buttonDisplay.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), InfosEvenementsActivity.class);
                    intent.putExtra("event", e1);
                    startActivity(intent);
                });

                Date d = null;
                try {
                    d = Constants.allTimeFormat.parse(e1.getStartDate());
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }

                if (d == null) {
                    textViewDetailNbMembers.setText(e1.getNbMembers() + "");
                    textViewDetailCity.setText(e1.getCity());
                    textViewDetailDate.setText(Constants.dateFormat.format(d));
                    textViewDetailName.setText(e1.getName());
                    imageViewDetailCategory.setImageResource(e1.getImageCategory());
                } else {
                    textViewDetailNbMembers.setText(e1.getNbMembers() + "");
                    textViewDetailCity.setText(e1.getCity());
                    textViewDetailDate.setText(Constants.dateFormat.format(d));
                    textViewDetailName.setText(e1.getName());
                    textViewDetailTime.setText(Constants.timeFormat.format(d));
                    imageViewDetailCategory.setImageResource(e1.getImageCategory());
                }
            }

            // Check if a click count was set, then display the click count.
            bottomSheetBehaviorDetails.setState(BottomSheetBehavior.STATE_EXPANDED);

            return false;
        });
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onLowMemory() {
        mMapView.onLowMemory();
        super.onLowMemory();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onResume() {
        super.onResume();
        createFragment();
        updateCircle();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundel = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundel == null) {
            mapViewBundel = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundel);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        mMapView.onStop();
        super.onStop();
    }
}