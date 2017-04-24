package it.generaladapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import static it.generaladapter.RecyclerViewLoadingFooterView.State;

@SuppressLint("LongLogTag")
public class GeneralAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_HEADER_VIEW = 1 << 2;
    public static final int TYPE_FOOTER_VIEW = 1 << 3;

    private RecyclerView.Adapter<RecyclerView.ViewHolder> mInnerAdapter;
    private List<View> mHeaderViewArray = new ArrayList<>();
    private List<View> mFooterViewArray = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private OnLoadMoreListener mLoadMoreListener;
    private boolean mIsLoadingFooter;
    private Context mContext;

    public GeneralAdapter(Context context, RecyclerView recyclerView, RecyclerView.Adapter<RecyclerView.ViewHolder> adapter, boolean isLoadingFooter) {
        this.mContext = context;
        this.mRecyclerView = recyclerView;
        this.mIsLoadingFooter = isLoadingFooter;
        if (mIsLoadingFooter) {
            addFooterView(new RecyclerViewLoadingFooterView(mContext));
        }
        setAdapter(adapter);
    }

    /**
     * 设置adapter
     * @param adapter 本框架按照装饰者模式编写，你只要将你原本写的Adapter直接set到这个类中则完成
     */
    public void setAdapter(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {

        if (adapter != null) {
            if (!(adapter instanceof RecyclerView.Adapter))
                throw new IllegalArgumentException("Adapter Incorrect type");
        }

        if (mInnerAdapter != null) {
            notifyItemRangeRemoved(getHeaderViewsCount(), mInnerAdapter.getItemCount());
            mInnerAdapter.unregisterAdapterDataObserver(mDataObserver);
        }

        this.mInnerAdapter = adapter;
        mInnerAdapter.registerAdapterDataObserver(mDataObserver);
        notifyItemRangeInserted(getHeaderViewsCount(), mInnerAdapter.getItemCount());
    }

    public void setLoadMoreListener(OnLoadMoreListener listener) {
        mLoadMoreListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int headerViewsCountCount = getHeaderViewsCount();
        if (viewType < TYPE_HEADER_VIEW + headerViewsCountCount) {
            return new ViewHolder(mHeaderViewArray.get(viewType - TYPE_HEADER_VIEW));
        } else if (viewType >= TYPE_FOOTER_VIEW && viewType < Integer.MAX_VALUE / 2) {
            return new ViewHolder(mFooterViewArray.get(viewType - TYPE_FOOTER_VIEW));
        } else {
            return mInnerAdapter.onCreateViewHolder(parent, viewType - Integer.MAX_VALUE / 2);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int headerViewsCountCount = getHeaderViewsCount();
        if (position >= headerViewsCountCount && position < headerViewsCountCount + mInnerAdapter.getItemCount()) {
            mInnerAdapter.onBindViewHolder(holder, position - headerViewsCountCount);
        } else {
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
                ((StaggeredGridLayoutManager.LayoutParams) layoutParams).setFullSpan(true);
            }
        }
    }

    @Override
    public int getItemCount() {
        return getHeaderViewsCount() + getFooterViewsCount() + mInnerAdapter.getItemCount();
    }

    /**
     * 解决添加头部问题
     */
    private RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {

        @Override
        public void onChanged() {
            super.onChanged();
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            notifyItemRangeChanged(positionStart + getHeaderViewsCount(), itemCount);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            notifyItemRangeInserted(positionStart + getHeaderViewsCount(), itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            notifyItemRangeRemoved(positionStart + getHeaderViewsCount(), itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            int headerViewsCountCount = getHeaderViewsCount();
            notifyItemRangeChanged(fromPosition + headerViewsCountCount, toPosition + headerViewsCountCount + itemCount);
        }
    };

    public RecyclerView.Adapter getInnerAdapter() {
        return mInnerAdapter;
    }

    public void addHeaderView(View header) {

        if (header == null) {
            throw new RuntimeException("header is null");
        }

        mFooterViewArray.add(header);
        this.notifyDataSetChanged();
    }

    public void addFooterView(View footer) {

        if (footer == null) {
            throw new RuntimeException("footer is null");
        }

        mFooterViewArray.add(footer);
        this.notifyDataSetChanged();
    }

    /**
     * @return 返回第一个FoView
     */
    public View getFooterView() {
        return getFooterViewsCount() > 0 ? mFooterViewArray.get(0) : null;
    }

    /**
     * @return 返回第一个HeaderView
     */
    public View getHeaderView() {
        return getHeaderViewsCount() > 0 ? mHeaderViewArray.get(0) : null;
    }

    public void removeHeaderView(View view) {
        mHeaderViewArray.remove(view);
        this.notifyDataSetChanged();
    }

    public void removeFooterView(View view) {
        mFooterViewArray.remove(view);
        this.notifyDataSetChanged();
    }

    public int getHeaderViewsCount() {
        return mHeaderViewArray.size();
    }

    public int getFooterViewsCount() {
        return mFooterViewArray.size();
    }

    /**
     * @param position 对应的位置
     * @return 判断是否是第一个HeaderView
     */
    public boolean isFirstHeader(int position) {
        return getHeaderViewsCount() > 0 && position == 0;
    }

    /**
     * @param position 对应的位置
     * @return 判断是否是最后一个FooterView
     */
    public boolean isLastFooter(int position) {
        return getFooterViewsCount() > 0 && position == (getItemCount() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        int innerCount = mInnerAdapter.getItemCount();
        int headerViewsCountCount = getHeaderViewsCount();
        if (position < headerViewsCountCount) {
            return TYPE_HEADER_VIEW + position;
        } else if (headerViewsCountCount <= position && position < headerViewsCountCount + innerCount) {
            int innerItemViewType = mInnerAdapter.getItemViewType(position - headerViewsCountCount);
            if (innerItemViewType >= Integer.MAX_VALUE / 2) {
                throw new IllegalArgumentException();
            }
            return innerItemViewType + Integer.MAX_VALUE / 2;
        } else {
            if (mLoadMoreListener != null) {
                State footerViewState = RecyclerViewScrollStateUtils.getFooterViewState(mRecyclerView);
                if (footerViewState != State.NetWorkError)
                    mLoadMoreListener.onLoadMoreData(mRecyclerView, footerViewState);
            }
            return TYPE_FOOTER_VIEW + position - headerViewsCountCount - innerCount;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            ((GridLayoutManager) layoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (isFirstHeader(position) || isLastFooter(position)) ? ((GridLayoutManager) layoutManager).getSpanCount() : 1;
                }
            });
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams && (isFirstHeader(holder.getAdapterPosition()) || isLastFooter(holder.getAdapterPosition()))) {
            ((StaggeredGridLayoutManager.LayoutParams) layoutParams).setFullSpan(true);
        }
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }
}
