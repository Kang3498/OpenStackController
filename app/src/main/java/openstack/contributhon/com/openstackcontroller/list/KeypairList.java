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
import android.widget.TextView;
import android.widget.Toast;
import openstack.contributhon.com.openstackcontroller.JsonConverter;
import openstack.contributhon.com.openstackcontroller.MakeBody;
import openstack.contributhon.com.openstackcontroller.R;
import openstack.contributhon.com.openstackcontroller.adapter.KeypairAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static openstack.contributhon.com.openstackcontroller.Config.*;

public class KeypairList extends MyList {

    private KeypairAdapter mAdapter;

    public static KeypairList newInstance() {
        return new KeypairList();
    }

    public void addDialog(boolean isEdit, int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_addkeypair, null);
        Button addButton = view.findViewById(R.id.btnAdd);
        final EditText nameEdit = view.findViewById(R.id.name);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<Void> call = mInterface.createKeypair(cToken, MakeBody.createKeypair(nameEdit.getText().toString()));
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Create Success", Toast.LENGTH_SHORT).show();
                            getList();
                            mDialog.dismiss();
                        } else {
                            Toast.makeText(getContext(), "Create Fail : " + response.code(), Toast.LENGTH_SHORT).show();
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
        p.getMenu().findItem(R.id.edit).setVisible(false);
        p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete:
                        Call<Void> call = mInterface.deleteKeypair(cToken, mAdapter.getItem(pos).keypair.name);
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
        cDetailId = mAdapter.getItem(position).keypair.name;
    }

    public void getList() {
        Call<JsonConverter> call = mInterface.getKeypairList(cToken);
        call.enqueue(new Callback<JsonConverter>() {
            @Override
            public void onResponse(Call<JsonConverter> call, Response<JsonConverter> response) {
                if (response.isSuccessful()) {
                    JsonConverter list = response.body();
                    mAdapter = new KeypairAdapter(getContext(), list.getKeypairs(), KeypairList.this);
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
