package com.example.gyeol.coopproject;

/**
 * Created by Park Gyeol on 2018-10-12.
 */

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class Specific_Case extends Activity {

    ListView list;
    EditText editText, editText2 ,editText3, editText4;
    DBHelper dbHelper;
    SQLiteDatabase db;
    String sql;
    Cursor cursor;

    final static String dbName = "person.db";
    final static int dbVersion = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.specific_case);
        editText = (EditText) findViewById(R.id.editText);
        editText2 = (EditText) findViewById(R.id.editText2);
        editText3 = (EditText) findViewById(R.id.editText3);
        editText4 = (EditText) findViewById(R.id.editText4);

        list = (ListView) findViewById(R.id.list);
        dbHelper = new DBHelper(this, dbName, null, dbVersion);

        selectDB();

        /* 해당 index에 해당하는 DB 클릭시 동작 수행하는 부분 */
        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                cursor.moveToPosition(position);
                String str = cursor.getString(cursor.getColumnIndex("name"));
                Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
            }
        });

    }

    /* DB에 값을 넣는 과정 */
    public void insert(View v) {
        String name = editText.getText().toString();
        Integer distance = Integer.parseInt(editText2.getText().toString());
        String info = editText3.getText().toString();
        db.execSQL("INSERT INTO tableName VALUES (null, '" + name + "', '" + distance + "','" + info + "');");

        Toast.makeText(getApplicationContext(), "추가 성공", Toast.LENGTH_SHORT).show();

        editText.setText("");
        editText2.setText("");
        editText3.setText("");
        selectDB();
    }

    /* DB에서 값을 삭제하는 과정, 일단 편의를 위해서 가게이름만 입력하고 삭제버튼 누르면 삭제하도록 구현 */
    public void delete(View v) {
        String name = editText.getText().toString();
        db.execSQL("DELETE FROM tableName WHERE name = '" + name + "';");
        Toast.makeText(getApplicationContext(), "삭제 성공", Toast.LENGTH_SHORT).show();
        editText.setText("");
        editText2.setText("");
        editText3.setText("");
        selectDB();
    }

    /* 검색 기능 - 가게 범주에 따라(한식, 중식, 일식, 기타, ...) */
    public void query(View v) {
        String info_condition = editText4.getText().toString();
        String sqlSelect = "SELECT * " + " from " + " tableName " + " where info = ?";
        String[] args = {info_condition};
        Cursor cursor = null;
        cursor = db.rawQuery(sqlSelect, args);

        if(cursor.getCount() > 0) {
            startManagingCursor(cursor);
            DBAdapter dbAdapter = new DBAdapter(this, cursor);
            list.setAdapter(dbAdapter);
        }
    }

    /* SelectDB - DB의 데이터를 가져오는 함수. 쿼리를 통해 전부 다 가져옴 */
    private void selectDB() {
        db = dbHelper.getWritableDatabase();
        sql = "SELECT * FROM tableName;";

        cursor = db.rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            startManagingCursor(cursor);
            DBAdapter dbAdapter = new DBAdapter(this, cursor);
            list.setAdapter(dbAdapter);
        }
    }
}
