package org.AFM.rssbridge.uitl;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class Validator {
    public boolean isValidPassword(String password){
        String regExpn = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*+=])(?=\\S+$).{8,20}$";

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(password);

        return matcher.matches();
    }
    public boolean isValidEmail(String email){
        String regexPattern = "^(.+)@(\\S+)$";

        Pattern pattern = Pattern.compile(regexPattern);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }
    public boolean isValidIIN(String IIN) {
        if (IIN == null || IIN.length() != 12 || !IIN.chars().allMatch(Character::isDigit)) {
            return false;
        }

        int[] digits = IIN.chars().map(Character::getNumericValue).toArray();

        int s = 0;
        for (int i = 0; i < 11; i++) {
            s += (i + 1) * digits[i];
        }
        int k = s % 11;

        if (k == 10) {
            s = 0;
            for (int i = 0; i < 11; i++) {
                int t = (i + 3) % 11;
                if (t == 0) {
                    t = 11;
                }
                s += t * digits[i];
            }
            k = s % 11;
            if (k == 10) {
                return false;
            }
        }

        return k == digits[11];
    }
}
