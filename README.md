# SF-Filming-Locations
This app shows locations in San Francisco where movies have been filmed, on a map. Users can search for a particular movie, location or a director. It has a minSdkVersion 14, though it can be lowered. 14 was chosen with the ViewPropertyAnimator in mind as it might be used in future for animation.

# How it works
When the app starts for the first time, all the data is downloaded from the SODA SFGOV api - https://data.sfgov.org/resource/wwmu-gmzc.json and saved locally in a sqlite database. All further queries on the dataset are executed on this database.

The data from the above api contains only location names, not latitude or longitude. So a geocoding request is made to Google's Maps api - https://developers.google.com/maps/documentation/geocoding/intro?csw=1#Geocoding to get the latitude and longitude associated with a location and then it is shown on the map. Once the latitude and longitude of a location are received they are also saved locally in the database to reduce future network calls.

# Future Tasks
Implement custom markers and custom info window. Implement paging, 1000 results at a time, in requests to the SODA SFGOV api. Show more info, like movie poster, about a movie when user clicks on info window from TheMovieDb api - http://docs.themoviedb.apiary.io/#reference/search/searchmovie.
