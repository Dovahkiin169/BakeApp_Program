package com.omens.bakeapp;

import androidx.core.content.ContextCompat;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends BaseActivity implements DataInterface.View {
    private DataInterface.UserActionsListener actionListener;
    EditText editTextFirstNumber, editTextSecondNumber;
    Button buttonCountPrimes, buttonSendToDB, buttonCancelCounting;
    ProgressBar progressBarLoading;
    TextView textViewResult,textViewResText;

    ExecutorService executor;
    Operations operations = new Operations();

    int whenShowCancelButton=60000; // if less then 30k button shown only for less then 1 sec
                                    // 60k couple seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseManager dbMgr = DatabaseManager.getSharedInstance();
        dbMgr.initCouchbaseLite(getApplicationContext());
        dbMgr.openOrCreateDatabase(getApplicationContext(), "nameOfDocument");

        editTextFirstNumber = findViewById(R.id.editTextFirstNumber);
        editTextSecondNumber = findViewById(R.id.editTextSecondNumber);
        buttonCountPrimes = findViewById(R.id.buttonCountPrimes);
        buttonSendToDB = findViewById(R.id.buttonSendToDB);
        buttonCancelCounting = findViewById(R.id.buttonCancelCounting);
        progressBarLoading = findViewById(R.id.progressBarLoading);
        textViewResult = findViewById(R.id.textViewResult);
        textViewResText = findViewById(R.id.textViewResText);

        editTextFirstNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textViewResult.setText("");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        editTextSecondNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                textViewResult.setText("");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        actionListener = new DataSetterAndGetter(this);
        runOnUiThread(() -> actionListener.fetchDataFormDB());
    }


    public void countPrimes(View view) {
        textViewResult.setBackgroundColor(Color.TRANSPARENT);
        textViewResult.setText("");
        if (Operations.isDataIncorrectEditText(editTextFirstNumber,getApplicationContext()) || Operations.isDataIncorrectEditText(editTextSecondNumber,getApplicationContext())) {
            return;
        }
        if(Long.parseLong(editTextFirstNumber.getText().toString())>=Long.parseLong(editTextSecondNumber.getText().toString())) {
            Toast toast = Toast.makeText(getApplicationContext(), "Sorry, second number must be greater then first", Toast.LENGTH_SHORT);
            toast.show();
        }
        else if(Long.parseLong(editTextSecondNumber.getText().toString())-Long.parseLong(editTextFirstNumber.getText().toString())>whenShowCancelButton) {
                buttonCancelCounting.setVisibility(View.VISIBLE);
                AlertDialog.Builder  builder;
                builder = new AlertDialog.Builder(MainActivity.this);
                builder.setCancelable(true);
                builder.setTitle("Are you sure you want to continue?");
                builder.setMessage("This operation may take some time if there is large difference between the entered numbers");
                builder.setCancelable(false);
                builder.setNegativeButton("NO", (dialogInterface, i) -> {});
                builder.setPositiveButton("Yes", (dialog, id) -> threadOperation());
                builder.show();
            }
        else
        threadOperation();
    }

    @Override
    public void setData(Map<String, Object> profile) {
        editTextFirstNumber.setText((String) profile.get("first_number"));
        editTextSecondNumber.setText((String) profile.get("second_number"));
    }

    public void saveToDB(View view) {
        Map<String, Object> data = new HashMap<>();
        data.put("document", DatabaseManager.getSharedInstance().currentDoc);
        data.put("first_number", editTextFirstNumber.getText().toString());
        data.put("second_number", editTextSecondNumber.getText().toString());
        actionListener.saveData(data);

        Toast.makeText(this, "Data successfully updated", Toast.LENGTH_SHORT).show();
    }
    public void CancelCounting(View view) {
        operations.BreakPrimeCounter=true;
        buttonCancelCounting.setVisibility(View.INVISIBLE);
        Operations.showProgress(false, getApplicationContext(), this, progressBarLoading);
        buttonCountPrimes.setClickable(true);
        buttonSendToDB.setClickable(true);
    }


boolean ifCounting = false;
    public void threadOperation() {
        executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            ifCounting = true;
            operations.BreakPrimeCounter=false;
            Operations.showProgress(true, getApplicationContext(), MainActivity.this, progressBarLoading);
            buttonCountPrimes.setClickable(false);
            buttonSendToDB.setClickable(false);
            buttonCancelCounting.setClickable(true);
            long res = operations.PrimeCounter(Long.parseLong(editTextFirstNumber.getText().toString()), Long.parseLong(editTextSecondNumber.getText().toString()));
            runOnUiThread(() -> {
                if(!operations.BreakPrimeCounter) {
                    textViewResult.setText(String.valueOf(res));
                    buttonCancelCounting.setVisibility(View.GONE);
                }
                else {
                    textViewResult.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.button_cancel_shape));
                    textViewResult.setText(R.string.canceled);
                }
            });
            Operations.showProgress(false, getApplicationContext(), MainActivity.this, progressBarLoading);
            buttonCountPrimes.setClickable(true);
            buttonSendToDB.setClickable(true);
            buttonCancelCounting.setClickable(false);
            ifCounting = false;
        });
        executor.shutdown();
    }


    /** Theme Changer**/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        if (sharedPreferences.getTheme(getApplicationContext())<= 1)
            menu.getItem(0).setIcon(R.drawable.moon);
        else
            menu.getItem(0).setIcon(R.drawable.sun);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.change_theme_button && !GetStatus() && !ifCounting) {
            item.setIcon(R.drawable.moon);
            saveData(1);
            recreateActivity();
        }
        else if (item.getItemId() == R.id.change_theme_button && GetStatus() && !ifCounting) {
            item.setIcon(R.drawable.sun);
            saveData(2);
            recreateActivity();
        }
        else if(ifCounting) {
            Toast toast = Toast.makeText(getApplicationContext(), "Sorry, you need to cancel your counting to change theme", Toast.LENGTH_SHORT);
            toast.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        saveData(sharedPreferences.getTheme(getApplicationContext()));
    }

    public void recreateActivity() {
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    public void saveData(int theme) {
        sharedPreferences.setTheme(getApplicationContext(), theme);
    }
    /*************************************************************/


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        operations.BreakPrimeCounter=true;
        DatabaseManager.getSharedInstance().closeDatabase();
        finish();
    }
}
