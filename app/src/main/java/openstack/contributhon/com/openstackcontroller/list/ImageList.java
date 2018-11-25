package openstack.contributhon.com.openstackcontroller.list;


import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import openstack.contributhon.com.openstackcontroller.JsonConverter;
import openstack.contributhon.com.openstackcontroller.MakeBody;
import openstack.contributhon.com.openstackcontroller.adapter.ImageAdapter;
import openstack.contributhon.com.openstackcontroller.R;
import openstack.contributhon.com.openstackcontroller.model.ImageVO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static openstack.contributhon.com.openstackcontroller.Config.MY_TAG;
import static openstack.contributhon.com.openstackcontroller.Config.cDetailId;
import static openstack.contributhon.com.openstackcontroller.Config.cToken;

public class ImageList extends MyList {

    ImageAdapter mAdapter;

    public static ImageList newInstance() {
        return new ImageList();
    }

    public void addDialog(final boolean isEdit, int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_imageinfo, null);
        TextView title = view.findViewById(R.id.title);
        Button addButton = view.findViewById(R.id.btnAdd);

        final EditText nameEdit = view.findViewById(R.id.name);
        final Spinner containerEdit = view.findViewById(R.id.container);
        final Spinner diskEdit = view.findViewById(R.id.disk);

        mId ="";
        if(isEdit){
            ImageVO vo = mAdapter.getItem(pos);
            nameEdit.setText(vo.name);
            int container = 0;
            for(String t : view.getResources().getStringArray(R.array.image_container)){
                if(vo.container_format.equals(t))
                    break;
                container++;
            }
            containerEdit.setSelection(container,true);
            container = 0;
            for(String t : view.getResources().getStringArray(R.array.image_disk)){
                if(vo.disk_format.equals(t))
                    break;
                container++;
            }
            diskEdit.setSelection(container, true);
            mId = vo.id;
            addButton.setText("Edit");
            title.setText("Edit Image");
        } else {
            addButton.setText("Add");
            title.setText("Add Image");
        }

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<Void> call = null;
                if(isEdit){
                    call = mInterface.editImage(cToken, MakeBody.editImage(nameEdit.getText().toString(), containerEdit.getSelectedItem().toString(), diskEdit.getSelectedItem().toString()), mId);
                }else{
                    call = mInterface.createImage(cToken, MakeBody.createImage(nameEdit.getText().toString(), containerEdit.getSelectedItem().toString(), diskEdit.getSelectedItem().toString()));
                }

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                            getList();
                            mDialog.dismiss();
                        } else {
                            Toast.makeText(getContext(), "Fail", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e(MY_TAG, t.getMessage());
                        Toast.makeText(getContext(), "Connect Error!! : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setView(view);
        mDialog = builder.create();
        mDialog.show();
    }

    public void itemDialog(final int pos, View v) {
        Context wrapper = new ContextThemeWrapper(getActivity(), R.style.PopupmenuStyle);
        PopupMenu p = new PopupMenu(wrapper, v);
        p.getMenuInflater().inflate(R.menu.menu_popup, p.getMenu());
        p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit:
                        addDialog(true, pos);
                        break;
                    case R.id.delete:
                        Call<Void> call = mInterface.deleteImage(cToken, mAdapter.getItem(pos).id);
                        call.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(getContext(), "Delete Success", Toast.LENGTH_SHORT).show();
                                    getList();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Log.e(MY_TAG, t.getMessage());
                                Toast.makeText(getContext(), "Connect Error!! : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                }
                return false;
            }
        });
        p.show();
    }

    public void setPostiion(int position){
        cDetailId = mAdapter.getItem(position).id;
    }

    public void getList(){
        Call<JsonConverter> call = mInterface.getImageList(cToken);
        call.enqueue(new Callback<JsonConverter>() {
            @Override
            public void onResponse(Call<JsonConverter> call, Response<JsonConverter> response) {
                if (response.isSuccessful()) {
                    JsonConverter list = response.body();
                    mAdapter = new ImageAdapter(getContext(), list.getImages(), ImageList.this);
                    mListView.setAdapter(mAdapter);
                } else
                    Toast.makeText(getContext(), "Connect Error!!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<JsonConverter> call, Throwable t) {
                Toast.makeText(getContext(), "Connect Error!! : " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
