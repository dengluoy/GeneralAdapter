package it.generaladapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class RecyclerViewScrollStateUtils {

    /**
     * 设置headerAndFooterAdapter的FooterView State
     *
     * @param instance      context
     * @param recyclerView  recyclerView
     * @param pageSize      分页展示时，recyclerView每一页的数量
     * @param state         FooterView State
     * @param errorListener FooterView处于Error状态时的点击事件
     */
    public static void setFooterViewState(Activity instance, RecyclerView recyclerView, int pageSize, RecyclerViewLoadingFooterView.State state, View.OnClickListener errorListener) {

        if (instance == null || instance.isFinishing()) {
            return;
        }

        RecyclerView.Adapter outerAdapter = recyclerView.getAdapter();

        if (outerAdapter == null || !(outerAdapter instanceof GeneralAdapter)) {
            return;
        }

        GeneralAdapter seamlessAutoLoadRecyclerViewAdapter = (GeneralAdapter) outerAdapter;

        //只有一页的时候，就别加什么FooterView了
        if (seamlessAutoLoadRecyclerViewAdapter.getInnerAdapter().getItemCount() < pageSize) {
            return;
        }

        RecyclerViewLoadingFooterView footerView;

        //已经有footerView了
        if (seamlessAutoLoadRecyclerViewAdapter.getFooterViewsCount() > 0) {
            footerView = (RecyclerViewLoadingFooterView) seamlessAutoLoadRecyclerViewAdapter.getFooterView();
            footerView.setState(state);

            if (state == RecyclerViewLoadingFooterView.State.NetWorkError) {
                footerView.setOnClickListener(errorListener);
            }
            recyclerView.scrollToPosition(seamlessAutoLoadRecyclerViewAdapter.getItemCount() - 1);
        } else {
            footerView = new RecyclerViewLoadingFooterView(instance);
            footerView.setState(state);

            if (state == RecyclerViewLoadingFooterView.State.NetWorkError) {
                footerView.setOnClickListener(errorListener);
            }

            seamlessAutoLoadRecyclerViewAdapter.addFooterView(footerView);
            recyclerView.scrollToPosition(seamlessAutoLoadRecyclerViewAdapter.getItemCount() - 1);
        }
    }

    /**
     * 获取当前RecyclerView.FooterView的状态
     *
     * @param recyclerView 指定RecyclerView
     * @return 返回的当前状态
     */
    public static RecyclerViewLoadingFooterView.State getFooterViewState(RecyclerView recyclerView) {

        RecyclerView.Adapter outerAdapter = recyclerView.getAdapter();
        if (outerAdapter != null && outerAdapter instanceof GeneralAdapter) {
            if (((GeneralAdapter) outerAdapter).getFooterViewsCount() > 0) {
                RecyclerViewLoadingFooterView footerView = (RecyclerViewLoadingFooterView) ((GeneralAdapter) outerAdapter).getFooterView();
                return footerView.getState();
            }
        }

        return RecyclerViewLoadingFooterView.State.Normal;
    }

    /**
     * 设置当前RecyclerView.FooterView的状态
     *
     * @param recyclerView 指定RecyclerView
     * @param state        指定的状态
     */
    public static void setFooterViewState(RecyclerView recyclerView, RecyclerViewLoadingFooterView.State state) {
        RecyclerView.Adapter outerAdapter = recyclerView.getAdapter();
        if (outerAdapter != null && outerAdapter instanceof GeneralAdapter) {
            if (((GeneralAdapter) outerAdapter).getFooterViewsCount() > 0) {
                RecyclerViewLoadingFooterView footerView = (RecyclerViewLoadingFooterView) ((GeneralAdapter) outerAdapter).getFooterView();
                footerView.setState(state);
            }
        }
    }

    /**
     * 请使用本方法替代RecyclerView.ViewHolder的getAdapterPosition()方法
     *
     * @param recyclerView 指定RecyclerView
     * @param holder       指定的ViewHolder
     * @return 除去头部返回的Position
     */
    public static int getAdapterPosition(RecyclerView recyclerView, RecyclerView.ViewHolder holder) {
        RecyclerView.Adapter outerAdapter = recyclerView.getAdapter();
        if (outerAdapter != null && outerAdapter instanceof GeneralAdapter) {

            int headerViewCounter = ((GeneralAdapter) outerAdapter).getHeaderViewsCount();
            if (headerViewCounter > 0) {
                return holder.getAdapterPosition() - headerViewCounter;
            }
        }

        return holder.getAdapterPosition();
    }

}
