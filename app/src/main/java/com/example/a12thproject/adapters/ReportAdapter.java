package com.example.a12thproject.adapters;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.a12thproject.R;
import com.example.a12thproject.classes.Report;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ReportAdapter extends ArrayAdapter<Report> {
    Context context;
    List<Report> objects;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    public ReportAdapter(Context context, int resource, List<Report> objects) {
        super(context, resource, objects);
        this.objects = objects;
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = ((Activity) context).getLayoutInflater();
        View view;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(R.layout.onerowreport, parent, false);
        } else {
            view = convertView;
        }

        ImageView iv = (ImageView) view.findViewById(R.id.IVReport);

        TextView from = (TextView) view.findViewById(R.id.tvReportFrom);
        TextView to = (TextView) view.findViewById(R.id.tvReportTo);
        TextView type = (TextView) view.findViewById(R.id.tvReportType);

        from.setText(objects.get(position).getFrom());
        to.setText(objects.get(position).getTo());
        type.setText(objects.get(position).getType());





        iv.setVisibility(View.VISIBLE);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef = storageReference.child("reports/"+objects.get(position).getId());
        final long ONE_MEGABYTE = 1024 * 1024;
        profileRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            iv.setImageBitmap(bitmap);
        }).addOnFailureListener(exception -> {

        });



        return view;
    }

}