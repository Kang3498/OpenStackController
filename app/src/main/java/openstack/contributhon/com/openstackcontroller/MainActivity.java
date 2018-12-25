package openstack.contributhon.com.openstackcontroller;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.util.Calendar;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import okhttp3.ResponseBody;
import openstack.contributhon.com.openstackcontroller.adapter.SessionAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static openstack.contributhon.com.openstackcontroller.Config.*;

public class MainActivity extends AppCompatActivity {

    AlertDialog mUserinfoDialog;
    private Realm mRealm;
    private SessionAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userDialog(0);
            }
        });

        ListView listview = findViewById(R.id.list);
        final SwipeRefreshLayout mSwipeRefreshLayout = findViewById(R.id.swipe_layout);
        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        mRealm = Realm.getInstance(realmConfig);

        RealmResults<SessionVO> vos = mRealm.where(SessionVO.class).sort("date", Sort.DESCENDING).findAll();
        mAdapter = new SessionAdapter(vos, MainActivity.this);
        listview.setAdapter(mAdapter);
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                itemDialog(pos, null);
                return true;
            }
        });
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SessionVO item = mAdapter.getItem(position);
                checkConnection(item.address, item.domain, item.user, item.passwd, item.id);
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                mAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onDestroy() {
        mRealm.close();
        super.onDestroy();
    }

    public void itemDialog(final int pos, View v) {
        Context wrapper = new ContextThemeWrapper(this, R.style.PopupmenuStyle);
        PopupMenu p = new PopupMenu(wrapper, v);
        p.getMenuInflater().inflate(R.menu.menu_popup, p.getMenu());
        p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit:
                        userDialog(mAdapter.getItem(pos).id);
                        break;
                    case R.id.delete:
                        mRealm.beginTransaction();
                        SessionVO vo = mRealm.where(SessionVO.class)
                                .equalTo("id", mAdapter.getItem(pos).id)
                                .findFirst();
                        vo.deleteFromRealm();
                        mRealm.commitTransaction();
                        mAdapter.notifyDataSetChanged();
                        break;
                }
                return false;
            }
        });
        p.show();
    }

    public void userDialog(final int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.dialog_addsession, null);
        Button addButton = view.findViewById(R.id.btnAdd);

        final EditText nameEt = view.findViewById(R.id.name);
        final EditText addressEt = view.findViewById(R.id.address);
        final EditText domainEt = view.findViewById(R.id.domain);
        final EditText userEt = view.findViewById(R.id.user);
        final EditText passwordEt = view.findViewById(R.id.password);

        if (id > 0) {
            final SessionVO vo = mRealm.where(SessionVO.class)
                    .equalTo("id", id)
                    .findFirst();
            nameEt.setText(vo.name);
            addressEt.setText(vo.address);
            domainEt.setText(vo.domain);
            userEt.setText(vo.user);
            passwordEt.setText(vo.passwd);
        }

        addButton.setOnClickListener(new View.OnClickListener() {
            private String name, address, domain, user, password;

            @Override
            public void onClick(View v) {
                name = nameEt.getText().toString();
                address = addressEt.getText().toString();
                domain = domainEt.getText().toString();
                user = userEt.getText().toString();
                password = passwordEt.getText().toString();

                if(name.isEmpty() || address.isEmpty() || domain.isEmpty() || user.isEmpty() || password.isEmpty())
                    return;

                address = checkURL(address);

                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        if (id > 0) {
                            final SessionVO vo = mRealm.where(SessionVO.class)
                                    .equalTo("id", id)
                                    .findFirst();
                            vo.setAll(id, name, address, domain, user, password);
                            mRealm.insertOrUpdate(vo);
                        } else {
                            SessionVO vo = realm.createObject(SessionVO.class);
                            Number currentIdNum = realm.where(SessionVO.class).max("id");
                            vo.setAll((currentIdNum == null) ? 1 : currentIdNum.intValue() + 1,
                                    name, address, domain, user, password);
                        }
                    }
                });
                mUserinfoDialog.dismiss();
            }
        });
        builder.setView(view);
        mUserinfoDialog = builder.create();
        mUserinfoDialog.show();
    }

    private String checkURL(String s) {
        String[] sarr = s.split("/");
        if (s.startsWith("http://"))
            return "http://" + sarr[2];
        else
            return "http://" + sarr[0];
    }

    private void checkConnection(final String host, final String domain, final String user, final String password, final int id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(host)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        IRestApi keystone = retrofit.create(IRestApi.class);
        Call<ResponseBody> call = keystone.getToken(MakeBody.getToken(domain, user, password));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    cToken = response.headers().get("X-Subject-Token");
                    cUser = user;
                    cHost = host;
                    mRealm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            final SessionVO vo = mRealm.where(SessionVO.class)
                                    .equalTo("id", id)
                                    .findFirst();
                            vo.setDate(Calendar.getInstance().getTime());
                            mRealm.insertOrUpdate(vo);
                        }
                    });
                    Intent intent = new Intent(MainActivity.this,NavigationActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } else
                    Toast.makeText(getBaseContext(), "Connect Error!!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getBaseContext(), "Connect Error!!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
