package com.example.eLearningPlatform.utils;

public class SaltUtil {

    private static final String SALT = "bruh, who are you trying to crack ***hole!";
    public static String  getSaltedPassword(String password){
        return password + SALT;
    }

}
