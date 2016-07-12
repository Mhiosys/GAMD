package app.gamd.dialogfragment;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

import app.gamd.R;

/**
 * Created by Sigcomt on 11/07/2016.
 */
public class CustomDatePickerFragmentDialog extends DialogFragment {

    private OnDateSelectedListener mHandler;

    public interface OnDateSelectedListener {

        public void onDateSelected(int year, int month, int day);

        public void onCancel();
    }

    private DatePicker mPicker;
    private int mYear = -1, mMonth = -1, mDay = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_datepicker,
                container, false);

        if (mYear == -1 && mMonth == -1 && mDay == -1) {
            Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
        }

        mPicker = (DatePicker) view.findViewById(R.id.date_picker);
        mPicker.updateDate(mYear, mMonth, mDay);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        TextView tvCancelar = (TextView) view.findViewById(R.id.tvCancelar);
        tvCancelar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (mHandler != null)
                    mHandler.onCancel();
                getDialog().dismiss();
            }
        });

        TextView tvAceptar = (TextView) view.findViewById(R.id.tvAceptar);
        tvAceptar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if (mHandler != null) {
                    mDay = mPicker.getDayOfMonth();
                    mMonth = mPicker.getMonth() + 1;
                    mYear = mPicker.getYear();

                    mHandler.onDateSelected(mYear, mMonth, mDay);
                }

                getDialog().dismiss();
            }
        });

        return view;
    }

    public void setHandler(OnDateSelectedListener object) {
        this.mHandler = (OnDateSelectedListener) object;
    }

    public void setDate(int year, int month, int day) {
        this.mYear = year;
        this.mMonth = month;
        this.mDay = day;
    }
}