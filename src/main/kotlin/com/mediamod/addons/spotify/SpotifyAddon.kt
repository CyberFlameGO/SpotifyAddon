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
import com.mediamod.core.addon.MediaModAddon
import com.mediamod.core.config.MediaModConfigRegistry
import com.mediamod.core.service.MediaModServiceRegistry
import dev.cbyrne.kotify.api.authorization.flows.authorizationCodeFlow
import dev.cbyrne.kotify.api.scopes.SpotifyScope
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/**
 * The addon class for the Spotify Addon for MediaMod
 * @author Conor Byrne
 */
class SpotifyAddon : MediaModAddon("mediamod-spotify") {
    companion object {
        val logger: Logger = LogManager.getLogger("MediaMod: Spotify Integration")
        val authorizationFlow = authorizationCodeFlow {
            clientID = "88ddf756462c4e078933a42f4cdb33e8"
            redirectURI = "http://localhost:9103/callback"
            scopes {
                +SpotifyScope.USER_READ_PLAYBACK_POSITION
                +SpotifyScope.USER_READ_PLAYBACK_STATE
                +SpotifyScope.USER_READ_CURRENTLY_PLAYING
            }
        }

        const val apiURL = "http://localhost:3001"
    }

    /**
     * Called when MediaMod is initialising your addon
     * The addon should be ready for usage when this method is complete
     */
    override fun initialise() {
        MediaModConfigRegistry.registerConfig(identifier, Configuration)
        MediaModServiceRegistry.registerService(identifier, SpotifyService)
    }
}