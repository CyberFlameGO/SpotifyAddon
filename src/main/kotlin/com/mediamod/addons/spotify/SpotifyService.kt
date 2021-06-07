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

import com.google.gson.Gson
import com.mediamod.addons.spotify.config.Configuration
import com.mediamod.addons.spotify.server.LocalServer
import com.mediamod.core.bindings.threading.ThreadingService
import com.mediamod.core.service.MediaModService
import com.mediamod.core.track.TrackMetadata
import dev.cbyrne.kotify.Kotify
import dev.cbyrne.kotify.api.authorization.flows.KotifyTokenResponse
import dev.cbyrne.kotify.api.section.error.KotifyAPIRequestException
import dev.cbyrne.kotify.api.section.user.types.KotifyUserCurrentTrack
import dev.cbyrne.kotify.builder.KotifyBuilder.Companion.credentials
import dev.cbyrne.kotify.kotify
import khttp.post
import kotlin.concurrent.fixedRateTimer

/**
 * The service for the Spotify addon for MediaMod
 * @author Conor Byrne
 */
object SpotifyService : MediaModService("spotify-addon-service") {
    private val gson = Gson()
    private val callbackServer = LocalServer()
    private var currentTrack: KotifyUserCurrentTrack? = null

    private val kotify: Kotify
        get() = kotify {
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
        val currentTrack = currentTrack ?: return null
        val trackItem = currentTrack.item ?: return null

        return TrackMetadata(
            trackItem.name ?: "Unknown Track",
            trackItem.artists?.get(0)?.name ?: "Unknown Artist",
            currentTrack.progressMs ?: 0,
            trackItem.durationMs ?: 0,
            trackItem.album?.images?.get(0)?.url
        )
    }

    /**
     * Called when MediaMod is querying your service to check if it is ready to provide track information*
     * @return true if you are ready to return [TrackMetadata], otherwise false
     */
    override fun hasTrackMetadata(): Boolean {
        currentTrack = try {
            kotify.api.user.fetchCurrentTrack()
        } catch (e: KotifyAPIRequestException) {
            null
        }

        return currentTrack?.isPlaying ?: false
    }

    /**
     * Called when your service is being registered
     * You should do any once-off operations in here like configuration file reading, etc.
     * Once this method is complete, your service needs to be ready to use
     */
    override fun initialise() {
        ThreadingService.runAsync(callbackServer::start)

        // 3300000ms = 55 minutes, the access token expires after an hour, but let's refresh it 5 minutes beforehand to
        // be safe.
        fixedRateTimer("Spotify API - Refresh", true, 0, 3300000) {
            refreshAccessToken()
        }
    }

    /**
     * Gets an access and a refresh token for a Spotify API code via the MediaMod API
     */
    fun login(code: String) {
        try {
            val response =
                post("${SpotifyAddon.apiURL}/api/v1/spotify/exchange?code=${code}")

            if (response.statusCode == 200) {
                val tokenResponse = gson.fromJson(response.text, KotifyTokenResponse::class.java)
                refreshCredentials(tokenResponse.accessToken, tokenResponse.refreshToken ?: "")

                SpotifyAddon.logger.info("Successfully logged in to Spotify!")
            } else {
                SpotifyAddon.logger.error("Failed to login! (${response.statusCode}) Error: ${response.text}")
            }
        } catch (t: Throwable) {
            SpotifyAddon.logger.error("Failed to login! Error:", t)
        }
    }

    /**
     * Refreshes the user's access token via the MediaMod API
     * If a new refresh token wasn't provided the existing one is still valid
     */
    private fun refreshAccessToken() {
        val token = Configuration.refreshToken
        if (token.isEmpty()) return

        try {
            val response =
                post("${SpotifyAddon.apiURL}/api/v1/spotify/refresh?refreshToken=${token}")

            if (response.statusCode == 200) {
                val tokenResponse = gson.fromJson(response.text, KotifyTokenResponse::class.java)
                refreshCredentials(tokenResponse.accessToken, tokenResponse.refreshToken ?: token)

                SpotifyAddon.logger.info("Successfully refreshed access token!")
            } else {
                SpotifyAddon.logger.error("Failed to refresh access token! (${response.statusCode}) Error: ${response.text}")
            }
        } catch (t: Throwable) {
            SpotifyAddon.logger.error("Failed to refresh access token! Error:", t)
        }
    }

    private fun refreshCredentials(accessToken: String, refreshToken: String) {
        Configuration.accessToken = accessToken
        Configuration.refreshToken = refreshToken

        kotify.credentials = credentials {
            this.accessToken = accessToken
            this.refreshToken = refreshToken
        }
    }
}
