package coms.geeknewbee.doraemon.robot;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.entity.SmsNotify;
import coms.geeknewbee.doraemon.robot.bean.MsgBean;

/**
 * Created by chensiyuan on 2016/5/4.
 * Desc：消息通知的Adapter
 */
public class SmsAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context ctx;
    private List mListData;

    public SmsAdapter(List listData, Context ctx) {
        this.mInflater = LayoutInflater.from(ctx);
        this.mListData = listData;
    }

    @Override
    public int getCount() {
        if(mListData == null)
            return 0;
        return mListData.size();
    }

    @Override
    public Object getItem(int position) {
        return mListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        public TextView date;
        public TextView content;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_smsnotify, null);
            holder.date = (TextView) convertView.findViewById(R.id.tv_date_created);
            holder.content = (TextView) convertView.findViewById(R.id.tv_content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        MsgBean bean = (MsgBean) mListData.get(position);
        holder.date.setText(bean.getDate_created());
        holder.content.setText(bean.getContent());
        return convertView;
    }

    public void setmListData(List mListData) {
        this.mListData = mListData;
        this.notifyDataSetChanged();
    }
}
