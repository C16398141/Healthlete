package fyp.c16398141.healthlete;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceReport;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Period;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.valueOf;

public class workout_area extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnPoiClickListener { //, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    Location currentLocation;
    EditText etext;
    Button button;

    ArrayList<String> aday = new ArrayList<>();
    ArrayList<String> atimes = new ArrayList<>();
    ArrayList<Integer> opening = new ArrayList<>();
    ArrayList<Integer> closing = new ArrayList<>();

    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int request_code = 101;

    private GoogleApi googleApi;
    //String key = getString(R.string.my_maps_key);
    //GoogleApiClient client;

    // Initialize Places.
    //Places.initialize(getApplicationContext(), key);
    // Create a new Places client instance.
    //PlacesClient placesClient = Places.createClient(this);
    List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.OPENING_HOURS);

    FetchPlaceRequest request;
    PlacesClient placesClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_area);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        etext = (EditText) findViewById(R.id.text);
        button = (Button) findViewById(R.id.button);
        button.setVisibility(View.INVISIBLE);

        Places.initialize(getApplicationContext(), getString(R.string.my_maps_key));
        placesClient = Places.createClient(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, request_code);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>(){
           @Override
           public void onSuccess(Location location) {
               if (location != null){
                   currentLocation = location;
                   Toast.makeText(getApplicationContext(),currentLocation.getLatitude() + "" +currentLocation.getLongitude(),Toast.LENGTH_SHORT).show();
                   SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                           .findFragmentById(R.id.map);
                   mapFragment.getMapAsync(workout_area.this);
               }
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
        Toast.makeText(getApplicationContext(), poi.name + "\nPlace ID:" + poi.placeId + "\nLatitude:" + poi.latLng.latitude + " Longitude:" + poi.latLng.longitude, Toast.LENGTH_SHORT).show();

        request = FetchPlaceRequest.newInstance(poi.placeId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            try {
                List<String> hours = place.getOpeningHours().getWeekdayText();
                Log.i("Opening Times", "Weekday Text Found" + place.getName() + " " + hours);

                // convert from default abstract arraylist
                Collection<String> times = hours;
                Log.i("Collection", "Weekday Text Found" + place.getName() + " " + times);
                String always = "Open 24 hours";
                String never = "Closed";
                Pattern pday = Pattern.compile(":");
                Pattern ptimes = Pattern.compile("–");
                Pattern pm = Pattern.compile("PM");

                for (String daytime : times){
                    String type, time_brackets, opening_time, closing_time;

                    Matcher matcher = pday.matcher(daytime);
                    if (matcher.find()) {
                        if (daytime.contains(always)){
                            type = "Open";
                            aday.add(daytime.substring(0, matcher.start()));
                            atimes.add(type);
                        }
                        else if (daytime.contains(never)){
                            type = "Closed";
                            aday.add(daytime.substring(0, matcher.start()));
                            atimes.add(type);
                        }
                        else{
                            type = "Periodic";
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
                                        opening_time = opening_time.replaceAll("PM","");
                                        opening.add((Integer.parseInt(opening_time))+1200);
                                    }
                                    else{
                                        opening_time = opening_time.replaceAll("AM","");
                                        opening.add(Integer.parseInt(opening_time));
                                    }

                                    Matcher matcher4 = pm.matcher(closing_time);
                                    if(matcher4.find()){
                                        closing_time = closing_time.replaceAll("PM","");
                                        closing.add((Integer.parseInt(closing_time))+1200);
                                    }
                                    else{
                                        closing_time = closing_time.replaceAll("AM","");
                                        closing.add(Integer.parseInt(closing_time));
                                    }

                                }
                        }
                        }

                    }
                int x = 0;
                for(int i = 0; i<7; i++){
                    Log.i(aday.get(i),atimes.get(i));
                    if(atimes.get(i).equals("Periodic")){
                        Log.i("Opening",valueOf(opening.get(x)));
                        Log.i("Closing",valueOf(closing.get(x)));
                        x++;
                    }
                }

                aday.clear();
                atimes.clear();

                etext.setText(place.getName());
                button.setVisibility(View.VISIBLE);
            } catch(NullPointerException e) {
                Log.i("Opening Times", place.getName() + " opening Times not available");
            }

            /*if (place.getOpeningHours().getWeekdayText() != null){
                List<String> hours = place.getOpeningHours().getWeekdayText();
                Log.i("TAG", "Place found: " + place.getName() + " " + hours);
            }
            else if(place.getOpeningHours().getPeriods() != null){
                List<Period> periods = place.getOpeningHours().getPeriods();
                Log.i("TAG", "Place found: " + place.getName() + " " + periods);
            }
            else{
                Log.i("TAG", "Place found: " + place.getName() + " Opening Times not available");
            }*/
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();
                // Handle error with given status code.
                Log.e("TAG", "Place not found: " + exception.getMessage());
            }
        });
    }


   public void onLocationChanged(Location location) {

    }

    /*public void onConnected(@Nullable Bundle bundle){

    }

    public void onConnectionFailed(@NonNull ConnectionResult connectionResult){

    }

    public void onConnectionSuspended(int i){

    }*/
}


