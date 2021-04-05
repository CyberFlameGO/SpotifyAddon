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
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

/**
 * The addon class for the Spotify Addon for MediaMod
 * @author Conor Byrne (dreamhopping)
 */
class SpotifyAddon : MediaModAddon("mediamod-spotify") {
    companion object {
        val logger: Logger = LogManager.getLogger("MediaMod: Spotify Integration")
        const val apiURL = "http://localhost:8080"
    }

    /**
     * Called when MediaMod is initialising your addon
     * The addon should be ready for usage when this method is complete
     */
    override fun initialise() {
        MediaModServiceRegistry.registerService(identifier, SpotifyService())
        MediaModConfigRegistry.registerConfig(identifier, Configuration)
    }
}