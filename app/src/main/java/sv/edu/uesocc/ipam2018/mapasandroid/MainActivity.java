package sv.edu.uesocc.ipam2018.mapasandroid;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap Mapa;
    private Button btnOpciones;
    private Button btnIr;
    private Button btnPosicion;
    private Button btnMostrar;
    private Button btnIrUES;
    private EditText latitud;
    private EditText longitud;
    private static final int MI_PERMISO = 1;
    private double lat = 0.0;
    private double lng = 0.0;
    private ArrayList<String> latCirculos;
    private ArrayList<String> lngCirculos;
    private Set<String> latCirculo;
    private Set<String> lngCirculo;
    private boolean doit;
    private String tamanio;
    private String color;
    private String mapa;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("map_preferences",MODE_PRIVATE);
        latCirculos = new ArrayList<>();
        lngCirculos = new ArrayList<>();
        latCirculo = new HashSet<String>();
        lngCirculo = new HashSet<String>();
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        latCirculo = preferences.getStringSet("latCirculo",null);
        lngCirculo = preferences.getStringSet("lngCirculo", null);
        mapa = preferences.getString("tipo","Normal");
        tamanio = preferences.getString("tamanio", "3000");
        color = preferences.getString("color", "Blue");
        lat = Double.parseDouble(preferences.getString("latitud", "13.970263"));
        lng = Double.parseDouble(preferences.getString("longitud", "-89.574808"));
        if (latCirculo != null){
            latCirculos = new ArrayList<String>(latCirculo);
            lngCirculos = new ArrayList<String>(lngCirculo);

        }
        Log.d("TIPO DE MAPA",mapa);
        /*if (savedInstanceState != null) {
            latCirculos = savedInstanceState.getStringArrayList("latCirculos");
            lngCirculos = savedInstanceState.getStringArrayList("lngCirculos");

        }*/

        btnOpciones = (Button) findViewById(R.id.btnOpciones);
        btnOpciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OpcionesMapa.class);
                startActivityForResult(intent, 1);
            }
        });

        btnIrUES = findViewById(R.id.btnUES);
        btnIrUES.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irUES();
            }
        });

        btnIr = (Button) findViewById(R.id.btnMover);
        btnIr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((latitud.getText().toString().isEmpty() || latitud.getText().toString() == null)
                        && (longitud.getText().toString().isEmpty() || longitud.getText().toString() == null)) {
                    lat = 0.0;
                    lng = 0.0;
                    irPosicion();
                } else {
                    lat = Double.parseDouble(latitud.getText().toString());
                    lng = Double.parseDouble(longitud.getText().toString());
                    irPosicion();
                }

            }
        });

        btnPosicion = (Button) findViewById(R.id.btnPosicion);
        btnPosicion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerPosicion();
            }
        });

        btnMostrar = (Button) findViewById(R.id.btnPin);
        btnMostrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarPunto();
            }
        });

        latitud = (EditText) findViewById(R.id.latitudtext);
        longitud = (EditText) findViewById(R.id.longitudtxt);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MI_PERMISO);
    }

    /*@Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("latitud", latitud.getText().toString());
        outState.putString("longitud", longitud.getText().toString());
        outState.putStringArrayList("latCirculos", latCirculos);
        outState.putStringArrayList("lngCirculos", lngCirculos);
        super.onSaveInstanceState(outState);
    }*/

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Mapa = googleMap;
        mapType();
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
                addCirculo(point);
            }
        });

        if (latCirculos != null) {
            Log.d("COLOCARA PUNTOS","Deberia servir");
                int i = 0;
                int tam = latCirculos.size();
                while (i < tam){
                    Log.d("LATITUD",latCirculos.get(i));
                    Log.d("LONGITUD",lngCirculos.get(i));
                    addCirculo(new LatLng(Double.parseDouble( latCirculos.get(i)),
                            Double.parseDouble(latCirculos.get(i))));
                    i++;
                }
        }
        moveCamara();
    }

    private void addCirculo(LatLng point){
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(point);
        latCirculos.add(point.latitude + "");
        lngCirculos.add(point.longitude + "");

        if (color.equals("Negro")) {
            circleOptions.fillColor(Color.HSVToColor(75, new float[]{Color.BLACK, 1, 1}));
            Log.d("COLOR",color);
        } else if (color.equals("Azul")) {
            circleOptions.fillColor(Color.HSVToColor(75, new float[]{Color.BLUE, 1, 1}));
            Log.d("COLOR",color);
        } else if (color.equals("Verde")) {
            circleOptions.fillColor(Color.HSVToColor(75, new float[]{Color.GREEN, 1, 1}));
            Log.d("COLOR",color);
        } else if (color.equals("Rojo")) {
            circleOptions.fillColor(Color.HSVToColor(75, new float[]{Color.RED, 1, 1}));
            Log.d("COLOR",color);
        }
        circleOptions.radius(Integer.parseInt(tamanio));
        circleOptions.strokeWidth(1);
        Circle circulo = Mapa.addCircle(circleOptions);
    }

    private void mapType(){
        if (mapa.equals("Normal")) {
            Mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            Mapa.getUiSettings().setZoomControlsEnabled(true);
        } else if (mapa.equals("Satelite")) {
            Mapa.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            Mapa.getUiSettings().setZoomControlsEnabled(true);
        } else if (mapa.equals("Terreno")) {
            Mapa.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            Mapa.getUiSettings().setZoomControlsEnabled(true);
        } else if (mapa.equals("Hibrido")) {
            Mapa.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            Mapa.getUiSettings().setZoomControlsEnabled(true);
        }
    }

    private void irPosicion() {
        CameraUpdate camUpd1 = CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 16);
        Mapa.moveCamera(camUpd1);
    }

    public void moveCamara(){
        Mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng),16));
    }

    public void guardar(){
        if (latCirculos.size() > 0){
            latCirculo = new HashSet<String>(latCirculos);
            lngCirculo = new HashSet<String>(lngCirculos);
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet("latCirculo", latCirculo);
        editor.putStringSet("lngCirculo", lngCirculo);
        editor.putString("latitud", lat+"");
        editor.putString("longitud", lng+"");
        editor.commit();
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
        lat = coordenadas.latitude;
        lng = coordenadas.longitude;
        Toast.makeText(this, "Lat: " + lat + " | Long: " + lng, Toast.LENGTH_SHORT).show();
    }

    private void agregarPunto() {
        Mapa.addMarker(new MarkerOptions().position(new LatLng(13.970263, -89.574808))
                .title("Santa Ana: UES"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MI_PERMISO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    Mapa.setMyLocationEnabled(true);
                } else {
                    Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                mapa = data.getStringExtra("tipo");
                tamanio = data.getStringExtra("tamanio");
                color = data.getStringExtra("color");
                mapType();
            }
        }
    }

    @Override
    protected void onDestroy() {
        obtenerPosicion();
        guardar();
        super.onDestroy();
    }
}
