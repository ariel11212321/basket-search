package com.example.a12thproject.widgets;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;

public class MultiSelectionDialog extends AlertDialog.Builder implements DialogInterface.OnMultiChoiceClickListener{
    // create a multi selection spinner dialog
    // fields
    private String title;
    private String[] items;
    private boolean[] selection;
    private String positiveButtonText;
    private String negativeButtonText;
    private ArrayAdapter adapter;
    private DialogInterface.OnMultiChoiceClickListener listener;

    public MultiSelectionDialog(Context context) {
        super(context);
        this.items = new String[0];
        this.selection = new boolean[0];
        this.positiveButtonText = "OK";
        this.negativeButtonText = "Cancel";
        this.adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1);
    }
    // methods
    public MultiSelectionDialog setTitle(String title) {
        this.title = title;
        return this;
    }
    public MultiSelectionDialog setItems(String[] items) {
        this.items = items;
        this.selection = new boolean[items.length];
        return this;
    }
    public MultiSelectionDialog setSelection(boolean[] selection) {
        this.selection = selection;
        return this;
    }
    public MultiSelectionDialog setPositiveButton(String text, DialogInterface.OnClickListener listener) {
        this.positiveButtonText = text;
        super.setPositiveButton(text, listener);
        return this;
    }
    public MultiSelectionDialog setNegativeButton(String text, DialogInterface.OnClickListener listener) {
        this.negativeButtonText = text;
        super.setNegativeButton(text, listener);
        return this;
    }
    public MultiSelectionDialog setOnMultiChoiceClickListener(DialogInterface.OnMultiChoiceClickListener listener) {
        this.listener = listener;
        return this;
    }
    public AlertDialog create() {
        super.setTitle(title);
        super.setMultiChoiceItems(items, selection, this);
        super.setPositiveButton(positiveButtonText, null);
        super.setNegativeButton(negativeButtonText, null);
        return super.create();
    }
    public AlertDialog show() {
        AlertDialog dialog = create();
        dialog.show();
        return dialog;
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if (selection != null && which < selection.length) {
            selection[which] = isChecked;
        } else {
            throw new IllegalArgumentException(
                    "Argument 'which' is out of bounds.");
        }
    }

}
