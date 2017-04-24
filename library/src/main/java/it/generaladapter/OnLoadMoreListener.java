package it.generaladapter;

import android.support.v7.widget.RecyclerView;

public interface OnLoadMoreListener {
    /**
     * 加载更多
     * @param recyclerView RecyclerView
     * @param footerViewState Footer状态
     */
    void onLoadMoreData(RecyclerView recyclerView, RecyclerViewLoadingFooterView.State footerViewState);
}
