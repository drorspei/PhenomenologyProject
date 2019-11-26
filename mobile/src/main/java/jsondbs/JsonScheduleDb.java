package jsondbs;

import android.os.Environment;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.wordpress.drorspei.phenomenologyproject.data.IScheduleDb;
import com.wordpress.drorspei.phenomenologyproject.data.Phenomenon;
import com.wordpress.drorspei.phenomenologyproject.data.ScheduleItem;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class JsonScheduleDb implements IScheduleDb {
    private static final Type SCHEDULED_ITEM_TYPE = new TypeToken<ArrayList<ScheduleItem>>() {}.getType();
    private ArrayList<ScheduleItem> scheduleItems;

    private static String getScheduleItemsDbPath() {
        return Environment.getExternalStorageDirectory().getPath() + "/Documents/scheduledphenomena.json";
    }

    private Gson makeGson() {
        JsonSerializer<Date> ser = new JsonSerializer<Date>() {
            @Override
            public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext
                    context) {
                return src == null ? null : new JsonPrimitive(src.getTime());
            }
        };

        JsonDeserializer<Date> deser = new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonElement json, Type typeOfT,
                                    JsonDeserializationContext context) throws JsonParseException {
                return json == null ? null : new Date(json.getAsLong());
            }
        };

        return new GsonBuilder()
                .registerTypeAdapter(Date.class, ser)
                .registerTypeAdapter(Date.class, deser).create();
    }

    public JsonScheduleDb() {
        Gson gson = makeGson();

        try {
            JsonReader reader = new JsonReader(new FileReader(getScheduleItemsDbPath()));
            scheduleItems = gson.fromJson(reader, SCHEDULED_ITEM_TYPE);
        } catch (FileNotFoundException e) {
            scheduleItems = new ArrayList<>();
        }
    }

    private void saveScheduleItems() {
        try (Writer writer = new FileWriter(getScheduleItemsDbPath())) {
            Gson gson = makeGson();
            gson.toJson(scheduleItems, writer);
        } catch (IOException e) {
            Log.e("PhenomenologyProject", "JsonScheduleDb.saveScheduleItems failed to write to file.");
        }
    }

    @Override
    public ScheduleItem getNext() {
        if (scheduleItems.size() > 0) {
            return scheduleItems.get(0);
        } else {
            return null;
        }
    }

    @Override
    public void add(Phenomenon phenomenon, Date date) {
        if (phenomenon != null && date != null) {
            int index = 0;
            for (ScheduleItem scheduleItem : scheduleItems) {
                if (scheduleItem.date.before(date)) {
                    index++;
                } else {
                    break;
                }
            }

            scheduleItems.add(index, new ScheduleItem(phenomenon, date));
            saveScheduleItems();
        }
    }

    @Override
    public void remove(Phenomenon phenomenon) {
        boolean removedItem = false;

        Iterator<ScheduleItem> scheduleItemIterator = scheduleItems.iterator();
        while (scheduleItemIterator.hasNext()) {
            ScheduleItem scheduleItem = scheduleItemIterator.next();
            if (scheduleItem.phenomenon == phenomenon) {
                scheduleItemIterator.remove();
                removedItem = true;
            }
        }

        if (removedItem) {
            saveScheduleItems();
        }
    }

    @Override
    public void clear() {
        if (scheduleItems.size() > 0) {
            scheduleItems.clear();
            saveScheduleItems();
        }
    }
}
