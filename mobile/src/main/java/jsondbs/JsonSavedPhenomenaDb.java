package jsondbs;

import android.os.Environment;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.wordpress.drorspei.phenomenologyproject.data.ISavedPhenomenaDb;
import com.wordpress.drorspei.phenomenologyproject.data.SavedPhenomenon;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class JsonSavedPhenomenaDb implements ISavedPhenomenaDb {
    private static final Type SAVED_PHENOMENON_TYPE = new TypeToken<ArrayList<SavedPhenomenon>>() {}.getType();
    private ArrayList<SavedPhenomenon> savedPhenomena;

    private static String getSavedPhenomenaDbPath() {
        return Environment.getExternalStorageDirectory().getPath() + "/Documents/savedphenomena.json";
    }

    public JsonSavedPhenomenaDb() {
        Gson gson = new Gson();

        try {
            JsonReader reader = new JsonReader(new FileReader(getSavedPhenomenaDbPath()));
            savedPhenomena = gson.fromJson(reader, SAVED_PHENOMENON_TYPE);
        } catch (FileNotFoundException e) {
            savedPhenomena = new ArrayList<>();
        }
    }

    private void saveSavedPhenomena() {
        try (Writer writer = new FileWriter(getSavedPhenomenaDbPath())) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(savedPhenomena, writer);
        } catch (IOException e) {
            Log.e("PhenomenologyProject", "JsonSavedPhenomenaDb.saveSavedPhenomena failed to write to file.");
        }
    }


    @Override
    public void add(SavedPhenomenon savedPhenomenon) {
        savedPhenomena.add(savedPhenomenon);
        saveSavedPhenomena();
    }
}
