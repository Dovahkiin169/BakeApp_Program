package com.omens.bakeapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

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
            if (!flag && firstNumber != 0 && firstNumber != 1) {
                numberOfPrimes++;
                Log.e("Primes", firstNumber + " ");
            }
            ++firstNumber;
        }
        return numberOfPrimes;
    }

    public static boolean isEditTextEmpty(EditText editText) {
        if (TextUtils.isEmpty(editText.getText().toString())) {
            editText.setError("You need to enter number");
            return false;
        }
        return true;
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
