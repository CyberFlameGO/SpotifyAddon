package com.mediamod.addons.spotify.config

import club.sk1er.vigilance.Vigilant
import club.sk1er.vigilance.data.Property
import club.sk1er.vigilance.data.PropertyType
import com.mediamod.core.MediaModCore
import java.io.File

object Configuration : Vigilant(
    File(MediaModCore.addonConfigDirectory, "mediamod-spotify.toml"),
    "Spotify"
) {
    @Property(
        type = PropertyType.CHECKBOX, name = "Auto refresh",
        description = "When this is enabled, the addon will automatically handle refreshing the access token. If this is disabled, you may have to sign in to Spotify again each hour.",
        category = "Authentication", subcategory = "General"
    )
    var autoRefresh: Boolean = true

    @Property(
        type = PropertyType.TEXT, name = "Access Token",
        description = "The access token used to login to your Spotify Account. This is updated every hour via the refresh token.",
        category = "Authentication", subcategory = "Credentials",
        hidden = true
    )
    var accessToken: String = ""

    @Property(
        type = PropertyType.TEXT, name = "Refresh Token",
        description = "The refresh token used to refresh the access token. This means that you will not have to re-login each hour.",
        category = "Authentication", subcategory = "Credentials",
        hidden = true
    )
    var refreshToken: String = ""
}
