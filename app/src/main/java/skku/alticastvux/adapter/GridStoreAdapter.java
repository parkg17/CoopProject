package skku.alticastvux.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import skku.alticastvux.R;
import skku.alticastvux.data.Store;
import skku.alticastvux.model.Menu;

public class GridStoreAdapter extends BaseAdapter {

    LayoutInflater inflater;
    ArrayList<Store> storeList;

    public GridStoreAdapter(Context context, ArrayList<Store> storeList) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.storeList = storeList;
    }

    @Override
    public int getCount() {
        return storeList.size();
    }

    @Override
    public Store getItem(int i) {
        return storeList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.grid_card, null);
        }
        TextView tv = (TextView) view.findViewById(R.id.grid_item_text);
        ImageView iv = (ImageView) view.findViewById(R.id.grid_item_iv);
        tv.setText(getItem(i).name);
        if (getItem(i).thumUrl != null)
            Picasso.get().load(getItem(i).thumUrl).into(iv);
        return view;
    }
}
