package fyp.c16398141.healthlete;

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

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;
/*
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;*/

import static java.lang.String.valueOf;

public class food_entry extends AppCompatActivity {

    LocalDB ldb;
    private Button button;
    EditText food,qty_field,datefield,cal_field,carb_field,protein_field;
    ChipGroup chipGroup;
    Chip unitchip, gramchip, mlchip;

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

        food   = (EditText)findViewById(R.id.food);
        qty_field   = (EditText)findViewById(R.id.qty_field);
        datefield = (EditText) findViewById(R.id.datefield);
        cal_field   = (EditText)findViewById(R.id.cal_field);
        carb_field   = (EditText)findViewById(R.id.carb_field);
        protein_field   = (EditText)findViewById(R.id.protein_field);

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
        datefield.setText(mDay + "/" + mMonth + "/" + mYear);
        date = "" + mDay + mMonth + mYear;

        datefield.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // date picker dialog
                datePickerDialog = new DatePickerDialog(food_entry.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                datefield.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                date = "" + dayOfMonth + (monthOfYear +1) + year;
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
                String foodname = food.getText().toString();
                String qty = qty_field.getText().toString();
                int quantity = Integer.parseInt(qty);
                String cals = cal_field.getText().toString();
                int calsperqty = Integer.parseInt(cals);
                String carbs = carb_field.getText().toString();
                int carbsperqty = Integer.parseInt(carbs);
                String proteins = protein_field.getText().toString();
                int proteinsperqty = Integer.parseInt(proteins);
                //String newDob = date.getText().toString();
                //String caloriesQ ="https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/quickAnswer?q=How%20many%20calories%20are%20in%20" + quantity + "%20" + foodname + "%253F";
                //String carbsQ ="https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/quickAnswer?q=How%20many%20carbohydrates%20are%20in%20" +quantity + "%20" + foodname + "%253F";
                //String proteinQ ="https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/quickAnswer?q=How%20many%20calories%20are%20in%20" +quantity + "%20" + foodname + "%253F";
               /* HttpUrl.Builder urlBuilder = new HttpUrl.Builder();
                //urlBuilder = HttpUrl.parse("https://httpbin.org/get).newBuilder();
                urlBuilder.addQueryParameter("website", "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/quickAnswer?q=");
                urlBuilder.addQueryParameter("question", "How%20many%20calories%20are%20in%20");
                urlBuilder.addQueryParameter("foodname", foodname);
                String url = urlBuilder.build().toString();*/
               String aggregate;
               if (!qtype.contentEquals(unitchip.getText().toString()))
               {
                   aggregate = quantity + " " + qtype;
               }
               else aggregate = "";

                String nutrients[] = {"calories", "carbohydrates", "protein"};
                for(String nutrient : nutrients){
                    try {
                        doGetRequest(nutrient, aggregate, foodname);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                /*Request request = new Request.Builder()
                        .url("https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/quickAnswer?q=How%20much%20vitamin%20c%20is%20in%202%20apples%253F")
                        .get()
                        .addHeader("x-rapidapi-host", "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com")
                        .addHeader("x-rapidapi-key", "70bc3950damsh77f862bb1d46fc6p15525djsn68d30d563427")
                        .build();*/

                /*Request request = new Request.Builder()
                        .url("https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/guessNutrition?title=Spaghetti%20Aglio%20et%20Olio")
                        .get()
                        .addHeader("x-rapidapi-host", "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com")
                        .addHeader("x-rapidapi-key", "70bc3950damsh77f862bb1d46fc6p15525djsn68d30d563427")
                        .build();*/

                /*try {
                    Response response = client.newCall(request).execute();
                    //final String logger = response.body().toString();
                    //Log.i("TAG",logger);
                } catch (IOException e) {
                    e.printStackTrace();
                }*/

                int result = 1;
                ldb.open();

                //if previous activity says to insert, then call the insert method
                if(result==1) {
                    boolean update = ldb.addFoodEntry(foodname, quantity, qtype, date, calsperqty, carbsperqty, proteinsperqty, user_id);

                    if (update == true) {
                        Toast.makeText(getApplicationContext(), "Successful insert", Toast.LENGTH_SHORT).show();
                        //Intent intent = new Intent(food_entry.this, food_log.class);
                        //startActivity(intent);
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

    void doGetRequest(String nutrient, String aggregate, String item) throws IOException{
        String site = "https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/quickAnswer?q=How%20much%20";
        String url = site + nutrient + "%20is%20in%20" + aggregate + "%20" + item + "%253F";

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("x-rapidapi-host", "spoonacular-recipe-food-nutrition-v1.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "70bc3950damsh77f862bb1d46fc6p15525djsn68d30d563427")
                .build();

        /*try {
            Response response = client.newCall(request).execute();
            //final String logger = response.body().toString();
            //Log.i("TAG",logger);
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // For the example, you can show an error dialog or a toast
                                // on the main UI thread
                            }
                        });
                    }

                    @Override
                    public void onResponse(final Response response) throws IOException {
                        String res = response.body().string();
                        Log.i("TAG",res);
                        // Do something with the response
                    }

                   /* @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        String res = response.body().string();

                        // Do something with the response
                    }*/
                });
    }
}
