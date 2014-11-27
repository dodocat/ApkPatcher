package org.quanqi.android.apkpatcher.utils;


import android.content.Context;
import android.os.AsyncTask;


/**
 * 加强版的开始线程的操作（网络判断）
 *
 * @param <Q> 调用execute(Q)方法出入的参数类型
 * @param <T> 得出的结果result的类型
 * @author Administrator
 */
public abstract class YouHttpTask<Q, T> extends AsyncTask<Q, Void, T> {

    private Context context;

    /**
     * 加强版的开始线程的操作（网络判断）
     *
     * @param <Q> 调用execute(Q)方法出入的参数类型
     * @param <T> 得出的结果result的类型
     * @author Administrator
     */
    public final AsyncTask<Q, Void, T> executeProxy(Q... params) {
        // 判断网络的状态
        if (NetUtils.checkNetWork(context)) {
            System.out.println("走的这里!!!!!" + NetUtils.checkNetWork(context));
            return super.execute(params);
        } else {
            PromptManager.showNoNetWork(context);
            System.out.println("走的这里!!!!!");
            return null;
        }
    }

    /**
     * 必须先调用此方法设置context.
     */
    public void setContext(Context context) {
        this.context = context;
    }

    protected void onPostExecute(T result) {

    }

}