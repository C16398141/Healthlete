package fyp.c16398141.healthlete;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.lang.String.valueOf;

public class food_entry extends AppCompatActivity {

    LocalDB ldb;
    private Button button;
    EditText food,qty_field,datefield,cal_field,carb_field,protein_field;
    TextView cal_text,carb_text,protein_text;
    ChipGroup chipGroup;
    Chip unitchip, gramchip, mlchip;
    List<Integer> nutrientQty = new ArrayList<>();
    Integer submitAttempts;

    DatePickerDialog datePickerDialog;
    //String update= getIntent().getStringExtra("EXTRA_ID");
    //Integer result = Integer.parseInt(update);
    String qtype, date;
    String user_id = "2013chrisclarke@gmail.com";
    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_entry);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ldb = new LocalDB(this);
        submitAttempts = 0;

        food   = (EditText)findViewById(R.id.food);
        qty_field   = (EditText)findViewById(R.id.qty_field);
        datefield = (EditText) findViewById(R.id.datefield);
        button = (Button)findViewById(R.id.submit);

        cal_text = (TextView)findViewById(R.id.cal_text);
        cal_text.setVisibility(View.INVISIBLE);
        carb_text = (TextView)findViewById(R.id.carb_text);
        carb_text.setVisibility(View.INVISIBLE);
        protein_text = (TextView)findViewById(R.id.protein_text);
        protein_text.setVisibility(View.INVISIBLE);

        cal_field   = (EditText)findViewById(R.id.cal_field);
        cal_field.setVisibility(View.INVISIBLE);
        carb_field   = (EditText)findViewById(R.id.carb_field);
        carb_field.setVisibility(View.INVISIBLE);
        protein_field   = (EditText)findViewById(R.id.protein_field);
        protein_field.setVisibility(View.INVISIBLE);

        chipGroup = (ChipGroup)findViewById(R.id.chip_group);
        unitchip = (Chip)findViewById(R.id.unitchip);
        gramchip = (Chip)findViewById(R.id.gramchip);
        mlchip = (Chip)findViewById(R.id.mlchip);

        qtype = unitchip.getText().toString();
        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, int i) {
                Chip chip = chipGroup.findViewById(i);
                if(chip != null){
                    qtype = chip.getText().toString();
                    Log.i("TAG", qtype);
                }
            }
        });


        button = (Button) findViewById(R.id.submit);
        //*******************************************************************
        //MAKE DYNAMIC, CENTRAL LAYOUT, DEFAULT SET TO TODAY'S DATE INCLUDING HINT ELEMENT AND COMPATIBLE WITH DB
        // THEN SORT TABLE ROWS BY DATE WITH SELECTABLE POSSIBLE DATE PICKER FILTER ABOVE TABLE WITH ERROR CHECKS

        // calender class's instance and get current date , month and year from calender
        Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR); // current year
        final int mMonth = c.get(Calendar.MONTH); // current month
        final int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        datefield.setText(mDay + "/" + (mMonth+1) + "/" + mYear);
        date = "" + mDay + (mMonth+1) + mYear;
        Log.i("date",date);

        datefield.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // date picker dialog
                datePickerDialog = new DatePickerDialog(food_entry.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                datefield.setText(dayOfMonth + "/" + (monthOfYear+1) + "/" + year);
                                date = "" + dayOfMonth + (monthOfYear+1) + year;
                                Log.i("TAG",valueOf(date));
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        //when button is clicked, collect the textview's contents and get ready to update the database with them
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard(food_entry.this);

                String foodname = food.getText().toString();
                String qty = qty_field.getText().toString();

                if (TextUtils.isEmpty(foodname)) {
                    food.setError("This field must be filled in");
                    return;
                }
                if (TextUtils.isEmpty(qty)) {
                    qty_field.setError("This field must be filled in");
                    return;
                }

                if (!TextUtils.isDigitsOnly(qty)) {
                    qty_field.setError("The quantity must be a whole number");
                    return;
                }

                int quantity = Integer.parseInt(qty);

                if (submitAttempts == 0) {

                    String nutrients[] = {"calories", "carbohydrates", "protein"};
                    int n = 0;
                    for (String nutrient : nutrients) {
                        n++;
                        try {
                            doGetRequest(nutrient, quantity, qtype, foodname);
                            while (nutrientQty.size() != n) {
                                //Toast.makeText(getApplicationContext(), "Calculating nutritional details", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.i("number", valueOf(nutrientQty.size()));
                    if (nutrientQty.get(0) == 10000) {
                        Toast.makeText(getApplicationContext(), "Nutrients not found, please enter estimates below", Toast.LENGTH_LONG).show();
                    }
                    Log.i("number", valueOf(nutrientQty.size()));
                    int result = 1;

                    //if previous activity says to insert, then call the insert method
                    if (nutrientQty.get(1) != 10000) {
                        if (result == 1) {
                            ldb.open();
                            boolean update = ldb.addFoodEntry(foodname, quantity, qtype, date, nutrientQty.get(0), nutrientQty.get(1), nutrientQty.get(2), user_id);

                            if (update == true) {
                                Toast.makeText(getApplicationContext(), "Successful insert", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(food_entry.this, food_log.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), "Unsuccessful insert", Toast.LENGTH_SHORT).show();
                            }
                            ViewGroup group = (ViewGroup) findViewById(R.id.constraint);
                            for (int i = 0, count = group.getChildCount(); i < count; ++i) {
                                View view = group.getChildAt(i);
                                if (view instanceof EditText) {
                                    ((EditText) view).setText("");
                                }
                            }
                            ldb.close();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Nutrients not found, please enter estimates above", Toast.LENGTH_LONG).show();
                        submitAttempts++;

                        ConstraintLayout constraintLayout = findViewById(R.id.constraint);
                        ConstraintSet constraintSet = new ConstraintSet();
                        constraintSet.clone(constraintLayout);
                        constraintSet.connect(R.id.submit, ConstraintSet.TOP, R.id.protein_field, ConstraintSet.BOTTOM, 0);
                        constraintSet.applyTo(constraintLayout);
                        cal_text.setVisibility(View.VISIBLE);
                        carb_text.setVisibility(View.VISIBLE);
                        protein_text.setVisibility(View.VISIBLE);
                        cal_field.setVisibility(View.VISIBLE);
                        carb_field.setVisibility(View.VISIBLE);
                        protein_field.setVisibility(View.VISIBLE);

                        //make global value above and change value here such that onclick second time requires all parameters below to be not null too

                    }
                } else {
                    String cals = cal_field.getText().toString();
                    String carbs = carb_field.getText().toString();
                    String proteins = protein_field.getText().toString();

                    if (TextUtils.isEmpty(cals)) {
                        cal_field.setError("This field must be filled in");
                        return;
                    }

                    if (!TextUtils.isDigitsOnly(cals)) {
                        cal_field.setError("The quantity must be a whole number");
                        return;
                    }

                    if (TextUtils.isEmpty(carbs)) {
                        carb_field.setError("This field must be filled in");
                        return;
                    }

                    if (!TextUtils.isDigitsOnly(carbs)) {
                        carb_field.setError("The quantity must be a whole number");
                        return;
                    }

                    if (TextUtils.isEmpty(proteins)) {
                        protein_field.setError("This field must be filled in");
                        return;
                    }

                    if (!TextUtils.isDigitsOnly(proteins)) {
                        protein_field.setError("The quantity must be a whole number");
                        return;
                    }

                    int calories = Integer.parseInt(cals);
                    int carbos = Integer.parseInt(carbs);
                    int protein = Integer.parseInt(proteins);

                    ldb.open();
                    boolean update = ldb.addFoodEntry(foodname, quantity, qtype, date, calories, carbos, protein, user_id);

                    if (update == true) {
                        Toast.makeText(getApplicationContext(), "Successful insert", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(food_entry.this, food_log.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Unsuccessful insert", Toast.LENGTH_SHORT).show();
                    }
                }
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

    void doGetRequest(String nutrient, Integer quantity, String qtype, String item) throws IOException
    {
        String aggregate;
        if (!qtype.contentEquals(unitchip.getText().toString()))
        {
            aggregate = quantity + " " + qtype;
        }
        else
        {
            aggregate = "" + quantity;
        }
        String site = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/quickAnswer?q=How%20much%20";
        String url = site + nutrient + "%20is%20in%20" + aggregate + "%20" + item + "%253F";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("x-rapidapi-host", "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "70bc3950damsh77f862bb1d46fc6p15525djsn68d30d563427")
                .build();

        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), "Food not identified", Toast.LENGTH_LONG).show();
                        });
                    }

                    @Override
                    public void onResponse(final Response response) throws IOException {
                        String res = response.body().string();
                        Log.i("Contents", res);
                        if (res.equals("{}"))
                        {
                            Log.i("Null", res);
                            nutrientQty.add(10000);
                        }
                        else {
                            res = res.replaceAll("[^[1-9]\\d*(\\.\\d+)?$]+", " ");

                            String[] figures = res.split(" ");
                            int data = 0;

                            for (int i = 1; i < figures.length; i++) {
                                Log.i("Exists", figures[i]);
                                if (!figures[i].contentEquals(quantity.toString())) {
                                    data = Math.round(Float.parseFloat(figures[i]));
                                    break;
                                }
                            }
                            Log.i("Nutrient", valueOf(data));//valueOf(data));
                            nutrientQty.add(data);
                        }

                        //Account account = JsonConvert.DeserializeObject<Account>(json);

                        //Log.i("TAG",account.Email);
                        // Do something with the response
                    }

                   /* @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        String res = response.body().string();

                        // Do something with the response
                    }*/
                });
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}