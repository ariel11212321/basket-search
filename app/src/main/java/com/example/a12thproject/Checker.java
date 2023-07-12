package com.example.a12thproject;

import java.util.regex.Pattern;

public class Checker {
    private String str;

    public Checker() {
        this("");
    }
    public Checker(String str) {
        this.str = str;
    }
    private boolean inRange(char c, char min, char max) {
        return c >= min && c <= max;
    }
    private boolean isDigit(char c) {
        return inRange(c, '0', '9');
    }
    private boolean isLetter(char c) {
        return inRange(c, 'a', 'z') || inRange(c, 'A', 'Z');
    }
    private boolean isLetterOrDigit(char c) {
        return isLetter(c) || isDigit(c);
    }
    private boolean isWhitespace(char c) {
        return c == ' ' || c == '\t';
    }

    public boolean validPhone() {
        return str.startsWith("05") && str.length() == 10;
    }
    public boolean validWebsite() {
        return str.startsWith("http://") || str.startsWith("https://");
    }

    public boolean validEmail() {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (str == null || str.equals(""))
            return false;
        return pat.matcher(str).matches();
    }
    public void setStr(String str) {
        this.str = str;
    }
    public String getStr() {
        return str;
    }

    public boolean validPassword() {

      return str.length() >= 8 && str.length() <= 20 && str.chars().anyMatch(Character::isLowerCase) && str.chars().anyMatch(Character::isDigit) && Character.isUpperCase(str.charAt(0));
    }
    public boolean validHeight() {
        return str.length() == 3 &&  Integer.parseInt(str) >= 100 && Integer.parseInt(str) <= 300;
    }
    public boolean checkPosition() {
        return str.length() == 1 && Integer.parseInt(str) >= 1 && Integer.parseInt(str) <= 5;
    }

}
