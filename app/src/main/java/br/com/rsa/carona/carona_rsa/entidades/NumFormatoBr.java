package br.com.rsa.carona.carona_rsa.entidades;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.lang.ref.WeakReference;

/**
 * Created by Artur Antunes on 09/12/2016.
 */
public class NumFormatoBr implements TextWatcher {
    final int MAX_LENGTH = 11;


    private boolean mFormatting;
    private boolean clearFlag;
    private int mLastStartLocation;
    private String mLastBeforeText;
    private WeakReference<EditText> mWeakEditText;

    public NumFormatoBr(WeakReference<EditText> weakEditText) {
        this.mWeakEditText = weakEditText;
    }


    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (after == 0 && s.toString().equals("(")) {
            clearFlag = true;
        }
        mLastStartLocation = start;
        mLastBeforeText = s.toString();
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // TODO: Do nothing
    }

    public void afterTextChanged(Editable s) {
        // Make sure to ignore calls to afterTextChanged
        // caused by the work done below
        if (!mFormatting) {
            mFormatting = true;
            int curPos = mLastStartLocation;
            String beforeValue = mLastBeforeText;
            String currentValue = s.toString();
            String formattedValue = formatUsNumber(s);
            if (currentValue.length() > beforeValue.length()) {
                int setCusorPos = formattedValue.length()
                        - (beforeValue.length() - curPos);
                mWeakEditText.get().setSelection(setCusorPos < 0 ? 0 : setCusorPos);
            } else {
                int setCusorPos = formattedValue.length()
                        - (currentValue.length() - curPos);
                if(setCusorPos > 0 && !Character.isDigit(formattedValue.charAt(setCusorPos -1))){
                    setCusorPos--;
                }
                mWeakEditText.get().setSelection(setCusorPos < 0 ? 0 : setCusorPos);
            }
            mFormatting = false;
        }
    }

    private String formatUsNumber(Editable text) {
        StringBuilder formattedString = new StringBuilder();

        int p = 0;
        while (p < text.length()) {
            char ch = text.charAt(p);
            if (!Character.isDigit(ch)) {
                text.delete(p, p + 1);
            } else {
                p++;
            }
        }


        String allDigitString = text.toString();

        int totalDigitCount = allDigitString.length();

        if(totalDigitCount > MAX_LENGTH) {
            allDigitString = allDigitString.substring(0, MAX_LENGTH);
            totalDigitCount--;
        }


        boolean isLonger = totalDigitCount == MAX_LENGTH;
        int dashAfter = isLonger ? 5 : 4;

        if (totalDigitCount == 0
                || (totalDigitCount > 11 && !allDigitString.startsWith("("))
                || totalDigitCount > 12) {

            text.clear();
            text.append(allDigitString);
            return allDigitString;
        }

        int alreadyPlacedDigitCount = 0;
        if (allDigitString.equals("(") && clearFlag) {
            text.clear();
            clearFlag = false;
            return "";
        }


        if (totalDigitCount - alreadyPlacedDigitCount > 2) {
            formattedString.append("("
                    + allDigitString.substring(alreadyPlacedDigitCount,
                    alreadyPlacedDigitCount + 2) + ") ");
            alreadyPlacedDigitCount += 2;
        }


        if (totalDigitCount - alreadyPlacedDigitCount > dashAfter) {
            formattedString.append(allDigitString.substring(
                    alreadyPlacedDigitCount, alreadyPlacedDigitCount + dashAfter)
                    + "-");
            alreadyPlacedDigitCount += dashAfter;
        }


        if (totalDigitCount > alreadyPlacedDigitCount) {
            formattedString.append(allDigitString
                    .substring(alreadyPlacedDigitCount));
        }

        text.clear();
        text.append(formattedString.toString());
        return formattedString.toString();
    }
}
