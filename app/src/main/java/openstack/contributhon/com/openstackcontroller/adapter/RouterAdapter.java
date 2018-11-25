package openstack.contributhon.com.openstackcontroller.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import openstack.contributhon.com.openstackcontroller.R;
import openstack.contributhon.com.openstackcontroller.list.InstanceList;
import openstack.contributhon.com.openstackcontroller.list.RouterList;
import openstack.contributhon.com.openstackcontroller.model.RouterVO;

public class RouterAdapter extends ArrayAdapter<RouterVO> {

    RouterList mList;

    private static class ViewHolder {
        TextView hostText;
        TextView userText;
        TextView dateText;
        ImageButton moreBtn;
        ImageView moreImg;
    }

    public RouterAdapter(@NonNull Context context, ArrayList<RouterVO> datas, RouterList list) {
        super(context, 0, datas);
        mList = list;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.hostText = convertView.findViewById(R.id.row_title);
            viewHolder.userText = convertView.findViewById(R.id.row_below);
            viewHolder.dateText = convertView.findViewById(R.id.row_option);
            viewHolder.moreImg = convertView.findViewById(R.id.row_img);
            viewHolder.moreBtn = convertView.findViewById(R.id.row_more);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        RouterVO vo = getItem(position);
        viewHolder.hostText.setText(vo.name);
        if(vo.routes.size()>0)
            viewHolder.userText.setText(vo.routes.get(0).destination);
        viewHolder.dateText.setText(vo.status);
        viewHolder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mList.itemDialog(position, viewHolder.moreImg);
            }
        });

        return convertView;
    }
}
