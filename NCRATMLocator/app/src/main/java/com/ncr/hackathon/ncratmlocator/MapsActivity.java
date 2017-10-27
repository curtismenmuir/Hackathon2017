package com.ncr.hackathon.ncratmlocator;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import static android.location.LocationManager.GPS_PROVIDER;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    Location location;

    private static final LatLng ATM_ASDA_MYREKIRK = new LatLng(56.474945, -3.048928);
    private static final LatLng ATM_TESCO_SOUTH_ROAD = new LatLng(56.471594, -3.042930);
    private static final LatLng ATM_DUNHOME_ROAD = new LatLng(56.472479, -3.045260);
    private static final LatLng ATM_BIRKHILL_COOP = new LatLng(56.493895, -3.053856);
    private static final LatLng ATM_CRAIGOWAN_ROAD = new LatLng(56.473089, -3.037547);
    private static final LatLng ATM_COOP_KINGSWAY_WEST = new LatLng(56.477679, -3.03382);
    private static final LatLng ATM_COOP_CHARLESTON_DRIVE = new LatLng(56.467583, -3.045755);

    private Marker mATM_ASDA_MYREKIRK;
    private Marker mATM_TESCO_SOUTH_ROAD;
    private Marker mATM_DUNHOME_ROAD;
    private Marker mATM_BIRKHILL_COOP;
    private Marker mATM_CRAIGOWAN_ROAD;
    private Marker mATM_COOP_KINGSWAY_WEST;
    private Marker mATM_COOP_CHARLESTON_DRIVE;

    private String ATM_ASDA_MYREKIRK_DETAILS[] = {"Available","Free","Barclays","Busy","English/French/Italian/Polish","£5, £10, £20","Cash Withdrawl, Deposit",};
    private String ATM_TESCO_SOUTH_ROAD_DETAILS[] = {"Out Of Service","Free","Santander","Quiet","English/French/German","£5, £10, £20, £50","Cash Withdrawl, Deposit",};
    private String ATM_DUNHOME_ROAD_DETAILS[] = {"In Use","£1.75","Lloyds","Quiet","English","£20","Cash Withdrawl",};
    private String ATM_BIRKHILL_COOP_DETAILS[] = {"Engineer Visit","£2.00","Santander","Quiet","English/French","£5, £10","Cash Withdrawl",};
    private String ATM_CRAIGOWAN_ROAD_DETAILS[] = {"Available","£1.50","Barclays","Quiet","English","£10, £20","Cash Withdrawl",};
    private String ATM_COOP_KINGSWAY_WEST_DETAILS[] = {"Available","Free","Barclays","Busy","English","£5, £20","Cash Withdrawl, Cheque Deposit",};
    private String ATM_COOP_CHARLESTON_DRIVE_DETAILS[] = {"Out Of Service","Free","Barclays","Busy","English","£10, £20","Cash Withdrawl",};


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        getSupportActionBar().setTitle("ATM Finder");

        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_style_choose) {

            showOptionsDialog();
        }
        return true;
    }

    /**
     * Shows a dialog listing the styles to choose from, and applies the selected
     * style when chosen.
     */
    private void showOptionsDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(MapsActivity.this);
        final View additionalView = layoutInflater.inflate(R.layout.atm_filter, null);

        Switch cashSwitch = (Switch)additionalView.findViewById(R.id.switch1);
        cashSwitch.setChecked(true);
        cashSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    Log.d("cashSwitch", "IsChecked: " + isChecked);
                    SharedPreferences sharedPref = MapsActivity.this.getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("cashSwitch", "IsChecked");
                    editor.apply();
                } else {
                    Log.d("cashSwitch", "NotChecked: " + isChecked);
                    SharedPreferences sharedPref = MapsActivity.this.getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("cashSwitch", "NotChecked");
                    editor.apply();
                }
            }
        });

        Switch depositSwitch = (Switch)additionalView.findViewById(R.id.switch2);
        depositSwitch.setChecked(true);
        depositSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    Log.d("depositSwitch ", "IsChecked: " + isChecked);
                    SharedPreferences sharedPref = MapsActivity.this.getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("depositSwitch", "IsChecked");
                    editor.apply();
                } else {
                    Log.d("depositSwitch", "NotChecked: " + isChecked);
                    SharedPreferences sharedPref = MapsActivity.this.getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("depositSwitch", "NotChecked");
                    editor.apply();
                }
            }
        });

        Switch chequeSwitch = (Switch)additionalView.findViewById(R.id.switch3);
        chequeSwitch.setChecked(true);
        chequeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    Log.d("chequeSwitch ", "IsChecked: " + isChecked);
                    SharedPreferences sharedPref = MapsActivity.this.getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("chequeSwitch", "IsChecked");
                    editor.apply();
                } else {
                    Log.d("chequeSwitch", "NotChecked: " + isChecked);
                    SharedPreferences sharedPref = MapsActivity.this.getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("chequeSwitch", "NotChecked");
                    editor.apply();
                }
            }
        });

        final AlertDialog.Builder dialog1 = new AlertDialog.Builder(MapsActivity.this);
        dialog1.setCancelable(false);
        dialog1.setView(additionalView);
        dialog1.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Overwritten onClick below
            }
        });
        dialog1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Overwritten onClick below
            }
        });

        AlertDialog alertDialog = dialog1.create();
        alertDialog.show();

        Button confirmButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        confirmButton.setOnClickListener(new PositiveCustomListener(alertDialog) );

        Button cancelButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        cancelButton.setOnClickListener(new NegativeCustomListener(alertDialog));
    }

    class PositiveCustomListener implements View.OnClickListener {
        private final AlertDialog dialog;
        public PositiveCustomListener(AlertDialog dialog) {
            this.dialog = dialog;
        }
        @Override
        public void onClick(View v) {
            // Get Switch values and update map
            SharedPreferences sharedPref = MapsActivity.this.getPreferences(Context.MODE_PRIVATE);
            String CashValue = sharedPref.getString("cashSwitch","IsChecked");
            String DepositValue = sharedPref.getString("depositSwitch","IsChecked");
            String ChequeValue = sharedPref.getString("chequeSwitch","IsChecked");

            Log.d("CashValue", "CashValue: " + CashValue);
            Log.d("DepositValue", "DepositValue: " + DepositValue);
            Log.d("ChequeValue", "ChequeValue: " + ChequeValue);

            updateMap(CashValue, DepositValue, ChequeValue);
            resetSharedPrefs();
            dialog.dismiss();
        }
    }

    public void updateMap(String CashValue, String DepositValue, String ChequeValue){
        mATM_ASDA_MYREKIRK.remove();
        mATM_TESCO_SOUTH_ROAD.remove();
        mATM_DUNHOME_ROAD.remove();
        mATM_BIRKHILL_COOP.remove();
        mATM_CRAIGOWAN_ROAD.remove();
        mATM_COOP_KINGSWAY_WEST.remove();
        mATM_COOP_CHARLESTON_DRIVE.remove();

        if((CashValue.equals("NotChecked") && DepositValue.equals("NotChecked") && ChequeValue.equals("NotChecked")) || (CashValue.equals("IsChecked") && DepositValue.equals("IsChecked") && ChequeValue.equals("IsChecked"))){
            addAllMarkersToMap();
        }else {
            if (CashValue.equals("IsChecked")){
                mATM_ASDA_MYREKIRK = mGoogleMap.addMarker(new MarkerOptions().position(ATM_ASDA_MYREKIRK).title("ATM @ ASDA MYREKIRK"));
                mATM_TESCO_SOUTH_ROAD = mGoogleMap.addMarker(new MarkerOptions().position(ATM_TESCO_SOUTH_ROAD).title("ATM @ TESCO SOUTH ROAD"));
                mATM_DUNHOME_ROAD = mGoogleMap.addMarker(new MarkerOptions().position(ATM_DUNHOME_ROAD).title("ATM @ DUNHOME ROAD"));
                mATM_BIRKHILL_COOP = mGoogleMap.addMarker(new MarkerOptions().position(ATM_BIRKHILL_COOP).title("ATM @ BIRKHILL COOP"));
                mATM_CRAIGOWAN_ROAD = mGoogleMap.addMarker(new MarkerOptions().position(ATM_CRAIGOWAN_ROAD).title("ATM @ CRAIGOWAN ROAD"));
                mATM_COOP_KINGSWAY_WEST = mGoogleMap.addMarker(new MarkerOptions().position(ATM_COOP_KINGSWAY_WEST).title("ATM @ COOP KINGSWAY WEST"));
                mATM_COOP_CHARLESTON_DRIVE = mGoogleMap.addMarker(new MarkerOptions().position(ATM_COOP_CHARLESTON_DRIVE).title("ATM @ COOP CHARLESTON DRIVE"));

            }
            if (DepositValue.equals("IsChecked")){
                mATM_ASDA_MYREKIRK = mGoogleMap.addMarker(new MarkerOptions().position(ATM_ASDA_MYREKIRK).title("ATM @ ASDA MYREKIRK"));
                mATM_TESCO_SOUTH_ROAD = mGoogleMap.addMarker(new MarkerOptions().position(ATM_TESCO_SOUTH_ROAD).title("ATM @ TESCO SOUTH ROAD"));
            }
            if(ChequeValue.equals("IsChecked")){
                mATM_COOP_KINGSWAY_WEST = mGoogleMap.addMarker(new MarkerOptions().position(ATM_COOP_KINGSWAY_WEST).title("ATM @ COOP KINGSWAY WEST"));
            }
        }
    }

    public void resetSharedPrefs(){
        SharedPreferences sharedPref = MapsActivity.this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();
    }
    class NegativeCustomListener implements View.OnClickListener {
        private final Dialog dialog;
        public NegativeCustomListener(Dialog dialog) {
            this.dialog = dialog;
        }
        @Override
        public void onClick(View v) {
            dialog.dismiss();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //stop location updates when Activity is no longer active
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap=googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mGoogleMap.setOnInfoWindowClickListener(this);
                location = new Location(GPS_PROVIDER);
                onLocationChanged(location);
                addAllMarkersToMap();
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            buildGoogleApiClient();
        }
    }

    private void addAllMarkersToMap() {
        mATM_ASDA_MYREKIRK = mGoogleMap.addMarker(new MarkerOptions().position(ATM_ASDA_MYREKIRK).title("ATM @ ASDA MYREKIRK"));
        mATM_TESCO_SOUTH_ROAD = mGoogleMap.addMarker(new MarkerOptions().position(ATM_TESCO_SOUTH_ROAD).title("ATM @ TESCO SOUTH ROAD"));
        mATM_DUNHOME_ROAD = mGoogleMap.addMarker(new MarkerOptions().position(ATM_DUNHOME_ROAD).title("ATM @ DUNHOME ROAD"));
        mATM_BIRKHILL_COOP = mGoogleMap.addMarker(new MarkerOptions().position(ATM_BIRKHILL_COOP).title("ATM @ BIRKHILL COOP"));
        mATM_CRAIGOWAN_ROAD = mGoogleMap.addMarker(new MarkerOptions().position(ATM_CRAIGOWAN_ROAD).title("ATM @ CRAIGOWAN ROAD"));
        mATM_COOP_KINGSWAY_WEST = mGoogleMap.addMarker(new MarkerOptions().position(ATM_COOP_KINGSWAY_WEST).title("ATM @ COOP KINGSWAY WEST"));
        mATM_COOP_CHARLESTON_DRIVE = mGoogleMap.addMarker(new MarkerOptions().position(ATM_COOP_CHARLESTON_DRIVE).title("ATM @ COOP CHARLESTON DRIVE"));
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        //Place current location marker
        //LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        LatLng latLng = new LatLng(56.472812, -3.054847);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
        //move map camera
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    // This will open Dialog to display information about selected ATM
    @Override
    public void onInfoWindowClick(Marker marker) {
        String markerName = marker.getTitle();

        LayoutInflater layoutInflater = LayoutInflater.from(MapsActivity.this);
        final View atmDetailView = layoutInflater.inflate(R.layout.atm_detail_view, null);

        TextView AtmHeading = (TextView)atmDetailView.findViewById(R.id.atmHeading);
        TextView AtmStatusValue = (TextView)atmDetailView.findViewById(R.id.atmStatusValue);
        TextView AtmCostValue = (TextView)atmDetailView.findViewById(R.id.atmCostValue);
        TextView AtmOwnerValue = (TextView)atmDetailView.findViewById(R.id.atmOwnerValue);
        TextView AtmUsageValue = (TextView)atmDetailView.findViewById(R.id.atmUsageValue);
        TextView AtmLanguageValue = (TextView)atmDetailView.findViewById(R.id.atmLanguageValue);
        TextView AtmAvailableNotesValue = (TextView)atmDetailView.findViewById(R.id.atmAvailableNotesValue);
        TextView AtmServicesValue = (TextView)atmDetailView.findViewById(R.id.atmServicesValue);

        String ATM_CHOSEN_DETAILS[] = new String[7];

        AtmHeading.setText(markerName);

        if(markerName.equals("ATM @ ASDA MYREKIRK")) {
            ATM_CHOSEN_DETAILS = ATM_ASDA_MYREKIRK_DETAILS;
        }
        else if(markerName.equals("ATM @ TESCO SOUTH ROAD")){
            ATM_CHOSEN_DETAILS = ATM_TESCO_SOUTH_ROAD_DETAILS;
        }
        else if(markerName.equals("ATM @ DUNHOME ROAD")){
            ATM_CHOSEN_DETAILS = ATM_DUNHOME_ROAD_DETAILS;
        }
        else if(markerName.equals("ATM @ BIRKHILL COOP")){
            ATM_CHOSEN_DETAILS = ATM_BIRKHILL_COOP_DETAILS;
        }
        else if(markerName.equals("ATM @ CRAIGOWAN ROAD")){
            ATM_CHOSEN_DETAILS = ATM_CRAIGOWAN_ROAD_DETAILS;
        }
        else if(markerName.equals("ATM @ COOP KINGSWAY WEST")){
            ATM_CHOSEN_DETAILS = ATM_COOP_KINGSWAY_WEST_DETAILS;
        }
        else if (markerName.equals("ATM @ COOP CHARLESTON DRIVE")){
            ATM_CHOSEN_DETAILS = ATM_COOP_CHARLESTON_DRIVE_DETAILS;
        }

        AtmStatusValue.setText(ATM_CHOSEN_DETAILS[0]);
        AtmCostValue.setText(ATM_CHOSEN_DETAILS[1]);
        AtmOwnerValue.setText(ATM_CHOSEN_DETAILS[2]);
        AtmUsageValue.setText(ATM_CHOSEN_DETAILS[3]);
        AtmLanguageValue.setText(ATM_CHOSEN_DETAILS[4]);
        AtmAvailableNotesValue.setText(ATM_CHOSEN_DETAILS[5]);
        AtmServicesValue.setText(ATM_CHOSEN_DETAILS[6]);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MapsActivity.this);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setView(atmDetailView);
        alertDialogBuilder.setPositiveButton("Directions", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Overwritten onClick below
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Overwritten onClick below
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        Button directionsButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        directionsButton.setOnClickListener(new DirectionsCustomListener(alertDialog) );

        //Button cancelButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        //cancelButton.setOnClickListener(new NegativeCustomListener(alertDialog));
    }

    class DirectionsCustomListener implements View.OnClickListener {
        private final AlertDialog dialog;
        public DirectionsCustomListener(AlertDialog dialog) {
            this.dialog = dialog;
        }
        @Override
        public void onClick(View v) {
            dialog.dismiss();
        }
    }


}