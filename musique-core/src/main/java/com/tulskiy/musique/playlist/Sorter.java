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

package com.tulskiy.musique.playlist;

import com.tulskiy.musique.playlist.formatting.Parser;

import java.util.Collections;
import java.util.List;

/**
 * Utility class for sorting tracks in a playlist.
 *
 * Author: Denis Tulskiy
 * Date: 6/3/11
 */
public class Sorter {

    /** Sort tracks by a format expression string (e.g. "%artist%"). */
    public void sortBy(List<Track> tracks, String formatExpression) {
        Collections.sort(tracks, new TrackComparator(Parser.parse(formatExpression)));
    }

    /** Randomize the order of tracks. */
    public void randomize(List<Track> tracks) {
        Collections.shuffle(tracks);
    }

    /** Reverse the order of tracks. */
    public void reverse(List<Track> tracks) {
        Collections.reverse(tracks);
    }

    /** Sort tracks by artist. */
    public void sortByArtist(List<Track> tracks) {
        sortBy(tracks, "%artist%");
    }

    /** Sort tracks by album. */
    public void sortByAlbum(List<Track> tracks) {
        sortBy(tracks, "%album%");
    }

    /** Sort tracks by file path. */
    public void sortByFilePath(List<Track> tracks) {
        sortBy(tracks, "%file%");
    }

    /** Sort tracks by title. */
    public void sortByTitle(List<Track> tracks) {
        sortBy(tracks, "%title%");
    }

    /** Sort tracks by track number. */
    public void sortByTrackNumber(List<Track> tracks) {
        sortBy(tracks, "%trackNumber%");
    }

    /** Sort tracks by album artist / year / album / disc / track / filename. */
    public void sortByAlbumArtistYearAlbum(List<Track> tracks) {
        sortBy(tracks, "%albumArtist% - %year% - %album% - %discNumber% - %trackNumber% - %fileName%");
    }
}
