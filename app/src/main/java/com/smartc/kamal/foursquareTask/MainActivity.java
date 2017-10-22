package com.smartc.kamal.foursquareTask;

import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  LocationManager mLocationManager;
  String mprovider;
  private List<VenueModel> venuesList;
  private RecyclerView.Adapter venuesRecyclerAdapter;
  private MainController mainController;

  int MY_PERMISSIONS_REQUEST_FIND_LOCATION = 0;

  @RequiresApi(api = Build.VERSION_CODES.M)
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    venuesList = new ArrayList<>();
    mainController = new MainControllerImpl();

    RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.venues_recycler_view);
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

    assert mRecyclerView != null;
    mRecyclerView.setLayoutManager(linearLayoutManager);
    venuesRecyclerAdapter = new VenueRecyclerAdapter(venuesList, MainActivity.this);
    mRecyclerView.setAdapter(venuesRecyclerAdapter);

    checkLocationAllow();
    getLocation();


  }

  public static boolean checkLocation(final Context context) {
    LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    boolean gps_enabled = false;
    boolean network_enabled = false;

    try {
      gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    } catch (Exception ex) {
    }

    try {
      network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    } catch (Exception ex) {
    }

    if (!gps_enabled && !network_enabled) {
      return false;
    }
    return true;
  }

  public static void openLocationDialog(final Context context) {
    // notify user
    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
    dialog.setTitle("Error");
    dialog.setMessage("open GPS");
    dialog.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface paramDialogInterface, int paramInt) {
        // TODO Auto-generated method stub
        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(myIntent);
        //get gps
      }
    });
    dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

      @Override
      public void onClick(DialogInterface paramDialogInterface, int paramInt) {
        // TODO Auto-generated method stub

      }
    });
    dialog.show();
  }

  private void getLocation() {
    Location location;
    mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      // TODO: Consider calling
      //    ActivityCompat#requestPermissions
      // here to request the missing permissions, and then overriding
      //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
      //                                          int[] grantResults)
      // to handle the case where the user grants the permission. See the documentation
      // for ActivityCompat#requestPermissions for more details.
      return;
    }
    if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
      mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 500.0f, locationListener);
      location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }
    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000L, 500.0f, locationListener);
    location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);


    if (location != null) {
      double fromLat = location.getLatitude();
      double fromLong = location.getLongitude();
      String lo = fromLat+","+fromLong;
      mainController.getVenuesData(lo, venuesList, venuesRecyclerAdapter, MainActivity.this);
    }else {
      mainController.getVenuesData("30.154440,%2031.357501", venuesList, venuesRecyclerAdapter, MainActivity.this);
    }

  }

  private final LocationListener locationListener = new LocationListener() {
    public void onLocationChanged(Location location) {

    }

    public void onProviderDisabled(String provider) {

    }

    public void onProviderEnabled(String provider) {
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
  };

  @RequiresApi(api = Build.VERSION_CODES.M)
  public void checkLocationAllow() {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
      requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
              MY_PERMISSIONS_REQUEST_FIND_LOCATION);
      return;
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {

    if (requestCode == MY_PERMISSIONS_REQUEST_FIND_LOCATION) {
      if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

      } else {
        startInstalledAppDetailsActivity(this);
      }
    }
  }

  public static void startInstalledAppDetailsActivity(final Activity context) {
    if (context == null) {
      return;
    }
    final Intent i = new Intent();
    i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
    i.addCategory(Intent.CATEGORY_DEFAULT);
    i.setData(Uri.parse("package:" + context.getPackageName()));
    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
    i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
    context.startActivity(i);
  }
}