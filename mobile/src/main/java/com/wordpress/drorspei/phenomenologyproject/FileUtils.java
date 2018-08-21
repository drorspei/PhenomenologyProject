package com.wordpress.drorspei.phenomenologyproject;

import java.io.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileUtils {
    private static final String dbPath = "/sdcard/Documents/phenomenons.json";

    private static final Type REVIEW_TYPE = new TypeToken<ArrayList<Phenomenon>>() {}.getType();

    public static ArrayList<Phenomenon> loadPhenomena() {
        Gson gson = new Gson();

        try {
            JsonReader reader = new JsonReader(new FileReader(dbPath));
            return gson.fromJson(reader, REVIEW_TYPE);
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        }
    }

    public static void savePhenomena(List<Phenomenon> phenomena) throws IOException{
        try (Writer writer = new FileWriter(dbPath)) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(phenomena, writer);
        }
    }
}
