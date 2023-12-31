package com.example.a12thproject.widgets;



import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import androidx.appcompat.widget.AppCompatSpinner;


import com.example.a12thproject.R;

import java.util.ArrayList;
import java.util.Arrays;

public class MultiSelectionSpinner extends AppCompatSpinner implements
        DialogInterface.OnMultiChoiceClickListener {

    ArrayList<Item> items = null;
    boolean[] selection = null;
    ArrayAdapter adapter;

    public MultiSelectionSpinner(Context context) {
        super(context);

        adapter = new ArrayAdapter(context,
                android.R.layout.simple_list_item_1);
        super.setAdapter(adapter);
    }

    public MultiSelectionSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);

        adapter = new ArrayAdapter(context,
                android.R.layout.simple_list_item_1);
        super.setAdapter(adapter);
    }

    @Override
    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
        if (selection != null && which < selection.length) {
            selection[which] = isChecked;

            adapter.clear();
            adapter.add(buildSelectedItemString());
        } else {
            throw new IllegalArgumentException(
                    "Argument 'which' is out of bounds.");
        }
    }

    @Override
    public boolean performClick() {
        // create builder that supports a editText near the checkbox
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMultiChoiceItems(
                items.toArray(new CharSequence[items.size()]), selection, this);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
        return true;
    }

    @Override
    public void setAdapter(SpinnerAdapter adapter) {
        throw new RuntimeException(
                "setAdapter is not supported by MultiSelectSpinner.");
    }

    public void setItems(ArrayList<Item> items) {
        this.items = items;
        selection = new boolean[this.items.size()];
        adapter.clear();
        adapter.add("");
        Arrays.fill(selection, false);
    }

    public void setSelection(ArrayList<Item> selection) {
        for (int i = 0; i < this.selection.length; i++) {
            this.selection[i] = false;
        }

        for (Item sel : selection) {
            for (int j = 0; j < items.size(); ++j) {
                if (items.get(j).getValue().equals(sel.getValue())) {
                    this.selection[j] = true;
                }
            }
        }

        adapter.clear();
        adapter.add(buildSelectedItemString());
    }

    private String buildSelectedItemString() {
        StringBuilder sb = new StringBuilder();
        boolean foundOne = false;

        for (int i = 0; i < items.size(); ++i) {
            if (selection[i]) {
                if (foundOne) {
                    sb.append(", ");
                }

                foundOne = true;

                sb.append(items.get(i).getName());
            }
        }

        return sb.toString();
    }

    public ArrayList<Item> getSelectedItems() {
        ArrayList<Item> selectedItems = new ArrayList<>();

        for (int i = 0; i < items.size(); ++i) {
            if (selection[i]) {
                selectedItems.add(items.get(i));
            }
        }

        return selectedItems;
    }
}