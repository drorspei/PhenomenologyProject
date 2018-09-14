package jsondbs;

import android.os.Environment;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.wordpress.drorspei.phenomenologyproject.data.IPhenomenaDb;
import com.wordpress.drorspei.phenomenologyproject.data.Phenomenon;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JsonPhenomenaDb implements IPhenomenaDb {
    private static final Type PHENOMENON_TYPE = new TypeToken<ArrayList<Phenomenon>>() {}.getType();
    private ArrayList<Phenomenon> phenomena;

    private static String getPhenomenaDbPath() {
        return Environment.getExternalStorageDirectory().getPath() + "/Documents/phenomena.json";
    }

    public JsonPhenomenaDb() {
        Gson gson = new Gson();

        try {
            JsonReader reader = new JsonReader(new FileReader(getPhenomenaDbPath()));
            phenomena = gson.fromJson(reader, PHENOMENON_TYPE);
        } catch (FileNotFoundException e) {
            phenomena = new ArrayList<>();
        }
    }

    private void savePhenomena() {
        try (Writer writer = new FileWriter(getPhenomenaDbPath())) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(phenomena, writer);
        } catch (IOException e) {
            Log.e("PhenomenonologyProject", "JsonPhenomenaDb.savePhenomena() failed to write to file.");
        }
    }

    @Override
    public void add(Phenomenon phenomenon) {
        phenomena.add(phenomenon);
        savePhenomena();
    }

    @Override
    public void remove(Phenomenon phenomenon) {
        if (phenomena.remove(phenomenon)) {
            savePhenomena();
        }
    }

    @Override
    public List<Phenomenon> getAll() {
        return phenomena;
    }
}
