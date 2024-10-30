package com.example.expenseye.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.expenseye.R;
import com.example.expenseye.model.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    public interface OnCategoryButtonClickListener {
        void onCategoryButtonClick(Category category);
    }

    private List<Category> mListData;
    private Context mContext;
    private OnCategoryButtonClickListener mListener;

    // Constructor updated to accept the listener
    public CategoryAdapter(Context context, List<Category> listData, OnCategoryButtonClickListener listener) {
        mListData = listData;
        mContext = context;
        mListener = listener;
    }

    private Context getmContext() {
        return mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the single item layout
        View view = inflater.inflate(R.layout.item_category_list, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // bind data to the view holder
        Category category = mListData.get(position);
        holder.tvCategoryName.setText(category.getCategoryName());

        // Step 4: Set OnClickListener for the button
        holder.btnEditCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Pass the clicked category to the listener
                mListener.onCategoryButtonClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListData != null ? mListData.size() : 0;
    }

    // Step 2: Update ViewHolder to include the button
    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvCategoryName;
        public Button btnEditCategory;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCategoryName = (TextView) itemView.findViewById(R.id.tvCategoryName);
            btnEditCategory = (Button) itemView.findViewById(R.id.btnEditCategory);
        }
    }

    public void CategoryExpenses(List<Category> newCategories) {
        mListData.clear();
        mListData.addAll(newCategories);
        notifyDataSetChanged();
    }
}

