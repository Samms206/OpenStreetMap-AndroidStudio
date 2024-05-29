package com.example.openstreetmap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

public class MainActivity extends AppCompatActivity {

    MapController mapController;
    EditText etLat, etLong;
    MapView mapApp;
    Button btnSearch;
    Double latCurrentPosition = -7.811528494881481, longCurrentPosition = 113.2968430795848; // Set default to home location
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        AccessPermission();

        etLat = (EditText) findViewById(R.id.tf_latitude);
        etLong = (EditText) findViewById(R.id.tf_longtitude);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        mapApp = (MapView) findViewById(R.id.mapView);

        mapApp.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        Configuration.getInstance().setUserAgentValue(getPackageName());
        mapApp.setBuiltInZoomControls(true);
        mapApp.setMultiTouchControls(true);
        mapController = (MapController) mapApp.getController();
        mapController.setZoom(15);

        // Mengatur pusat default ke koordinat yang diinginkan
        GeoPoint pointCenter = new GeoPoint(latCurrentPosition, longCurrentPosition);
        mapController.setCenter(pointCenter);

        final MyLocationNewOverlay myLocationNewOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(this), mapApp);
        myLocationNewOverlay.enableMyLocation();
        myLocationNewOverlay.runOnFirstFix(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        GeoPoint myLocation = myLocationNewOverlay.getMyLocation();
                        if (myLocation != null) {
                            mapApp.getController().animateTo(myLocation);
                            latCurrentPosition = myLocation.getLatitude();
                            longCurrentPosition = myLocation.getLongitude();
                            Log.d(TAG, "Location updated: " + latCurrentPosition + ", " + longCurrentPosition);
                        }
                    }
                });
            }
        });

        mapApp.getOverlays().add(myLocationNewOverlay);

        GeoPoint mPoint = new GeoPoint(-7.127808022477208, 112.72311288174512);
        Marker myMarkerPoint = new Marker(mapApp);
        myMarkerPoint.setPosition(mPoint);
        myMarkerPoint.setTitle("Universitas Trunojoyo Madura");
        myMarkerPoint.setIcon(this.getResources().getDrawable(R.mipmap.ic_launcher_round));
        myMarkerPoint.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapApp.getOverlays().add(myMarkerPoint);

        MapEventsReceiver mapEventsReceiver = new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                Toast.makeText(getApplicationContext(), p.getLatitude() + "-" + p.getLongitude(), Toast.LENGTH_SHORT).show();
                etLat.setText(String.valueOf(p.getLatitude()));
                etLong.setText(String.valueOf(p.getLongitude()));
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        };

        MapEventsOverlay eventsOverlay = new MapEventsOverlay(getApplicationContext(), mapEventsReceiver);
        mapApp.getOverlays().add(eventsOverlay);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Current position: " + latCurrentPosition + ", " + longCurrentPosition);
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                intent.putExtra("LatTujuan", Double.parseDouble(etLat.getText().toString()));
                intent.putExtra("LongTujuan", Double.parseDouble(etLong.getText().toString()));
                intent.putExtra("LatAsal", latCurrentPosition);
                intent.putExtra("LongAsal", longCurrentPosition);
                startActivity(intent);
            }
        });
    }

    void AccessPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Permission Granted Fine Location", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Permission Denied Fine Location", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

