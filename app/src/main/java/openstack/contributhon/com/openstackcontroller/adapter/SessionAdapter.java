package openstack.contributhon.com.openstackcontroller.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;
import openstack.contributhon.com.openstackcontroller.MainActivity;
import openstack.contributhon.com.openstackcontroller.R;
import openstack.contributhon.com.openstackcontroller.SessionVO;
import openstack.contributhon.com.openstackcontroller.list.InstanceList;

public class SessionAdapter extends RealmBaseAdapter<SessionVO> implements ListAdapter {

    static SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
    MainActivity mList;

    private static class ViewHolder {
        TextView nameText;
        TextView hostText;
        TextView dateText;
        ImageButton moreBtn;
        ImageView moreImg;
    }

    public SessionAdapter(OrderedRealmCollection<SessionVO> realmResults, MainActivity list) {
        super(realmResults);
        mList = list;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.nameText = convertView.findViewById(R.id.row_title);
            viewHolder.hostText = convertView.findViewById(R.id.row_below);
            viewHolder.dateText = convertView.findViewById(R.id.row_option);
            viewHolder.moreImg = convertView.findViewById(R.id.row_img);
            viewHolder.moreBtn = convertView.findViewById(R.id.row_more);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if(adapterData != null){
            final SessionVO vo = adapterData.get(position);
            viewHolder.nameText.setText(vo.name);
            viewHolder.hostText.setText(vo.address);
            if(vo.date != null)
                viewHolder.dateText.setText("Last accessed : " + sdf.format(vo.date));
            viewHolder.moreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mList.itemDialog(position, viewHolder.moreImg);
                }
            });
        }

        return convertView;
    }
}
