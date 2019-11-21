package com.example.norman_lee.myapplication;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    //TODO 5.4 Write unit tests to check the ExchangeRate class

    //the no-arg constructor will give an exchange rate of 2.95 by default
    @Test
    public void exchangeRateNoArg() {
        String exchangeRate = "2.95000";
        assertEquals(exchangeRate, new ExchangeRate().getExchangeRate().toString());
    }

    @Test
    public void exchangeRateTwoArg() {
        String home = "1.0";
        String foreign = "3.0";
        String exchangeRate = "0.33333";
        assertEquals(exchangeRate, new ExchangeRate(home, foreign).getExchangeRate().toString());
    }

    //get exchangeRate shall return a big decimal object
    @Test
    public void exchangeRateTwoArgBigDecimal() {
        String home = "1.0";
        String foreign = "3.0";
        String exchangeRate = "0.33333";
        assertEquals(new BigDecimal(exchangeRate), new ExchangeRate(home, foreign).getExchangeRate());
    }


    //CalculateAmount() shall return a bigdeicmal object
    @Test
    public void calculateAmountPos() {
        String home = "1.0";
        String foreign = "3.0";
        assertEquals(new BigDecimal("33.33300"), new ExchangeRate(home, foreign).calculateAmount("100"));
    }
    @Test
    public void calculateAmountNeg() {
        String home = "1.0";
        String foreign = "3.0";
        assertEquals(new BigDecimal("-33.33300"), new ExchangeRate(home, foreign).calculateAmount("-100"));
    }

    //in writing test, think of ays to "break" your methods
    //for infinte input, divide them up   eg. positive and negative


    @Test (expected= NumberFormatException.class)
    public void exchangeRateOneArgThrowException () {
        new ExchangeRate("");
    }

    @Test (expected= NumberFormatException.class)
    public void exchangeRateTwoArgThrowException () {
        new ExchangeRate("42","4555jjh");
    }
8



}