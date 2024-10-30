package com.example.expenseye.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.expenseye.model.Category;

import java.util.List;

public class CategorySpinnerAdapter extends ArrayAdapter<Category> {
    public CategorySpinnerAdapter(Context context, List<Category> persons) {
        super(context, 0, persons);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Category c = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_item, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(c.getCategoryName());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        Category c = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(c.getCategoryName());

        return convertView;
    }
}
