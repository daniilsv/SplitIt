package ru.daniils.splitit.data.components;

import android.content.Context;
import android.database.Cursor;
import android.view.View;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.daniils.splitit.data.DataBase;
import ru.daniils.splitit.data.network.PostQuery;
import ru.daniils.splitit.utils.Callback;

public abstract class Model {
    public final String table;
    public int localId;
    public int originalId;
    private Context mContext = null;
    private String mWhere = null;
    private String mOrder = null;
    private String mMethod = null;

    public Model(String table) {
        this.table = table;
        this.localId = 0;
        this.originalId = 0;
    }

    protected void setContext(Context context) {
        mContext = context;
    }

    public void queryItemsFromSite(final HashMap<String, String> additional, Callback.ISuccessError callback) {
        PostQuery task = new PostQuery(mContext, "get", "rooms") {
            @Override
            public void onSuccess() {
                try {
                    Model item;
                    DataBase db = new DataBase(mContext);
                    if (mResponse.opt("count") == null) {
                        item = parseItemByJson(mResponse.getJSONObject("item").toString());
                        HashMap map = item.getHashMap();
                        db.insertOrUpdate(table, "original_id=" + item.originalId, map);
                    } else {
                        JSONArray items = mResponse.getJSONArray("items");
                        for (int i = 0; i < items.length(); i++) {
                            item = parseItemByJson(items.get(i).toString());
                            HashMap map = item.getHashMap();
                            db.insertOrUpdate(table, "original_id=" + item.originalId, map);
                        }
                    }
                    db.close();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError() {

            }
        };
        if (additional != null)
            for (Map.Entry o : additional.entrySet()) {
                task.put((String) o.getKey(), (String) o.getValue());
            }
        if (callback != null)
            task.setCallback(callback);
        task.execute();
    }

    public ArrayList<Model> getItems() {
        ArrayList<Model> items = new ArrayList<>();
        DataBase db = new DataBase(mContext);
        if (mOrder == null) orderBy("id", "DESC");
        Cursor cursor = db.query(table, null, mWhere, null, null, null, mOrder);

        if (cursor == null || !cursor.moveToFirst()) return items;
        try {
            do {
                Model item = parseCursorFromDB(cursor);
                item.setContext(mContext);
                items.add(item);
            } while (cursor.moveToNext());
        } finally {
            cursor.close();
            db.close();
            clearDataBase();
        }
        return items;
    }

    public Model getItemById(int localId) {
        return filter("id=" + localId).getItemFiltered();
    }

    public Model getItemFiltered() {
        Model ret = null;
        DataBase db = new DataBase(mContext);
        Cursor cursor = db.query(table, null, mWhere, null, null, null, null);

        if (cursor == null || !cursor.moveToFirst()) return null;
        try {
            ret = parseCursorFromDB(cursor);
            ret.setContext(mContext);
        } finally {
            cursor.close();
            db.close();
            clearDataBase();
        }
        return ret;
    }

    public void deleteItem(int localId) {

    }

    public Model filter(String where) {
        mWhere = where;
        return this;
    }

    public Model orderBy(String key, String order) {
        mOrder = "`" + key + "` " + order;
        return this;
    }

    public void clearDataBase() {
        mWhere = null;
        mOrder = null;
    }

    public abstract HashMap getHashMap();

    public Model parseItemByJson(String json) {
        try {
            Class dataClass = Class.forName(this.getClass().getName() + "$Data");
            Data data = (Data) new ObjectMapper().readValue(json, dataClass);
            return data.item;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public abstract Model parseCursorFromDB(Cursor cursor);

    public abstract void fillViewForList(View itemView);

    protected static abstract class Data {
        @JsonIgnore
        public Model item;
        @JsonIgnore
        protected Cursor cursor;

        protected final boolean cursorGetBoolean(String column) {
            return cursor.getInt(cursor.getColumnIndex(column)) == 1;
        }

        protected final int cursorGetInt(String column) {
            return cursor.getInt(cursor.getColumnIndex(column));
        }

        protected final String cursorGetString(String column) {
            return cursor.getString(cursor.getColumnIndex(column));
        }

        protected final double cursorGetDouble(String column) {
            return cursor.getDouble(cursor.getColumnIndex(column));
        }

        protected abstract void setVarByItem(String name);

        protected abstract void setItemVar(String name, Object value);
    }
}
