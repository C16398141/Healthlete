package fyp.c16398141.healthlete;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import fyp.c16398141.healthlete.ui.fitness_entry;

import static java.lang.String.valueOf;
import static maes.tech.intentanim.CustomIntent.customType;

public class achievements extends AppCompatActivity {

    String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            user = firebaseUser.getEmail();
        } else {
            Log.i("Nope",":(");
            // No user is signed in
        }

        LocalDB ldb;
        ldb = new LocalDB(this);

        TableLayout table = findViewById(R.id.achievement_table);
        Switch switcher = findViewById(R.id.switcher);
        initialiseSwitch(switcher, table);
        displayTable(table, switcher);


        ChipGroup chipGroup = findViewById(R.id.chip_group);
        Chip daychip = findViewById(R.id.daychip);

        final String[] timeframe_type = {daychip.getText().toString()};
        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, int i) {
                Chip chip = chipGroup.findViewById(i);
                if(chip != null){
                    timeframe_type[0] = chip.getText().toString();
                    Log.i("TAG", timeframe_type[0]);
                }
            }
        });

        SeekBar seekBar= findViewById(R.id.seekbar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                //Toast.makeText(getApplicationContext(), "seekbar progress: " + progress, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(getApplicationContext(),"seekbar touch started!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(getApplicationContext(),valueOf(seekBar.getProgress()), Toast.LENGTH_SHORT).show();
            }
        });

        MaterialCardView popup = findViewById(R.id.popup);
        ImageButton confirm = findViewById(R.id.confirm);
        ImageButton cancel = findViewById(R.id.cancel);
        EditText achievement_name = findViewById(R.id.edit_achievement);
        EditText details = findViewById(R.id.edit_details);
        EditText timeframe = findViewById(R.id.edit_timeframe);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideKeyboard(achievements.this);
                String name = achievement_name.getText().toString();
                String description = details.getText().toString();
                String time = timeframe.getText().toString();

                if (TextUtils.isEmpty(name)) {
                    achievement_name.setError("This field must be filled in");
                    return;
                }

                if (TextUtils.isEmpty(description)) {
                    details.setError("This field must be filled in");
                    return;
                }

                if (TextUtils.isEmpty(time)) {
                    timeframe.setError("This field must be filled in");
                    return;
                }

                if (!TextUtils.isDigitsOnly(time)) {
                    timeframe.setError("The quantity must be a whole number");
                    return;
                }

                String expectedTimeframe = time + " " + timeframe_type[0];
                String dateAdded = getCurrentDate();
                Integer difficulty = seekBar.getProgress();

                ldb.open();
                boolean result = ldb.addAchievement(name, description, expectedTimeframe, difficulty, dateAdded, user);
                if (result == true) {
                    Toast.makeText(getApplicationContext(), "Successful insert", Toast.LENGTH_SHORT).show();
                    table.removeAllViews();
                    switcher.setChecked(false);
                    displayTable(table,switcher);
                } else {
                    Toast.makeText(getApplicationContext(), "Unsuccessful insert", Toast.LENGTH_SHORT).show();
                }
                popup.setVisibility(View.GONE);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void displayTable(TableLayout table, Switch switcher) {
        TextView first = findViewById(R.id.instructions);
        first.setVisibility(View.GONE);
        TextView second = findViewById(R.id.delete_instructions);
        second.setVisibility(View.GONE);
        Boolean completed = false;
        if (switcher.isChecked()){
            completed = true;
        }
        TableRow heading = new TableRow(this);
        heading.setGravity(Gravity.CENTER);
        heading.setBackgroundColor(Color.BLUE);
        int buttonStyle = R.style.Widget_AppCompat_Button_Borderless;
        ImageButton add = new ImageButton(new ContextThemeWrapper(this, buttonStyle));
        add.setId(View.generateViewId());
        add.setImageResource(R.drawable.plus_circle);
        add.setBackgroundColor(Color.BLUE);
        add.setMaxWidth(10);
        heading.addView(add);

        TextView tv1 = new TextView(this);
        tv1.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        tv1.setText(" Title ");
        tv1.setTypeface(Typeface.DEFAULT_BOLD);
        tv1.setTextColor(Color.WHITE);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        tv1.setMaxWidth(5);
        heading.addView(tv1);

        TextView tv3 = new TextView(this);
        tv3.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        tv3.setText(" Set Time Frame ");
        tv3.setTextSize(10);
        tv3.setTypeface(Typeface.DEFAULT_BOLD);
        tv3.setTextColor(Color.WHITE);
        heading.addView(tv3);

        TextView tv4 = new TextView(this);
        tv4.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        tv4.setText(" Difficulty ");
        tv4.setTypeface(Typeface.DEFAULT_BOLD);
        tv4.setTextColor(Color.WHITE);
        heading.addView(tv4);

        TextView tv5 = new TextView(this);
        tv5.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        if (completed){
            tv5.setText("Complete");
        }else{
            tv5.setText(" Added ");
        }
        tv5.setTypeface(Typeface.DEFAULT_BOLD);
        tv4.setTextSize(10);
        tv5.setTextColor(Color.WHITE);
        heading.addView(tv5);

        table.addView(heading);

        LocalDB ldb = new LocalDB(this);
        ldb.open();
        Cursor entries;
        if (completed){
            entries = ldb.getCompletedAchievements(user);
        }else {
            entries = ldb.getUncompletedAchievements(user);
        }
        int rows = entries.getCount();
        if (rows == 0) {
            if (!completed){
                first.setVisibility(View.VISIBLE);
            }
        } else {
            if (rows == 1) {
                if (!completed) {
                    first.setVisibility(View.VISIBLE);
                    second.setVisibility(View.VISIBLE);
                    first.setText("Tap on your achievement title to see more details below");
                }
            }
            List<ImageButton> update = new ArrayList<ImageButton>();
            List<Button> select = new ArrayList<Button>();
            HashMap<Integer, Button> hashievement = new HashMap<Integer, Button>();
            entries.moveToFirst();
            do {
                TableRow tbrow = new TableRow(this);
                Integer id = entries.getInt(0);
                tbrow.setId(id);
                tbrow.setGravity(Gravity.CENTER);
                ImageButton tick = new ImageButton(this);
                if (completed){
                    tick.setImageResource(R.drawable.ic_school_gold_24dp);
                    tick.setTag("achieved");
                    update.add(tick);
                }else{
                    tick.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
                    tick.setTag("update");
                    update.add(tick);
                }
                tick.setClickable(false);
                tick.setBackgroundResource(R.drawable.backgroundstate);
                tick.setId(entries.getInt(0));
                tbrow.addView(tick);

                Button t2v = new Button(this);
                t2v.setClickable(true);
                t2v.setBackgroundColor(Color.WHITE);
                t2v.setText(entries.getString(1));
                t2v.setTextColor(Color.BLACK);
                t2v.setMaxWidth(5);
                t2v.setId(entries.getInt(0));
                tbrow.addView(t2v);

                hashievement.put(id,t2v);
                select.add(t2v);

                TextView t4v = new TextView(this);
                t4v.setText(entries.getString(3));
                t4v.setBackgroundColor(Color.WHITE);
                t4v.setTextColor(Color.BLACK);
                t4v.setGravity(Gravity.CENTER);
                tbrow.addView(t4v, lp);

                TextView t5v = new TextView(this);
                t5v.setText(valueOf(entries.getInt(4)));
                t5v.setBackgroundColor(Color.WHITE);
                t5v.setTextColor(Color.BLACK);
                t5v.setGravity(Gravity.CENTER);
                tbrow.addView(t5v);

                TextView t6v = new TextView(this);
                if (completed){
                    t6v.setText(entries.getString(6));
                }else{
                    t6v.setText(entries.getString(5));
                }
                t6v.setBackgroundColor(Color.WHITE);
                t6v.setTextColor(Color.BLACK);
                t6v.setGravity(Gravity.CENTER);
                t6v.setTextSize(10);
                tbrow.addView(t6v);

                tbrow.setBackgroundColor(Color.WHITE);
                table.addView(tbrow);
            } while (entries.moveToNext());

            //using a hash table to identify the button to make clickable and the id to identify the button's row/achievement
            hashievement.forEach((integer, button) -> {
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ldb.open();
                        Cursor result = ldb.getAchievement(integer);
                        result.moveToFirst();
                        Toast.makeText(getApplicationContext(),result.getString(2),Toast.LENGTH_LONG).show();
                    }
                });
                button.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        for (ImageButton minus : update){
                            if (minus.getId()==integer){
                                minus.setImageResource(R.drawable.minus_circle);
                                minus.setTag("delete");
                                minus.setClickable(true);
                            }
                        }
                        return true;
                    }
                });
            });


            for(final ImageButton check : update) {
                int id = check.getId();
                check.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(check.getTag().toString()=="delete"){
                            boolean result = ldb.deleteAchievement(id);
                            if (result) {
                                Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                switcher.callOnClick();
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to delete row", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            String date = getCurrentDate();
                            ldb.open();
                            boolean result = ldb.completeAchievement(id, date);
                            if (result) {
                                Toast.makeText(getApplicationContext(), "Congratulations on your Achievement!", Toast.LENGTH_SHORT).show();
                                switcher.callOnClick();
                            } else {
                                Toast.makeText(getApplicationContext(), "Failed to recognise Achievement", Toast.LENGTH_SHORT).show();
                            }
                        }
                        ldb.close();
                    }
                });
            }
        }

        add.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                MaterialCardView popup = findViewById(R.id.popup);
                popup.setVisibility(View.VISIBLE);
                popup.setBackground(ContextCompat.getDrawable(achievements.this, R.drawable.popup_background));
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

    public void initialiseSwitch(Switch switcher, TableLayout table){

        switcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Switch","Clicked");
                Boolean switched = switcher.isChecked();
                if (switched){
                    switcher.setText(" Completed ");
                } else{
                    switcher.setText(" To Do ");
                }
                table.removeAllViews();
                displayTable(table, switcher);
            }
        });
    }

    public String getCurrentDate(){
        Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR); // current year
        final int mMonth = c.get(Calendar.MONTH); // current month
        final int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        String dateAdded = "" + mDay + "-" + (mMonth+1) + "-" + mYear;
        return  dateAdded;
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
        Intent data = new Intent(achievements.this,home.class);
        data.putExtra("userId", user);
        startActivity(data);
        customType(achievements.this,"fadein-to-fadeout");
        super.onBackPressed();
    }
}

