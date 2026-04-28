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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Manages a collection of playlists and playback ordering.
 */
public class PlaylistManager {
    private final Logger logger = Logger.getLogger(getClass().getName());
    private final List<Playlist> playlists = new ArrayList<Playlist>();
    private Playlist activePlaylist;
    private Playlist visiblePlaylist;
    private final PlaybackOrder order = new PlaybackOrder();
    private final List<PlaylistListener> listeners = new ArrayList<PlaylistListener>();

    public PlaylistManager() {
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public void setActivePlaylist(Playlist playlist) {
        if (activePlaylist != playlist) {
            activePlaylist = playlist;
            notifyListeners(playlist, PlaylistListener.Event.ACTIVATED);
        }
        order.setPlaylist(playlist);
    }

    public Playlist getActivePlaylist() {
        return activePlaylist;
    }

    public void setVisiblePlaylist(Playlist playlist) {
        if (visiblePlaylist != playlist) {
            visiblePlaylist = playlist;
            notifyListeners(playlist, PlaylistListener.Event.SELECTED);
        }
    }

    public Playlist getVisiblePlaylist() {
        return visiblePlaylist;
    }

    public PlaybackOrder getPlaybackOrder() {
        return order;
    }

    public Playlist addPlaylist(String name) {
        Playlist p = new Playlist();
        p.setName(name);
        playlists.add(p);
        notifyListeners(p, PlaylistListener.Event.ADDED);
        if (activePlaylist == null) {
            setActivePlaylist(p);
        }
        return p;
    }

    public void addPlaylist(Playlist playlist) {
        playlists.add(playlist);
        notifyListeners(playlist, PlaylistListener.Event.ADDED);
    }

    public void removePlaylist(Playlist playlist) {
        playlists.remove(playlist);
        notifyListeners(playlist, PlaylistListener.Event.REMOVED);
        if (activePlaylist == playlist) {
            activePlaylist = playlists.isEmpty() ? null : playlists.get(0);
            if (activePlaylist != null) {
                order.setPlaylist(activePlaylist);
            }
        }
    }

    public synchronized void addPlaylistListener(PlaylistListener listener) {
        listeners.add(listener);
    }

    public synchronized void removePlaylistListener(PlaylistListener listener) {
        listeners.remove(listener);
    }

    private synchronized void notifyListeners(Playlist playlist, PlaylistListener.Event event) {
        for (PlaylistListener listener : listeners) {
            switch (event) {
                case ADDED:
                    listener.playlistAdded(playlist);
                    break;
                case REMOVED:
                    listener.playlistRemoved(playlist);
                    break;
                case SELECTED:
                    listener.playlistSelected(playlist);
                    break;
                case UPDATED:
                    listener.playlistUpdated(playlist);
                    break;
                case ACTIVATED:
                    listener.playlistActivated(playlist);
                    break;
            }
        }
    }
}
