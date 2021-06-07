package com.mediamod.addons.spotify.config

import com.mediamod.addons.spotify.SpotifyAddon
import com.mediamod.core.MediaModCore
import com.mediamod.core.bindings.desktop.Desktop
import gg.essential.vigilance.Vigilant
import gg.essential.vigilance.data.Property
import gg.essential.vigilance.data.PropertyType
import java.io.File
import java.net.URL

object Configuration : Vigilant(
    File(MediaModCore.addonConfigDirectory, "mediamod-spotify.toml"),
    "Spotify"
) {
    @Property(
        type = PropertyType.CHECKBOX, name = "Auto refresh",
        description = "When this is enabled, the addon will automatically handle refreshing the access token. If this is disabled, you may have to sign in to Spotify again each hour.",
        category = "Authentication"
    )
    var autoRefresh: Boolean = true

    @Property(
        type = PropertyType.BUTTON, name = "Login to Spotify",
        description = "Opens a URL in your browser which allows you to authorize with Spotify.",
        category = "Authentication"
    )
    fun login() {
        Desktop.open(URL(SpotifyAddon.authorizationFlow.authorizationURL))
    }

    @Property(
        type = PropertyType.TEXT, name = "Access Token",
        description = "The access token used to login to your Spotify Account. This is updated every hour via the refresh token. Please do not touch this value.",
        category = "Authentication", subcategory = "Credentials"
    )
    @Volatile
    var accessToken: String = ""

    @Property(
        type = PropertyType.TEXT, name = "Refresh Token",
        description = "The refresh token used to refresh the access token. Please do not touch this value.",
        category = "Authentication", subcategory = "Credentials"
    )
    @Volatile
    var refreshToken: String = ""
}
