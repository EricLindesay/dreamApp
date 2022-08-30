package com.android.androidTesting.utility;

import android.content.DialogInterface;

import java.util.concurrent.Callable;

public class CreateDialogBox {

    public static DialogInterface.OnClickListener create(final Callable<Void> positiveResult, final Callable<Void> negativeResult) {
        // create a dialog click listener, containing the positive and negative results
        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        try {
                            positiveResult.call();
                        } catch (Exception e) {
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        try {
                            negativeResult.call();
                        } catch (Exception e) {
                        }
                        break;
                }
            }
        };
        return dialogClickListener;
    }
}
