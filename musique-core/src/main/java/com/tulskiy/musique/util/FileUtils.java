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

package com.tulskiy.musique.util;

import java.io.File;

/**
 * @author Maksim Liauchuk
 */
public class FileUtils {

    private FileUtils() {
        // prevent instantiation
    }

    /**
     * Deletes empty parent folders of the given file.
     * When {@code isConfirmationRequired} is {@code true} the caller is
     * expected to have already obtained user consent; this method always
     * proceeds with deletion.
     */
    public static void deleteEmptyParentFolders(final File file, final boolean isConfirmationRequired) {
        File current = file.getParentFile();
        File parent;
        File[] files = current.listFiles();
        if (files != null && files.length == 0) {
            while (current != null) {
                parent = current.getParentFile();
                current.delete();
                files = parent.listFiles();
                if (files != null && files.length == 0) {
                    current = parent;
                } else {
                    current = null;
                }
            }
        }
    }

}
