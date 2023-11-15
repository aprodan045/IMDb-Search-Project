[![GitHub issues](https://img.shields.io/github/issues/Naereen/StrapDown.js.svg)](https://github.com/aprodan045/IMDb-Search-Project/issues/)

# IMDb-Search-Project
This is a rest api backend project that uses Spring Boot + Hibernate for retrieving information about films, tv series/mini-series, actors, directors and so on.

The project also has features for creating an account, just like on IMDb, where you can add your favorite movies, tv shows on the favorite list or the watchlist.

The project is not using any movies related database or any other api for getting a movie info, the information is scraped from the [IMDb website](https://www.imdb.com/?ref_=nv_home) using JSoup library.

### Api Endpoints
| Method |                    Endpoint                     | Request                                                                                                                          | 
|--------|:-----------------------------------------------:|----------------------------------------------------------------------------------------------------------------------------------|
| GET    |         http://localhost:8080/api/actor         | name = (name of the actor) or id = (id of the from imdb, usually found in the url page of the actor)                             | 
| GET    |       http://localhost:8080/api/director        | name = (name of the director) or id = (id of the from imdb, usually found in the url page of the director)                       |
| GET    |         http://localhost:8080/api/movie         | name = (name of the movie) or id = (id of the from imdb, usually found in the url page of the movie)                             |
| GET    |        http://localhost:8080/api/tv-show        | name = (name of the tv show) or id = (id of the from imdb, usually found in the url page of the tv show)                         |
| GET    |        http://localhost:8080/api/top-250        | top = movie (for top 250 movies of all time) or top = tv show (for top 250 tv shows of all time)                                 | 
| GET    |     http://localhost:8080/api/most-popular      | most popular = movie (for most popular movies at that time) or most popular = tv show (for most popular tv shows at that time)   | 
| POST   |      http://localhost:8080/api/user/singup      | { "firstName": "string", "lastName": "string", "email": "string", "password": "string", "role": "string"                         | 
| POST   |      http://localhost:8080/api/user/singin      | { "username": "string", "password": "string"                                                                                     |
| DELETE | http://localhost:8080/api/user/delete-user/{id} | id = id of the user from database                                                                                                |
| POST   |    http://localhost:8080/api/user/add-movie     | list = watchlist/checkins (to add a movie into the watchlist/checkins) and id/name = id/name ( of the movie you want to add )    |
| POST   |   http://localhost:8080/api/user/add-tv-show    | list = watchlist/checkins (to add a tv show into the watchlist/checkins) and id/name = id/name ( of the tv show you want to add ) |
| POST   |       http://localhost:8080/api/user/rate       | movie/tv-show = id (id of the movie/tv show you want to rate) and rating = value from 1-10                                       |
| GET    |     http://localhost:8080/api/user/get-list     | list = watchlist / checkins / reviews                                                                                            |


### Dependencies
* Java 17
* Spring Boot
* Spring Data JPA
* Spring Security
* Spring Validation
* SLF4J
* Lombok
* JSoup
### Author
* [Alex Prodan](https://github.com/aprodan045)

### Demo
