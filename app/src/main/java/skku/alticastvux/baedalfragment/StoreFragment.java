package skku.alticastvux.baedalfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import skku.alticastvux.R;
import skku.alticastvux.activity.PlaybackActivity;
import skku.alticastvux.adapter.GridStoreAdapter;
import skku.alticastvux.adapter.GridStoreMenuAdapter;
import skku.alticastvux.data.Store;
import skku.alticastvux.data.StoreMenu;
import skku.alticastvux.util.ExpandableGridView;
import skku.alticastvux.voiceable.CommandListener;
import skku.alticastvux.voiceable.pattern.FindFoodPattern;
import skku.alticastvux.voiceable.pattern.VoiceablePattern;

public class StoreFragment extends BaseBaedalFragment implements CommandListener {

    @BindView(R.id.gridview_menu)
    ExpandableGridView grid_menu;

    @BindView(R.id.imageview_store)
    ImageView imageview_store;

    @BindView(R.id.tv_desc)
    TextView tv_desc;

    @BindView(R.id.tv_storename)
    TextView tv_storename;

    View layout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /*
        if(layout != null) {
            return layout;
        }
        */
        layout = inflater.inflate(R.layout.fragment_store, null);
        ButterKnife.bind(this, layout);
        grid_menu.setExpanded(true);
        final String storeName = getArguments().getString("storeName");
        tv_storename.setText(storeName);
        final String storeID = getArguments().getString("storeID");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect("https://m.store.naver.com/restaurants/"+storeID+"/tabs/menus/list").get();
                    final Elements metadesc = doc.select("meta[property=og:description]");
                    final Elements metaimage = doc.select("meta[property=og:image]");

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setImgDesc(metaimage.attr("content"), metadesc.attr("content"));
                        }
                    });

                    Elements ul = doc.select("div.list_area > ul");
                    Elements li = ul.select("a");
                    final ArrayList<StoreMenu> storeMenuList = new ArrayList<>();
                    for (int i = 0; i < li.size(); i++) {
                        StoreMenu m = new StoreMenu();
                        m.name = li.get(i).select("span > span").text();
                        m.price = li.get(i).select("div.price").text();
                        m.description = li.get(i).select("div.txt").text();
                        m.thumUrl = li.get(i).select("img").attr("src");
                        storeMenuList.add(m);
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setData(storeMenuList);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return layout;
    }

    public void setImgDesc(String img, String desc) {
        tv_desc.setText(desc);
        Picasso.get().load(img).into(imageview_store);
    }

    public void setData(final ArrayList<StoreMenu> storeMenuList) {
        grid_menu.setAdapter(new GridStoreMenuAdapter(getActivity(), storeMenuList));
        grid_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // add item to checkout list
                Toast.makeText(getActivity(), "add item", 0).show();;
                ((PlaybackActivity)getActivity()).addItemToCheckoutList(storeMenuList.get(i));
            }
        });
        imageview_store.setFocusableInTouchMode(true);
        imageview_store.setFocusable(true);
        imageview_store.requestFocus();
    }

    @Override
    public void popBacked() {
        imageview_store.setFocusableInTouchMode(true);
        imageview_store.setFocusable(true);
        imageview_store.requestFocus();
    }

    @Override
    public boolean receiveCommand(VoiceablePattern pattern) {
        if (pattern instanceof FindFoodPattern) {
            /*
            String foodname = ((FindFoodPattern) pattern).getFood();
            int index = 0;
            for (Store store : storeMenuList) {
                if (storeMenuList.get(index).name.equals(foodname)) {
                    ((PlaybackActivity) getActivity()).addItemToCheckoutList(storeMenuList.get(index));
                    return true;
                }
                index++;
            }
            */
        }
        return false;
    }
}
