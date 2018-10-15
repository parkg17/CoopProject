package com.example.gyeol.coopproject;

/**
 * Created by Park Gyeol on 2018-10-12.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    String sql;

    public DBHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        sql = "CREATE TABLE tableName ( _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " name TEXT, distance TEXT, info TEXT);";
        db.execSQL(sql);

        /* 기본 케이스들. API에서 받아오는거 구현하면 이 부분 고치면 됨. */
        /* 가게 이름, 거리(m단위), 가게 범주(일단 한식, 중식, 일식, 기타) 를 각각 표현 */
        /* 사진도 가게 범주에 따라서 나중에 바꿀 예정이고, 가게마다 메뉴에 대한 데이터 정보도 넣어야 됨 */

        db.execSQL("INSERT INTO tableName VALUES(NULL, '달콤매콤떡볶이', '220', '한식');");
        db.execSQL("INSERT INTO tableName VALUES(NULL, '낙곱새', '2300', '한식');");
        db.execSQL("INSERT INTO tableName VALUES(NULL, '장터김치찌개', '690', '한식');");
        db.execSQL("INSERT INTO tableName VALUES(NULL, '보영만두', '550', '한식');");
        db.execSQL("INSERT INTO tableName VALUES(NULL, '썬더치킨', '1150', '한식');");
        db.execSQL("INSERT INTO tableName VALUES(NULL, '네네치킨', '1854', '한식');");
        db.execSQL("INSERT INTO tableName VALUES(NULL, '포명천천', '422', '중식');");
        db.execSQL("INSERT INTO tableName VALUES(NULL, '수상한탕수육', '2534', '중식');");
        db.execSQL("INSERT INTO tableName VALUES(NULL, '만리향', '11231', '중식');");
        db.execSQL("INSERT INTO tableName VALUES(NULL, '이찌돈', '522', '일식');");
        db.execSQL("INSERT INTO tableName VALUES(NULL, '미소돈까스', '321', '일식');");
        db.execSQL("INSERT INTO tableName VALUES(NULL, '우야꼬', '190', '일식');");
        db.execSQL("INSERT INTO tableName VALUES(NULL, '스시&참치', '1123', '일식');");
        db.execSQL("INSERT INTO tableName VALUES(NULL, '베스킨라빈스', '2421', '기타');");
        db.execSQL("INSERT INTO tableName VALUES(NULL, '할리스커피', '3221', '기타');");
    }

    /* 값의 삽입이나 삭제로 인해 버전이 업그레이드 될 경우 실행하도록.. */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tableName");
        onCreate(db);
    }
}

