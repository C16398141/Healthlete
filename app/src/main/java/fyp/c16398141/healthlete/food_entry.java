package fyp.c16398141.healthlete;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.okhttp.Callback;
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
import static maes.tech.intentanim.CustomIntent.customType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.lang.String.valueOf;

public class food_entry extends AppCompatActivity {

    List<Integer> nutrientQty = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_entry);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final String[] user_id = {"null"};
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            user_id[0] = firebaseUser.getEmail();
        }else{
            Intent logged_out = new Intent(food_entry.this, MainActivity.class);
            startActivity(logged_out);
        }

        LocalDB ldb = new LocalDB(this);
        final Integer[] submitAttempts = {0};

        EditText food   = (EditText)findViewById(R.id.food);
        EditText qty_field   = (EditText)findViewById(R.id.qty_field);
        EditText datefield = (EditText) findViewById(R.id.datefield);
        Button button = (Button) findViewById(R.id.submit);

        TextView cal_text = (TextView)findViewById(R.id.cal_text);
        cal_text.setVisibility(View.INVISIBLE);
        TextView carb_text = (TextView)findViewById(R.id.carb_text);
        carb_text.setVisibility(View.INVISIBLE);
        TextView protein_text = (TextView)findViewById(R.id.protein_text);
        protein_text.setVisibility(View.INVISIBLE);

        EditText cal_field   = (EditText)findViewById(R.id.cal_field);
        cal_field.setVisibility(View.INVISIBLE);
        EditText carb_field   = (EditText)findViewById(R.id.carb_field);
        carb_field.setVisibility(View.INVISIBLE);
        EditText protein_field   = (EditText)findViewById(R.id.protein_field);
        protein_field.setVisibility(View.INVISIBLE);

        ChipGroup chipGroup = (ChipGroup)findViewById(R.id.chip_group);
        Chip unitchip = (Chip)findViewById(R.id.unitchip);
        Chip gramchip = (Chip)findViewById(R.id.gramchip);
        Chip mlchip = (Chip)findViewById(R.id.mlchip);

        String qtype[] = {unitchip.getText().toString()};
        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, int i) {
                Chip chip = chipGroup.findViewById(i);
                if(chip != null){
                    qtype[0] = chip.getText().toString();
                    Log.i("TAG", qtype[0]);
                }
            }
        });

        // calender class's instance and get current date , month and year from calender
        Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR); // current year
        final int mMonth = c.get(Calendar.MONTH); // current month
        final int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        datefield.setText(mDay + "/" + (mMonth+1) + "/" + mYear);
        final String[] date = {"" + mDay + (mMonth + 1) + mYear};

        datefield.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // date picker dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(food_entry.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                datefield.setText(dayOfMonth + "/" + (monthOfYear+1) + "/" + year);
                                date[0] = "" + dayOfMonth + (monthOfYear+1) + year;
                                Log.i("TAG",valueOf(date[0]));
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

                String user_id = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                if (submitAttempts[0] == 0) {

                    String nutrients[] = {"calories", "carbohydrates", "protein"};
                    int n = 0;
                    for (String nutrient : nutrients) {
                        n++;
                        try {
                            doGetRequest(nutrient, quantity, qtype[0], foodname, unitchip);
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
                            boolean update = ldb.addFoodEntry(foodname, quantity, qtype[0], date[0], nutrientQty.get(0), nutrientQty.get(1), nutrientQty.get(2), user_id);

                            if (update == true) {
                                Toast.makeText(getApplicationContext(), "Successful insert", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(food_entry.this, food_log.class);
                                startActivity(intent);
                                customType(food_entry.this,"right-to-left");
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
                        submitAttempts[0]++;

                        ConstraintLayout constraintLayout = findViewById(R.id.constraint);
                        ConstraintSet constraintSet = new ConstraintSet();
                        constraintSet.clone(constraintLayout);
                        constraintSet.connect(R.id.submit, ConstraintSet.TOP, R.id.protein_field, ConstraintSet.BOTTOM, 20);
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
                    boolean update = ldb.addFoodEntry(foodname, quantity, qtype[0], date[0], calories, carbos, protein, user_id);

                    if (update == true) {
                        Toast.makeText(getApplicationContext(), "Successful insert", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(food_entry.this, food_log.class);
                        startActivity(intent);
                        customType(food_entry.this,"right-to-left");
                    } else {
                        Toast.makeText(getApplicationContext(), "Unsuccessful insert", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    void doGetRequest(String nutrient, Integer quantity, String qtype, String item, Chip unitchip) throws IOException
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


        OkHttpClient client = new OkHttpClient();
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
                    }

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

    @Override
    public void onBackPressed()
    {
        Intent data = new Intent(food_entry.this,food_log.class);
        startActivity(data);
        customType(food_entry.this,"right-to-left");
        super.onBackPressed();
    }
}