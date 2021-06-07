package com.mediamod.addons.spotify.server

import com.mediamod.addons.spotify.SpotifyAddon
import com.mediamod.addons.spotify.SpotifyService
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import java.net.InetSocketAddress

class LocalServer {
    fun start(port: Int = 9103) {
        try {
            val server = HttpServer.create(InetSocketAddress("localhost", port), 0)
            server.createContext("/callback", SpotifyCallbackHandler())
            server.start()
        } catch (t: Throwable) {
            SpotifyAddon.logger.info("Failed to start Spotify callback server! Is the port $port already in use?")
        }
    }

    private class SpotifyCallbackHandler : HttpHandler {
        override fun handle(exchange: HttpExchange) {
            if (exchange.requestMethod != "GET") return

            var title = "Success"
            var message = "You can now close this window and return to Minecraft."
            val query = queryToMap(exchange.requestURI.query)
            val code = query["code"]

            try {
                SpotifyService.login(code ?: error("No code supplied"))
            } catch (t: Throwable) {
                title = "Failure"
                message = "Failed to get authorization code. Please try again!"
            }

            val response = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "  <head>\n" +
                    "    <meta charset=\"utf-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                    "    <title>MediaMod</title>\n" +
                    "    <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/bulma/0.7.5/css/bulma.min.css\">\n" +
                    "    <script defer src=\"https://use.fontawesome.com/releases/v5.3.1/js/all.js\"></script>\n" +
                    "  </head>\n" +
                    "  <body class=\"hero is-dark is-fullheight\">\n" +
                    "  <section class=\"section has-text-centered\">\n" +
                    "    <div class=\"container\">\n" +
                    "      <img src=\"https://raw.githubusercontent.com/MediaModMC/MediaMod/master/src/main/resources/assets/mediamod/header.png\" width=\"400px\">" + "\n" +
                    "      <h1 class=\"title\">\n" +
                    "        " + title + "\n" +
                    "      </h1>\n" +
                    "      <p class=\"subtitle\">\n" +
                    "        " + message + "\n" +
                    "      </p>\n" +
                    "    </div>\n" +
                    "  </section>\n" +
                    "  </body>\n" +
                    "</html>"

            exchange.sendResponseHeaders(200, response.length.toLong())

            val stream = exchange.responseBody
            stream.write(response.toByteArray())
            stream.close()
        }

        /**
         * Parses a http query string into a java Map
         *
         * @param query: http query
         * @see SpotifyCallbackHandler.handle
         */
        fun queryToMap(query: String): Map<String, String> {
            val result = mutableMapOf<String, String>()
            for (param in query.split("&")) {
                val entry = param.split("=")
                result[entry[0]] = if (entry.size > 1) entry[1] else ""
            }

            return result
        }
    }
}