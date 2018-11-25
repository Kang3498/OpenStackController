package openstack.contributhon.com.openstackcontroller.list;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import openstack.contributhon.com.openstackcontroller.JsonConverter;
import openstack.contributhon.com.openstackcontroller.MakeBody;
import openstack.contributhon.com.openstackcontroller.R;
import openstack.contributhon.com.openstackcontroller.adapter.FlavorAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static openstack.contributhon.com.openstackcontroller.Config.*;

public class FlavorList extends MyList {

    private FlavorAdapter mAdapter;

    public static FlavorList newInstance() {
        return new FlavorList();
    }

    public void addDialog(final boolean isEdit, int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_addflavor, null);
        TextView title = view.findViewById(R.id.title);
        Button addButton = view.findViewById(R.id.btnAdd);
        final EditText nameEdit = view.findViewById(R.id.name);
        final EditText ramEdit = view.findViewById(R.id.ram);
        final EditText vcpusEdit = view.findViewById(R.id.vcpus);
        final EditText diskEdit = view.findViewById(R.id.disk);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<Void> call = mInterface.createFlavor(cToken, MakeBody.createFlavor(nameEdit.getText().toString(), ramEdit.getText().toString(), vcpusEdit.getText().toString(), diskEdit.getText().toString()));
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                            getList();
                            mDialog.dismiss();
                        } else {
                            Toast.makeText(getContext(), "Fail : " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.e(MY_TAG, t.getMessage());
                        Toast.makeText(getContext(), "Error!! : " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                        Call<Void> call = mInterface.deleteFlavor(cToken, mAdapter.getItem(pos).id);
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

    public void getList() {
        Call<JsonConverter> call = mInterface.getFlavorList(cToken);
        call.enqueue(new Callback<JsonConverter>() {
            @Override
            public void onResponse(Call<JsonConverter> call, Response<JsonConverter> response) {
                if (response.isSuccessful()) {
                    JsonConverter list = response.body();
                    mAdapter = new FlavorAdapter(getContext(), list.getFlavors(), FlavorList.this);
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
