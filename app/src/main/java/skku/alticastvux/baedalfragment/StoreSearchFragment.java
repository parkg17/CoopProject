package skku.alticastvux.baedalfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import skku.alticastvux.R;
import skku.alticastvux.adapter.GridMenuAdapter;
import skku.alticastvux.adapter.GridStoreAdapter;
import skku.alticastvux.data.SearchMoreResult;
import skku.alticastvux.data.Store;
import skku.alticastvux.util.FragmentStackV4;
import skku.alticastvux.util.Util;
import skku.alticastvux.voiceable.CommandListener;
import skku.alticastvux.voiceable.pattern.FindMenuPattern;
import skku.alticastvux.voiceable.pattern.FindStorePattern;
import skku.alticastvux.voiceable.pattern.VoiceablePattern;

public class StoreSearchFragment extends BaseBaedalFragment implements CommandListener {

    @BindView(R.id.gridview_menu)
    GridView grid_menu;

    View layout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        /*
        if(layout != null) {
            return layout;
        }
        */
        layout = inflater.inflate(R.layout.fragment_menusearch, null);
        ButterKnife.bind(this, layout);

        final String keyword = getArguments().getString("keyword");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpGet httpget = new HttpGet("https://m.map.naver.com/search2/searchMore.nhn?query=" + keyword
                            + "&sm=clk&centerCoord=37.3042611:126.9636783&page=1&displayCount=75");
                    httpget.addHeader("cookie",
                            "m_loc=c763aac26c6bd594f66cf584777779bd35d538beb151be081f06eff1245ee0029dd8d08037fb893a5ef47264d4ba762e9c726f5fcc7d36378512198d8061127e0cc58f2464b0e4eb308b4eeab88e44b6a0090d6d8d64e7719765d45718d2df50034c290ced85fb38f2fe2fd0a39b9847");
                    HttpResponse response = httpclient.execute(httpget);
                    HttpEntity entity = response.getEntity();
                    InputStream is = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null)
                        sb.append(line + "\n");
                    String resString = sb.toString();
                    is.close();
                    System.out.println(resString);
                    final SearchMoreResult sresult = new Gson().fromJson(resString, SearchMoreResult.class);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            setData(sresult.getList());
                        }
                    });
                } catch (Exception e) {

                }
            }
        }).start();

        return layout;
    }

    public void setData(final ArrayList<Store> storeList) {
        grid_menu.setAdapter(new GridStoreAdapter(getActivity(), storeList));
        grid_menu.setFocusableInTouchMode(true);
        grid_menu.setFocusable(true);
        grid_menu.requestFocus();
        grid_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Store store = storeList.get(i);
                StoreFragment fragment = new StoreFragment();
                Bundle args = new Bundle();
                args.putString("storeName", store.name);
                args.putString("storeID", store.id.substring(1));
                fragment.setArguments(args);
                FragmentStackV4.add(getFragmentManager(), R.id.layout_order, fragment);
            }

        });
        Util.setGridViewHeightBasedOnChildren(grid_menu);
    }

    @Override
    public void popBacked() {
        grid_menu.setFocusableInTouchMode(true);
        grid_menu.setFocusable(true);
        grid_menu.requestFocus();
    }

    @Override
    public boolean receiveCommand(VoiceablePattern pattern) {
        if (pattern instanceof FindStorePattern) {
            /*
            String storename = ((FindStorePattern) pattern).getStore();
            int index = 0;
            for (Store store : storeList) {
                if (storeList.get(index).name.equals(storename)) {
                    StoreFragment fragment = new StoreFragment();
                    Bundle args = new Bundle();
                    args.putString("storeName", store.name);
                    args.putString("storeID", store.id.substring(1));
                    fragment.setArguments(args);
                    FragmentStackV4.add(getFragmentManager(), R.id.layout_order, fragment);
                    return true;
                }
                index++;
            }
            */
        }
        return false;
    }
}

