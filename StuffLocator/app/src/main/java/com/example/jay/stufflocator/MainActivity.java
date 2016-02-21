package com.example.jay.stufflocator;

import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Toast;

import com.esri.android.map.MapOptions;
import com.esri.android.map.MapView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.firebase.client.Firebase;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    //if you can figure out how to get rid of these props to you and do it
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private MapView mMapView;
    private LocationRequest mLocationRequest;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        //setup stuff
        createLocationRequest();
        buildGoogleApiClient();
        Firebase.setAndroidContext(this); //Firebase intialization
        Firebase understoodFirebase = new Firebase("https://stufflocater.firebaseio.com/");
        understoodFirebase.child("Init Confirmation").setValue("Firebase intialized");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMapView = (MapView) findViewById(R.id.map);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    //set up google api client
    protected synchronized void buildGoogleApiClient() {
         mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    //set up location request
    protected void createLocationRequest() {
        //these values probably shouldn't be zero - use higher values to drain battery less
        //I think they default to 5ms anyways
        mLocationRequest = LocationRequest.create()
                .setInterval(0)
                .setFastestInterval(0)
                .setSmallestDisplacement(0)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    //set up location updates
    protected void startLocationUpdates(){
        if(Build.VERSION.SDK_INT == 23) {
            //if statement only required on API level 23
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    //handle when your location changes, should probably update map too
    public void onLocationChanged(Location location){
        mCurrentLocation = location;
    }

    public void onConnected(Bundle connectionHint){
        if(Build.VERSION.SDK_INT == 23) {
            //again if statement only required on API level 23
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //get current location
                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            }
        } else {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        //set map to center on current location
        final MapOptions mMapOptions = new MapOptions(MapOptions.MapType.STREETS,
                mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), 9);
        //push options to the map
        mMapView.setMapOptions(mMapOptions);
        //call this to check if locations actually is correct
        Log.i("Location", String.valueOf(mCurrentLocation.getLatitude()));

        startLocationUpdates();
    }

    public void onConnectionSuspended(int cause){
        Toast toast = Toast.makeText(this, "GPS Connection Suspended", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void onConnectionFailed(ConnectionResult result){
        Toast toast = Toast.makeText(this, "GPS Connection Failed", Toast.LENGTH_SHORT);
        toast.show();
    }
}
