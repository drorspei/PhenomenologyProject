package com.wordpress.drorspei.phenomenologyproject;

import java.io.*;

import android.os.Environment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.wordpress.drorspei.phenomenologyproject.data.Phenomenon;
import com.wordpress.drorspei.phenomenologyproject.data.SavedPhenomenon;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

class FileUtils {
    private static final Type PHENOMENON_TYPE = new TypeToken<ArrayList<Phenomenon>>() {}.getType();
    private static final Type SAVED_PHENOMENON_TYPE = new TypeToken<ArrayList<SavedPhenomenon>>() {}.getType();

    private static String getPhenomenaDbPath() {
        return Environment.getExternalStorageDirectory().getPath() + "/Documents/phenomena.json";
    }

    private static String getSavedPhenomenaDbPath() {
        return Environment.getExternalStorageDirectory().getPath() + "/Documents/savedphenomena.json";
    }

    static ArrayList<Phenomenon> loadPhenomena() {
        Gson gson = new Gson();

        try {
            JsonReader reader = new JsonReader(new FileReader(getPhenomenaDbPath()));
            return gson.fromJson(reader, PHENOMENON_TYPE);
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        }
    }

    static ArrayList<SavedPhenomenon> loadSavedPhenomena() {
        Gson gson = new Gson();

        try {
            JsonReader reader = new JsonReader(new FileReader(getSavedPhenomenaDbPath()));
            return gson.fromJson(reader, SAVED_PHENOMENON_TYPE);
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        }
    }

    static void savePhenomena(List<Phenomenon> phenomena) throws IOException{
        try (Writer writer = new FileWriter(getPhenomenaDbPath())) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(phenomena, writer);
        }
    }

    static void saveSavedPhenomena(List<SavedPhenomenon> savedPhenomena) throws IOException{
        try (Writer writer = new FileWriter(getSavedPhenomenaDbPath())) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(savedPhenomena, writer);
        }
    }
}
