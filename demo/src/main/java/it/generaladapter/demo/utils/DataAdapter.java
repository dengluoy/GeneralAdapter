package it.generaladapter.demo.utils;

import android.content.Context;
import android.support.v7.util.SortedList;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.generaladapter.demo.R;

/**
 * @author dengwei
 * @date 2017/4/20
 * @description
 */


public class DataAdapter extends RecyclerView.Adapter {

    private LayoutInflater mLayoutInflater;
    private SortedList<ItemModel> mSortedList;
    private int mResLayoutId;
    private boolean mIsStaggeredLayout;
    private Context mContext;

    public DataAdapter(Context context, int resLayoutId) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mResLayoutId = resLayoutId;
        mSortedList = new SortedList<>(ItemModel.class, new SortedList.Callback<ItemModel>() {

            /**
             * 返回一个负整数（第一个参数小于第二个）、零（相等）或正整数（第一个参数大于第二个）
             */
            @Override
            public int compare(ItemModel o1, ItemModel o2) {

                if (o1.id < o2.id) {
                    return -1;
                } else if (o1.id > o2.id) {
                    return 1;
                }

                return 0;
            }

            @Override
            public boolean areContentsTheSame(ItemModel oldItem, ItemModel newItem) {
                return oldItem.title.equals(newItem.title);
            }

            @Override
            public boolean areItemsTheSame(ItemModel item1, ItemModel item2) {
                return item1.id == item2.id;
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }
        });
    }

    public void addItems(List<ItemModel> list) {
        mSortedList.beginBatchedUpdates();

        for (ItemModel itemModel : list) {
            mSortedList.add(itemModel);
        }

        mSortedList.endBatchedUpdates();
    }

    public void deleteItems(ArrayList<ItemModel> items) {
        mSortedList.beginBatchedUpdates();
        for (ItemModel item : items) {
            mSortedList.remove(item);
        }
        mSortedList.endBatchedUpdates();
    }

    public void setStaggeredGridLayout(boolean isStaggeredGridLayout){
        mIsStaggeredLayout = isStaggeredGridLayout;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DataAdapter.ViewHolder(mLayoutInflater.inflate(mResLayoutId, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ItemModel item = mSortedList.get(position);

        DataAdapter.ViewHolder viewHolder = (DataAdapter.ViewHolder) holder;
        viewHolder.textView.setText(item.title);
        if (mIsStaggeredLayout) {
            viewHolder.cardView.getLayoutParams().height = (position % 2) != 0 ?
                    (int)mContext.getResources().getDisplayMetrics().density * 300 :
                    (int) (mContext.getResources().getDisplayMetrics().density * 200);
        }
    }

    @Override
    public int getItemCount() {
        return mSortedList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private CardView cardView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.info_text);
            cardView = (CardView) itemView.findViewById(R.id.card_view);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    ItemModel item = mSortedList.get(RecyclerViewScrollStateUtils.getAdapterPosition(mRecyclerView, DataAdapter.ViewHolder.this));
//                    Toast.makeText(LinearVerticalActivity.this, item.title, Toast.LENGTH_SHORT).show();
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mSortedList, DataAdapter.ViewHolder.this, v);
                    }
                }
            });
        }
    }

    private OnItemClickListener mOnItemClickListener;

    public void setItemCickListener(OnItemClickListener onItemClickListener){
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClick(SortedList list, DataAdapter.ViewHolder viewHolder, View view);
    }
}
