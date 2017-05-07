package ru.daniils.splitit.data.components;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.TextView;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;

import ru.daniils.splitit.R;

public class Room extends Model {
    public String title = null;
    public String description = null;
    public String keycode = null;
    public String term = null;
    public double price = 0;
    public double deposit = 0;
    public String isPublic = null;

    private Room() {
        super("com_splitit_rooms");
    }

    public Room(Context context) {
        super("com_splitit_rooms");
        setContext(context);
        title = "";
        description = "";
        keycode = "";
        term = "";
        isPublic = "";
    }

    public String toString() {
        return title;
    }

    @Override
    public HashMap getHashMap() {
        return new ObjectMapper().convertValue(new Data(this), HashMap.class);
    }

    @Override
    public Model parseCursorFromDB(Cursor cursor) {
        return new Data(cursor).item;
    }

    @Override
    public void fillViewForList(View itemView) {
        ((TextView) itemView.findViewById(R.id.title)).setText(title);
        ((TextView) itemView.findViewById(R.id.percents)).setText((int) (100 * deposit / price) + "%");
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Data extends Model.Data {
        @JsonIgnore
        public Room mItem;
        @JsonIgnore
        int localId;

        @JsonProperty("original_id")
        int originalId;
        @JsonProperty("admin_user_id")
        int adminUserId;
        double price;
        double deposit;
        String title;
        String description;
        String keycode;
        String term;
        @JsonProperty("is_public")
        String isPublic;

        @JsonCreator
        public Data(@JsonProperty("id") int originalId,
                    @JsonProperty("admin_user_id") int adminUserId,
                    @JsonProperty("title") String title,
                    @JsonProperty("description") String description,
                    @JsonProperty("keycode") String keycode,
                    @JsonProperty("term") String term,
                    @JsonProperty("price") double price,
                    @JsonProperty("deposit") double deposit,
                    @JsonProperty("is_public") int is_public) {
            mItem = new Room();
            setItemVar("originalId", originalId);
            setItemVar("adminUserId", adminUserId);
            setItemVar("keycode", keycode);
            setItemVar("term", term);
            setItemVar("title", title);
            setItemVar("description", description);
            setItemVar("price", price);
            setItemVar("deposit", deposit);
            setItemVar("isPublic", is_public == 1 ? "ON" : "OFF");
            item = mItem;
        }

        Data(Room item) {
            mItem = item;
            setVarByItem("localId");
            setVarByItem("originalId");
            setVarByItem("adminUserId");
            setVarByItem("keycode");
            setVarByItem("term");
            setVarByItem("title");
            setVarByItem("description");
            setVarByItem("price");
            setVarByItem("deposit");
            setVarByItem("isPublic");
        }

        Data(Cursor cursor) {
            this.cursor = cursor;
            mItem = new Room();
            setItemVar("localId", cursorGetInt("id"));
            setItemVar("originalId", cursorGetInt("original_id"));
            setItemVar("adminUserId", cursorGetInt("admin_user_id"));
            setItemVar("keycode", cursorGetString("keycode"));
            setItemVar("term", cursorGetString("term"));
            setItemVar("title", cursorGetString("title"));
            setItemVar("description", cursorGetString("description"));
            setItemVar("price", cursorGetDouble("price"));
            setItemVar("deposit", cursorGetDouble("deposit"));
            setItemVar("isPublic", cursorGetString("is_public"));
            item = mItem;
        }

        protected void setVarByItem(String name) {
            try {
                Data.class.getField(name).set(this, mItem.getClass().getField(name).get(mItem));
            } catch (Exception ignored) {
            }
        }

        protected void setItemVar(String name, Object value) {
            try {
                mItem.getClass().getField(name).set(mItem, value);
            } catch (Exception ignored) {
            }
        }
    }
}