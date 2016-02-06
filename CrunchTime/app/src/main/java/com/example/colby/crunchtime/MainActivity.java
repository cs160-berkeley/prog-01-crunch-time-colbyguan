package com.example.colby.crunchtime;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.audiofx.BassBoost;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends ActionBarActivity {

    EditText[] values;
    double[] denoms = {350, 200, 225, 25, 25, 10, 100, 12, 20, 12, 13, 15};
    double weightFactor;
    double resetCalories;

    SharedPreferences SP;
    Set<Integer> repsIndices = new HashSet<>(Arrays.asList(0, 1, 2, 6));
    EditText calories, totalCalories;
    Button addCaloriesButton, resetCaloriesButton;
    DecimalFormat df;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);

        SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        // Editables
        calories = (EditText) findViewById(R.id.calories);
        totalCalories = (EditText) findViewById(R.id.totalCalories);
        totalCalories.setText("2000");
        resetCalories = 2000;

        addCaloriesButton = (Button) findViewById(R.id.addCaloriesButton);
        resetCaloriesButton = (Button) findViewById(R.id.resetCaloriesButton);

        values = new EditText[] {
                (EditText) findViewById(R.id.editText),
                (EditText) findViewById(R.id.editText2),
                (EditText) findViewById(R.id.editText3),
                (EditText) findViewById(R.id.editText4),
                (EditText) findViewById(R.id.editText5),
                (EditText) findViewById(R.id.editText6),
                (EditText) findViewById(R.id.editText7),
                (EditText) findViewById(R.id.editText8),
                (EditText) findViewById(R.id.editText9),
                (EditText) findViewById(R.id.editText10),
                (EditText) findViewById(R.id.editText11),
                (EditText) findViewById(R.id.editText12)
        };

        // Bind change on user input
        for (int i = 0; i < values.length; i++) {
            final int finalI = i;
            values[i].setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    Log.d("caught event", "" + actionId);
                    if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT ||
                            event != null && event.getAction() == KeyEvent.ACTION_DOWN &&
                                    (event.getKeyCode() == KeyEvent.KEYCODE_ENTER || event.getKeyCode() == KeyEvent.KEYCODE_NUMPAD_ENTER)) {
                        MainActivity.this.updateValues(finalI);
                    }
                    return true;
                }
            });
            values[i].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        String strValue = values[finalI].getText().toString();
                        if (strValue.contains("reps") || strValue.contains("mins")) {
                            values[finalI].setText("");
                        }
                    }
                }
            });
            values[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String strValue = values[finalI].getText().toString();
                    if (strValue.contains("reps") || strValue.contains("mins")) {
                        values[finalI].setText("");
                    }
                }
            });
        }

        // User enters calories-to-exercise events
        calories.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT ||
                        event != null && event.getAction() == KeyEvent.ACTION_DOWN &&
                                (event.getKeyCode() == KeyEvent.KEYCODE_ENTER || event.getKeyCode() == KeyEvent.KEYCODE_NUMPAD_ENTER)) {
                    MainActivity.this.updateFromCalories();
                }
                return true;
            }
        });
        calories.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    String strValue = calories.getText().toString();
                    if (strValue.contains("reps") || strValue.contains("mins")) {
                        calories.setText("");
                    }
                }
            }
        });
        calories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strValue = calories.getText().toString();
                if (strValue.contains("reps") || strValue.contains("mins")) {
                    calories.setText("");
                }
            }
        });

        totalCalories.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT ||
                        event != null && event.getAction() == KeyEvent.ACTION_DOWN &&
                                (event.getKeyCode() == KeyEvent.KEYCODE_ENTER || event.getKeyCode() == KeyEvent.KEYCODE_NUMPAD_ENTER)) {
                    try {
                        MainActivity.this.resetCalories = Double.valueOf(MainActivity.this.totalCalories.getText().toString());
                    } catch (Exception e) {
                        return true;
                    }
                }
                return true;
            }
        });

        addCaloriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    double netValue = Double.valueOf(totalCalories.getText().toString());
                    double calorieValue = Double.valueOf(calories.getText().toString());
                    totalCalories.setText("" + df.format(netValue - calorieValue));
                    calories.setText("");
                } catch (Exception e) {
                    return;
                }
            }
        });

        resetCaloriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                totalCalories.setText("" + df.format(resetCalories));
            }
        });

    }

    // idx: index last changed
    private void updateValues(int idx) {
        String focusedText = this.values[idx].getText().toString();
        if (!focusedText.matches("")) {
            double focusedValue;
            try {
                focusedValue = Double.valueOf(focusedText);
            } catch (Exception e) {
                return;
            }
            this.pollWeight();
            double newCalories = weightFactor * focusedValue / this.denoms[idx] * 100;

            calories.setText("" + df.format(newCalories));

            for (int i = 0; i < values.length; i++) {
                double converted = focusedValue / this.denoms[idx] * this.denoms[i];

                if (repsIndices.contains(i)) {
                    values[i].setText(df.format(converted) + " reps");
                } else {
                    values[i].setText(df.format(converted) + " mins");
                }
            }
        }
    }

    private void updateFromCalories() {
        String caloriesText = calories.getText().toString();
        if (!caloriesText.matches("")) {
            double caloriesValue;
            try {
                caloriesValue = Double.valueOf(caloriesText);
            } catch (Exception e) {
                return;
            }
            this.pollWeight();
            double multiplier = caloriesValue / 100.0 / weightFactor;

            for (int i = 0; i < values.length; i++) {
                double converted = this.denoms[i] * multiplier;

                if (repsIndices.contains(i)) {
                    values[i].setText(df.format(converted) + " reps");
                } else {
                    values[i].setText(df.format(converted) + " mins");
                }
            }
        }

    }

    private void pollWeight() {
        try {
            double weight = Double.valueOf(SP.getString("weight", "150"));
            weightFactor = weight / 150 * 1.005;
        } catch (Exception e) {
            weightFactor = 1.005;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
