package skku.alticastvux.baedalfragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import skku.alticastvux.R;
import skku.alticastvux.adapter.GridMenuAdapter;
import skku.alticastvux.model.Menu;
import skku.alticastvux.util.FragmentStackV4;
import skku.alticastvux.voiceable.CommandListener;
import skku.alticastvux.voiceable.pattern.FindMenuPattern;
import skku.alticastvux.voiceable.pattern.VoiceablePattern;

public class MenuSearchFragment extends BaseBaedalFragment implements CommandListener {

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
        final ArrayList<Menu> menuList = new ArrayList<>();
        {
            Menu m = new Menu();
            m.setImgurl("http://img.khan.co.kr/news/2018/04/19/l_2018042001002453300196681.jpg");
            m.setName("치킨");
            menuList.add(m);
        }
        {
            Menu m = new Menu();
            m.setImgurl("https://cdn.dominos.co.kr/admin/upload/goods/20180917_8VOpWOKk.jpg");
            m.setName("피자");
            menuList.add(m);
        }
        {
            Menu m = new Menu();
            m.setImgurl("https://upload.wikimedia.org/wikipedia/commons/thumb/6/6f/Jajangmyeon_by_stu_spivack.jpg/240px-Jajangmyeon_by_stu_spivack.jpg");
            m.setName("중식");
            menuList.add(m);
        }
        {
            Menu m = new Menu();
            m.setImgurl("http://jpninfo.com/wp-content/uploads/2015/09/sushi-variety.jpeg");
            m.setName("일식");
            menuList.add(m);
        }
        {
            Menu m = new Menu();
            m.setImgurl("http://img.daily.co.kr/@files/www.daily.co.kr/content/food/2017/20170310/8383e1ff029938ebc4aa15d15badda0d.jpg");
            m.setName("족발");
            menuList.add(m);
        }
        {
            Menu m = new Menu();
            m.setImgurl("https://upload.wikimedia.org/wikipedia/commons/thumb/4/4d/Tteokbokki.JPG/220px-Tteokbokki.JPG");
            m.setName("분식");
            menuList.add(m);
        }

        grid_menu.setAdapter(new GridMenuAdapter(getActivity(), menuList));
        grid_menu.setFocusableInTouchMode(true);
        grid_menu.setFocusable(true);
        grid_menu.requestFocus();
        grid_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Menu m = menuList.get(i);
                String keyword = m.getName();

                StoreSearchFragment fragment = new StoreSearchFragment();
                Bundle args = new Bundle();
                args.putString("keyword", keyword);
                fragment.setArguments(args);
                FragmentStackV4.add(getFragmentManager(), R.id.layout_order, fragment);
            }
        });
        return layout;
    }

    @Override
    public void popBacked() {
        grid_menu.setFocusableInTouchMode(true);
        grid_menu.setFocusable(true);
        grid_menu.requestFocus();
    }

    @Override
    public boolean receiveCommand(VoiceablePattern pattern) {
        if (pattern instanceof FindMenuPattern) {
            String keyword = ((FindMenuPattern) pattern).getMenu();

            StoreSearchFragment fragment = new StoreSearchFragment();
            Bundle args = new Bundle();
            args.putString("keyword", keyword);
            fragment.setArguments(args);
            FragmentStackV4.add(getFragmentManager(), R.id.layout_order, fragment);
        }
        return false;
    }
}
