package com.omens.bakeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity  implements DataInterface.View {
    private DataInterface.UserActionsListener actionListener;
    EditText editTextFirstNumber, editTextSecondNumber;
    Button buttonCountPrimes, buttonSendToDB;
    ProgressBar progressBarLoading;

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
        progressBarLoading = findViewById(R.id.progressBarLoading);

        actionListener = new DataSetterAndGetter(this);

        runOnUiThread(() -> actionListener.fetchDataFormDB());
    }

    ExecutorService executor;
    Prime p = new Prime();
    public void countPrimes(View view) {
        if (isEditTextEmpty(editTextFirstNumber) && isEditTextEmpty(editTextSecondNumber)) {
            executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                showProgress(true,getApplicationContext(),this,progressBarLoading);
                buttonCountPrimes.setClickable(false);
                buttonSendToDB.setClickable(false);
                long firstNumber = Long.parseLong(editTextFirstNumber.getText().toString());
                long secondNumber = Long.parseLong(editTextSecondNumber.getText().toString());
                Log.e("result","There is " + p.PrimeCounter(firstNumber, secondNumber) + " prime numbers between entered numbers");
                showProgress(false,getApplicationContext(),this,progressBarLoading);
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

        Toast.makeText(this, "Dat successfully updated", Toast.LENGTH_SHORT).show();
    }


    public boolean isEditTextEmpty(EditText editText) {
        if (TextUtils.isEmpty(editText.getText().toString())) {
            editText.setError("You need to enter number");
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        p.Break=true;
        DatabaseManager.getSharedInstance().closeDatabase();
        finish();
    }


    public void showProgress(final boolean show, final Context con, final Activity act, View ProgressView) {
        act.runOnUiThread(() -> {
            int shortAnimTime = con.getResources().getInteger(android.R.integer.config_shortAnimTime);
            ProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            ProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    ProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        });
    }
}
