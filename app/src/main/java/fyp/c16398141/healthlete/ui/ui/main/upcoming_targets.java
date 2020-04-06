package fyp.c16398141.healthlete.ui.ui.main;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DecimalFormat;

import fyp.c16398141.healthlete.LocalDB;
import fyp.c16398141.healthlete.R;
import fyp.c16398141.healthlete.ui.fitness_entry;

import static java.lang.String.valueOf;


public class upcoming_targets extends Fragment {

    String user_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_upcoming_targets, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Integer exercise_id = ((fitness_entry) getActivity()).getExercisePassed();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            user_id = firebaseUser.getEmail();
        }

        LocalDB ldb;
        ldb = new LocalDB(getContext());
        ldb.open();
        String exercisename = ldb.getExerciseName(exercise_id);
        ldb.close();

        TableLayout table = view.findViewById(R.id.exercise_table);
        setupTable(table);
        calculateTargets(exercisename, table);

    }

    public void setupTable(TableLayout table) {

        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        lp.setMargins(0, 0, 0, 0);

        TableRow.LayoutParams lp2 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
        lp.width = 120;

        TableRow heading = new TableRow(getContext());
        heading.setGravity(Gravity.CENTER);
        heading.setBackgroundColor(Color.BLUE);

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

        TextView tv4 = new TextView(getContext());
        tv4.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
        tv4.setText(" 1 Rep Max Estimate ");
        tv4.setTypeface(Typeface.DEFAULT_BOLD);
        tv4.setTextColor(Color.WHITE);
        tv4.setTextSize(10);
        heading.addView(tv4, lp);

        table.addView(heading);
        table.setShrinkAllColumns(true);
        table.setColumnShrinkable(1, false);
    }

    public void addRow(TableLayout table, Double weight, Integer reps, Double repmax)
    {
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT);
        lp.setMargins(0, 0, 0, 0);

        TableRow.LayoutParams lp2 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
        lp.width = 120;

            TableRow tbrow = new TableRow(getContext());
            tbrow.setGravity(Gravity.CENTER);

            TextView t2v = new Button(getContext());
            t2v.setBackgroundColor(Color.WHITE);
            t2v.setText(valueOf(weight));
            t2v.setTextColor(Color.BLACK);
            t2v.setGravity(Gravity.CENTER);
            tbrow.addView(t2v, lp);

            TextView t3v = new Button(getContext());
            t3v.setBackgroundColor(Color.WHITE);
            t3v.setText(valueOf(reps));
            t3v.setTextColor(Color.BLACK);
            t3v.setGravity(Gravity.CENTER);
            tbrow.addView(t3v, lp);

            TextView t5v = new Button(getContext());
            t5v.setBackgroundColor(Color.WHITE);
            t5v.setText(valueOf(repmax));
            t5v.setTextColor(Color.BLACK);
            t5v.setGravity(Gravity.CENTER);
            tbrow.addView(t5v, lp);

            tbrow.setBackgroundColor(Color.WHITE);
            table.addView(tbrow);

    }

    public void calculateTargets(String exercisename, TableLayout table) {

        LocalDB ldb;
        ldb = new LocalDB(getContext());
        ldb.open();
        Cursor entry = ldb.getExerciseEntries(exercisename, user_id);
        int rows = entry.getCount();
        if (rows == 0) {
        } else {
            entry.moveToFirst();
            ldb.close();
            Double last_repmax = entry.getDouble(5);
            Log.i("Value", valueOf(last_repmax));

            Double weight =1.0, result = 1.0;
            Integer reps = 1;
            Integer[] reprange = {12, 11, 10, 9, 8, 7, 6, 5, 4, 3};
            Double min_weight = 2.5, max_weight = 500.0, starting_weight = 2.5;

            //find a weight for each rep in rep range that is over the last 1rm estimate (navigate by increasing weight by 50 each time until greater then reset last one then 10, then 2.5 minimum)
            for (Integer rep : reprange) {
                for (weight = starting_weight; weight < max_weight; weight+=min_weight) {
                    result = CalculateRepMax(weight, rep);
                    if (result>last_repmax && (Math.abs(result - last_repmax)<(last_repmax*.1))){

                        addRow(table,weight,rep,result);

                        //as the rep range decreases, the amount of weight achievable can only go up
                        starting_weight = weight;
                        break;
                    }
                }
            }
        }
    }

    public double CalculateRepMax (Double weight, Integer reps){

        Double r = reps.doubleValue();
        Double epley, brzycki, repmax;
        repmax = epley = brzycki = 0.0;
        Double numerator, denominator;

        //use epley formula if rep range is greater than or equal to 8
        if (reps >= 8) {
            numerator = weight * r;
            denominator = 30.0;
            Double sum = numerator / denominator;
            repmax = epley = sum + weight;
        }
        //use brzychi formula if rep range is less than 10
        if (reps < 10) {
            numerator = 36 * weight;
            denominator = 37 - r;
            repmax = brzycki = numerator / denominator;
        }
        //if rep range falls within scope for both of them, find the mean result
        if (brzycki != 0.0 && epley != 0.0) {
            repmax = ((brzycki + epley) / 2);
        }
        //round result to two decimal places
        DecimalFormat df = new DecimalFormat("####0.00");
        repmax = Double.parseDouble(df.format(repmax));
        return repmax;
    }

}

