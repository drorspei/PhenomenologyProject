package com.wordpress.drorspei.phenomenologyproject;

import java.io.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    private static final String phenomenaDbPath = "/sdcard/Documents/phenomena.json";
    private static final String savedPhenomenaDbPath = "/sdcard/Documents/savedphenomena.json";

    private static final Type PHENOMENON_TYPE = new TypeToken<ArrayList<Phenomenon>>() {}.getType();
    private static final Type SAVED_PHENOMENON_TYPE = new TypeToken<ArrayList<SavedPhenomenon>>() {}.getType();

    public static ArrayList<Phenomenon> loadPhenomena() {
        Gson gson = new Gson();

        try {
            JsonReader reader = new JsonReader(new FileReader(phenomenaDbPath));
            return gson.fromJson(reader, PHENOMENON_TYPE);
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        }
    }

    public static ArrayList<SavedPhenomenon> loadSavedPhenomena() {
        Gson gson = new Gson();

        try {
            JsonReader reader = new JsonReader(new FileReader(savedPhenomenaDbPath));
            return gson.fromJson(reader, SAVED_PHENOMENON_TYPE);
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        }
    }

    public static void savePhenomena(List<Phenomenon> phenomena) throws IOException{
        try (Writer writer = new FileWriter(phenomenaDbPath)) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(phenomena, writer);
        }
    }

    public static void saveSavedPhenomena(List<SavedPhenomenon> savedPhenomena) throws IOException{
        try (Writer writer = new FileWriter(savedPhenomenaDbPath)) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(savedPhenomena, writer);
        }
    }
}
