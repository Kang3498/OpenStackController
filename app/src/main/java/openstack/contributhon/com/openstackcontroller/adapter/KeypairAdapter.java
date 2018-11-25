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
import openstack.contributhon.com.openstackcontroller.list.KeypairList;
import openstack.contributhon.com.openstackcontroller.list.NetworkList;
import openstack.contributhon.com.openstackcontroller.model.Keypair;
import openstack.contributhon.com.openstackcontroller.model.KeypairVO;

public class KeypairAdapter extends ArrayAdapter<KeypairVO> {

    KeypairList mList;

    private static class ViewHolder {
        TextView hostText;
        TextView userText;
      //  TextView userText2;
      ImageButton moreBtn;
        ImageView moreImg;
    }

    public KeypairAdapter(@NonNull Context context, ArrayList<KeypairVO> datas, KeypairList list) {
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
            //viewHolder.userText2 = convertView.findViewById(R.id.row_nova_status);
            viewHolder.moreImg = convertView.findViewById(R.id.row_img);
            viewHolder.moreBtn = convertView.findViewById(R.id.row_more);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Keypair vo = getItem(position).keypair;
        viewHolder.hostText.setText(vo.name);
        viewHolder.userText.setText(vo.fingerprint);
        //viewHolder.userText2.setText(vo.type);
        viewHolder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mList.itemDialog(position, viewHolder.moreImg);
            }
        });
        return convertView;
    }
}
