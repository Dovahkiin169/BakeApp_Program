package com.omens.bakeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity  implements DataInterface.View {
    private DataInterface.UserActionsListener actionListener;
    EditText editTextFirstNumber, editTextSecondNumber;
    Button buttonCountPrimes, buttonSendToDB, buttonCancelCounting;
    ProgressBar progressBarLoading;
    TextView textViewResult;

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
        textViewResult = findViewById(R.id.editTextResult);

        actionListener = new DataSetterAndGetter(this);
        runOnUiThread(() -> actionListener.fetchDataFormDB());
        Operations.showProgress(false, getApplicationContext(), this, progressBarLoading);

    }

    ExecutorService executor;
    Operations operations = new Operations();
    public void countPrimes(View view) {
        if (Operations.isDataIncorrectEditText(editTextFirstNumber,getApplicationContext()) || Operations.isDataIncorrectEditText(editTextSecondNumber,getApplicationContext())) {
            return;
        }

        if(Long.parseLong(editTextFirstNumber.getText().toString())>=Long.parseLong(editTextSecondNumber.getText().toString())) {
            Toast toast = Toast.makeText(getApplicationContext(), "Sorry, second number must be greater then first", Toast.LENGTH_SHORT);
            toast.show();
        }
        else{
                executor = Executors.newSingleThreadExecutor();
                executor.submit(() -> {
                    Operations.showProgress(true, getApplicationContext(), this, progressBarLoading);
                    buttonCancelCounting.setVisibility(View.VISIBLE);
                    buttonCountPrimes.setClickable(false);
                    buttonSendToDB.setClickable(false);
                    long res = operations.PrimeCounter(Long.parseLong(editTextFirstNumber.getText().toString()), Long.parseLong(editTextSecondNumber.getText().toString()));
                    runOnUiThread(() -> { textViewResult.setText(String.valueOf(res)); });
                    buttonCancelCounting.setVisibility(View.GONE);
                    Operations.showProgress(false, getApplicationContext(), this, progressBarLoading);
                    buttonCountPrimes.setClickable(true);
                    buttonSendToDB.setClickable(true);
                });
                executor.shutdown();
        }
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
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        operations.BreakPrimeCounter=true;
        DatabaseManager.getSharedInstance().closeDatabase();
        finish();
    }
}
