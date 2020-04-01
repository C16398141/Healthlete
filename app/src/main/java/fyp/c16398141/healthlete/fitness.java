package fyp.c16398141.healthlete;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.request.DataReadRequest.Builder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.gms.fitness.data.*;
import com.google.android.gms.fitness.request.*;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import fyp.c16398141.healthlete.ui.fitness_entry;

import static com.google.android.gms.fitness.data.DataType.TYPE_WORKOUT_EXERCISE;
import static java.lang.String.valueOf;
import static maes.tech.intentanim.CustomIntent.customType;

public class fitness extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitness);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        displayTable();
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

    public void displayTable(){
        TableLayout table = findViewById(R.id.exercise_table);
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
        lp.setMargins(0,0,0,0);
        heading.addView(tv1,lp);
        TextView tv2 = new TextView(this);
        tv2.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        tv2.setText(" Muscle group ");
        tv2.setTypeface(Typeface.DEFAULT_BOLD);
        tv2.setTextColor(Color.WHITE);
        heading.addView(tv2,lp);
        table.addView(heading);

        add.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(fitness.this, fitness_entry.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                customType(fitness.this,"left-to-right");
            }
        });
    }

}
