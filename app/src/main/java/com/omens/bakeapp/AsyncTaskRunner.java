package com.omens.bakeapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class AsyncTaskRunner extends AsyncTask<String, String, String> {

    private String resp;
    ProgressDialog progressDialog;
    String firstNumberString,secondNumberString;
    Context con;
    AsyncTaskRunner(String firstNumberString, String secondNumberString, Context context) {

        this.firstNumberString = firstNumberString;
        this.secondNumberString = secondNumberString;
        this.con = context;
    }

    @Override
    protected String doInBackground(String... params) {
        publishProgress("Sleeping..."); // Calls onProgressUpdate()

        long firstNumber = Long.parseLong(firstNumberString);
        long secondNumber = Long.parseLong(secondNumberString);
        Log.e("result","There is " + Prime.PrimeCounter(firstNumber, secondNumber) + " prime numbers between entered numbers");

        return resp;
    }


    @Override
    protected void onPostExecute(String result) {

    }


    @Override
    protected void onPreExecute() {

    }


    @Override
    protected void onProgressUpdate(String... text) {

    }
}
