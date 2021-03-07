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

import com.mediamod.core.addon.MediaModAddon
import com.mediamod.core.service.MediaModServiceRegistry
import dev.dreamhopping.kotify.Kotify
import dev.dreamhopping.kotify.kotify

/**
 * The addon class for the Spotify Addon for MediaMod
 * @author Conor Byrne (dreamhopping)
 */
class SpotifyAddon : MediaModAddon("mediamod-spotify") {
    companion object {
        private var accessToken: String? = null
        private var refreshToken: String? = null

        val kotify: Kotify
            get() = kotify {
                credentials {
                    accessToken = this@Companion.accessToken
                    refreshToken = this@Companion.refreshToken
                }
            }
    }

    /**
     * Called when MediaMod is initialising your addon
     * The addon should be ready for usage when this method is complete
     */
    override fun initialise() {
        MediaModServiceRegistry.registerService(identifier, SpotifyService())

        // TODO: Load access and refresh token
    }

    /**
     * Called when MediaMod is unloading your addon
     * The addon should do any configuration saving, etc. in this method
     */
    override fun unload() {
        // TODO: Save access & refresh token
    }
}