/*
 * Copyright (c) 2008, 2009, 2010, 2011 Denis Tulskiy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * version 3 along with this work.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.tulskiy.musique.system.configuration;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Simple key-value configuration store for musique-core library.
 * All GUI-specific (Color, Font, Rectangle) methods have been removed.
 */
public class Configuration {

    public static final int VERSION = 1;
    public static final String PROPERTY_INFO_VERSION = "info.version";

    private final Logger logger = Logger.getLogger(getClass().getName());
    private final Map<String, Object> map = new LinkedHashMap<>();
    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    public void load(Reader reader) {
        logger.fine("Loading configuration");
        try (BufferedReader br = new BufferedReader(reader)) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                int idx = line.indexOf('=');
                if (idx > 0) {
                    String key = line.substring(0, idx).trim();
                    String value = line.substring(idx + 1).trim();
                    map.put(key, value);
                }
            }
        } catch (IOException e) {
            logger.warning("Could not load configuration: " + e.getMessage());
        }
    }

    public void save(Writer writer) {
        logger.fine("Saving configuration");
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(writer))) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                pw.println(entry.getKey() + "=" + entry.getValue());
            }
        }
    }

    public Object get(String key) {
        return map.get(key);
    }

    public void put(String key, Object value) {
        Object old = map.get(key);
        if (value == null) {
            map.remove(key);
        } else {
            map.put(key, value.toString());
        }
        changeSupport.firePropertyChange(key, old, value);
    }

    public String getString(String key, String def) {
        Object val = map.get(key);
        return val != null ? val.toString() : def;
    }

    public String getString(String key) {
        return getString(key, null);
    }

    public int getInt(String key, int def) {
        try {
            Object val = map.get(key);
            return val != null ? Integer.parseInt(val.toString()) : def;
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public float getFloat(String key, float def) {
        try {
            Object val = map.get(key);
            return val != null ? Float.parseFloat(val.toString()) : def;
        } catch (NumberFormatException e) {
            return def;
        }
    }

    public boolean getBoolean(String key, boolean def) {
        Object val = map.get(key);
        return val != null ? Boolean.parseBoolean(val.toString()) : def;
    }

    public void setInt(String key, int value) {
        put(key, value);
    }

    public void setFloat(String key, float value) {
        put(key, value);
    }

    public void setString(String key, String value) {
        put(key, value);
    }

    public void setBoolean(String key, boolean value) {
        put(key, value);
    }

    public void setList(String key, List<?> values) {
        map.remove(key);
        if (values != null) {
            for (int i = 0; i < values.size(); i++) {
                map.put(key + "[" + i + "]", values.get(i) != null ? values.get(i).toString() : "");
            }
        }
    }

    public List<String> getList(String key) {
        List<String> result = new ArrayList<>();
        int i = 0;
        while (map.containsKey(key + "[" + i + "]")) {
            result.add(map.get(key + "[" + i + "]").toString());
            i++;
        }
        return result;
    }

    public <E extends Enum<E>> E getEnum(String key, E def) {
        String val = getString(key, def.name());
        try {
            @SuppressWarnings("unchecked")
            Class<E> clazz = (Class<E>) def.getClass();
            return Enum.valueOf(clazz, val);
        } catch (IllegalArgumentException e) {
            return def;
        }
    }

    public <E extends Enum<E>> void setEnum(String key, E value) {
        setString(key, value.name());
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public void addPropertyChangeListener(String propertyName, boolean initialize, PropertyChangeListener listener) {
        addPropertyChangeListener(propertyName, listener);
        if (initialize) {
            listener.propertyChange(new PropertyChangeEvent(this, propertyName, null, get(propertyName)));
        }
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }
}
