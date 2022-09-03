package com.android.androidTesting.utility;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class CalendarClass {
    Context context;
    TextView dateInput;

    public CalendarClass(Context context, TextView dateInput) {
        // on below line we are getting
        // the instance of our calendar.
        final Calendar c = Calendar.getInstance();

        // on below line we are getting
        // our default day, month and year.
        int year = c.get(Calendar.YEAR);
        final int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        this.context = context;
        this.dateInput = dateInput;

        createDatePickerDialog(year, month, day);
    }

    public CalendarClass(Context context, TextView dateInput, String dateString) {
        // If you want to set the date to be anything other than the default in the calendar view.
        final Calendar c = Calendar.getInstance();
        this.context = context;
        this.dateInput = dateInput;

        long date = Format.date(dateString);
        Date test = new Date(date);
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(test);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        Log.d("Eric", ""+year+" "+month+" "+day);

        createDatePickerDialog(year, month, day);
    }

    void createDatePickerDialog(int year, int month, int day) {
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
}
