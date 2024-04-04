package com.example.entrega.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.example.entrega.R;

public class ChangeInfoDialogFragment extends DialogFragment {

    private TextView infoLabel;
    private EditText editTextInfo;

    public static ChangeInfoDialogFragment newInstance(String currentValue) {
        ChangeInfoDialogFragment fragment = new ChangeInfoDialogFragment();
        Bundle args = new Bundle();
        args.putString("currentValue", currentValue);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.change_info_dialog, null);
        editTextInfo = dialogView.findViewById(R.id.edit_text_info);
        infoLabel = dialogView.findViewById(R.id.info_label);

        String label = getArguments().getString("label");
        String currentValue = getArguments().getString("currentValue");

        infoLabel.setText(label + ":");
        editTextInfo.setText(currentValue);

        builder.setView(dialogView)
                .setTitle("Change Information")
                .setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newInfo = editTextInfo.getText().toString();
                        ((ProfileActivity) getActivity()).onInfoChanged(newInfo);
                        dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });

        return builder.create();
    }
}
