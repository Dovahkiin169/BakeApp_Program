package com.omens.bakeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText editTextFirstNumber,editTextSecondNumber;
    Button buttonCountPrimes,buttonSendToDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextFirstNumber = findViewById(R.id.editTextFirstNumber);
        editTextSecondNumber = findViewById(R.id.editTextSecondNumber);
        buttonCountPrimes = findViewById(R.id.buttonCountPrimes);
        buttonSendToDB = findViewById(R.id.buttonSendToDB);
    }

    public void countPrimes(View view) {
        if(isEditTextEmpty(editTextFirstNumber) && isEditTextEmpty(editTextSecondNumber)) {
            int firstNumber = Integer.parseInt(editTextFirstNumber.getText().toString());
            int secondNumber = Integer.parseInt(editTextSecondNumber.getText().toString());
            Toast toast = Toast.makeText(getApplicationContext(), "There is " + PrimeCounter(firstNumber, secondNumber) + " prime numbers between entered numbers", Toast.LENGTH_LONG);
            toast.show();
        }
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
}