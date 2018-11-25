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
import skku.alticastvux.data.StoreMenu;

public class GridStoreMenuAdapter extends BaseAdapter {

    LayoutInflater inflater;
    ArrayList<StoreMenu> storeMenuList;

    public GridStoreMenuAdapter(Context context, ArrayList<StoreMenu> storeMenuList) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.storeMenuList = storeMenuList;
    }

    @Override
    public int getCount() {
        return storeMenuList.size();
    }

    @Override
    public StoreMenu getItem(int i) {
        return storeMenuList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.list_card_menu, null);
        }
        TextView tv = (TextView) view.findViewById(R.id.grid_item_text);
        TextView tv_price = (TextView) view.findViewById(R.id.grid_item_price);
        tv_price.setText(getItem(i).price);
        TextView tv_desc = (TextView) view.findViewById(R.id.grid_item_text_desc);
        tv_desc.setText(getItem(i).description);
        ImageView iv = (ImageView) view.findViewById(R.id.grid_item_iv);
        tv.setText(getItem(i).name);
        if (getItem(i).thumUrl != null)
            Picasso.get().load(getItem(i).thumUrl).into(iv);
        return view;
    }
}
