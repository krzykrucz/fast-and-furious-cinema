package com.krzykrucz.fastfurious.support

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.ok
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.isRootTest

class OMDbApiMockTestSupport : TestListener {

    private val port = 9000
    private val server = WireMockServer(port)

    val apiKey = "12345678"

    val baseUrl = "http://localhost:$port"

    fun verifyAllMoviesDownloadedOnce() {
        listOf(
            "tt0232500", "tt0322259", "tt0463985", "tt1013752", "tt1596343", "tt1905041", "tt2820852", "tt4630562",
            "tt5433138"
        ).forEach { imdbId ->
            server.verify(1, getRequestedFor(urlEqualTo("/?apikey=$apiKey&i=$imdbId")))
        }
    }

    override suspend fun beforeSpec(spec: Spec) {
        server.start()

        server.stubFor(
            get(urlEqualTo("/?apikey=$apiKey&i=tt0232500"))
                .willReturn(
                    ok().withBody(
                        "{\"Title\":\"The Fast and the Furious\",\"Year\":\"2001\",\"Rated\":\"PG-13\",\"Released\":\"22 Jun 2001\",\"Runtime\":\"106 min\",\"Genre\":\"Action, Crime, Thriller\",\"Director\":\"Rob Cohen\",\"Writer\":\"Ken Li, Gary Scott Thompson, Erik Bergquist\",\"Actors\":\"Vin Diesel, Paul Walker, Michelle Rodriguez\",\"Plot\":\"Los Angeles police officer Brian O'Conner must decide where his loyalty really lies when he becomes enamored with the street racing world he has been sent undercover to destroy.\",\"Language\":\"English, Spanish\",\"Country\":\"United States, Germany\",\"Awards\":\"11 wins & 18 nominations\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BNzlkNzVjMDMtOTdhZC00MGE1LTkxODctMzFmMjkwZmMxZjFhXkEyXkFqcGdeQXVyNjU0OTQ0OTY@._V1_SX300.jpg\",\"Ratings\":[{\"Source\":\"Internet Movie Database\",\"Value\":\"6.8/10\"},{\"Source\":\"Rotten Tomatoes\",\"Value\":\"54%\"},{\"Source\":\"Metacritic\",\"Value\":\"58/100\"}],\"Metascore\":\"58\",\"imdbRating\":\"6.8\",\"imdbVotes\":\"370,116\",\"imdbID\":\"tt0232500\",\"Type\":\"movie\",\"DVD\":\"03 Jun 2003\",\"BoxOffice\":\"\$144,533,925\",\"Production\":\"N/A\",\"Website\":\"N/A\",\"Response\":\"True\"}"
                    )
                        .withHeader("content-type", "application/json; charset=utf-8")
                )
        )
        server.stubFor(
            get(urlEqualTo("/?apikey=$apiKey&i=tt0322259"))
                .willReturn(
                    ok().withBody(
                        "{\"Title\":\"2 Fast 2 Furious\",\"Year\":\"2003\",\"Rated\":\"PG-13\",\"Released\":\"06 Jun 2003\",\"Runtime\":\"107 min\",\"Genre\":\"Action, Crime, Thriller\",\"Director\":\"John Singleton\",\"Writer\":\"Gary Scott Thompson, Michael Brandt, Derek Haas\",\"Actors\":\"Paul Walker, Tyrese Gibson, Cole Hauser\",\"Plot\":\"Former cop Brian O'Conner is called upon to bust a dangerous criminal and he recruits the help of a former childhood friend and street racer who has a chance to redeem himself.\",\"Language\":\"English, Spanish\",\"Country\":\"United States, Germany\",\"Awards\":\"4 wins & 13 nominations\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BMzExYjcyYWMtY2JkOC00NDUwLTg2OTgtMDI3MGY2OWQzMDE2XkEyXkFqcGdeQXVyMTQxNzMzNDI@._V1_SX300.jpg\",\"Ratings\":[{\"Source\":\"Internet Movie Database\",\"Value\":\"5.9/10\"},{\"Source\":\"Rotten Tomatoes\",\"Value\":\"36%\"},{\"Source\":\"Metacritic\",\"Value\":\"38/100\"}],\"Metascore\":\"38\",\"imdbRating\":\"5.9\",\"imdbVotes\":\"264,489\",\"imdbID\":\"tt0322259\",\"Type\":\"movie\",\"DVD\":\"30 Sep 2003\",\"BoxOffice\":\"\$127,154,901\",\"Production\":\"N/A\",\"Website\":\"N/A\",\"Response\":\"True\"}"
                    )
                        .withHeader("content-type", "application/json; charset=utf-8")
                )
        )
        server.stubFor(
            get(urlEqualTo("/?apikey=$apiKey&i=tt0463985"))
                .willReturn(
                    ok().withBody(
                        "{\"Title\":\"The Fast and the Furious: Tokyo Drift\",\"Year\":\"2006\",\"Rated\":\"PG-13\",\"Released\":\"16 Jun 2006\",\"Runtime\":\"104 min\",\"Genre\":\"Action, Crime, Thriller\",\"Director\":\"Justin Lin\",\"Writer\":\"Chris Morgan\",\"Actors\":\"Lucas Black, Zachery Ty Bryan, Shad Moss\",\"Plot\":\"A teenager becomes a major competitor in the world of drift racing after moving in with his father in Tokyo to avoid a jail sentence in America.\",\"Language\":\"English, Japanese, Portuguese\",\"Country\":\"United States, Germany, Japan\",\"Awards\":\"1 win & 4 nominations\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BMTQ2NTMxODEyNV5BMl5BanBnXkFtZTcwMDgxMjA0MQ@@._V1_SX300.jpg\",\"Ratings\":[{\"Source\":\"Internet Movie Database\",\"Value\":\"6.0/10\"},{\"Source\":\"Rotten Tomatoes\",\"Value\":\"38%\"},{\"Source\":\"Metacritic\",\"Value\":\"45/100\"}],\"Metascore\":\"45\",\"imdbRating\":\"6.0\",\"imdbVotes\":\"259,218\",\"imdbID\":\"tt0463985\",\"Type\":\"movie\",\"DVD\":\"26 Sep 2006\",\"BoxOffice\":\"\$62,514,415\",\"Production\":\"N/A\",\"Website\":\"N/A\",\"Response\":\"True\"}\n"
                    )
                        .withHeader("content-type", "application/json; charset=utf-8")
                )
        )
        server.stubFor(
            get(urlEqualTo("/?apikey=$apiKey&i=tt1013752"))
                .willReturn(
                    ok().withBody(
                        "{\"Title\":\"Fast & Furious\",\"Year\":\"2009\",\"Rated\":\"PG-13\",\"Released\":\"03 Apr 2009\",\"Runtime\":\"107 min\",\"Genre\":\"Action, Thriller\",\"Director\":\"Justin Lin\",\"Writer\":\"Chris Morgan, Gary Scott Thompson\",\"Actors\":\"Vin Diesel, Paul Walker, Michelle Rodriguez\",\"Plot\":\"Brian O'Conner, back working for the FBI in Los Angeles, teams up with Dominic Toretto to bring down a heroin importer by infiltrating his operation.\",\"Language\":\"English, Spanish\",\"Country\":\"United States, Japan\",\"Awards\":\"6 wins & 2 nominations\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BYjQ1ZTMxNzgtZDcxOC00NWY5LTk3ZjAtYzRhMDhlNDZlOWEzXkEyXkFqcGdeQXVyNzkwMjQ5NzM@._V1_SX300.jpg\",\"Ratings\":[{\"Source\":\"Internet Movie Database\",\"Value\":\"6.6/10\"},{\"Source\":\"Rotten Tomatoes\",\"Value\":\"28%\"},{\"Source\":\"Metacritic\",\"Value\":\"46/100\"}],\"Metascore\":\"46\",\"imdbRating\":\"6.6\",\"imdbVotes\":\"276,684\",\"imdbID\":\"tt1013752\",\"Type\":\"movie\",\"DVD\":\"28 Jul 2009\",\"BoxOffice\":\"\$155,064,265\",\"Production\":\"N/A\",\"Website\":\"N/A\",\"Response\":\"True\"}\n"
                    )
                        .withHeader("content-type", "application/json; charset=utf-8")
                )
        )
        server.stubFor(
            get(urlEqualTo("/?apikey=$apiKey&i=tt1596343"))
                .willReturn(
                    ok().withBody(
                        "{\"Title\":\"Fast Five\",\"Year\":\"2011\",\"Rated\":\"PG-13\",\"Released\":\"29 Apr 2011\",\"Runtime\":\"130 min\",\"Genre\":\"Action, Adventure, Crime\",\"Director\":\"Justin Lin\",\"Writer\":\"Chris Morgan, Gary Scott Thompson\",\"Actors\":\"Vin Diesel, Paul Walker, Dwayne Johnson\",\"Plot\":\"Dominic Toretto and his crew of street racers plan a massive heist to buy their freedom while in the sights of a powerful Brazilian drug lord and a dangerous federal agent.\",\"Language\":\"English, Portuguese, Spanish, Italian, French\",\"Country\":\"United States, Brazil, Japan\",\"Awards\":\"9 wins & 21 nominations\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BMTUxNTk5MTE0OF5BMl5BanBnXkFtZTcwMjA2NzY3NA@@._V1_SX300.jpg\",\"Ratings\":[{\"Source\":\"Internet Movie Database\",\"Value\":\"7.3/10\"},{\"Source\":\"Rotten Tomatoes\",\"Value\":\"77%\"},{\"Source\":\"Metacritic\",\"Value\":\"66/100\"}],\"Metascore\":\"66\",\"imdbRating\":\"7.3\",\"imdbVotes\":\"368,801\",\"imdbID\":\"tt1596343\",\"Type\":\"movie\",\"DVD\":\"04 Oct 2011\",\"BoxOffice\":\"\$209,837,675\",\"Production\":\"N/A\",\"Website\":\"N/A\",\"Response\":\"True\"}\n"
                    )
                        .withHeader("content-type", "application/json; charset=utf-8")
                )
        )
        server.stubFor(
            get(urlEqualTo("/?apikey=$apiKey&i=tt1905041"))
                .willReturn(
                    ok().withBody(
                        "{\"Title\":\"Fast & Furious 6\",\"Year\":\"2013\",\"Rated\":\"PG-13\",\"Released\":\"24 May 2013\",\"Runtime\":\"130 min\",\"Genre\":\"Action, Adventure, Thriller\",\"Director\":\"Justin Lin\",\"Writer\":\"Chris Morgan, Gary Scott Thompson\",\"Actors\":\"Vin Diesel, Paul Walker, Dwayne Johnson\",\"Plot\":\"Hobbs has Dominic and Brian reassemble their crew to take down a team of mercenaries: Dominic unexpectedly gets sidetracked with facing his presumed deceased girlfriend, Letty.\",\"Language\":\"English, Spanish, Russian, Japanese, Cantonese, Dutch\",\"Country\":\"United States, Japan, Spain, United Kingdom\",\"Awards\":\"10 wins & 22 nominations\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BMTM3NTg2NDQzOF5BMl5BanBnXkFtZTcwNjc2NzQzOQ@@._V1_SX300.jpg\",\"Ratings\":[{\"Source\":\"Internet Movie Database\",\"Value\":\"7.0/10\"},{\"Source\":\"Rotten Tomatoes\",\"Value\":\"71%\"},{\"Source\":\"Metacritic\",\"Value\":\"61/100\"}],\"Metascore\":\"61\",\"imdbRating\":\"7.0\",\"imdbVotes\":\"383,258\",\"imdbID\":\"tt1905041\",\"Type\":\"movie\",\"DVD\":\"29 Oct 2013\",\"BoxOffice\":\"\$238,679,850\",\"Production\":\"N/A\",\"Website\":\"N/A\",\"Response\":\"True\"}\n"
                    )
                        .withHeader("content-type", "application/json; charset=utf-8")
                )
        )
        server.stubFor(
            get(urlEqualTo("/?apikey=$apiKey&i=tt2820852"))
                .willReturn(
                    ok().withBody(
                        "{\"Title\":\"Furious 7\",\"Year\":\"2015\",\"Rated\":\"PG-13\",\"Released\":\"03 Apr 2015\",\"Runtime\":\"137 min\",\"Genre\":\"Action, Adventure, Thriller\",\"Director\":\"James Wan\",\"Writer\":\"Chris Morgan, Gary Scott Thompson\",\"Actors\":\"Vin Diesel, Paul Walker, Dwayne Johnson\",\"Plot\":\"Deckard Shaw seeks revenge against Dominic Toretto and his family for his comatose brother.\",\"Language\":\"English, Thai, Arabic, Spanish\",\"Country\":\"United States, China, Japan, Canada, United Arab Emirates\",\"Awards\":\"36 wins & 36 nominations\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BMTQxOTA2NDUzOV5BMl5BanBnXkFtZTgwNzY2MTMxMzE@._V1_SX300.jpg\",\"Ratings\":[{\"Source\":\"Internet Movie Database\",\"Value\":\"7.1/10\"},{\"Source\":\"Rotten Tomatoes\",\"Value\":\"82%\"},{\"Source\":\"Metacritic\",\"Value\":\"67/100\"}],\"Metascore\":\"67\",\"imdbRating\":\"7.1\",\"imdbVotes\":\"376,958\",\"imdbID\":\"tt2820852\",\"Type\":\"movie\",\"DVD\":\"15 Sep 2015\",\"BoxOffice\":\"\$353,007,020\",\"Production\":\"N/A\",\"Website\":\"N/A\",\"Response\":\"True\"}\n"
                    )
                        .withHeader("content-type", "application/json; charset=utf-8")
                )
        )
        server.stubFor(
            get(urlEqualTo("/?apikey=$apiKey&i=tt4630562"))
                .willReturn(
                    ok().withBody(
                        "{\"Title\":\"The Fate of the Furious\",\"Year\":\"2017\",\"Rated\":\"PG-13\",\"Released\":\"14 Apr 2017\",\"Runtime\":\"136 min\",\"Genre\":\"Action, Adventure, Crime\",\"Director\":\"F. Gary Gray\",\"Writer\":\"Gary Scott Thompson, Chris Morgan\",\"Actors\":\"Vin Diesel, Jason Statham, Dwayne Johnson\",\"Plot\":\"When a mysterious woman seduces Dominic Toretto into the world of terrorism and a betrayal of those closest to him, the crew face trials that will test them as never before.\",\"Language\":\"English, Russian, Spanish\",\"Country\":\"China, United States, Japan\",\"Awards\":\"2 wins & 12 nominations\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BMjMxODI2NDM5Nl5BMl5BanBnXkFtZTgwNjgzOTk1MTI@._V1_SX300.jpg\",\"Ratings\":[{\"Source\":\"Internet Movie Database\",\"Value\":\"6.6/10\"},{\"Source\":\"Rotten Tomatoes\",\"Value\":\"67%\"},{\"Source\":\"Metacritic\",\"Value\":\"56/100\"}],\"Metascore\":\"56\",\"imdbRating\":\"6.6\",\"imdbVotes\":\"221,120\",\"imdbID\":\"tt4630562\",\"Type\":\"movie\",\"DVD\":\"11 Jul 2017\",\"BoxOffice\":\"\$226,008,385\",\"Production\":\"N/A\",\"Website\":\"N/A\",\"Response\":\"True\"}\n"
                    )
                        .withHeader("content-type", "application/json; charset=utf-8")
                )
        )
        server.stubFor(
            get(urlEqualTo("/?apikey=$apiKey&i=tt5433138"))
                .willReturn(
                    ok().withBody(
                        "{\"Title\":\"F9: The Fast Saga\",\"Year\":\"2021\",\"Rated\":\"PG-13\",\"Released\":\"25 Jun 2021\",\"Runtime\":\"143 min\",\"Genre\":\"Action, Crime, Thriller\",\"Director\":\"Justin Lin\",\"Writer\":\"Daniel Casey, Justin Lin, Alfredo Botello\",\"Actors\":\"Vin Diesel, Michelle Rodriguez, Jordana Brewster\",\"Plot\":\"Dom and the crew must take on an international terrorist who turns out to be Dom and Mia's estranged brother.\",\"Language\":\"English\",\"Country\":\"United States, Thailand, Canada, Japan\",\"Awards\":\"4 nominations\",\"Poster\":\"https://m.media-amazon.com/images/M/MV5BMjI0NmFkYzEtNzU2YS00NTg5LWIwYmMtNmQ1MTU0OGJjOTMxXkEyXkFqcGdeQXVyMjMxOTE0ODA@._V1_SX300.jpg\",\"Ratings\":[{\"Source\":\"Internet Movie Database\",\"Value\":\"5.2/10\"},{\"Source\":\"Rotten Tomatoes\",\"Value\":\"59%\"},{\"Source\":\"Metacritic\",\"Value\":\"58/100\"}],\"Metascore\":\"58\",\"imdbRating\":\"5.2\",\"imdbVotes\":\"106,141\",\"imdbID\":\"tt5433138\",\"Type\":\"movie\",\"DVD\":\"30 Jul 2021\",\"BoxOffice\":\"\$173,005,945\",\"Production\":\"N/A\",\"Website\":\"N/A\",\"Response\":\"True\"}\n"
                    )
                        .withHeader("content-type", "application/json; charset=utf-8")
                )
        )
    }

    override suspend fun afterSpec(spec: Spec) {
        server.stop()
    }

    override suspend fun beforeTest(testCase: TestCase) {
        if (testCase.isRootTest()) {
            server.resetRequests()
        }
    }

}