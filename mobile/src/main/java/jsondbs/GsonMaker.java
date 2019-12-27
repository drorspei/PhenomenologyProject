package jsondbs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import java.util.Date;


class GsonMaker {
    static Gson makeGson() {
        JsonSerializer<Date> ser = (src, typeOfSrc, context) -> src == null ? null : new JsonPrimitive(src.getTime());

        JsonDeserializer<Date> deserializer = (json, typeOfT, context) -> json == null ? null : new Date(json.getAsLong());

        return new GsonBuilder()
                .registerTypeAdapter(Date.class, ser)
                .registerTypeAdapter(Date.class, deserializer).create();
    }
}
