package fyp.c16398141.healthlete.ui.ui.main;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.util.Calendar;

import fyp.c16398141.healthlete.LocalDB;
import fyp.c16398141.healthlete.R;
import fyp.c16398141.healthlete.ui.fitness_entry;

import static java.lang.String.valueOf;


public class workout_entry extends Fragment {

    String date;
    String user_id;
    DatePickerDialog datePickerDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_workout_entry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Integer exercise_id = ((fitness_entry) getActivity()).getExercisePassed();

        user_id = "2013chrisclarke@gmail.com";

        Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR); // current year
        final int mMonth = c.get(Calendar.MONTH); // current month
        final int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        EditText datefield = getActivity().findViewById(R.id.date_field);
        datefield.setText(mDay + "/" + (mMonth+1) + "/" + mYear);
        date = "" + mDay + "-" + (mMonth+1) + "-" + mYear;

        Log.i("Date", valueOf(date));
        TableLayout table = view.findViewById(R.id.workout_table);
        displayTable(table, date);

        datefield.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // date picker dialog
                datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                datefield.setText(dayOfMonth + "/" + (monthOfYear+1) + "/" + year);
                                date = "" + dayOfMonth + (monthOfYear+1) + year;
                                Log.i("TAG",valueOf(date));
                                table.removeAllViews();
                                displayTable(table, date);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

    }

    public void displayTable(TableLayout table, String date) {
        table.removeAllViews();
        table.setShrinkAllColumns(true);
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        lp.setMargins(0, 0, 0, 0);

        TableRow.LayoutParams lp2 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
        lp.width = 120;

        TableRow heading = new TableRow(getContext());
        heading.setGravity(Gravity.CENTER);
        heading.setBackgroundColor(Color.BLUE);

        TextView tv = new TextView(getContext());
        tv.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        tv.setText(" Exercise ");
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setTextColor(Color.WHITE);
        heading.addView(tv, lp);

        TextView tv1 = new TextView(getContext());
        tv1.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        tv1.setText(" Weight ");
        tv1.setTypeface(Typeface.DEFAULT_BOLD);
        tv1.setTextColor(Color.WHITE);
        heading.addView(tv1, lp);

        TextView tv2 = new TextView(getContext());
        tv2.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        tv2.setText(" Reps ");
        tv2.setTypeface(Typeface.DEFAULT_BOLD);
        tv2.setTextColor(Color.WHITE);
        heading.addView(tv2, lp);

        TextView tv3 = new TextView(getContext());
        tv3.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        tv3.setText(" Sets ");
        tv3.setTypeface(Typeface.DEFAULT_BOLD);
        tv3.setTextColor(Color.WHITE);
        heading.addView(tv3, lp);

        TextView tv4 = new TextView(getContext());
        tv4.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        tv4.setText(" 1 Rep Max Estimate ");
        tv4.setTypeface(Typeface.DEFAULT_BOLD);
        tv4.setTextColor(Color.WHITE);
        tv4.setTextSize(10);
        heading.addView(tv4, lp);

        TextView tv5 = new TextView(getContext());
        tv5.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        tv5.setText(" Date ");
        tv5.setTypeface(Typeface.DEFAULT_BOLD);
        tv5.setTextColor(Color.WHITE);
        heading.addView(tv5, lp);

        table.addView(heading);

        LocalDB ldb = new LocalDB(getContext());
        ldb.open();

        Cursor entries = ldb.getWorkoutEntry(date, user_id);
        int rows = entries.getCount();
        if (rows == 0) {
        } else {
            entries.moveToFirst();
            do {
                TableRow tbrow = new TableRow(getContext());
                tbrow.setGravity(Gravity.CENTER);

                TextView t1v = new Button(getContext());
                t1v.setBackgroundColor(Color.WHITE);
                t1v.setText(entries.getString(1));
                t1v.setTextColor(Color.BLACK);
                t1v.setGravity(Gravity.CENTER);
                tbrow.addView(t1v, lp);

                TextView t2v = new Button(getContext());
                t2v.setBackgroundColor(Color.WHITE);
                t2v.setText(valueOf(entries.getDouble(2)));
                t2v.setTextColor(Color.BLACK);
                t2v.setGravity(Gravity.CENTER);
                tbrow.addView(t2v, lp);

                TextView t3v = new Button(getContext());
                t3v.setBackgroundColor(Color.WHITE);
                t3v.setText(valueOf(entries.getInt(3)));
                t3v.setTextColor(Color.BLACK);
                t3v.setGravity(Gravity.CENTER);
                tbrow.addView(t3v, lp);

                TextView t4v = new Button(getContext());
                t4v.setBackgroundColor(Color.WHITE);
                t4v.setText(valueOf(entries.getInt(4)));
                t4v.setTextColor(Color.BLACK);
                t4v.setGravity(Gravity.CENTER);
                tbrow.addView(t4v, lp);

                TextView t5v = new Button(getContext());
                t5v.setBackgroundColor(Color.WHITE);
                t5v.setText(valueOf(entries.getDouble(5)));
                t5v.setTextColor(Color.BLACK);
                t5v.setGravity(Gravity.CENTER);
                t5v.setId(entries.getInt(0));
                tbrow.addView(t5v, lp);

                TextView t6v = new Button(getContext());
                t6v.setBackgroundColor(Color.WHITE);
                t6v.setText(valueOf(entries.getString(6)));
                t6v.setInputType(InputType.TYPE_CLASS_DATETIME);
                t6v.setTextColor(Color.BLACK);
                t6v.setTextSize(10);
                t6v.setGravity(Gravity.CENTER);
                t6v.setId(entries.getInt(0));
                tbrow.addView(t6v, lp);

                tbrow.setBackgroundColor(Color.WHITE);
                table.addView(tbrow);
            } while (entries.moveToNext());

        }

    }
}

