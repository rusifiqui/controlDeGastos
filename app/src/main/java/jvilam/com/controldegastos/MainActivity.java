package jvilam.com.controldegastos;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Variables globales
    Spinner spinnerExpenseType;
    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        spinnerExpenseType = (Spinner) findViewById(R.id.spinnerType);
        //TODO crear el resto de variables

        // Introducimos los valores de los tipos de gasto.
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.expense_types, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExpenseType.setAdapter(arrayAdapter);
        // Fin valores tipos de gasto

        // Localización
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // La primera vez que se obtiene la localización, se notifica al usuario.
                if(address == null || address.equals("")){
                    Toast locToast = Toast.makeText(getApplicationContext(), R.string.loc_granted, Toast.LENGTH_SHORT);
                    locToast.show();
                }
                address = getAddress(location);
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        // Fin localización


        // TODO Crear el resto de los botones flotantes
        FloatingActionButton saveToDatabaseFab = (FloatingActionButton) findViewById(R.id.save_to_database_fab);
        saveToDatabaseFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Guardado en la base de datos", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
     * Método que devuelve una cadena de texto que representa la dirección obtenida.
     * @param l La localización
     * @return  La cadena con la dirección
     */
    protected String getAddress(Location l){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        String direccion = "";
        try {
            addresses = geocoder.getFromLocation(l.getLatitude(), l.getLongitude(), 1);
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            direccion = address + " - " + city + " - " + state + " - " + country + " - " + postalCode;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return direccion;
    }
}
