package fyp.c16398141.healthlete;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import static java.lang.String.valueOf;
import static maes.tech.intentanim.CustomIntent.customType;

public class view_workout_areas extends AppCompatActivity {

    String username = "2013chrisclarke@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_workout_areas);
        Button map_button = findViewById(R.id.map);
        Button home_button = findViewById(R.id.home);

        map_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view_workout_areas.this, workout_area.class);
                startActivity(intent);
                customType(view_workout_areas.this,"up-to-bottom");
            }
        });

        home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(view_workout_areas.this, home.class);
                startActivity(intent);
                customType(view_workout_areas.this,"right-to-left");
            }
        });
        setupTable();
    }

    void setupTable(){
        LocalDB ldb;
        ldb = new LocalDB(this);

        TextView name = findViewById(R.id.name);
        TableLayout table = (TableLayout) findViewById(R.id.opening_times_table);
        ldb.open();

        Cursor area = ldb.getWorkoutArea();
        int rows = area.getCount();
        if (rows == 0) {
        } else {
            area.moveToFirst();
            int area_id = (area.getInt(0));
            name.setText(area.getString(2));

            if (area.getInt(5)==1){
                Cursor times = ldb.getWorkoutAvailability(area_id);
                rows = times.getCount();
                if (rows == 0) {
                    Log.i("Times", "NA");
                } else {
                    times.moveToFirst();
                    do {
                        TableRow row = new TableRow(this);
                        row.setGravity(Gravity.CENTER);

                        TextView tv = new TextView(this);
                        String day = times.getString(1);
                        char[] array = day.toCharArray();
                        array[0] = Character.toUpperCase(array[0]);
                        day = new String(array);
                        day = day + ": ";
                        tv.setText(day);
                        tv.setTextSize(30);
                        tv.setBackgroundColor(Color.WHITE);
                        if (rows % 2 == 1){
                            tv.setBackgroundColor(Color.LTGRAY);
                        }
                        tv.setTextColor(Color.BLACK);
                        tv.setGravity(Gravity.CENTER);
                        row.addView(tv);

                        String hours = new String("");
                        Integer opening = times.getInt(3);
                        if (opening == 0){
                            hours = "Closed";
                        }
                        else if (opening == 1){
                            hours = "Open 24 Hours";
                        }
                        else {
                            Integer closing = times.getInt(4);
                            hours = valueOf(opening) + " - " + valueOf(closing);
                        }
                        TextView slot = new TextView(this);
                        slot.setText(hours);
                        slot.setTextSize(30);
                        if (rows % 2 == 1){
                            slot.setBackgroundColor(Color.LTGRAY);
                        }
                        rows++;
                        slot.setTextColor(Color.BLACK);
                        slot.setGravity(Gravity.CENTER);
                        row.addView(slot);

                        table.addView(row);

                    } while (times.moveToNext());
                }
            }
            else {
                Log.i("Times","Not inserted");
            }
        }
        ldb.close();
    }
}


