package skku.alticastvux.presenter;

import org.apache.commons.collections4.map.HashedMap;

/**
 * Created by woorim on 2018. 7. 25..
 */

public class CardInfo {
    private int row;
    private int col;
    private int type;

    public Object getObject(String key) {
        return objectMap.get(key);
    }

    public void putObject(String key, Object o) {
        objectMap.put(key,o);
    }

    private HashedMap<String, Object> objectMap;

    public CardInfo(int row, int col, int type) {
        this.row = row;
        this.col = col;
        this.type = type;
        objectMap = new HashedMap<>();
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
