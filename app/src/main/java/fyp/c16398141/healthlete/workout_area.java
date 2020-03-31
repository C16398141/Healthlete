package fyp.c16398141.healthlete;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.valueOf;
import static maes.tech.intentanim.CustomIntent.customType;

// TODO make nice view areas activity and then edit profile (incl goals). Set goals to default average human intake (possibly based on whether gender is received on sign up)
public class workout_area extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnPoiClickListener {

    private GoogleMap mMap;
    Location currentLocation;

    LocalDB ldb;

    ArrayList<String> aday = new ArrayList<>();
    ArrayList<String> atimes = new ArrayList<>();
    ArrayList<Integer> opening = new ArrayList<>();
    ArrayList<Integer> closing = new ArrayList<>();

    String place_id;
    String name;
    Double lat,lng;
    String user_id = "2013chrisclarke@gmail.com";
    Integer times;

    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int request_code = 101;

    List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.OPENING_HOURS, Place.Field.LAT_LNG);
    FetchPlaceRequest request;
    PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_area);
        ldb = new LocalDB(this);

        ImageButton add = findViewById(R.id.add);
        add.setVisibility(View.INVISIBLE);

        ImageButton view_areas = findViewById(R.id.view_areas);
        ImageButton home = findViewById(R.id.home);

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                Toast.makeText(getApplicationContext(), "Place Selected", Toast.LENGTH_SHORT).show();

                request = FetchPlaceRequest.newInstance(place.getId(), placeFields);

                placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                    Place filtered_place = response.getPlace();

                    processPlaceDetails(filtered_place);

                });
            }

            @Override
            public void onError(Status status) {

                Log.i("TAG", "An error occurred: " + status);
            }
        });

        Places.initialize(getApplicationContext(), getString(R.string.my_maps_key));
        placesClient = Places.createClient(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();

        add.setOnClickListener(v -> {

                ldb.open();
                Cursor area = ldb.getWorkoutArea();
                int rows = area.getCount();
                if (rows == 0) {
                } else {
                    ldb.deletePreviousWorkoutArea();
                    Log.i("Deleted","Probably");
                }

                long update = ldb.addWorkoutArea(place_id, name, lat, lng, times, user_id);

                if (update == -1) {
                    Toast.makeText(getApplicationContext(), "Unsuccessful area insert", Toast.LENGTH_SHORT).show();
                } else if (times == 1){
                    int area_id = (int) update;
                    int inserts = 0;
                    for (int i = 0; i<aday.size(); i++){
                        boolean result = ldb.addWorkoutAvailability(aday.get(i), atimes.get(i), opening.get(i), closing.get(i), area_id);
                        if (result == true){
                            inserts++;
                        }
                    }
                    if (inserts == 0)
                    {
                        Toast.makeText(getApplicationContext(), "Failed to add workout area", Toast.LENGTH_SHORT).show();
                    }
                    else if (inserts == aday.size())
                    {
                        Toast.makeText(getApplicationContext(), "Workout area added", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Workout area added with partial opening times available", Toast.LENGTH_LONG).show();
                    }
                } else{
                    Toast.makeText(getApplicationContext(), "Workout area added with opening times not available", Toast.LENGTH_LONG).show();
                }
                Intent intent = new Intent(workout_area.this, view_workout_areas.class);
                startActivity(intent);
                ldb.close();
            });

        view_areas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(workout_area.this, view_workout_areas.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                customType(workout_area.this,"bottom-to-up");
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMap("Current Location",currentLocation.getLatitude(),currentLocation.getLongitude());
            }
        });
    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, request_code);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null){
                currentLocation = location;
                Toast.makeText(getApplicationContext(),currentLocation.getLatitude() + "" +currentLocation.getLongitude(),Toast.LENGTH_SHORT).show();
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(workout_area.this);
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng latLng = new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(latLng).title("Look where I am"));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        mMap.setOnPoiClickListener(this);
    }

    public void updateMap(String name, double lat, double lng){
        mMap.clear();
        LatLng latLng = new LatLng(lat,lng);
        Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).title(name));
        marker.showInfoWindow();
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        switch (requestCode){
            case request_code:
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    fetchLastLocation();
                }
                break;
        }
    }

    @Override
    public void onPoiClick(PointOfInterest poi) {

        //Toast.makeText(getApplicationContext(), poi.name + "\nPlace ID:" + poi.placeId + "\nLatitude:" + poi.latLng.latitude + " Longitude:" + poi.latLng.longitude, Toast.LENGTH_SHORT).show();

        request = FetchPlaceRequest.newInstance(poi.placeId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            processPlaceDetails(place);

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();
                // Handle error with given status code.
                Log.e("TAG", "Place not found: " + exception.getMessage());
            }
        });
    }

    public void processPlaceDetails(Place place){
        place_id = place.getId();
        name = place.getName();
        lat = place.getLatLng().latitude;
        lng = place.getLatLng().longitude;
        updateMap(name,lat,lng);

        aday.clear();
        atimes.clear();
        opening.clear();
        closing.clear();

        try {
            List<String> hours = place.getOpeningHours().getWeekdayText();
            Log.i("Opening Times", "Found " + name + " " + hours);

            times = 1;
            // convert from default abstract arraylist
            Collection<String> times = hours;

            String always = "Open 24 hours";
            String never = "Closed";
            Pattern pday = Pattern.compile(":");
            Pattern ptimes = Pattern.compile("–");
            Pattern pm = Pattern.compile("pm");
            Pattern multiple = Pattern.compile(",");

            for (String daytime : times){
                String type, time_brackets, opening_time, closing_time;
                Matcher mult = multiple.matcher(daytime);

                daytime = daytime.toLowerCase();
                Matcher matcher = pday.matcher(daytime);
                if (matcher.find()) {
                    if (daytime.contains(always)){
                        type = "Open";
                        int open = 1;
                        aday.add(daytime.substring(0, matcher.start()));
                        atimes.add(type);
                        opening.add(open);
                        closing.add(open);
                    }
                    else if (daytime.contains(never)){
                        type = "Closed";
                        int closed = 0;
                        aday.add(daytime.substring(0, matcher.start()));
                        atimes.add(type);
                        opening.add(closed);
                        closing.add(closed);
                    }
                    else{
                        type = "Periodic";

                        //if there are multiple opening and closing times in a single day, keep only the first opening time and last closing time
                        if (mult.find()) {
                            Log.i("Multiple Start",daytime);
                            Matcher first = ptimes.matcher(daytime);
                            if (first.find()) {
                                String opening = daytime.substring(0, first.start());
                                String body = daytime.substring(first.end());
                                Matcher last = ptimes.matcher(body);
                                if (last.find()){
                                    String closing = body.substring(last.end());
                                    daytime = opening + " – " + closing;
                                    Log.i("Multiple End",daytime);
                                }
                            }
                        }
                        aday.add(daytime.substring(0, matcher.start()));
                        atimes.add(type);
                        time_brackets = (daytime.substring(matcher.end()));

                        String[] unwantedChars = {":","\\s"};
                        for (String s : unwantedChars){
                            time_brackets = time_brackets.replaceAll(s,"");
                        }

                        Matcher matcher2 = ptimes.matcher(time_brackets);
                        if (matcher2.find()) {
                            opening_time = (time_brackets.substring(0, matcher2.start()));
                            closing_time = (time_brackets.substring(matcher2.end()));

                            Matcher matcher3 = pm.matcher(opening_time);
                            if(matcher3.find()){
                                opening_time = opening_time.replaceAll("pm","");
                                if (opening_time.contains("12"))
                                {
                                    opening.add(Integer.parseInt(opening_time));
                                }
                                else{
                                    opening.add((Integer.parseInt(opening_time))+1200);
                                }
                            }
                            else{
                                opening_time = opening_time.replaceAll("am","");
                                opening.add(Integer.parseInt(opening_time));
                            }

                            Matcher matcher4 = pm.matcher(closing_time);
                            if(matcher4.find()){
                                closing_time = closing_time.replaceAll("pm","");
                                if (closing_time.contains("12"))
                                {
                                    closing.add(Integer.parseInt(closing_time));
                                }
                                else{
                                    closing.add((Integer.parseInt(closing_time))+1200);
                                }
                            }
                            else{
                                closing_time = closing_time.replaceAll("am","");
                                closing.add(Integer.parseInt(closing_time));
                            }
                        }
                    }
                }
            }
        } catch(NullPointerException e) {
            Log.i("Opening Times", place.getName() + " opening Times not available");
            times = 0;
        }

        ImageButton add = findViewById(R.id.add);
        add.setVisibility(View.VISIBLE);
    }

   public void onLocationChanged(Location location) {

    }

}


