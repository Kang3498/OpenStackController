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
import okhttp3.ResponseBody;
import openstack.contributhon.com.openstackcontroller.JsonConverter;
import openstack.contributhon.com.openstackcontroller.MakeBody;
import openstack.contributhon.com.openstackcontroller.R;
import openstack.contributhon.com.openstackcontroller.adapter.RouterAdapter;
import openstack.contributhon.com.openstackcontroller.model.RouterVO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static openstack.contributhon.com.openstackcontroller.Config.*;

public class RouterList extends MyList {

    private RouterAdapter mAdapter;

    public static RouterList newInstance() {
        return new RouterList();
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
        View view = getLayoutInflater().inflate(R.layout.dialog_addrouter, null);
        TextView title = view.findViewById(R.id.title);
        Button addButton = view.findViewById(R.id.btnAdd);
        final EditText nameEdit = view.findViewById(R.id.name);
        final Spinner adminSpinner = view.findViewById(R.id.admin_state_up);
        if(isEdit){
            RouterVO vo = mAdapter.getItem(pos);
            nameEdit.setText(vo.name);
            int admin_state = 1;
            if(vo.admin_state_up.equals("true"))
                admin_state = 0;
            adminSpinner.setSelection(admin_state,true);
            mId = vo.id;
            addButton.setText("Edit");
            title.setText("Edit Router");
        }else {
            addButton.setText("Add");
            title.setText("Add Router");
        }

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<ResponseBody> call = null;
                if(isEdit){
                    call = mInterface.editRouter(cToken, MakeBody.createRouter(nameEdit.getText().toString(), adminSpinner.getSelectedItem().toString()),mId);
                }else{
                    call = mInterface.createRouter(cToken, MakeBody.createRouter(nameEdit.getText().toString(), adminSpinner.getSelectedItem().toString()));
                }

                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                            getList();
                            mDialog.dismiss();
                        } else {
                            Toast.makeText(getContext(), "Fail : " + getError(mRetrofit, response.errorBody(), 2), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e(MY_TAG, t.getMessage());
                        Toast.makeText(getContext(), "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                        Call<Void> call = mInterface.deleteRouter(cToken, mAdapter.getItem(pos).id);
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
        Call<JsonConverter> call = mInterface.getRouterList(cToken);
        call.enqueue(new Callback<JsonConverter>() {
            @Override
            public void onResponse(Call<JsonConverter> call, Response<JsonConverter> response) {
                if (response.isSuccessful()) {
                    JsonConverter list = response.body();
                    mAdapter = new RouterAdapter(getContext(), list.getRouters(), RouterList.this);
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
