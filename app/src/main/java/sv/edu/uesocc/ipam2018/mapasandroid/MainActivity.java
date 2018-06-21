package sv.edu.uesocc.ipam2018.mapasandroid;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap Mapa;
    private Button btnOpciones;
    private Button btnIr;
    private Button btnPosicion;
    private Button btnMostrar;
    private static final int MI_PERMISO =1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        btnOpciones = findViewById(R.id.btnOpciones);
        btnOpciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarOpciones();
            }
        });

        btnIr = findViewById(R.id.btnMover);
        btnIr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irUES();
            }
        });

        btnPosicion = findViewById(R.id.btnPosicion);
        btnPosicion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerPosicion();
            }
        });

        btnMostrar = findViewById(R.id.btnPin);
        btnMostrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarPunto();
            }
        });

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MI_PERMISO);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Mapa = googleMap;

        Mapa.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            public void onMapClick(LatLng point) {


                Toast.makeText(
                        MainActivity.this,
                        "Click\n" +
                                "Lat: " + point.latitude + "\n" +
                                "Lng: " + point.longitude + "\n",
                        Toast.LENGTH_SHORT).show();
            }
        });

        Mapa.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            public void onMapLongClick(LatLng point) {

                Toast.makeText(MainActivity.this, "Punto del Clic: " + point, Toast.LENGTH_LONG).show();
                CircleOptions circleOptions = new CircleOptions();
                circleOptions.center(point);
                circleOptions.fillColor(Color.HSVToColor(75, new float[]{Color.BLUE, 1, 1}));
                circleOptions.radius(1000);
                circleOptions.strokeWidth(1);
                Circle circulo = Mapa.addCircle(circleOptions);
            }
        });

        Mapa.getUiSettings().setZoomGesturesEnabled(true);
        Mapa.getUiSettings().setScrollGesturesEnabled(true);
        Mapa.getUiSettings().setTiltGesturesEnabled(true);
    }

    private void cambiarOpciones() {
        Mapa.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        Mapa.getUiSettings().setZoomControlsEnabled(true);
    }

    private void irUES() {
        CameraUpdate camUpd1 =
                CameraUpdateFactory
                        .newLatLngZoom(new LatLng(13.970263, -89.574808), 16);

        Mapa.moveCamera(camUpd1);
    }


    private void obtenerPosicion() {
        CameraPosition camPosicion = Mapa.getCameraPosition();

        LatLng coordenadas = camPosicion.target;
        double latitud = coordenadas.latitude;
        double longitud = coordenadas.longitude;

        Toast.makeText(this, "Lat: " + latitud + " | Long: " + longitud, Toast.LENGTH_SHORT).show();
    }


    private void agregarPunto() {
        Mapa.addMarker(new MarkerOptions()
                .position(new LatLng(13.970263, -89.574808))
                .title("Santa Ana: UES"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MI_PERMISO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Mapa.setMyLocationEnabled(true);
                } else {
                    Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

}
