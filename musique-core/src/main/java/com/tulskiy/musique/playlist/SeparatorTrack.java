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

package com.tulskiy.musique.playlist;

/**
 * A pseudo-track used as a group separator in a grouped playlist.
 * Its TrackData has no location (null), which is the indicator used
 * by PlaybackOrder to skip it during navigation.
 */
public class SeparatorTrack extends Track {
    private final int groupSize;
    private final String groupName;

    public SeparatorTrack(String groupName, int groupSize) {
        this.groupName = groupName;
        this.groupSize = groupSize;
    }

    public String getGroupName() {
        return groupName;
    }

    public int getGroupSize() {
        return groupSize;
    }

    @Override
    public String toString() {
        return groupName;
    }
}
