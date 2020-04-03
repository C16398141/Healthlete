package fyp.c16398141.healthlete.ui.ui.main;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import fyp.c16398141.healthlete.LocalDB;
import fyp.c16398141.healthlete.R;
import fyp.c16398141.healthlete.fitness;
import fyp.c16398141.healthlete.ui.fitness_entry;

import static java.lang.String.valueOf;
import static maes.tech.intentanim.CustomIntent.customType;


public class upcoming_targets extends Fragment {

    String date;
    DatePickerDialog datePickerDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_upcoming_targets, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);

        Integer exercise_id = ((fitness_entry) getActivity()).getExercisePassed();

        LocalDB ldb;
        ldb = new LocalDB(getContext());
        ldb.open();
        String exercisename = ldb.getExerciseName(exercise_id);
        ldb.close();

        toolbar.setTitle(valueOf(exercisename));
        Log.i("ExerciseNAME", valueOf(exercisename));
        TableLayout table = view.findViewById(R.id.exercise_table);
        displayTable(table, exercisename);

        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        MaterialCardView popup = view.findViewById(R.id.popup);
        ImageButton confirm = view.findViewById(R.id.confirm);

        EditText e_weight = view.findViewById(R.id.edit_weight);
        EditText e_reps = view.findViewById(R.id.edit_reps);
        EditText e_sets = view.findViewById(R.id.edit_sets);

        Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR); // current year
        final int mMonth = c.get(Calendar.MONTH); // current month
        final int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        EditText datefield = getActivity().findViewById(R.id.datefield);
        datefield.setText(mDay + "/" + (mMonth+1) + "/" + mYear);
        date = "" + mDay + "-" + (mMonth+1) + "-" + mYear;

        datefield.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Date","Selected-click");
                // date picker dialog
                datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                datefield.setText(dayOfMonth + "/" + (monthOfYear+1) + "/" + year);
                                date = "" + dayOfMonth + "-" + (monthOfYear+1) + "-" + year;
                                Log.i("TAG",valueOf(date));
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                // hideKeyboard(exercise_entry.this);

                if (TextUtils.isEmpty(e_weight.getText())) {
                    e_weight.setError("This field must be filled in");
                    return;
                }

                if (TextUtils.isEmpty(e_reps.getText())) {
                    e_reps.setError("This field must be filled in");
                    return;
                }

                if (TextUtils.isEmpty(e_sets.getText())) {
                    e_sets.setError("This field must be filled in");
                    return;
                }

                Double weight;
                try {
                    weight = Double.parseDouble(e_weight.getText().toString());
                } catch (NumberFormatException nfe) {
                    e_weight.setError("This field is numeric");
                    return;
                }

                if (!TextUtils.isDigitsOnly(e_reps.getText())) {
                    e_reps.setError("This field requires a whole number");
                    return;
                }

                if (!TextUtils.isDigitsOnly(e_sets.getText())) {
                    e_sets.setError("This field requires a whole number");
                    return;
                }

                Integer reps = Integer.parseInt(e_reps.getText().toString());
                Integer sets = Integer.parseInt(e_sets.getText().toString());
                Double repmax = 5.5;
                String user_id = "2013chrisclarke@gmail.com";

                ldb.open();
                boolean result = ldb.addWorkoutEntry(exercisename, weight, reps, sets, repmax, date, user_id);
                ldb.close();
                if (result == true) {
                    Toast.makeText(getContext(), "Successful insert", Toast.LENGTH_SHORT).show();
                    table.removeAllViews();
                    displayTable(table, exercisename);
                } else {
                    Toast.makeText(getContext(), "Unsuccessful insert", Toast.LENGTH_SHORT).show();
                }
                popup.setVisibility(View.GONE);
            }
        });
    }

    public void displayTable(TableLayout table, String exercisename) {

        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        lp.setMargins(0, 0, 0, 0);

        TableRow.LayoutParams lp2 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
        lp.width = 120;

        TableRow heading = new TableRow(getContext());
        heading.setGravity(Gravity.CENTER);
        heading.setBackgroundColor(Color.BLUE);

        ImageButton add = new ImageButton(getContext());
        add.setId(View.generateViewId());
        add.setImageResource(R.drawable.plus_circle);
        add.setBackgroundColor(Color.BLUE);
        //add.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
        heading.addView(add, lp2);

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
        table.setShrinkAllColumns(true);
        table.setColumnShrinkable(1, false);

        LocalDB ldb = new LocalDB(getContext());
        ldb.open();

        Cursor entries = ldb.getExerciseEntries(exercisename);
        int rows = entries.getCount();
        if (rows == 0) {
        } else {
            List<ImageButton> delete = new ArrayList<ImageButton>();
            entries.moveToFirst();
            Log.i("DBreceievd",entries.getString(1));
            do {
                TableRow tbrow = new TableRow(getContext());
                tbrow.setGravity(Gravity.CENTER);

                ImageButton minus = new ImageButton(getContext());
                minus.setImageResource(R.drawable.minus_circle);
                minus.setBackgroundResource(R.drawable.backgroundstate);
                minus.setId(entries.getInt(0));
                tbrow.addView(minus, lp2);
                delete.add(minus);

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

            for (final ImageButton minus : delete) {
                minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int id = minus.getId();
                        ldb.open();
                        boolean result = ldb.deleteWorkoutEntry(id);
                        if (result) {
                            Toast.makeText(getContext(), "Successfully deleted", Toast.LENGTH_SHORT).show();
                            table.removeAllViews();
                            displayTable(table, exercisename);
                        } else {
                            Toast.makeText(getContext(), "Unsuccessful delete", Toast.LENGTH_SHORT).show();
                        }
                        ldb.close();
                    }
                });

            }
        }

        add.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                MaterialCardView popup = getActivity().findViewById(R.id.popup);
                popup.setVisibility(View.VISIBLE);
                popup.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.popup_background));
            }
        });
    }

}

