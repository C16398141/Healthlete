package fyp.c16398141.healthlete;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static java.lang.String.valueOf;
import static java.lang.System.currentTimeMillis;
import static maes.tech.intentanim.CustomIntent.customType;

public class reminders_setup extends AppCompatActivity {

    String updated, area_name;
    ArrayList<String> time_list = new ArrayList<>();
    ArrayList<String> day_list = new ArrayList<>();
    ArrayList<Integer> closing_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders_setup);
        Button map_button = findViewById(R.id.map);
        Button home_button = findViewById(R.id.home);

        String username = "null";
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            username = firebaseUser.getEmail();
        }else{
            Intent logged_out = new Intent(reminders_setup.this, MainActivity.class);
            startActivity(logged_out);
        }

        try{
            updated = getIntent().getExtras().getString("updated");
            if (updated.contentEquals("true")){
                cancelNotificationTimers();
            }
        }catch (NullPointerException e){

        }


        map_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(reminders_setup.this, workout_area.class);
                startActivity(intent);
                customType(reminders_setup.this, "up-to-bottom");
                finish();
            }
        });

        home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(reminders_setup.this, home.class);
                startActivity(intent);
                customType(reminders_setup.this, "bottom-to-up");
                finish();
            }
        });
        setupTable();
        initialiseReminders();
    }

    void setupTable() {
        LocalDB ldb;
        ldb = new LocalDB(this);

        TextView name = findViewById(R.id.name);
        TableLayout table = (TableLayout) findViewById(R.id.opening_times_table);
        ldb.open();
        Cursor area = ldb.getWorkoutArea(FirebaseAuth.getInstance().getCurrentUser().getEmail());
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
                            time_list.add(day);
                        }

                        String hours = new String("");
                        Integer opening = times.getInt(3);
                        if (opening == 0) {
                            hours = "Closed";
                        } else if (opening == 1) {
                            hours = "Open 24 Hours";
                        } else {
                            Integer closing = times.getInt(4);
                            if (times.getInt(5) == 1) {
                                day_list.add(day);
                                closing_list.add(closing);
                            }
                            hours = valueOf(opening) + " - " + valueOf(closing);
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
                                if (!updated) {
                                    Toast.makeText(getApplicationContext(), "Failed to update", Toast.LENGTH_SHORT).show();
                                    ldb.close();
                                    return;
                                }
                                ldb.close();
                                Toast.makeText(getApplicationContext(), "Successfully updated", Toast.LENGTH_SHORT).show();
                                cancelNotificationTimers();
                                table.removeAllViews();
                                check.clear();
                                time_list.clear();
                                day_list.clear();
                                closing_list.clear();
                                setupTable();
                                initialiseReminders();
                            }
                        });
                        ldb.close();
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), "Times not available for this area", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initialiseReminders(){

        Integer t, id = 0;
        for (String d : day_list) {
            Log.i("Day", d);
            Integer position = day_list.indexOf(d);
            t = closing_list.get(position);
            Log.i("Closing time", valueOf(t));
            Long future_time = null;
            try {
                future_time = formatTime(d, t);
            } catch (ParseException e) {
                e.printStackTrace();
                return;
            }
            Long difference = future_time - currentTimeMillis();
            Long two_hour_warning = 7200000L;
            Long countdown = difference - two_hour_warning;

            id++;
            setNotificationTimer(buildNotification(area_name), countdown, id);
        }
    }

    Notification buildNotification(String area_name) {

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

        return builder.build();
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Healthlete Channel";
            String description = "Reminder about workout";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    String getCurrentDay() {
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

    private long formatTime(String day, Integer time) throws ParseException {
        Calendar c = Calendar.getInstance();
        LocalDate nextdate = LocalDate.of(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
        if (!day.contentEquals(getCurrentDay())) {
            switch (day) {
                case "Monday":
                    nextdate = nextdate.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
                    break;
                case "Tuesday":
                    nextdate = nextdate.with(TemporalAdjusters.next(DayOfWeek.TUESDAY));
                    break;
                case "Wednesday":
                    nextdate = nextdate.with(TemporalAdjusters.next(DayOfWeek.WEDNESDAY));
                    break;
                case "Thursday":
                    nextdate = nextdate.with(TemporalAdjusters.next(DayOfWeek.THURSDAY));
                    break;
                case "Friday":
                    nextdate = nextdate.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
                    break;
                case "Saturday":
                    nextdate = nextdate.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
                    break;
                case "Sunday":
                    nextdate = nextdate.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
                    break;
            }
        }

        Double h = (Math.floor(time / 100));
        Integer hours = h.intValue();
        String hour = hours.toString();
        if (hours < 10) {
            hour = "0" + hour;
        }

        Integer minutes = time % 100;
        String mins;
        if (minutes < 10) {
            mins = minutes + "0";
        } else {
            mins = minutes.toString();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedString = nextdate.format(formatter) + " " + hours + ":" + mins + ":00";

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = dateFormat.parse(formattedString);
        Log.i("Date", valueOf(date));
        long milliseconds = date.getTime();

        return milliseconds;
    }

    private void setNotificationTimer(Notification notification, long timer, Integer id) {

        Intent intent = new Intent(this, post_notification.class);
        intent.putExtra("notification", notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long reminderTime = SystemClock.elapsedRealtime() + timer;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, reminderTime, pendingIntent);
    }


    public void cancelNotificationTimers(){
        for (int id=1; id<8; id++) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent myIntent = new Intent(getApplicationContext(), post_notification.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), id, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pendingIntent);
        }

    }

}


