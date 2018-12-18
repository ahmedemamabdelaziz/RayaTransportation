package com.example.emam.rayatransportation;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DriverHome extends FragmentActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , OnMapReadyCallback
        , GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener
        , RoutingListener {


    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int locationPermissionRequstedCode = 12345;
    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};
    GoogleApiClient mgoogleApiClient;
    Location mlocation;
    LocationRequest mlocationRequst;
    Boolean mLocationGranted = false;
    FrameLayout frameLayout;
    Spinner startRoutFrom, endRoutTo, shiftTime;
    Button startShift, newShift;
    ImageButton mOpenDrawer;
    boolean checkedData = false;
    boolean hasFinshedData;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private String startShiftFrom;
    private String startShiftTo;
    private String startShiftTime;
    LocationCallback mlocationCallback = new LocationCallback() {


        @Override
        public void onLocationResult(LocationResult locationResult) {
            Log.v("DRIVERHOMETAG", "LocationCallback");
            statusCheck();
            super.onLocationResult(locationResult);
            //Location location = locationResult.getLastLocation();
            mlocation = locationResult.getLocations().get(0);
            if (mlocation != null) {
                Log.i("DriverMap.this", "Location: " + mlocation.getLatitude() + " " + mlocation.getLongitude());


            }


            LatLng mDriverLocation = new LatLng(mlocation.getLatitude(), mlocation.getLongitude());


            // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mlocation.getLatitude(), mlocation.getLongitude()), 13));


            String user_id = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();


            if (startShift == null || startShiftTime == null) {

                mMap.moveCamera(CameraUpdateFactory.newLatLng(mDriverLocation));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(17));
                return;

            } else {

             /*  LatLngBounds.Builder bld = new LatLngBounds.Builder();
                for (int i = 0; i < PickUpPoint.size(); i++) {
                    //LatLng ll = new LatLng(YOUR_ARRAYLIST.get(i).getPos().getLat(), YOUR_ARRAYLIST.get(i).getPos().getLon());
                    bld.include(PickUpPoint.get(i));
                }
                bld.include(mDriverLocation);
                LatLngBounds bounds = bld.build();
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 70));

                */
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(mDriverLocation)      // Sets the center of the map to location user
                        .zoom(13)                       // Sets the zoom
                        .bearing(90)                   // Sets the orientation of the camera to east
                        .tilt(40)                     // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                String DriverAvilabel = startShiftFrom + startShiftTime;
                DatabaseReference mref = FirebaseDatabase.getInstance().getReference(DriverAvilabel);
                GeoFire mgeoFire = new GeoFire(mref);
                LatLng drivercurrrntlocation = new LatLng(mlocation.getLatitude(), mlocation.getLongitude());
                mgeoFire.setLocation(user_id, new GeoLocation(mlocation.getLatitude(), mlocation.getLongitude()));
                // getDriverRout(drivercurrrntlocation);

            }


        }
    };
    private ArrayList<LatLng> PickUpPoint;
    private Route mrout;
    private List<Polyline> polylines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_home);


        //turnGPSOn();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);


        mFusedLocationClient = new FusedLocationProviderClient(this);

        polylines = new ArrayList<>();

        startRoutFrom = findViewById(R.id.start_from);
        endRoutTo = findViewById(R.id.end_shift_to);
        shiftTime = findViewById(R.id.shift_time);
        startShift = findViewById(R.id.start_shift);
        newShift = findViewById(R.id.StartShfit);
        frameLayout = findViewById(R.id.DriverFram);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.DriverRout));
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.ShiftTime));

        startRoutFrom.setAdapter(adapter);
        shiftTime.setAdapter(adapter2);
        endRoutTo.setAdapter(adapter);

        startRoutFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                if (position == 0) {
                    endRoutTo.setSelection(0);
                }
                if (position == 1) {
                    endRoutTo.setSelection(4);
                }
                if (position == 2) {
                    endRoutTo.setSelection(4);
                }
                if (position == 3) {
                    endRoutTo.setSelection(4);
                }
                if (position == 4) {
                    endRoutTo.setSelection(0);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        startShift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startShiftFrom = startRoutFrom.getSelectedItem().toString();
                startShiftTo = endRoutTo.getSelectedItem().toString();
                startShiftTime = shiftTime.getSelectedItem().toString();


                if (!hasData(startShiftFrom, startShiftTo, startShiftTime)) {

                    return;
                }

                frameLayout.setVisibility(View.GONE);

                PickUpPoint = new ArrayList<>();
                // mrout = new Route() ;
                PickUpPoint = Route.getRout(DriverHome.this, startShiftFrom, startShiftTo);


                // hasFinshedData = true ;


                for (int index = 0; index < PickUpPoint.size(); index++) {


                    mMap.addMarker(new MarkerOptions().position(PickUpPoint.get(index)).title("picup here" + index)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.point)));


                }


                // CameraPosition cameraPosition = new CameraPosition.Builder()
                //       .target(PickUpPoint.get(0))      // Sets the center of the map to location user
                //       .zoom(13)                   // Sets the zoom
                //      .bearing(90)                // Sets the orientation of the camera to east
                //     .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                //         .build();                   // Creates a CameraPosition from the builder
                //  mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


            }
        });


        newShift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newShift.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);
            }
        });


        //  PickUpPoint = new ArrayList<>() ;
        // mrout = new Route() ;

        //  if(startShift == null || startShiftTime == null) {
        //     return;
        // }else {

        //     if(startShiftFrom.equals("R1 Madinat Nasr/Mohandseen")){Log.v("DriverMapACTIVITY" , "DATA" + "   R1");}
        //     if(startShiftFrom.equals("R2 Maadi/Haram")){Log.v("DriverMapACTIVITY" , "DATA" + "   R2");}
        //    if(startShiftFrom.equals("R3 6th Of October ")){PickUpPoint = mrout.getR3_6th_Of_October();}

        //    Log.v("DriverMapACTIVITY" , "DATA" + PickUpPoint.get(0));

        // }


        // for(int index = 0 ; index<PickUpPoint.size() ; index ++){


        //     mMap.addMarker(new MarkerOptions().position(PickUpPoint.get(index)).title("picup here")) ;
        // }


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        mOpenDrawer = findViewById(R.id.opendrawere);


        final DrawerLayout drawer = findViewById(R.id.drawer_layout);


        mOpenDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.END);
            }
        });


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.driver_home, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        drawer.closeDrawer(GravityCompat.END);
        return true;
    }

    private void getDriverRout(LatLng start) {

        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(false)
                .waypoints(PickUpPoint.get(0), PickUpPoint.get(1))
                .build();
        routing.execute();

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;

        try {
            boolean isSuccess = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.uber_style_map));

            if (!isSuccess) {

            }
        } catch (Resources.NotFoundException ex) {


        }

        //Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        //  for(int index = 0 ; index<PickUpPoint.size() ; index ++){


        //    mMap.addMarker(new MarkerOptions().position(PickUpPoint.get(index)).title("picup here" + index)
        //           .icon(BitmapDescriptorFactory.fromResource(R.mipmap.point))) ;

        // }


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // See the documentation for ActivityCompat#requestPermissions for more details.
            Log.v("DRIVERHOMETAG", "Ahmed Emam");

            return;
        }


        Log.v("DRIVERHOMETAG", "ADAM Ahmed Emam");


        bildgoogleclint();
        mMap.setMyLocationEnabled(true);

        Log.v("DRIVERHOMETAG", "ADAM Ahmed Emam");


    }

    protected synchronized void bildgoogleclint() {

        mgoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .build();

        mgoogleApiClient.connect();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.v("DRIVERHOMETAG", "onConnected");
        mlocationRequst = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setFastestInterval(1000)
                .setInterval(1000);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            Log.v("DRIVERHOMETAG", "oZEPY");
            return;
        }


        mFusedLocationClient.requestLocationUpdates(mlocationRequst, mlocationCallback, Looper.myLooper());


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    protected void onStop() {

        //String user_id  = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String DriverId = startShiftFrom + startShiftTime;
        DatabaseReference mref = FirebaseDatabase.getInstance().getReference();
        GeoFire mgeoFire = new GeoFire(mref);
        mgeoFire.removeLocation(DriverId);
        super.onStop();

    }

    private boolean hasData(String mstartShiftFrom,
                            String mstartShiftTo, String mstartShiftTime) {


        if (mstartShiftFrom.equals(getResources().getStringArray(R.array.DriverRout)[0])) {

            custoumAlartDialog("Error Massage", "please Select your start point");

            return checkedData;
        }
        if (mstartShiftTo.equals(getResources().getStringArray(R.array.DriverRout)[0])) {

            custoumAlartDialog("Error Massage", "Select your end point");

            return checkedData;
        }
        if (mstartShiftTime.equals(getResources().getStringArray(R.array.ShiftTime)[0])) {

            custoumAlartDialog("Error Massage", "select your shift Time");

            return checkedData;
        }
        if (mstartShiftFrom.equals(mstartShiftTo)) {

            custoumAlartDialog("Error Massage", "it is impossipole start point equal end point ");
            return checkedData;
        }

        if ((mstartShiftFrom.equals(getResources().getStringArray(R.array.DriverRout)[1]) || mstartShiftFrom.equals(getResources().getStringArray(R.array.DriverRout)[2]) ||
                mstartShiftFrom.equals(getResources().getStringArray(R.array.DriverRout)[3])) && (!mstartShiftTo.equals(getResources().getStringArray(R.array.DriverRout)[4]))) {

            custoumAlartDialog("Error Massage", "must start point or end poind is Office  ");
            return checkedData;

        }

        return true;
    }


    private void custoumAlartDialog(String titel, String massage) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(DriverHome.this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle(titel);
        builder.setMessage(massage);

        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }


    @Override
    public void onRoutingFailure(RouteException e) {


        if (e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.v("DriverMapACTIVITY", e.getMessage());
        } else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
            Log.v("DriverMapACTIVITY", "Something went wrong, Try again");
        }

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<com.directions.route.Route> route, int shortestRoutIndex) {

        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(), "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRoutingCancelled() {

    }


    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }


                })
                .setNegativeButton("exit", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                        finish();
                        return;
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        statusCheck();
    }


}


