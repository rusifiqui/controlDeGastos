package jvilam.com.controldegastos;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import jvilam.com.controldegastos.Helpers.ExpensesDatabaseHelper;

import static org.apache.commons.lang3.StringUtils.leftPad;

public class MainActivity extends AppCompatActivity {

    // Variables globales
    Spinner spinnerExpenseType;
    EditText editTextAmount;
    EditText editTextDate;
    EditText editTextDescription;
    String address;
    FloatingActionButton saveToDatabaseFab;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Comprobamos que la aplicación tenga permisos para utilizar la ubicación y, en caso de no tenerlo, se solicita.
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION )
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1);
        }

        // Variables de la interfaz
        spinnerExpenseType = (Spinner) findViewById(R.id.spinnerType);
        editTextAmount = (EditText) findViewById(R.id.editTextAmount);
        editTextDate = (EditText) findViewById(R.id.editTextDate);
        editTextDescription = (EditText) findViewById(R.id.editTextDescription);
        // fin variables de la interfaz

        // Introducimos los valores de los tipos de gasto.
        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(this,
                R.array.expense_types, android.R.layout.simple_spinner_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExpenseType.setAdapter(arrayAdapter);
        // Fin valores tipos de gasto

        // Introducimos la fecha actual en el campo #editTextAmount
        Calendar cal = Calendar.getInstance();
        editTextDate.setText(leftPad(String.valueOf(cal.get(Calendar.DATE)), 2, "0") + "/" +
                leftPad(String.valueOf(cal.get(Calendar.MONTH) + 1), 2, "0") + "/" + cal.get(Calendar.YEAR));

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

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        // Fin localización


        // Botones flotantes
        saveToDatabaseFab = (FloatingActionButton) findViewById(R.id.save_to_database_fab);
        saveToDatabaseFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Almacenamos la información en la base de datos
                if(insertExpense()){
                    Snackbar.make(view, R.string.expense_saved, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    saveToDatabaseFab.setEnabled(false);
                }else{
                    Snackbar.make(view, R.string.expense_saved_error, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        FloatingActionButton viewExpensesFab = (FloatingActionButton) findViewById(R.id.view_expenses_fab);
        viewExpensesFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ExpensesActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton resetExpenseFab = (FloatingActionButton) findViewById(R.id.reset_expense_fab);
        resetExpenseFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createResetDataDialog();
            }
        });
        // Fin botones flotantes
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

    /**
     * Método que inserta un nuevo registro con el gasto introducido por el usuario
     * @return true si se ha realizado correctamente la operación de inserción. False, en caso contrario.
     */
    protected boolean insertExpense(){
        ExpensesDatabaseHelper expensesHelper = new ExpensesDatabaseHelper(getApplicationContext(), ExpensesDatabaseHelper.DATABASE_TABLE, null, 1);
        SQLiteDatabase db = expensesHelper.getWritableDatabase();

        ContentValues newValues = new ContentValues();
        newValues.put(ExpensesDatabaseHelper.KEY_TYPE, spinnerExpenseType.getSelectedItem().toString());
        newValues.put(ExpensesDatabaseHelper.KEY_AMOUNT, editTextAmount.getText().toString());
        newValues.put(ExpensesDatabaseHelper.KEY_DATE, editTextDate.getText().toString());
        newValues.put(ExpensesDatabaseHelper.KEY_DESCRIPTION, editTextDescription.getText().toString());
        newValues.put(ExpensesDatabaseHelper.KEY_ADDRESS, address);
        if(db.insert(ExpensesDatabaseHelper.DATABASE_TABLE, null, newValues) == -1){
            db.close();
            return false;
        }else{
            db.close();
            return true;
        }


    }

    /**
     * Método que genera un diálogo para que el usuario seleccione si desea continuar con la operación
     * de reseteo de la vista.
     * En caso afirmativo, llama al método resetView()
     */
    protected void createResetDataDialog(){
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);

        builder.setMessage(R.string.reset_confirmation)
                .setTitle(R.string.attention)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        resetView();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog confirmDialog = builder.create();
        confirmDialog.show();
    }

    /**
     * Método que resetea la vista: elimina el importe y la descripción, y habilita el botón de guardado.
     */
    protected void resetView(){
        editTextAmount.setText("");
        editTextDescription.setText("");
        saveToDatabaseFab.setEnabled(true);
    }
}
