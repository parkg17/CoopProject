package skku.alticastvux.data;

import java.util.ArrayList;

public class SearchMoreResult {
    private class result_s {
        private class site_s {
            public ArrayList<Store> list;
        }
        private site_s site;
    }
    private result_s result;
    public ArrayList<Store> getList() {
        return result.site.list;
    }
}
