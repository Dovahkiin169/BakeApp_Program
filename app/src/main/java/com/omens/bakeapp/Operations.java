package com.omens.bakeapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import java.math.BigInteger;

public class Operations {
    boolean BreakPrimeCounter = false;

    public  long PrimeCounter(long firstNumber, long secondNumber) {
        int numberOfPrimes = 0;
        while (firstNumber < secondNumber) {
            boolean flag = false;

            for (long i = 2; i <= firstNumber / 2; ++i) {// condition for non prime number
                if (firstNumber % i == 0) {
                    flag = true;
                    break;
                }
            }
            if (BreakPrimeCounter) break;
            if (!flag && firstNumber != 0 && firstNumber != 1)
                numberOfPrimes++;
            ++firstNumber;
        }
        return numberOfPrimes;
    }

    public static boolean isDataIncorrectEditText(EditText editText, Context con) {
        if (TextUtils.isEmpty(editText.getText().toString())) {
            editText.setError("You need to enter number");
            return true;
        }
        BigInteger bigInt = new BigInteger(editText.getText().toString(), 10);
        BigInteger LongMax  = new BigInteger(String.valueOf(Long.MAX_VALUE), 10);
        if(bigInt.compareTo(LongMax) > 0) {
            editText.setError(con.getString(R.string.MaxQ) + con.getString(R.string.DecMax));
            return true;
        }
        return false;
    }

    public static void showProgress(final boolean show, final Context con, final Activity act, View ProgressView) {
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
