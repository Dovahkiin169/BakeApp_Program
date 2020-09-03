package com.omens.bakeapp;

import android.util.Log;

public class Prime {
    public static long PrimeCounter(long firstNumber, long secondNumber) {
        int numberOfPrimes = 0;
        while (firstNumber < secondNumber) {
            boolean flag = false;

            for (long i = 2; i <= firstNumber / 2; ++i) {// condition for non prime number
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
