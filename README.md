Android candidate test (Applicant Coding Challenge)
===================================

Task 1
-----
Loading data from given [api] and show in a list

Task 2
-----
Implement a map Activity/Fragment with Google Maps. Show all available vehicles on the map. Use the bounds of the map to request the
vehicles.
The map should zoom and center on a specific vehicle when it is selected in the previously implemented list.

## Project structure
The project is structured following the guideline of [Android Architecture Components]. 
The `ui` package contains `activity` and `fragments` which renders the data in screen and `viewModel` is used to contain and manage the state. 
Additionally there is a `vo` package and `adapter` package which contains `ViewObject` and adapters.
The `repositories` and `data` packages are part of data layer where `repositories` handles the business logic.
The `data` package contains `retrofit` client for API calls and `room` for local data storage and sync.
To manage dependencies between components `dagger-hilt` is used and the module is kept in `di` package
`other` package contains followings
- [ConnectivityObserver][] helps to observe connectivity.
- [BitmapHelper][] helps to draw marker from vector drawable. 
- [Constants][] constant object/values.
- [Ext][] extension functions or helper functions classes.
`Clean` architecture and `SOLID` principle has been strictly maintain during the process of development.
`Unit Test`, `Integration Test` and `E2E Testing` is also respectively implemented.

## Generic
In [MainActivity][] there is a `NavHostFragment` that hosts the `fragments`. A [Bottom Navigation View] is implemented so that user can easily navigate between `fragments`.

## Show useful vehicle information
Here we have a JSON as API response which contains a list of vehicle information (id, coordinate, fleetType, state and heading)
In [VehiclesFragment][] list of all available vehicles is shown. Each list item contains the value of `type`, `address-line` and `state`
The API often returns 100 or more vehicle information, hence, [paging-3] is used to slow loading the items in the list and provide a better user experience.
[VehicleDao][] provide access to `VehicleDB` which is used to store all new data from the list or update if change occurs in existing vehicle data.
When the app is live it will request [api] every 20 seconds using a coroutine's `Dispatchers.IO` context.
`Coroutine Flow` is used for data streaming hence with the change syncing in `vehicles` table, `paging-3` library have `PagingSource` API which maps data from `Flow` and stream it to Recycler view adapter which is a `PagingDataAdapter`
[VehicleListViewModel][] holds the state of all data so that the list page survives the configuration change.

## 


### Libraries
* [Android Support Library][support-lib]
* [Android Architecture Components][arch]
* [Bottom Navigation View][bottom-nav]
* [Navigation Components][nav]
* [Retrofit][retrofit] for REST api communication
* [Truth][truth] for Unit tests assertion
* [espresso][espresso] for UI tests

[1]: app/src/main/java/com/george/freenowassessment/ui/fragments/VehiclesFragment.kt
[2]: app/src/main/java/com/android/example/paging/pagingwithnetwork/reddit/repository/inMemory/byItem/InMemoryByItemRepository.kt
[3]: app/src/main/java/com/android/example/paging/pagingwithnetwork/reddit/db/RedditPostDao.kt
[4]: app/src/main/java/com/android/example/paging/pagingwithnetwork/reddit/repository/inMemory/byItem/ItemKeyedSubredditPagingSource.kt
[5]: app/src/main/java/com/android/example/paging/pagingwithnetwork/reddit/repository/inMemory/byPage/InMemoryByPageKeyRepository.kt
[6]: app/src/main/java/com/android/example/paging/pagingwithnetwork/reddit/repository/inMemory/byPage/PageKeyedSubredditPagingSource.kt
[7]: https://www.reddit.com/dev/api/#listings
[mockwebserver]: https://github.com/square/okhttp/tree/master/mockwebserver
[support-lib]: https://developer.android.com/topic/libraries/support-library/index.html
[arch]: https://developer.android.com/arch
[nav]: https://developer.android.com/guide/navigation/navigation-getting-started
[espresso]: https://google.github.io/android-testing-support-library/docs/espresso/
[retrofit]: http://square.github.io/retrofit
[glide]: https://github.com/bumptech/glide
[mockito]: http://site.mockito.org
[retrofit-mock]: https://github.com/square/retrofit/tree/master/retrofit-mock
[paging-3]: https://developer.android.com/topic/libraries/architecture/paging/v3-overview
[api]: https://fake-poi-api.mytaxi.com/?p1Lat=53.694865&p1Lon=9.757589&p2Lat=53.394655&p2Lon=10.099891
[bottom-nav]: https://material.io/components/bottom-navigation/android#using-bottom-navigation
[truth]: https://truth.dev/