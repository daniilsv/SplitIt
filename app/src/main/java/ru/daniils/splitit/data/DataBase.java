package ru.daniils.splitit.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class DataBase {

    private static final String LOG_TAG = "DataBase";
    private SQLiteDatabase db;

    public DataBase(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    //Добавляет элемент в таблицу
    public long insert(String table, Map sm) {
        ContentValues cv = new ContentValues();
        for (Object o : sm.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            if (String.valueOf(pair.getValue()).length() > 0)
                cv.put((String) pair.getKey(), String.valueOf(pair.getValue()));
        }
        return db.insert(table, null, cv);
    }

    //Обновляет элемент таблицы по ID
    public long update(String table, int id, Map sm) {
        ContentValues cv = new ContentValues();
        for (Object o : sm.entrySet()) {
            Map.Entry pair = (Map.Entry) o;
            if (String.valueOf(pair.getValue()).length() > 0)
                cv.put((String) pair.getKey(), String.valueOf(pair.getValue()));
        }
        return db.update(table, cv, "id=" + id, null);
    }

    //Добавляет или обновляет элемент в таблицу
    public void insertOrUpdate(String table, String where, HashMap<String, String> map) {
        Cursor bd_item = query(table, null, where, null, null, null, null);
        if (bd_item != null && bd_item.moveToFirst()) {//Элемент в таблице есть - обновляем данные
            update(table, bd_item.getInt(bd_item.getColumnIndex("id")), map);
            bd_item.close();
        } else {//Элемента в таблиц нет - добавляем его
            insert(table, map);
        }
    }

    //Удаляет по запросу элемент из таблицы
    public int delete(String table, String whereClause, String[] whereArgs) {
        return db.delete(table, whereClause, whereArgs);
    }

    //Поучает Cursor по запросу из таблицы
    @Nullable
    public Cursor query(String table, String[] columns, String selection,
                        String[] selectionArgs, String groupBy, String having,
                        String orderBy) {
        if (orderBy == null)
            orderBy = "id DESC";
        return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    //Получает кол-во элементов в таблице по запросу
    public int getCount(String table, String where) {
        Cursor c = db.query(table, null, where, null, null, null, null);
        int ret = c.getCount();
        c.close();
        return ret;
    }

    //Удаляет элемент по ID из таблицы
    public int removeById(String table, int id) {
        return db.delete(table, "id = " + id, null);
    }

    public void close() {
        try {
            db.endTransaction();
        } catch (IllegalStateException ignored) {
            db.close();
        }
        db = null;
    }

    private class DBHelper extends SQLiteOpenHelper {

        DBHelper(Context context) {
            super(context, "split_it_db", null, 1);
        }

        //При создании базы данных
        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d(LOG_TAG, "--- onCreate database ---");

            Log.d(LOG_TAG, "--- Create com_split_it_rooms");
            db.execSQL("CREATE TABLE com_splitit_rooms (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "original_id INTEGER NOT NULL," +
                    "admin_user_id INTEGER NOT NULL," +
                    "is_public TEXT," +
                    "title TEXT DEFAULT NULL," +
                    "description TEXT," +
                    "price REAL NOT NULL," +
                    "deposit REAL NOT NULL," +
                    "keycode TEXT DEFAULT NULL," +
                    "term TEXT DEFAULT NULL);" +
                    "CREATE INDEX IF NOT EXISTS uniq ON com_split_it_rooms (id, original_id);"
            );
            ////////////////////
            Log.d(LOG_TAG, "--- Create com_split_it_rooms_binds");
            db.execSQL("CREATE TABLE com_splitit_rooms_binds (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "original_id INTEGER NOT NULL," +
                    "user_id INTEGER NOT NULL," +
                    "deposit REAL NOT NULL);" +
                    "CREATE INDEX IF NOT EXISTS uniq ON com_split_it_categories (id, original_id);"
            );

            ///////////////////
            Log.d(LOG_TAG, "--- Create com_split_it_users");
            db.execSQL("CREATE TABLE com_splitit_users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "original_id INTEGER NOT NULL," +
                    "email TEXT NOT NULL," +
                    "password TEXT NOT NULL," +
                    "salt TEXT NOT NULL," +
                    "name TEXT NOT NULL," +
                    "token TEXT NOT NULL);" +
                    "CREATE INDEX IF NOT EXISTS uniq ON com_split_it_users (id, original_id);"
            );
        }

        //При обновлении приложения
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS split_it_db");
            onCreate(db);
        }
    }
}