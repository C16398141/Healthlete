package fyp.c16398141.healthlete;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static java.lang.String.valueOf;
import static maes.tech.intentanim.CustomIntent.customType;

public class view_workout_areas extends AppCompatActivity {

    String username, area_name;
    ArrayList<String>day_list = new ArrayList<>();
    ArrayList<Integer>closing_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_workout_areas);
        Button map_button = findViewById(R.id.map);
        Button home_button = findViewById(R.id.home);

        username = getIntent().getExtras().getString("userId");

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
                customType(view_workout_areas.this,"bottom-to-up");
            }
        });
        setupTable();
        buildNotification(area_name, false);

        //check every day
        String day = getCurrentDay();
        Integer t = 2500;
        for (String d : day_list){

            if (d.contains(day)){
                Log.i("Day",d);
                Integer position = day_list.indexOf(d);
                t = closing_list.get(position);
                Log.i("Closing time",valueOf(t));
            }
        }

        //check every minute
        Integer time = getCurrentTime();
        Integer difference = t - time;
        Log.i("Difference", valueOf(difference));

        //if there's a two hour gap between closing time and current time (military format) then make the notification
        if (difference == 200){
            Log.i("Now","now");
            buildNotification(area_name, true);
        }else{
            Log.i("Wrong time", "No notification");
        }
    }

    void setupTable() {
        LocalDB ldb;
        ldb = new LocalDB(this);

        TextView name = findViewById(R.id.name);
        TableLayout table = (TableLayout) findViewById(R.id.opening_times_table);
        ldb.open();
        Cursor area = ldb.getWorkoutArea(username);
        int rows = area.getCount();
        if (rows == 0) {
        } else {
            area.moveToFirst();
            int area_id = (area.getInt(0));
            area_name = (area.getString(2));
            name.setText(area_name);

            if (area.getInt(5) == 1) {
                ldb.open();
                Cursor times = ldb.getWorkoutAvailability(area_id);
                rows = times.getCount();
                if (rows == 0) {
                    Toast.makeText(getApplicationContext(), "Times not available for this area", Toast.LENGTH_LONG).show();
                } else {
                    times.moveToFirst();
                    ArrayList<CheckBox> check = new ArrayList<>();
                    do {
                        TableRow row = new TableRow(this);
                        row.setGravity(Gravity.CENTER);

                        CheckBox cb = new CheckBox(this);
                        cb.setId(times.getInt(0));
                        cb.setClickable(true);
                        if (times.getInt(5) == 1) {
                            cb.setChecked(true);
                        } else {
                            cb.setChecked(false);
                        }
                        check.add(cb);
                        row.addView(cb);

                        TextView tv = new TextView(this);
                        String day = times.getString(1);
                        char[] array = day.toCharArray();
                        array[0] = Character.toUpperCase(array[0]);
                        day = new String(array);
                        if (times.getInt(5) == 1) {
                            day_list.add(day);
                        }
                        day = day + ": ";
                        tv.setText(day);
                        tv.setTextSize(30);
                        tv.setBackgroundColor(Color.WHITE);
                        if (rows % 2 == 1) {
                            tv.setBackgroundColor(Color.LTGRAY);
                        }
                        tv.setTextColor(Color.BLACK);
                        tv.setGravity(Gravity.CENTER);
                        row.addView(tv);

                        String hours = new String("");
                        Integer opening = times.getInt(3);
                        if (opening == 0) {
                            hours = "Closed";
                        } else if (opening == 1) {
                            hours = "Open 24 Hours";
                        } else {
                            Integer closing = times.getInt(4);
                            if (times.getInt(5) == 1) {
                                closing_list.add(closing);
                            }
                            hours = valueOf(opening) + " - " + valueOf(closing);
                        }
                        TextView slot = new TextView(this);
                        slot.setText(hours);
                        slot.setTextSize(30);
                        if (rows % 2 == 1) {
                            slot.setBackgroundColor(Color.LTGRAY);
                        }
                        rows++;
                        slot.setTextColor(Color.BLACK);
                        slot.setGravity(Gravity.CENTER);
                        row.addView(slot);

                        table.addView(row);

                    } while (times.moveToNext());

                    for (CheckBox cb : check) {
                        cb.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Integer reminder = 0;
                                int id = cb.getId();
                                if (cb.isChecked()) {
                                    reminder = 1;
                                }
                                ldb.open();
                                boolean updated = ldb.updateWorkoutAvailability(id, reminder);
                                if (!updated){
                                    Toast.makeText(getApplicationContext(), "Failed to update", Toast.LENGTH_SHORT).show();
                                    ldb.close();
                                    return;
                                }
                                ldb.close();
                                Toast.makeText(getApplicationContext(), "Successfully updated", Toast.LENGTH_SHORT).show();
                                table.removeAllViews();
                                check.clear();
                                day_list.clear();
                                closing_list.clear();
                                setupTable();
                            }
                        });
                        ldb.close();
                    }
                }
            }else {
                Toast.makeText(getApplicationContext(), "Times not available for this area", Toast.LENGTH_LONG).show();
            }
        }
    }

    void buildNotification(String area_name, Boolean now){

        createNotificationChannel();

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.mipmap.healthlete_icon)
                .setContentTitle("Workout Reminder")
                .setContentText(area_name + " closing in 2 hours")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if(now){
            Log.i("Notification","Incoming");
            //ensuring that the generated id is unique by using the current phone awake time
            Integer notificationid = (int) SystemClock.uptimeMillis();
            notificationManager.notify(notificationid, builder.build());
        }

        return;
    }

    private void createNotificationChannel(){
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Healthlete Channel";//getString(R.string.channel_name);
            String description = "Reminder about workout";//getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    String getCurrentDay(){
        Calendar calendar = Calendar.getInstance();
        int current_day = calendar.get(Calendar.DAY_OF_WEEK);
        String day;
        switch (current_day) {
            case Calendar.MONDAY:
                day = "Monday";
                break;
            case Calendar.TUESDAY:
                day = "Tuesday";
                break;
            case Calendar.WEDNESDAY:
                day = "Wednesday";
            break;
            case Calendar.THURSDAY:
                day = "Thursday";
            break;
            case Calendar.FRIDAY:
                day = "Friday";
            break;
            case Calendar.SATURDAY:
                day = "Saturday";
            break;
            default:
                day = "Sunday";
            break;
        }
        return day;
    }

    Integer getCurrentTime(){
        String time;
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        if (minute<10){
            time = valueOf(hour) + "0" + valueOf(minute);
        } else{
            time = valueOf(hour) + valueOf(minute);
        }
        Integer parsed_time = Integer.parseInt(time);
        return parsed_time;
    }
}




