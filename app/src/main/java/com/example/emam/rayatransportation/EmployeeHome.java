package com.example.emam.rayatransportation;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.util.ArrayMap;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class EmployeeHome extends FragmentActivity
        implements NavigationView.OnNavigationItemSelectedListener
        , OnMapReadyCallback
        , GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener {


    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int locationPermissionRequstedCode = 12345;
    ImageButton mopendrawer;
    LatLng mEmployeeLocation;
    String employRout, employPickUPPoint, employeeShifttime;
    private GoogleMap mMap;
    private GoogleApiClient mgoogleApiClient;
    private Location mlocation;
    LocationCallback mlocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            //Location location = locationResult.getLastLocation();
            mlocation = locationResult.getLocations().get(0);

            Log.v("EMPLOOYMAPTAG", "Data : " + "oncreated");

            if (mlocation != null) {
                Log.i("DriverMap.this", "Location: " + mlocation.getLatitude() + " " + mlocation.getLongitude());

                //  Toast.makeText(DriverMap.this,"correct" , Toast.LENGTH_LONG).show();

            }

            //  MarkerOptions options = new MarkerOptions();
            // options.position(new LatLng(mlocation.getLatitude(), mlocation.getLongitude()));
            // BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
            // options.icon(icon);
            //  Marker marker = mMap.addMarker(options);

            //  mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 17));
            /**
             * To get location information consistently
             * mLocationRequest.setNumUpdates(1) Commented out
             * Uncomment the code below
             */
            //  mFusedLocationClient.removeLocationUpdates(mlocationCallback);


            mEmployeeLocation = new LatLng(mlocation.getLatitude(), mlocation.getLongitude());

            // Log.v("EMPLOOYMAPTAG","Data : "+ mDriverLocation);

            if (employRout == null) {

                mMap.moveCamera(CameraUpdateFactory.newLatLng(mEmployeeLocation));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(17));

            }


        }
    };
    private LocationRequest mlocationRequst;
    private FusedLocationProviderClient mFusedLocationClient;
    private Boolean mLocationGranted = false;
    private Button findCar, searchCar;
    private FrameLayout frameLayout;
    private Spinner yourRout, StartShift, PickupPoint;
    private Marker DrivernewMarker;
    private ArrayMap<String, Marker> DriverMarker = new ArrayMap<>();

    public static EmployeeHome.getdriverlatlong splitString(String midelstring) {
        char[] findidelstring = midelstring.toCharArray();
        char[] trminatedchar = new char[midelstring.toCharArray().length - 2];

        for (int i = 1; i < findidelstring.length - 1; i++) {

            trminatedchar[i - 1] = findidelstring[i];

        }

        String newstring = new String(trminatedchar);

        String[] separated = newstring.split(",");

        return new EmployeeHome.getdriverlatlong(Double.parseDouble(separated[0]), Double.parseDouble(separated[1]));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_home);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);


        mapFragment.getMapAsync(this);


        mFusedLocationClient = new FusedLocationProviderClient(this);
        yourRout = findViewById(R.id.spinner2);
        StartShift = findViewById(R.id.spinner3);
        PickupPoint = findViewById(R.id.spinner7);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.Rout));
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.ShiftTime));

        final ArrayAdapter<String> pickPointR1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.PickupPointRout1));
        final ArrayAdapter<String> pickPointR2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.PickupPointRout2));
        final ArrayAdapter<String> pickPointR3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.PickupPointRout3));
        final ArrayAdapter<String> pickPointDefaualt = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"Select your Rout First"});

        yourRout.setAdapter(adapter);
        StartShift.setAdapter(adapter2);

        yourRout.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {

                    case 0:
                        PickupPoint.setAdapter(pickPointDefaualt);
                        break;

                    case 1:
                        PickupPoint.setAdapter(pickPointR1);
                        break;
                    case 2:
                        PickupPoint.setAdapter(pickPointR2);
                        break;
                    case 3:
                        PickupPoint.setAdapter(pickPointR3);
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        findCar = findViewById(R.id.findcar);
        searchCar = findViewById(R.id.search_for_car);
        frameLayout = findViewById(R.id.myfarme);
        findCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findCar.setVisibility(View.GONE);
                frameLayout.setVisibility(View.VISIBLE);

            }
        });


        searchCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                employRout = yourRout.getSelectedItem().toString();
                employPickUPPoint = PickupPoint.getSelectedItem().toString();
                employeeShifttime = StartShift.getSelectedItem().toString();
                frameLayout.setVisibility(View.GONE);

                //  Log.v("EmployeeMapACTIVITY" , "DATA" + "    " + employRout + "    " + employPickUPPoint + "    " +employeeShifttime);

                getDriverLocation(employRout + employeeShifttime, employPickUPPoint);


            }
        });


        final DrawerLayout drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mopendrawer = findViewById(R.id.opendrawereemployee);

        mopendrawer.setOnClickListener(new View.OnClickListener() {
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
        getMenuInflater().inflate(R.menu.employee_home, menu);
        return true;
    }
    // private ArrayMap<String,LatLng> allDriverLocation = new ArrayMap<>();

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

    private void getDriverLocation(final String mchild, final String memployPickUPPoint) {


        LatLng emloyepoint = Route.getPoint(EmployeeHome.this, memployPickUPPoint);
        final Location location = new Location("");
        location.setLatitude(emloyepoint.latitude);
        location.setLongitude(emloyepoint.longitude);


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(mchild);


        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                s = dataSnapshot.getKey();
                Map<Object, Object> map = (Map<Object, Object>) dataSnapshot.getValue();
                String mDriverLocation = map.get("l").toString();
                EmployeeHome.getdriverlatlong getdriverlatlong = splitString(mDriverLocation);
                LatLng DriverLocation = new LatLng(getdriverlatlong.getDriverlat(), getdriverlatlong.getDriverlong());


                DrivernewMarker = mMap.addMarker(new MarkerOptions().position(DriverLocation).title("Your Car"));

                DriverMarker.put(s, DrivernewMarker);


                LatLngBounds.Builder bld = new LatLngBounds.Builder();

                bld.include(DriverMarker.get(s).getPosition());
                bld.include(mEmployeeLocation);

                LatLngBounds bounds = bld.build();
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 70));

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                s = dataSnapshot.getKey();
                Log.v("EmployeeMapACTIVITY", "DATA" + s);
                DriverMarker.get(s).remove();
                Map<Object, Object> map = (Map<Object, Object>) dataSnapshot.getValue();
                String mDriverLocation = map.get("l").toString();
                EmployeeHome.getdriverlatlong getdriverlatlong = splitString(mDriverLocation);
                LatLng DriverLocation = new LatLng(getdriverlatlong.getDriverlat(), getdriverlatlong.getDriverlong());
                DrivernewMarker = mMap.addMarker(new MarkerOptions().position(DriverLocation).title("Your Car"));
                DriverMarker.put(s, DrivernewMarker);
                // allDriverLocation.put(s,DriverLocation) ;

                Location location2 = new Location("");
                location2.setLatitude(DriverLocation.latitude);
                location2.setLongitude(DriverLocation.longitude);

                float distance = location2.distanceTo(location);


                LatLngBounds.Builder bld = new LatLngBounds.Builder();

                for (int index = 0; index < DriverMarker.size(); index++) {

                    bld.include(DriverMarker.valueAt(index).getPosition());
                    Log.v("EMPLOYEEHOMETAG", "DATA" + DriverMarker.valueAt(index).getPosition());
                }


                bld.include(mEmployeeLocation);

                LatLngBounds bounds = bld.build();
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 70));


                if (distance <= 2) {

                    NotificationCompat.Builder notification = new NotificationCompat.Builder(EmployeeHome.this, "xxxx");
                    notification.setAutoCancel(true)
                            .setSmallIcon(R.mipmap.ic_launcher_round)
                            .setTicker("This is the Ticker")
                            .setWhen(System.currentTimeMillis())
                            .setContentTitle("this is contact titl")
                            .setContentText("this is text");

                    Intent intent = new Intent(getBaseContext(), EmployeeHome.class);


                    PendingIntent pendingIntent = PendingIntent.getActivity(EmployeeHome.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    notification.setContentIntent(pendingIntent);
                    NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    nm.notify(0, notification.build());


                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                String s = dataSnapshot.getKey();
                DriverMarker.get(s).remove();
                // allDriverLocation.remove(s);

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });


        // reference.addValueEventListener(new ValueEventListener() {
        //     @Override
        //     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

        //        if (!dataSnapshot.exists()) {


        //           Toast.makeText(EmployeeMap.this, "when driver avilabil will appered on map ", Toast.LENGTH_LONG).show();
        //       } else {

        //           Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

        //           Set<String> Keyes = map.keySet();


        // LatLng [] DriverLocation = new LatLng[Keyes.toArray().length] ;
        // double distance [] = new double[Keyes.toArray().length] ;
        // JSONObject object = new JSONObject(map);
        // for(int index=0 ; index<Keyes.toArray().length ; index++) {

        //    try {
        //        JSONObject mdriverLocation = object.getJSONObject(String.valueOf(Keyes.toArray()[index])) ;
        //       String mDriverLocation = mdriverLocation.getString("l");

        //       getdriverlatlong getdriverlatlong = splitString(mDriverLocation) ;

        //       DriverLocation [index] = new LatLng(getdriverlatlong.getDriverlat(),getdriverlatlong.getDriverlong());

        //        if(DriverMarker[index] !=null) {

        //           DriverMarker[index].remove();
        //       }


        //       DriverMarker [index] = mMap.addMarker(new MarkerOptions().position(DriverLocation[index]).title("Your Car"));

        //       Location location2 = new Location("");
        //       location2.setLatitude(DriverLocation[index].latitude);
        //       location2.setLongitude(DriverLocation[index].longitude);

        //        distance [index] = location2.distanceTo(location) ;


        //       Log.v("EmployeeMapACTIVITY" , "Data"+ distance [index]);

        //   } catch (JSONException e) {
        //        e.printStackTrace();
        //   }

        //   }

        //  }
        //  }

        //  @Override
        //   public void onCancelled(@NonNull DatabaseError databaseError) {

        //   }
        // });
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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.


            Log.v("EMPLOOYMAPTAG", "Data : " + "have error");

            return;
        }


        bildgoogleclint();
        mMap.setMyLocationEnabled(true);


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

            Log.v("EMPLOOYMAPTAG", "Data : " + "need permissions ");
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

    static final class getdriverlatlong {

        private double driverlat;
        private double driverlong;

        public getdriverlatlong(double driverlat, double driverlong) {
            this.driverlat = driverlat;
            this.driverlong = driverlong;
        }


        public double getDriverlat() {
            return driverlat;
        }

        public double getDriverlong() {
            return driverlong;
        }
    }


}
