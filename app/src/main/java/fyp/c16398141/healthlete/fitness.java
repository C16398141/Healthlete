package fyp.c16398141.healthlete;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.request.DataReadRequest.Builder;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.gms.fitness.data.*;
import com.google.android.gms.fitness.request.*;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fyp.c16398141.healthlete.ui.fitness_entry;

import static maes.tech.intentanim.CustomIntent.customType;


public class fitness extends AppCompatActivity {

    static String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (user_id == null){
            user_id = getIntent().getExtras().getString("userId");
        }

        LocalDB ldb;
        ldb = new LocalDB(this);

        TableLayout table = findViewById(R.id.exercise_table);
        displayTable(table);

        MaterialCardView popup = findViewById(R.id.popup);
        ImageButton confirm = findViewById(R.id.confirm);
        EditText exercise_name = findViewById(R.id.edit_name);
        EditText muscle_group = findViewById(R.id.edit_muscle);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard(fitness.this);
                String exercise = exercise_name.getText().toString();
                String muscle = muscle_group.getText().toString();

                if (TextUtils.isEmpty(exercise)) {
                    exercise_name.setError("This field must be filled in");
                    return;
                }
                if (TextUtils.isEmpty(muscle)) {
                    muscle_group.setError("This field must be filled in");
                    return;
                }

                ldb.open();
                boolean result = ldb.addExercise(exercise, muscle, user_id);
                if (result == true) {
                    Toast.makeText(getApplicationContext(), "Successful insert", Toast.LENGTH_SHORT).show();
                    table.removeAllViews();
                    displayTable(table);
                } else {
                    Toast.makeText(getApplicationContext(), "Unsuccessful insert", Toast.LENGTH_SHORT).show();
                }
                popup.setVisibility(View.GONE);
            }
        });

        /*
        for each exercise set  onclick listener like minus from food log
        add.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(fitness.this, fitness_entry.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                customType(fitness.this,"left-to-right");
            }
        });
         */

        /*Log.i("Explanation",valueOf(TYPE_WORKOUT_EXERCISE.getClass()));
        Log.i("Explanation",valueOf(DataType.TYPE_WORKOUT_EXERCISE.getName()));
        Log.i("Explanation",valueOf(DataType.TYPE_WORKOUT_EXERCISE.describeContents()));
        Log.i("Explanation",valueOf(DataType.TYPE_WORKOUT_EXERCISE.getFields()));

        DataReadRequest reader = new Builder()
                .setTimeRange(10000, 20000, TimeUnit.MILLISECONDS)
                .read(TYPE_WORKOUT_EXERCISE)
                .build();

        Log.i("List",valueOf(createDeviceProtectedStorageContext().databaseList()));
        Log.i("List",valueOf(reader.getActivityDataSource()));
        Log.i("List",valueOf(reader.getDataSources()));
        Log.i("List",valueOf(reader.getDataTypes().(Field.FIELD_EXERCISE)));
        //List<DataType> = reader.getDataTypes();

        DataSource exerciseSource = new DataSource.Builder()
                .setDataType(DataType.TYPE_WORKOUT_EXERCISE).build();
        Log.i("Data type",valueOf(exerciseSource.getDataType()));*/


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void displayTable(TableLayout table) {
        TableRow heading = new TableRow(this);
        heading.setGravity(Gravity.CENTER);
        heading.setBackgroundColor(Color.BLUE);
        int buttonStyle = R.style.Widget_AppCompat_Button_Borderless;
        ImageButton add = new ImageButton(new ContextThemeWrapper(this, buttonStyle));
        add.setId(View.generateViewId());
        add.setImageResource(R.drawable.plus_circle);
        add.setBackgroundColor(Color.BLUE);
        heading.addView(add);
        TextView tv1 = new TextView(this);
        tv1.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        tv1.setText(" Exercise ");
        tv1.setTypeface(Typeface.DEFAULT_BOLD);
        tv1.setTextColor(Color.WHITE);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        lp.setMargins(0, 0, 0, 0);
        heading.addView(tv1, lp);
        TextView tv2 = new TextView(this);
        tv2.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        tv2.setText(" Muscle group ");
        tv2.setTypeface(Typeface.DEFAULT_BOLD);
        tv2.setTextColor(Color.WHITE);
        heading.addView(tv2, lp);
        table.addView(heading);

        LocalDB ldb = new LocalDB(this);
        ldb.open();
        Cursor entries = ldb.getAllExercises(user_id);
        int rows = entries.getCount();
        if (rows == 0) {
        } else {
            List<ImageButton> delete = new ArrayList<ImageButton>();
            List<Button> select = new ArrayList<Button>();
            entries.moveToFirst();
            do {
                TableRow tbrow = new TableRow(this);
                tbrow.setGravity(Gravity.CENTER);
                ImageButton minus = new ImageButton(this);
                minus.setImageResource(R.drawable.ic_fitness_center_black_24dp);
                minus.setClickable(false);
                minus.setBackgroundResource(R.drawable.backgroundstate);
                minus.setId(entries.getInt(0));
                tbrow.addView(minus);
                delete.add(minus);

                Button t2v = new Button(this);
                t2v.setClickable(true);
                t2v.setBackgroundColor(Color.WHITE);
                t2v.setText(entries.getString(1));
                t2v.setTextColor(Color.BLACK);
                t2v.setGravity(Gravity.CENTER);
                t2v.setId(entries.getInt(0));
                tbrow.addView(t2v, lp);
                select.add(t2v);

                Button t3v = new Button(this);
                t3v.setText(entries.getString(2));
                t3v.setBackgroundColor(Color.WHITE);
                t3v.setTextColor(Color.BLACK);
                t3v.setGravity(Gravity.CENTER);
                tbrow.addView(t3v, lp);

                tbrow.setBackgroundColor(Color.WHITE);
                table.addView(tbrow);
            } while (entries.moveToNext());

            for(final ImageButton minus : delete) {
                minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int id = minus.getId();
                        Log.i("TAG", "The id is" + id);
                        ldb.open();
                        boolean result = ldb.deleteExercise(id);
                        if (result) {
                            Toast.makeText(getApplicationContext(), "Successfully deleted", Toast.LENGTH_SHORT).show();
                            table.removeAllViews();
                            displayTable(table);
                        } else {
                            Toast.makeText(getApplicationContext(), "unsuccessful delete", Toast.LENGTH_SHORT).show();
                        }
                        ldb.close();
                    }
                });

                for(final Button exercise : select) {
                    exercise.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Integer exercise_id = exercise.getId();
                            Intent intent = new Intent(fitness.this, fitness_entry.class);
                            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("exercise",exercise_id);
                            startActivity(intent);
                            customType(fitness.this, "left-to-right");
                        }
                    });

                    exercise.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            int id = exercise.getId();

                            for (ImageButton minus : delete){
                                if (minus.getId()==id){
                                    minus.setImageResource(R.drawable.minus_circle);
                                    minus.setClickable(true);
                                }
                            }
                            return true;
                        }
                    });
                }
            }
        }

        add.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                MaterialCardView popup = findViewById(R.id.popup);
                popup.setVisibility(View.VISIBLE);
                popup.setBackground(ContextCompat.getDrawable(fitness.this, R.drawable.popup_background));
            }
        });

        ImageButton cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialCardView popup = findViewById(R.id.popup);
                popup.setVisibility(View.GONE);
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
        Intent data = new Intent(fitness.this,home.class);
        data.putExtra("userId", user_id);
        startActivity(data);
        customType(fitness.this,"fadein-to-fadeout");
        super.onBackPressed();
    }
}
