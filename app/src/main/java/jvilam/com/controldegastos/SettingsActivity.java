package jvilam.com.controldegastos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import jvilam.com.controldegastos.Helpers.ExpensesDatabaseHelper;

/**
 * Clase que gestiona los ajustes de la aplicación.
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Button deleteData = (Button) findViewById(R.id.buttonDeleteDatabase);

        // Listener para el botón de eliminar datos
        deleteData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deteleData();
            }
        });

        // Botón "Volver"
        FloatingActionButton backFab = (FloatingActionButton) findViewById(R.id.settings_back_fab);
        backFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * Método que elimina toda la información almacenada en la base de datos referente a los gastos.
     */
    protected void deteleData(){
        // Se genera un mensaje para advertir al usuario de que se va a eliminar toda la información
        // almacenada en la aplicación.
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_confirmation)
                .setTitle(R.string.attention)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // En caso de que el usuario decida continuar, eliminamos la información de la tabla EXPENSES
                        ExpensesDatabaseHelper expensesHelper =
                                new ExpensesDatabaseHelper(getApplicationContext(), ExpensesDatabaseHelper.DATABASE_TABLE, null, 1);
                        SQLiteDatabase db = expensesHelper.getWritableDatabase();
                        if(db.delete(ExpensesDatabaseHelper.DATABASE_TABLE, null, null) != -1) {
                            // Notificamos al usuario del resultado de la operación - OK
                            Toast locToast = Toast.makeText(getApplicationContext(),
                                    R.string.data_deleted, Toast.LENGTH_SHORT);
                            locToast.show();
                        }else{
                            // Notificamos al usuario del resultado de la operación - ERROR
                            Toast locToast = Toast.makeText(getApplicationContext(),
                                    R.string.data_deleted_error, Toast.LENGTH_SHORT);
                            locToast.show();
                        }
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
}
