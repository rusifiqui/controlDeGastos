package jvilam.com.controldegastos;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import jvilam.com.controldegastos.Helpers.ExpensesDatabaseHelper;
import jvilam.com.controldegastos.queries.Queries;

public class ExpensesActivity extends AppCompatActivity {

    TextView expenses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);

        expenses = (TextView) findViewById(R.id.textViewExpenses);
        expenses.setText("");
        queryExpenses();

        // Botón "Volver"
        FloatingActionButton backFab = (FloatingActionButton) findViewById(R.id.expenses_back_fab);
        backFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    protected void queryExpenses(){
        ExpensesDatabaseHelper expensesHelper = new ExpensesDatabaseHelper(getApplicationContext(), ExpensesDatabaseHelper.DATABASE_TABLE, null, 1);
        SQLiteDatabase db = expensesHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery(Queries.SELECT_ALL_EXPENSES, null);
        while(cursor.moveToNext()){
            String expensesSummary = "";
            for(int i = 0; i < cursor.getColumnCount(); i++){
                expensesSummary = expensesSummary.concat(cursor.getString(i) != null ? cursor.getString(i) : "");
                if(i<cursor.getColumnCount()-1)
                    expensesSummary = expensesSummary.concat(" - ");
            }
            expenses.setText(expenses.getText().toString().concat(expensesSummary + "\n"));
        }
        cursor.close();
        db.close();

    }
}
