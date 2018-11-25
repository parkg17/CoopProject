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
import skku.alticastvux.model.Menu;

public class GridMenuAdapter extends BaseAdapter {

    LayoutInflater inflater;
    ArrayList<Menu> menuList;

    public GridMenuAdapter(Context context, ArrayList<Menu> menuList) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.menuList = menuList;
    }

    @Override
    public int getCount() {
        return menuList.size();
    }

    @Override
    public Menu getItem(int i) {
        return menuList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            view = inflater.inflate(R.layout.grid_card, null);
        }
        TextView tv = (TextView) view.findViewById(R.id.grid_item_text);
        ImageView iv = (ImageView) view.findViewById(R.id.grid_item_iv);
        tv.setText(getItem(i).getName());
        Picasso.get().load(getItem(i).getImgurl()).into(iv);
        return view;
    }
}
