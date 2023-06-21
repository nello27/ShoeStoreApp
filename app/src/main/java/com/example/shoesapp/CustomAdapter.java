package com.example.shoesapp;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import android.content.Context;
import android.graphics.drawable.Drawable;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<MainActivity.ListItem> {
    private LayoutInflater inflater;

    public CustomAdapter(Context context, List<MainActivity.ListItem> items) {
        super(context, 0, items);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.list_item, parent, false);
        }

        // Obtener el elemento de la lista en la posición actual
        MainActivity.ListItem item = getItem(position);

        // Asignar la imagen según el ID
        ImageView imageView = view.findViewById(R.id.imageView);
        TextView textView = view.findViewById(R.id.textView);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                convertDpToPixels(60),  // Tamaño en dp convertido a píxeles
                LinearLayout.LayoutParams.WRAP_CONTENT);

        imageView.setLayoutParams(layoutParams);
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.image1);
        imageView.setImageDrawable(drawable);

        if (item.getId() == 1) {
            imageView.setImageResource(R.drawable.image1);
            textView.setText("Productos");
        }
        if (item.getId() == 2) {
            imageView.setImageResource(R.drawable.image2);
            textView.setText("Servicios");
        }
        if (item.getId() == 3) {
            imageView.setImageResource(R.drawable.image3);
            textView.setText("Sucursales");
        }

        // Otros elementos del diseño

        return view;
    }

    private int convertDpToPixels(int dp) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
