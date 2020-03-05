package fyp.c16398141.healthlete;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

public class food_entry extends AppCompatActivity {

    LocalDB ldb;
    private Button button;
    EditText field1,field2,field3,field4,field5,field6,date;
    DatePickerDialog datePickerDialog;
    //String update= getIntent().getStringExtra("EXTRA_ID");
    //Integer result = Integer.parseInt(update);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_entry);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ldb = new LocalDB(this);

        field1   = (EditText)findViewById(R.id.field1);
        field2   = (EditText)findViewById(R.id.field2);
        field3   = (EditText)findViewById(R.id.field3);
        field4   = (EditText)findViewById(R.id.field4);
        field5   = (EditText)findViewById(R.id.field5);
        field6   = (EditText)findViewById(R.id.field6);
        date = (EditText) findViewById(R.id.date);

        button = (Button) findViewById(R.id.button);
        //*******************************************************************
        //MAKE DYNAMIC, CENTRAL LAYOUT, DEFAULT SET TO TODAY'S DATE INCLUDING HINT ELEMENT AND COMPATIBLE WITH DB
        // THEN SORT TABLE ROWS BY DATE WITH SELECTABLE POSSIBLE DATE PICKER FILTER ABOVE TABLE WITH ERROR CHECKS
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(food_entry.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                date.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        //when button is clicked, collect the textview's contents and get ready to update the database with them
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String foodname = field1.getText().toString();
                String qty = field2.getText().toString();
                int quantity = Integer.parseInt(qty);
                String date = field3.getText().toString();
                String cals = field4.getText().toString();
                int calsperqty = Integer.parseInt(cals);
                String carbs = field5.getText().toString();
                int carbsperqty = Integer.parseInt(carbs);
                String proteins = field6.getText().toString();
                int proteinsperqty = Integer.parseInt(proteins);
                //String newDob = date.getText().toString();

                int result = 1;
                ldb.open();

                //if previous activity says to insert, then call the insert method
                if(result==1) {
                    boolean update = ldb.addFoodEntry(
                            foodname,
                            quantity,
                            date,
                            calsperqty,
                            carbsperqty,
                            proteinsperqty
                    );

                    if (update == true) {
                        Toast.makeText(getApplicationContext(), "Successful insert", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(food_entry.this, food_log.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "unsuccessful insert", Toast.LENGTH_SHORT).show();
                    }
                    ViewGroup group = (ViewGroup)findViewById(R.id.constraint);
                    for (int i = 0, count = group.getChildCount(); i < count; ++i) {
                        View view = group.getChildAt(i);
                        if (view instanceof EditText) {
                            ((EditText)view).setText("");
                        }
                    }
                }
                ldb.close();
                /*
                //if previous activity says to update, then call the update method
                else{
                    boolean update = ldb.editFoodEntry(
                            foodname,
                            quantity,
                            date,
                            calsperqty,
                            carbsperqty,
                            proteinsperqty
                    );

                    if (update == true) {
                        Toast.makeText(getApplicationContext(), "Successful update", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "unsuccessful update", Toast.LENGTH_SHORT).show();
                    }
                }*/
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
