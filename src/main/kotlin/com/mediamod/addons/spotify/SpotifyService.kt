/*
 *     SpotifyAddon is an addon for MediaMod which adds Spotify Support
 *     Copyright (C) 2021 Conor Byrne
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */


package com.mediamod.addons.spotify

import com.mediamod.addons.spotify.config.Configuration
import com.mediamod.core.service.MediaModService
import com.mediamod.core.track.TrackMetadata
import dev.dreamhopping.kotify.api.section.user.types.KotifyUserCurrentTrack
import dev.dreamhopping.kotify.kotify

/**
 * The service for the Spotify addon for MediaMod
 *
 * @author Conor Byrne (dreamhopping)
 */
class SpotifyService : MediaModService("spotify-addon-service") {
    private var currentTrack: KotifyUserCurrentTrack? = null
    private val kotify = kotify {
        credentials {
            accessToken = Configuration.accessToken
            refreshToken = Configuration.refreshToken
        }
    }

    /**
     * Called when MediaMod wants to get a [TrackMetadata] instance from you
     * This is called every 3 seconds to avoid rate limits if you are using an API
     * You can do network operations on this method
     *
     * @return null if there is no TrackMetadata available
     */
    override fun fetchTrackMetadata(): TrackMetadata? {
        val trackItem = currentTrack?.item ?: return null

        return TrackMetadata(
            trackItem.name ?: "Unknown Track",
            trackItem.artists?.get(0)?.name ?: "Unknown Artist",
            currentTrack?.progressMs ?: 0,
            trackItem.durationMs ?: 0,
            trackItem.album?.images?.get(0)?.url
        )
    }

    /**
     * Called when MediaMod is querying your service to check if it is ready to provide track information*
     * @return true if you are ready to return [TrackMetadata], otherwise false
     */
    override fun hasTrackMetadata(): Boolean {
        currentTrack = kotify.api.user.fetchCurrentTrack()
        return currentTrack?.isPlaying ?: false
    }

    /**
     * Called when your service is being registered
     * You should do any once-off operations in here like configuration file reading, etc.
     * Once this method is complete, your service needs to be ready to use
     */
    override fun initialise() {

    }
}
