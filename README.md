
# Android candidate test (Applicant Coding Challenge)
Task 1
-----
Loading data from given api (https://fake-poi-api.mytaxi.com/?p1Lat={Latitude1}&p1Lon={Longitude1}&p2Lat={Latitude2}&p2Lon={Longit
ude2 }) and show in a list

Task 2
-----
Implement a map Activity/Fragment with Google Maps. Show all available vehicles on the map. Use the bounds of the map to request the
vehicles.
The map should zoom and center on a specific vehicle when it is selected in the previously implemented list.



## Features

- Shows a list of vehicles with type, address and state
- Shows a map view using [Google Maps Platform APIs]
- Shows available vehicles in the map within the given bound
- The map zoom and center on a specific vehicle when selected from the list
- Request API for changes while app is live and show changes in list and map





## Description

The app consists two fragments [VehiclesFragment] and [MapFragment] which are contained in [MainActivity]. Android Navigation component along with a bottom navigation view is used to navigate between the fragments in the [MainActivity].
[VehiclesFragment] shows a list of vehicle retrieved from given API. [MapsFragment] shows all available vehicles as customized marker with heading value. [MapsFragment] uses Google Maps Platform APIs to show a map. Zooming and panning is enabled in the map. When user selects a vehicle in list they are redirected to the map and zoomed in to the marker of the selected vehicle.

[VehicleListViewModel] allows the UI components to interact with data that are provided by [VehicleListRepository]. [VehicleListRepository] uses [VehicleDataSource] and [VehicleDao] to store and provide [VehicleData] when requested.

[VehicleDataSource] uses [VehicleAPI] to get data from given API and then store the data in the [VehicleDB] using [VehicleDao]. It also simultaneously request the API every 20 seconds to get latest data and sync with existing data stored in [VehicleDB] so that user is able to see updated data.

### Libraries
* [Android Support Library][support-lib]
* [Android Architecture Components][arch]
* [Retrofit][retrofit] for REST api communication
* [Glide][glide] for image loading
* [espresso][espresso] for UI tests

[1]: app/src/main/java/com/android/example/paging/pagingwithnetwork/reddit/repository/inDb/DbRedditPostRepository.kt
[2]: app/src/main/java/com/android/example/paging/pagingwithnetwork/reddit/repository/inMemory/byItem/InMemoryByItemRepository.kt
[3]: app/src/main/java/com/android/example/paging/pagingwithnetwork/reddit/db/RedditPostDao.kt
[4]: app/src/main/java/com/android/example/paging/pagingwithnetwork/reddit/repository/inMemory/byItem/ItemKeyedSubredditPagingSource.kt
[5]: app/src/main/java/com/android/example/paging/pagingwithnetwork/reddit/repository/inMemory/byPage/InMemoryByPageKeyRepository.kt
[6]: app/src/main/java/com/android/example/paging/pagingwithnetwork/reddit/repository/inMemory/byPage/PageKeyedSubredditPagingSource.kt
[7]: https://www.reddit.com/dev/api/#listings
[mockwebserver]: https://github.com/square/okhttp/tree/master/mockwebserver
[support-lib]: https://developer.android.com/topic/libraries/support-library/index.html
[arch]: https://developer.android.com/arch
[espresso]: https://google.github.io/android-testing-support-library/docs/espresso/
[retrofit]: http://square.github.io/retrofit
[glide]: https://github.com/bumptech/glide
[mockito]: http://site.mockito.org
[retrofit-mock]: https://github.com/square/retrofit/tree/master/retrofit-mock