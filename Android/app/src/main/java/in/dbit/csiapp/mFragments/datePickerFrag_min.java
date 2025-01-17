package in.dbit.csiapp.mFragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import in.dbit.csiapp.R;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

public class datePickerFrag_min extends DialogFragment {

    DatePickerDialog.OnDateSetListener onDateSet;
    private boolean isModal = false;

    public static datePickerFrag_min newInstance()
    {
        datePickerFrag_min frag = new datePickerFrag_min();
        frag.isModal = true; // WHEN FRAGMENT IS CALLED AS A DIALOG SET FLAG
        return frag;
    }

    public datePickerFrag_min(){}

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog date = new DatePickerDialog(getActivity(), onDateSet, year, month, day);
        date.getDatePicker().setMinDate(System.currentTimeMillis());
        return date;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(isModal) // AVOID REQUEST FEATURE CRASH
        {
            return super.onCreateView(inflater, container, savedInstanceState);
        }
        else {
            View rootView = inflater.inflate(R.layout.activity_attendance, container, false);
            return rootView;
        }
    }

    public void setCallBack(DatePickerDialog.OnDateSetListener onDate) {
        onDateSet = onDate;
    }
}
