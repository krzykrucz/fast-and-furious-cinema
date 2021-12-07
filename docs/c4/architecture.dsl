workspace {

    model {
        moviegoer = person "Moviegoer"
        cinemaowner = person "Cinema owner"

        cinema = softwareSystem "Small Cinema Backend" {
            cinemaApi = container "Small Cinema API" "Modular monolith providing all server-side functionality for Small Cinema" "Docker/Ktor/Kotlin" {
                catalog = component "Catalog" "Movie catalog module" "Kotlin package"
                showtimes = component "Showtimes" "Movie show times module" "Kotlin package"
                ratings = component "Ratings" "Movie ratings module" "Kotlin package"
                movieDetails = component "Movie details" "Read model for movie details" "Kotlin package"

                catalogDB = component "Catalog store" "" "Kotlin in-mem. collection" "Database"
                showtimesDB = component "Showtimes store" "" "Kotlin in-mem. collection" "Database"
                movieCatalogView = component "Movie catalog view store" "for showtimes" "Kotlin in-mem. collection" "Database"
                ratingsDB = component "Ratings store" "" "Kotlin in-mem. collection" "Database"
                movieDetailsView = component "Movie details view store" "" "Kotlin in-mem. collection" "Database"
            }
        }
        omdb = softwareSystem "OMDb API" "Database about movies" "Existing System"

       moviegoer -> ratings "Leaves a movie rating"
       moviegoer -> showtimes "Fetches movie times"
       moviegoer -> movieDetails "Fetches movie details"
       cinemaowner -> showtimes "Cancels shows"
       cinemaowner -> showtimes "Updates show times and prices"

       catalog -> omdb "Fetches movie info from"
       catalog -> catalogDB "Stores movie info in"

       showtimes -> catalog "Listens to movie added to catalog events from"
       showtimes -> showtimesDB "Stores shows in"
       showtimes -> movieCatalogView "Reads cataloged movies from"

       ratings -> catalog "Listens to movie added to catalog events from"
       ratings -> ratingsDB "Stores movie ratings in"

       movieDetails -> catalog "Listens to movie added to catalog events from"
       movieDetails -> ratings "Listens to movie rated events from"
       movieDetails -> movieDetailsView "Reads/persists a view in"

    }

    views {
        systemContext cinema {
            include *
            autoLayout
        }
        container cinema {
            include *
            autoLayout
        }
        component cinemaApi {
            include *
            autoLayout
        }

        styles {
            element "Person" {
                color #ffffff
                fontSize 22
                shape Person
                background #08427b
            }
            element "Software System" {
                background #1168bd
                color #ffffff
            }
            element "Existing System" {
                background #999999
                color #ffffff
            }
            element "Container" {
                background #438dd5
                color #ffffff
            }
            element "Web Browser" {
                shape WebBrowser
            }
            element "Mobile App" {
                shape MobileDeviceLandscape
            }
            element "Database" {
                shape Cylinder
            }
            element "Component" {
                background #85bbf0
                color #000000
            }
            element "Failover" {
                opacity 25
            }
        }
    }

}