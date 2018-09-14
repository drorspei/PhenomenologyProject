package com.wordpress.drorspei.phenomenologyproject.data;

import java.util.List;

public interface IPhenomenaDb {
    /**
     * Add phenomenon to datebase.
     *
     * @param phenomenon Instance to add.
     */
    void add(Phenomenon phenomenon);

    /**
     * Remove phenomenon from database.
     *
     * @param phenomenon Instance to remove.
     */
    void remove(Phenomenon phenomenon);

    /**
     * Get all saved phenomena.
     *
     * @return List of saved phenomena.
     */
    List<Phenomenon> getAll();
}
