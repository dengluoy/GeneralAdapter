package it.generaladapter.demo.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;


import java.util.ArrayList;
import java.util.List;

/**
 * @author dengwei
 * @date 2017/4/20
 * @description
 */


public class TaskManager {

    private PreviewHandler mHandler = new PreviewHandler();
    private OnTaskLoadListener mOnTaskLoadListener;

    private Context mContext;
    public TaskManager(Context context){
        mContext = context;
    }

    public void setLoadDataListener(OnTaskLoadListener onTaskLoadListener) {
        this.mOnTaskLoadListener = onTaskLoadListener;
    }

    public void requestData(final int currentSize) {

        new Thread() {

            @Override
            public void run() {
                super.run();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

//                //模拟一下网络请求失败的情况
                Message message = mHandler.obtainMessage();
                if (NetworkUtils.isNetAvailable(mContext)) {
                    message.what = -1;
                    message.arg1 = currentSize;
                    mHandler.sendMessage(message);
                } else {
                    message.what = -3;
                    message.arg1 = currentSize;
                    mHandler.sendMessage(message);
                }
            }
        }.start();
    }

    public class PreviewHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case -1:
                    int currentSize = msg.arg1;
                    ArrayList<ItemModel> newList = new ArrayList<>();
                    for (int i = 0; i < 10; i++) {

                        ItemModel item = new ItemModel();
                        item.id = currentSize + i;
                        item.title = "item" + (item.id);

                        newList.add(item);
                    }
                    if (mOnTaskLoadListener != null) {
                        mOnTaskLoadListener.onTaskLoadSuccees(newList);
                    }
                    break;
                case -2:
                    if (mOnTaskLoadListener != null) {
                        mOnTaskLoadListener.onTaskLoadFaild();
                    }
                    break;
                case -3:
                    if (mOnTaskLoadListener != null) {
                        mOnTaskLoadListener.onTaskLoadNotNetwork();
                    }
                    break;
            }
        }
    }

    public interface OnTaskLoadListener {

        /**
         * 加载成功
         *
         * @param datas
         */
        void onTaskLoadSuccees(List<ItemModel> datas);

        /**
         * 加载失败
         */
        void onTaskLoadFaild();

        /**
         * 无网络
         */
        void onTaskLoadNotNetwork();
    }
}
