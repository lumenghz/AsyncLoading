/*******************************************************************************
 * Copyright 2014 Jiahe Winchance
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
package com.winchance.util;

import android.graphics.Bitmap;

import java.util.Collection;

/**
 * Interface for memory cache
 * @author lumeng
 * @since 1.0.0
 */
public interface MemoryCache {
    /**
     * Puts value into cache by key
     * @return <b>true</b> - if value was put into cache sucessfully, <b>false</b> - if value was <b>not</b> put into
     * cache
     */
    boolean put(String key, Bitmap value);

    /** Returns value by key. If there is no value for key then null will be returned. */
    Bitmap get(String key);

    /** Removes item by key */
    Bitmap remove(String key);

    /** Returns all keys of cache */
    Collection<String> keys();

    /** Remove all items from cache */
    void clear();
}
