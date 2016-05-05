# SF-Filming-Locations
This app shows locations in San Francisco where movies have been filmed, on a map. Users can search for a particular movie, location or a director. It has a minSdkVersion 14, though it can be lowered. 14 was chosen with the ViewPropertyAnimator in mind as it might be used in future for animation.

# How it works
When the app starts for the first time, all the data is downloaded from the [SODA SFGOV](https://data.sfgov.org/resource/wwmu-gmzc.json) api and saved locally in a sqlite database. All further queries on the dataset are executed on this database.

The data from the above api contains only location names, not latitude or longitude. So a geocoding request is made to [Google Maps Geocoding api](https://developers.google.com/maps/documentation/geocoding/intro?csw=1#Geocoding) to get the latitude and longitude associated with a location and then it is shown on the map. Once the latitude and longitude of a location are received they are also saved locally in the database to reduce future network calls.

# Future Tasks
Implement custom markers and custom info window. Implement paging, 1000 results at a time, in requests to the SODA SFGOV api. Show more info, like movie poster, about a movie when user clicks on info window from [TheMovieDb](http://docs.themoviedb.apiary.io/#reference/search/searchmovie) api.

#License
The MIT License

Copyright (c) 2016 Raghubansh Mani

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
