package app.gamd.dialogfragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import app.gamd.R;

/**
 * Created by Sigcomt on 15/07/2016.
 */
public class FinalizarCitaDialogFragment extends DialogFragment {

    private OnRegistrarListener mCallback;
    private int solicitudId;
    private EditText txtObservacion;
    private Button btnAceptar, btnCancelar;

    public interface OnRegistrarListener {
        void onRegistrar(int solicitudId, String observacion);
    }

    public FinalizarCitaDialogFragment() {
    }

    public FinalizarCitaDialogFragment(OnRegistrarListener handler) {
        this.mCallback = handler;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.alert_dialog_finish, container, false);

        solicitudId = getArguments().getInt("solicitudId");

        txtObservacion = (EditText) view.findViewById(R.id.txtObservacion);
        btnAceptar = (Button) view.findViewById(R.id.btnAceptarFinalizar);
        btnCancelar = (Button) view.findViewById(R.id.btnCancelarFinalizar);

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtObservacion.getText().toString().length() == 0) {
                    Toast.makeText(getActivity(), "Debe ingresar sus observaciones", Toast.LENGTH_SHORT).show();
                }else{
                    mCallback.onRegistrar(solicitudId, txtObservacion.getText().toString());
                    dismiss();
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return view;
    }
}
