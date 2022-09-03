package com.android.androidTesting.utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.IllegalFormatWidthException;
import java.util.List;

public class Format {
    public static String description(String description) {
        return description.trim();
    }

    public static long date(String date_to_format) {
        // Format the date from a string to a long
        // Convert it into a Date object and get how many millis that represents
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = format.parse(date_to_format);
        } catch (ParseException e) {
            e.printStackTrace();
            date = new Date();
        }
        return date.getTime();
    }

    public static String date(Long date_to_format) {
        // Convert a date from an long into the correct string representation
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(new Date(date_to_format));
    }

    public static String shortenString(String str, int charLimit) {
        // Go through each word in the string, if adding the new word increases the length over the
        // limit, then stop and add a "..." Otherwise just show the whole word.
        // If the first word exceeds the character limit, then print "Character limit exceeded".
        String ret = "";
        String[] words = str.split(" ");  // get each word.
        for (int i=0; i< words.length; i++) {
            String word = words[i];
            if (ret.length()+word.length() > charLimit) {
                int remainingWords = words.length-i;
                // overwrite any punctuation on the last character
                ret = removeTrailingPunctuation(ret.trim());

                // add the ... and how many words are left
                ret = ret + "... +"+remainingWords;
                break;
            } else {
                ret = ret + word + " ";
            }
        }
        ret = ret.trim();
        if (ret.isEmpty())
            return "Nothing to display";
        if (ret.startsWith("..."))  // The first word exceeds the total character limit.
            return words[0].substring(0, charLimit) + "... +"+words.length;

        return ret;
    }

    static String removeTrailingPunctuation(String tempStr) {
        // Remove any trailing punctuation from a string.
        StringBuilder str = new StringBuilder(tempStr);
        while (str.length() > 0) {
            int end_ind = str.length()-1;
            if (Character.isLetter(str.charAt(end_ind)) || Character.isDigit(str.charAt(end_ind))) {
                // if the last character is a letter or number, then there is no more trailing punctuation
                return str.toString();
            }
            str.deleteCharAt(end_ind);  // otherwise, delete the last character as it is punctuation
        }
        return str.toString();
    }
}
