package com.example.norman_lee.myapplication;

public class Utils {
    public static void main(String[] args) {
        try{
        }catch (NumberFormatException ex){
            ex.printStackTrace();
        }catch (IllegalArgumentException ex){
            ex.printStackTrace();
        }
        checkValidString("1");
    }

    static boolean checkValidString(String in) throws NumberFormatException, IllegalArgumentException {
        Double d = Double.valueOf(in);
        if (d < 0) {
            throw new IllegalArgumentException("Negative value is not allowed");
        }
        if (d == 0) {
            throw new IllegalArgumentException("Zero is not allowed");
        }
        System.out.println(d);
        return true;
    }
}
