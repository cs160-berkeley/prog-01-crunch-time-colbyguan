package com.example.colby.crunchtime;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    Set<Integer> repsIndices = new HashSet<>(Arrays.asList(0, 1, 2, 6));
    TextView calories;
    DecimalFormat df;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        df = new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.CEILING);

        calories = (TextView) findViewById(R.id.calories);
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

        for (int i = 0; i < values.length; i++) {
            final int finalI = i;
            values[i].setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
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
            double newCalories = focusedValue / this.denoms[idx] * 100;

            calories.setText("" + newCalories);

            for (int i = 0; i < values.length; i++) {
                double converted = focusedValue / this.denoms[idx] * this.denoms[i];

                if (repsIndices.contains(i)) {
                    values[i].setText(df.format(converted) + " reps");
                } else {
                    values[i].setText(df.format(converted) + " mins");
                }
                Log.d("updateValues", values[i].getText().toString());
            }
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
