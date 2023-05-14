package com.manech.manechapp.helper;

public class StringValidation {

    public String emptyIfNull(String string) {
        return (string == null) ? "" : string;
    }

    public boolean isValidPassword(final String password) {

           /* Pattern p;
            Matcher m;
            final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{4,}$";
            p = Pattern.compile(PASSWORD_PATTERN);
            m = p.matcher(password);
            boolean B = m.find();
            if(B && password.length()>=8)*/
        if (password.length() >= 8) {
            return true;
        } else {
            return false;
        }
    }
}

