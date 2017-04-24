package it.generaladapter.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import it.generaladapter.GeneralAdapter;
import it.generaladapter.OnLoadMoreListener;
import it.generaladapter.RecyclerViewLoadingFooterView;
import it.generaladapter.RecyclerViewScrollStateUtils;
import it.generaladapter.demo.utils.DataAdapter;
import it.generaladapter.demo.utils.ItemModel;
import it.generaladapter.demo.utils.TaskManager;

/**
 * @author dengwei
 * @date 2017/4/20
 * @description 瀑布流Activity
 */

public class StaggeredGridViewActivity extends AppCompatActivity implements DataAdapter.OnItemClickListener, TaskManager.OnTaskLoadListener {

    private RecyclerView mRecyclerView;

    /**
     * 服务器端一共多少条数据
     */
    private static final int TOTAL_COUNTER = 64;

    /**
     * 每一页展示多少条数据
     */
    private static final int REQUEST_COUNT = 10;

    /**
     * 已经获取到多少条数据了
     */
    private int mCurrentCounter = 0;

    private DataAdapter mDataAdapter = null;

    private GeneralAdapter mSeamlessAutoLoadRecyclerViewAdapter;
    private TaskManager mTaskManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        //init data
        ArrayList<ItemModel> dataList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {

            ItemModel item = new ItemModel();
            item.id = i;
            item.title = "item" + i;
            dataList.add(item);
        }

        mCurrentCounter = dataList.size();
        mDataAdapter = new DataAdapter(this, R.layout.item_staggered_text);
        mDataAdapter.setStaggeredGridLayout(true);
        mDataAdapter.setItemCickListener(new DataAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SortedList list, DataAdapter.ViewHolder viewHolder, View view) {
                ItemModel item = (ItemModel) list.get(RecyclerViewScrollStateUtils.getAdapterPosition(mRecyclerView, viewHolder));
                Toast.makeText(StaggeredGridViewActivity.this, item.title, Toast.LENGTH_SHORT).show();
            }
        });
        mDataAdapter.addItems(dataList);
        mTaskManager = new TaskManager(this);
        mTaskManager.setLoadDataListener(this);
        mSeamlessAutoLoadRecyclerViewAdapter = new GeneralAdapter(this, mRecyclerView, mDataAdapter, true);
        mRecyclerView.setAdapter(mSeamlessAutoLoadRecyclerViewAdapter);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mSeamlessAutoLoadRecyclerViewAdapter.setLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMoreData(RecyclerView recyclerView, RecyclerViewLoadingFooterView.State footerViewState) {
                if (footerViewState == RecyclerViewLoadingFooterView.State.Loading) {
                    return;
                }
                if (mCurrentCounter < TOTAL_COUNTER) {
                    RecyclerViewScrollStateUtils.setFooterViewState(StaggeredGridViewActivity.this, mRecyclerView, REQUEST_COUNT, RecyclerViewLoadingFooterView.State.Loading, null);
                    mTaskManager.requestData(mDataAdapter.getItemCount());
                } else {
                    RecyclerViewScrollStateUtils.setFooterViewState(StaggeredGridViewActivity.this, mRecyclerView, REQUEST_COUNT, RecyclerViewLoadingFooterView.State.End, null);
                }
            }
        });
    }
    @Override
    public void onTaskLoadSuccees(List<ItemModel> datas) {
        addItems(datas);
        RecyclerViewScrollStateUtils.setFooterViewState(mRecyclerView, RecyclerViewLoadingFooterView.State.Normal);
    }

    @Override
    public void onTaskLoadFaild() {
        notifyDataSetChanged();
    }

    @Override
    public void onTaskLoadNotNetwork() {
        RecyclerViewScrollStateUtils.setFooterViewState(this, mRecyclerView, REQUEST_COUNT, RecyclerViewLoadingFooterView.State.NetWorkError, mFooterClick);
    }

    private View.OnClickListener mFooterClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RecyclerViewScrollStateUtils.setFooterViewState(StaggeredGridViewActivity.this, mRecyclerView, REQUEST_COUNT, RecyclerViewLoadingFooterView.State.Loading, null);
            mTaskManager.requestData(mDataAdapter.getItemCount());
        }
    };


    private void notifyDataSetChanged() {
        mSeamlessAutoLoadRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void addItems(List<ItemModel> list) {

        mDataAdapter.addItems(list);
        mCurrentCounter += list.size();
    }

    @Override
    public void onItemClick(SortedList list, DataAdapter.ViewHolder viewHolder, View view) {
        ItemModel item = (ItemModel) list.get(RecyclerViewScrollStateUtils.getAdapterPosition(mRecyclerView, viewHolder));
        Toast.makeText(StaggeredGridViewActivity.this, item.title, Toast.LENGTH_SHORT).show();
    }
}
