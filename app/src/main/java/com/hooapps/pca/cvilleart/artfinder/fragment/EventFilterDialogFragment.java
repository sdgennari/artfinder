package com.hooapps.pca.cvilleart.artfinder.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.hooapps.pca.cvilleart.artfinder.R;
import com.hooapps.pca.cvilleart.artfinder.constants.C;

public class EventFilterDialogFragment extends DialogFragment {

    public interface EventFilterDialogListener {
        public void onFilterDialogPositiveClick(DialogFragment dialog,
                                                boolean[] checkedItems);
        public void onFilterDialogNegativeClick(DialogFragment dialog);
    }

    private EventFilterDialogListener mCallback;
    private boolean[] checkedItems;

    private EventFilterDialogListener getCallback() {
        if (mCallback == null) {
            Fragment targetFrag = this.getTargetFragment();
            try {
                mCallback = (EventFilterDialogListener) targetFrag;
            } catch (ClassCastException e) {
                throw new ClassCastException("TargetFragment must implement EventFilterDialogListener");
            }
        }
        return mCallback;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        checkedItems = this.getArguments().getBooleanArray(C.EXT_CHECKED_ITEMS);

        final String[] categoryArr = this.getResources().getStringArray(R.array.event_filter_categories);

        AlertDialog.Builder builder = new AlertDialog.Builder((this.getActivity()));
        builder.setTitle(R.string.filter_categories)
                .setMultiChoiceItems(categoryArr, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int pos, boolean isChecked) {
                        checkedItems[pos] = isChecked;
                    }
                })
                .setPositiveButton(R.string.apply, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getCallback().onFilterDialogPositiveClick(EventFilterDialogFragment.this,
                                checkedItems);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getCallback().onFilterDialogNegativeClick(EventFilterDialogFragment.this);
                    }
                });
        return builder.create();
    }
}
