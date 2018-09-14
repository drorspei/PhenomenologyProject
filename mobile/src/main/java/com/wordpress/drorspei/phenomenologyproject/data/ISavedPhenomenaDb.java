package com.wordpress.drorspei.phenomenologyproject.data;

public interface ISavedPhenomenaDb {
    /**
     * Adds a SavedPhenomenon to the database.
     *
     * @param savedPhenomenon: instance to add.
     */
    void add(SavedPhenomenon savedPhenomenon);
}
