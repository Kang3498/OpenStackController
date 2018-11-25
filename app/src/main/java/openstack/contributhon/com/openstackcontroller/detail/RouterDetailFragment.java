package openstack.contributhon.com.openstackcontroller.detail;

import android.support.v4.util.Pair;

import openstack.contributhon.com.openstackcontroller.model.RouterVO;

import static android.view.View.GONE;

public class RouterDetailFragment extends DetailFragment {

    public static RouterDetailFragment newInstance() {
        return new RouterDetailFragment();
    }

    public void setAll(RouterVO router){
        mLoadingBar.setVisibility(GONE);
        mItemList.clear();
        mItemList.add(Pair.create("Name", "Value"));
        mItemList.add(Pair.create("name", router.name));
        mItemList.add(Pair.create("status", router.status));
        mItemList.add(Pair.create("id", router.id));
        mItemList.add(Pair.create("flavor_id", router.flavor_id));
        mItemList.add(Pair.create("project_id", router.project_id));
        mItemList.add(Pair.create("tenant_id", router.tenant_id));
        mItemList.add(Pair.create("service_type_id", router.service_type_id));
        mItemList.add(Pair.create("admin_state_up", router.admin_state_up));
        if(router.routes.size() > 0) {
            mItemList.add(Pair.create("routes:destination", router.routes.get(0).destination));
            mItemList.add(Pair.create("routes:nexthoop", router.routes.get(0).nexthop));
        }
        mItemList.add(Pair.create("revision_number", router.revision_number));
        mItemList.add(Pair.create("ha", router.ha));
        mItemList.add(Pair.create("description", router.description));
        mItemList.add(Pair.create("created_at", router.created_at));
        mItemList.add(Pair.create("updated_at", router.updated_at));
        mDetailAdapter.notifyDataSetChanged();
    }
}
