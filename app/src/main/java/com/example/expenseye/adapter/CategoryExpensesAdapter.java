package com.example.expenseye.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expenseye.R;
import com.example.expenseye.model.Category;
import com.example.expenseye.model.CategoryExpenses;

import java.util.List;

public class CategoryExpensesAdapter extends RecyclerView.Adapter<CategoryExpensesAdapter.ViewHolder>{
    private List<CategoryExpenses> mListData;
    private List<Category> categoryList;
    private Context mContext;
    private double totalExpenses;

    // Constructor updated to accept the listener
    public CategoryExpensesAdapter(Context context, List<CategoryExpenses> listData, List<Category> categoryList, double totalExpenses) {
        mListData = listData;
        this.mContext = context;
        this.categoryList = categoryList;
        this.totalExpenses = totalExpenses;
    }

    private Context getmContext() {
        return mContext;
    }

    @Override
    public CategoryExpensesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the single item layout
        View view = inflater.inflate(R.layout.item_category_expenses_list, parent, false);

        // Return a new holder instance
        CategoryExpensesAdapter.ViewHolder viewHolder = new CategoryExpensesAdapter.ViewHolder(view);
        return viewHolder;
    }

    private Category findCategoryById(int categoryId) {
        for (Category c : categoryList) {
            if (c.getCategoryId() == categoryId) {
                return c;
            }
        }
        return null;
    }

    private int getColorByPosition(int position) {
        // Define the color names as they appear in your resources
        String[] colorNames = {
                "color0",
                "color1",
                "color2",
                "color3",
                "color4",
                "color5",
                "color6",
                "color7",
                "color8",
                "color9",
                "color10",
                "color11",
                "color12",
                "color13",
                "color14",
                "color15",
                "color16",
                "color17",
                "color18",
                "color19",
                "color20"
        };

        // Determine the color name based on the position
        int colorIndex = position % colorNames.length; // Loop through colors if more items than colors
        String colorName = colorNames[colorIndex];

        // Get the color resource ID
        int colorId = mContext.getResources().getIdentifier(colorName, "color", mContext.getPackageName());

        // Return the color
        return ContextCompat.getColor(mContext, colorId);
    }

    @Override
    public void onBindViewHolder(CategoryExpensesAdapter.ViewHolder holder, int position) {
        // bind data to the view holder
        CategoryExpenses categoryExpenses = mListData.get(position);

        String categoryName = "";
        double percentage = 0;
        double categoryTotalExpenses = 0;

        if(categoryExpenses != null) {

            int categoryId = categoryExpenses.getCategoryId();
            categoryTotalExpenses = categoryExpenses.getTotalExpenses();
            percentage = Math.round(((categoryTotalExpenses/totalExpenses) * 100.0) *100.0) / 100.0;

            Log.e("MyApp", "TotalCategory: " + totalExpenses);
            Log.e("MyApp", "TotalCategory: " + categoryTotalExpenses);

            Category c = findCategoryById(categoryId);

            if(c != null) {
                categoryName = c.getCategoryName();
            }
        }

        holder.tvPercentageCategoryName.setText(categoryName);
        holder.tvPercentageTotalExpenses.setText("RM " + categoryExpenses.getTotalExpenses());
        holder.tvPercentageCategory .setText(percentage + "%");

        // Use the position to determine the color
        int backgroundColor = getColorByPosition(position);
        //holder.layoutAnalysisItem.setBackgroundColor(backgroundColor);
        holder.tvPercentageCategoryName.setTextColor(backgroundColor);
        holder.tvPercentageTotalExpenses.setTextColor(backgroundColor);
        holder.tvPercentageCategory .setTextColor(backgroundColor);
    }

    @Override
    public int getItemCount() {
        return mListData != null ? mListData.size() : 0;
    }

    // Step 2: Update ViewHolder to include the button
    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvPercentageCategoryName;
        public TextView tvPercentageCategory;
        public TextView tvPercentageTotalExpenses;
        public GridLayout layoutAnalysisItem;

        public ViewHolder(View itemView) {
            super(itemView);
            tvPercentageCategoryName= (TextView) itemView.findViewById(R.id.tvPercentageCategoryName);
            tvPercentageCategory = (TextView) itemView.findViewById(R.id.tvPercentageCategory);
            tvPercentageTotalExpenses = (TextView) itemView.findViewById(R.id.tvPercentageTotalExpenses);
            layoutAnalysisItem = (GridLayout) itemView.findViewById(R.id.layoutAnalysisItem);
        }
    }

}
