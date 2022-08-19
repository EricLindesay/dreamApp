package com.android.androidTesting.utility;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

public class CalendarClass {

    public CalendarClass(Context context, TextView dateInput) {
        // on below line we are getting
        // the instance of our calendar.
        final Calendar c = Calendar.getInstance();

        // on below line we are getting
        // our day, month and year.
        int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // on below line we are creating a variable for date picker dialog.
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                // on below line we are passing context.
                context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // on below line we are setting date to our edit text.
                        monthOfYear++;
                        String monthOutput;
                        if (monthOfYear < 10) monthOutput = "0"+monthOfYear;
                        else monthOutput = ""+monthOfYear;

                        String dayOutput;
                        if (dayOfMonth < 10) dayOutput = "0"+dayOfMonth;
                        else dayOutput = ""+dayOfMonth;
                        dateInput.setText(year + "-" + monthOutput + "-" + dayOutput);
                    }
                },
                // on below line we are passing year,
                // month and day for selected date in our date picker.
                year, month, day);
        // at last we are calling show to
        // display our date picker dialog.
        datePickerDialog.show();
    }

    public CalendarClass(Context context, EditText yearInput, EditText monthInput, EditText dayInput) {
        // on below line we are getting
        // the instance of our calendar.
        final Calendar c = Calendar.getInstance();

        // on below line we are getting
        // our day, month and year.
        int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // on below line we are creating a variable for date picker dialog.
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                // on below line we are passing context.
                context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // on below line we are setting date to our edit text.
                        monthOfYear++;
                        String monthOutput;
                        if (monthOfYear < 10) monthOutput = "0"+monthOfYear;
                        else monthOutput = ""+monthOfYear;

                        String dayOutput;
                        if (dayOfMonth < 10) dayOutput = "0"+dayOfMonth;
                        else dayOutput = ""+dayOfMonth;
                        yearInput.setText(""+year);
                        monthInput.setText(""+monthOutput);
                        dayInput.setText(""+dayOutput);
                    }
                },
                // on below line we are passing year,
                // month and day for selected date in our date picker.
                year, month, day);
        // at last we are calling show to
        // display our date picker dialog.
        datePickerDialog.show();
    }
}
