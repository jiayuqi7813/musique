/*
 * Copyright (c) 2008, 2009, 2010 Denis Tulskiy
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the simplified, GUI-free Configuration class.
 */
public class ConfigTest {
    private Configuration config;

    @Before
    public void setUp() {
        Logger.getLogger(getClass().getName()).setLevel(Level.OFF);
        config = new Configuration();
        config.load(new StringReader(
                "int=12345\n" +
                "float=1.2345\n" +
                "string=some string\n" +
                "bool=true\n"));
    }

    @Test
    public void testLoad() {
        int anInt = config.getInt("int", -1);
        assertEquals(12345, anInt);

        float aFloat = config.getFloat("float", -1);
        assertEquals(1.2345, aFloat, 0.00001);

        String string = config.getString("string", null);
        assertNotNull(string);
        assertEquals("some string", string);

        boolean bool = config.getBoolean("bool", false);
        assertTrue(bool);
    }

    @Test
    public void testDefaults() {
        int anInt = config.getInt("doesNotExist4", -1);
        assertEquals(-1, anInt);

        float aFloat = config.getFloat("doesNotExist5", -1);
        assertEquals(-1, aFloat, 0.00001);

        String string = config.getString("doesNotExist6", null);
        assertNull(string);

        boolean bool = config.getBoolean("doesNotExist7", false);
        assertEquals(false, bool);
    }

    @Test
    public void testPut() {
        config.setInt("newInt", 123);
        assertEquals(123, config.getInt("newInt", -1));

        config.setFloat("newFloat", 1.23f);
        assertEquals(1.23, config.getFloat("newFloat", -1), 0.001);

        config.setString("newString", "new string");
        assertEquals("new string", config.getString("newString", null));

        config.setBoolean("newBool", true);
        assertTrue(config.getBoolean("newBool", false));
    }

    @Test
    public void testSaveAndLoad() {
        config.setInt("savedInt", 999);
        config.setString("savedStr", "hello");

        StringWriter sw = new StringWriter();
        config.save(sw);

        Configuration config2 = new Configuration();
        config2.load(new StringReader(sw.toString()));
        assertEquals(999, config2.getInt("savedInt", -1));
        assertEquals("hello", config2.getString("savedStr", null));
    }

    @Test
    public void testList() {
        config.setList("myList", java.util.Arrays.asList("alpha", "beta", "gamma"));
        List<String> list = config.getList("myList");
        assertNotNull(list);
        assertEquals(3, list.size());
        assertEquals("alpha", list.get(0));
        assertEquals("beta", list.get(1));
        assertEquals("gamma", list.get(2));
    }
}
