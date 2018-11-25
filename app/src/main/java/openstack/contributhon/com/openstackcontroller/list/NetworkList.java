package openstack.contributhon.com.openstackcontroller.list;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import openstack.contributhon.com.openstackcontroller.JsonConverter;
import openstack.contributhon.com.openstackcontroller.MakeBody;
import openstack.contributhon.com.openstackcontroller.R;
import openstack.contributhon.com.openstackcontroller.adapter.NetworkAdapter;
import openstack.contributhon.com.openstackcontroller.model.NetworkVO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static openstack.contributhon.com.openstackcontroller.Config.*;

public class NetworkList extends MyList {

    private NetworkAdapter mAdapter;

    public static NetworkList newInstance() {
        return new NetworkList();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        String[] host = cHost.split(":");
        host[1] = "http:" + host[1] + ":9696";
        mHost = host[1];
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void addDialog(final boolean isEdit, int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_addnetwork, null);
        TextView title = view.findViewById(R.id.title);
        Button addButton = view.findViewById(R.id.btnAdd);
        final EditText nameEdit = view.findViewById(R.id.name);
        final Spinner adminSpinner = view.findViewById(R.id.admin_state_up);
        final EditText mtuedit = view.findViewById(R.id.mtu);

        if(isEdit){
            NetworkVO vo = mAdapter.getItem(pos);
            nameEdit.setText(vo.name);
            mtuedit.setText(vo.mtu);
            int admin_state = 1;
            if(vo.admin_state_up.equals("true"))
                admin_state = 0;
            adminSpinner.setSelection(admin_state,true);
            mId = vo.id;
            addButton.setText("Edit");
            title.setText("Edit Network");
        }
        else {
            addButton.setText("Add");
            title.setText("Add Network");
        }

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<Void> call = null;
                if(isEdit){
                    call = mInterface.editNetwork(cToken, MakeBody.createNetwork(nameEdit.getText().toString(), adminSpinner.getSelectedItem().toString(), mtuedit.getText().toString()), mId);
                }else{
                    call = mInterface.createNetwork(cToken, MakeBody.createNetwork(nameEdit.getText().toString(), adminSpinner.getSelectedItem().toString(), mtuedit.getText().toString()));
                }

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                            getList();
                            mDialog.dismiss();
                        } else {
                            Toast.makeText(getContext(), "Fail : " + getError(mRetrofit, response.errorBody(), 2), Toast.LENGTH_SHORT).show();
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
                        Call<Void> call = mInterface.deleteNetwork(cToken, mAdapter.getItem(pos).id);
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

    @Override
    public void getList(){
        Call<JsonConverter> call = mInterface.getNetworkList(cToken);
        call.enqueue(new Callback<JsonConverter>() {
            @Override
            public void onResponse(Call<JsonConverter> call, Response<JsonConverter> response) {
                if (response.isSuccessful()) {
                    JsonConverter list = response.body();
                    mAdapter = new NetworkAdapter(getContext(), list.getNetworks(), NetworkList.this);
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
