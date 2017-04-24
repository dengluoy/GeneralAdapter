package it.generaladapter.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.SortedList;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import static it.generaladapter.RecyclerViewLoadingFooterView.State.Loading;

/**
 * @author dengwei
 * @date 2017/4/20
 * @description
 */


public class GridViewActivity extends AppCompatActivity implements DataAdapter.OnItemClickListener, TaskManager.OnTaskLoadListener {

    private static final int TOTAL_COUNTER = 64;
    private static final int REQUEST_COUNT = 10;
    private int mCurrentCounter = 0;

    private RecyclerView mRecyclerView;
    private DataAdapter mDataAdapter;
    private GeneralAdapter mSeamlessAutoLoadRecyclerViewAdapter;
    private TaskManager mTaskManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        //init data
        ArrayList<ItemModel> dataList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {

            ItemModel item = new ItemModel();
            item.id = i;
            item.title = "item" + i;
            dataList.add(item);
        }

        mCurrentCounter = dataList.size();
        mTaskManager = new TaskManager(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mDataAdapter = new DataAdapter(this, R.layout.item_grid_text);
        mDataAdapter.addItems(dataList);
        mDataAdapter.setItemCickListener(this);
        mTaskManager.setLoadDataListener(this);
        mSeamlessAutoLoadRecyclerViewAdapter = new GeneralAdapter(this, mRecyclerView, mDataAdapter, true);
        mRecyclerView.setAdapter(mSeamlessAutoLoadRecyclerViewAdapter);
        mSeamlessAutoLoadRecyclerViewAdapter.setLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMoreData(RecyclerView recyclerView, RecyclerViewLoadingFooterView.State footerViewState) {
                if (footerViewState == Loading) {
                    return ;
                }

                if (mCurrentCounter < TOTAL_COUNTER) {
                    RecyclerViewScrollStateUtils.setFooterViewState(mRecyclerView, Loading);
                    mTaskManager.requestData(mDataAdapter.getItemCount());
                } else{
                    RecyclerViewScrollStateUtils.setFooterViewState(mRecyclerView, RecyclerViewLoadingFooterView.State.End);
                }
            }
        });
    }

    @Override
    public void onItemClick(SortedList list, DataAdapter.ViewHolder viewHolder, View view) {
        ItemModel item = (ItemModel) list.get(RecyclerViewScrollStateUtils.getAdapterPosition(mRecyclerView, viewHolder));
        Toast.makeText(GridViewActivity.this, item.title, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTaskLoadSuccees(List<ItemModel> datas) {
        addItems(datas);
        RecyclerViewScrollStateUtils.setFooterViewState(mRecyclerView, RecyclerViewLoadingFooterView.State.Normal);
    }

    @Override
    public void onTaskLoadFaild() {
        mDataAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTaskLoadNotNetwork() {
        RecyclerViewScrollStateUtils.setFooterViewState(this, mRecyclerView, REQUEST_COUNT, RecyclerViewLoadingFooterView.State.NetWorkError, mFooterClick);
    }

    private void notifyDataSetChanged() {
        mSeamlessAutoLoadRecyclerViewAdapter.notifyDataSetChanged();
    }

    private View.OnClickListener mFooterClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RecyclerViewScrollStateUtils.setFooterViewState(GridViewActivity.this, mRecyclerView, REQUEST_COUNT, Loading, null);
            mTaskManager.requestData(mDataAdapter.getItemCount());
        }
    };

    private void addItems(List<ItemModel> list) {

        mDataAdapter.addItems(list);
        mCurrentCounter += list.size();
    }
}
