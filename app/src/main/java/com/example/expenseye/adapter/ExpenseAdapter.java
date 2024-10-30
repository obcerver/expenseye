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
import com.example.expenseye.model.Expense;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder>{

    private List<Expense> mListData;
    private List<Category> categoryList;
    private Context mContext;

    // Constructor updated to accept the listener
    public ExpenseAdapter(Context context, List<Expense> listData, List<Category> categoryList) {
        mListData = listData;
        mContext = context;
        this.categoryList = categoryList;
    }

    private Context getmContext() {
        return mContext;
    }

    @Override
    public ExpenseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the single item layout
        View view = inflater.inflate(R.layout.item_expense_list, parent, false);

        // Return a new holder instance
        ExpenseAdapter.ViewHolder viewHolder = new ExpenseAdapter.ViewHolder(view);
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

    @Override
    public void onBindViewHolder(ExpenseAdapter.ViewHolder holder, int position) {
        // bind data to the view holder
        Expense expense = mListData.get(position);

        String categoryName = "";

        if(expense != null) {

            int categoryId = expense.getCategoryId();

            Category c = findCategoryById(categoryId);

            if(c != null) {
                categoryName = c.getCategoryName();
            }
        }

        Date date = expense.getExpenseDate();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String expenseDate = dateFormat.format(date);

        holder.tvExpenseCategoryName.setText(categoryName);
        holder.tvExpenseDate.setText(expenseDate);
        holder.tvExpenseValue.setText("RM " + expense.getExpenseValue());
    }

    @Override
    public int getItemCount() {
        return mListData != null ? mListData.size() : 0;
    }

    // Step 2: Update ViewHolder to include the button
    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvExpenseCategoryName;
        public TextView tvExpenseDate;
        public TextView tvExpenseValue;

        public ViewHolder(View itemView) {
            super(itemView);
            tvExpenseCategoryName = (TextView) itemView.findViewById(R.id.tvExpenseCategoryName);
            tvExpenseDate = (TextView) itemView.findViewById(R.id.tvExpenseDate);
            tvExpenseValue = (TextView) itemView.findViewById(R.id.tvExpenseValue);
        }
    }
}
