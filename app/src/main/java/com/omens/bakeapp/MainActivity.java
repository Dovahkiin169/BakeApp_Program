package com.omens.bakeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity  implements DataInterface.View{
    private DataInterface.UserActionsListener actionListener;
    EditText editTextFirstNumber,editTextSecondNumber;
    Button buttonCountPrimes,buttonSendToDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseManager dbMgr = DatabaseManager.getSharedInstance();
        dbMgr.initCouchbaseLite(getApplicationContext());
        dbMgr.openOrCreateDatabase(getApplicationContext(),"nameOfDocument");

        editTextFirstNumber = findViewById(R.id.editTextFirstNumber);
        editTextSecondNumber = findViewById(R.id.editTextSecondNumber);
        buttonCountPrimes = findViewById(R.id.buttonCountPrimes);
        buttonSendToDB = findViewById(R.id.buttonSendToDB);

        actionListener = new DataSetterAndGetter(this);

        runOnUiThread(() -> actionListener.fetchDataFormDB());
    }

    public void countPrimes(View view) {
        if(isEditTextEmpty(editTextFirstNumber) && isEditTextEmpty(editTextSecondNumber)) {
            int firstNumber = Integer.parseInt(editTextFirstNumber.getText().toString());
            int secondNumber = Integer.parseInt(editTextSecondNumber.getText().toString());
            Toast toast = Toast.makeText(getApplicationContext(), "There is " + PrimeCounter(firstNumber, secondNumber) + " prime numbers between entered numbers", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    public void setData(Map<String, Object> profile) {
        editTextFirstNumber.setText((String)profile.get("first_number"));
        editTextSecondNumber.setText((String)profile.get("second_number"));
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

    public int PrimeCounter(int firstNumber,int secondNumber) {
        int numberOfPrimes =0;
        while (firstNumber < secondNumber) {
            boolean flag = false;

            for (int i = 2; i <= firstNumber / 2; ++i) {// condition for non prime number
                if (firstNumber % i == 0) {
                    flag = true;
                    break;
                }
            }

            if (!flag && firstNumber != 0 && firstNumber != 1) {
                numberOfPrimes++;
                Log.e("Primes", firstNumber + " ");
            }
            ++firstNumber;
        }
        return numberOfPrimes;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
            DatabaseManager.getSharedInstance().closeDatabase();
            finish();
    }
}