package coms.geeknewbee.doraemon.box.time_machine;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

import coms.geeknewbee.doraemon.R;
import coms.geeknewbee.doraemon.entity.RobotPhoto;
import coms.geeknewbee.doraemon.global.MyApplication;
import coms.geeknewbee.doraemon.utils.StringHandler;

/**
 * 时光机的图片处理适配器
 * Created by Administrator on 2016/6/20.
 */
public class PicAdapter extends BaseAdapter {

//    int width;

    List<RobotPhoto> photos;

    String robotPk;

    Context context;

    public PicAdapter(Context context, List<RobotPhoto> photos, String robotPk) {
        this.context = context;
        this.photos = photos;
        this.robotPk = robotPk;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;

        if (convertView == null) {
//            LayoutInflater laInflater = (LayoutInflater) parent.getContext()
//                    .getSystemService(android.app.Service.LAYOUT_INFLATER_SERVICE);
            convertView = View.inflate(MyApplication.getContext(), R.layout.item_pic_c, null);
            holder = new Holder();
            holder.pic = (ImageView) convertView.findViewById(R.id.pic);
            convertView.setTag(holder);
//            if (width == 0) {
//                holder.pic.measure(0, 0);
//                width = holder.pic.getMeasuredWidth();
//            }
        } else {
            holder = (Holder) convertView.getTag();
        }
//        holder.pic.setImageResource(R.mipmap.ic_pic_default);
        if (!StringHandler.isEmpty(photos.get(position).getThumbnail())) {
            holder.pic.setImageURI(Uri.parse(photos.get(position).getThumbnail()));
        }
        return convertView;
    }

    @Override
    public int getCount() {
        if (photos == null)
            return 0;
        return photos.size();
    }

    @Override
    public Object getItem(int position) {
        return photos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getLines() {
        return (getCount() + 2) / 3;
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int num = new Long(id).intValue();
            if (num >= 0) {
                Intent intent = new Intent(context, ShowImageActivity.class);
                intent.putExtra("photos", (Serializable) photos);
                intent.putExtra("num", num);
//                intent.putExtra("imageUrl", photos.get(num).getPhoto());
//                intent.putExtra("imageId", photos.get(num).getId());
//                intent.putExtra("dateKey", photos.get(num).getDate_created());
                intent.putExtra("robotPk", robotPk);
                context.startActivity(intent);
            }
        }
    };

    public AdapterView.OnItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    public static class Holder {
        public ImageView pic;
    }
}
