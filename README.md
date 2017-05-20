## 简述

RecyclerViewGeneralAdapter采用装饰者模式在完全不修改原有代码基础上拓展RecyclerView的加载更多功能以及添加Header。

## 使用

加载功能:
``` java
  //原有Adapter
  DataAdapter dataAdapter = new DataAdapter(this, R.layout.item_vertical_text);
  RecyclerViewGeneralAdapter generalAdapter = new RecyclerViewGeneralAdapter(context, recyclerView, dataAdapter);
  recyclerView.setAdapter(generalAdapter);
  //目前无论是VERTICAL或者HORIZONTAL 使用方式一样的.
  recyclerView.setLayoutManager(new LinearLayoutManager(context));
  generalAdapter.setLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMoreData(RecyclerView recyclerView, RecyclerViewLoadingFooterView.State footerViewState) {
              //如果是处在Loading状态则直接return
                if (footerViewState == RecyclerViewLoadingFooterView.State.Loading) {
                    return;
                }
                //如果还没有到数据末端，则在去获取数据
                if (mCurrentCounter < TOTAL_COUNTER) {
                    RecyclerViewScrollStateUtils.setFooterViewState(LinearVerticalActivity.this, mRecyclerView, REQUEST_COUNT, RecyclerViewLoadingFooterView.State.Loading, null);
                    mTaskManager.requestData(mDataAdapter.getItemCount());
                } else {
                //如果已经将所有数据获取完毕
                    RecyclerViewScrollStateUtils.setFooterViewState(LinearVerticalActivity.this, mRecyclerView, REQUEST_COUNT, RecyclerViewLoadingFooterView.State.End, null);
                }
            }
        });
```
加载成功：
```java
addItems(datas);
        RecyclerViewScrollStateUtils.setFooterViewState(recyclerView, RecyclerViewLoadingFooterView.State.Normal);
```
网络异常:
```java
footerClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RecyclerViewScrollStateUtils.setFooterViewState(LinearVerticalActivity.this, mRecyclerView, REQUEST_COUNT, RecyclerViewLoadingFooterView.State.Loading, null);
            mTaskManager.requestData(mDataAdapter.getItemCount());
        }
    };
RecyclerViewScrollStateUtils.setFooterViewState(this, mRecyclerView, REQUEST_COUNT, RecyclerViewLoadingFooterView.State.NetWorkError, footerClick);

```

添加头部：
```java
//可添加多个
generalAdapter.addHeaderView(new View(context));
```

添加多个Footer：
```java
generalAdapter.addFooterView(new View(context));
```
