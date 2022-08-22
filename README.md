Android candidate test (Applicant Coding Challenge)
===================================

Task 1
-----
Loading data from given [api] and showing them in a list.

Task 2
-----
Implement a map Activity/Fragment with Google Maps. Show all available vehicles on the map. Use the bounds of the map to request the
vehicles.
The map should zoom in and center on a specific vehicle when it is selected in the previously implemented list.

## Project structure
The project is structured following the guideline of [Android Architecture Components]. 
The `ui` package contains `activity` and `fragments` which renders the data on the screen and `viewModel` is used to contain and manage the state. 
Additionally there are `vo` and `adapter` packages which contain `ViewObject` and `adapters`.
The `repositories` and `data` packages are part of the data layer where `repositories` handle the business logic.
The `data` package contains `retrofit` client for API calls and `room` for local data storage and sync.
To manage dependencies between components `dagger-hilt` is used and the module is kept in `di` package
`other` package contains the followings
- [ConnectivityObserver][17] helps to observe connectivity
- [BitmapHelper][18] helps to draw marker from vector drawable 
- [Constants][19] constant object/values
- [Ext][20] extension functions or helper functions
`Clean` architecture and `SOLID` principle has been strictly maintain during the process of the development.
`Unit Test`, `Integration Test` and `E2E Testing` has also been respectively implemented.

## Generic
In [MainActivity][1] [Navigation Component] is used to host all the `fragments` in the `NavHostFragment`. 
A [Bottom Navigation View] is implemented so that user can easily navigate between `fragments`.
[Dagger-hilt] is used for dependency injection.
[Turbine] is used to test flow and [Truth] for easy assertion in unit tests.
[Retrofit] is used for API services and [Room] for storing and managing data locally.
[api] is implemented in [VehicleApi][12].

## Show useful vehicle information
Here, we have a JSON as API response which contains a list of vehicle information (id, coordinate, fleetType, state and heading)
In the [VehiclesFragment][2], list of all available vehicles as [SingleVehicle][7] is shown. Each list item contains the value of `type`, `address-line` and `state`
[GeoCoder] is used to convert all `Coordinate` from [VehicleData][13] into `address-line`
The API often returns 100 or more vehicle information, hence, [paging-3] is used to slow loading the items in the list and provide a better user experience.
[VehicleDao][15] provides access to `VehicleDB` which is used to store all new data from the list or update if a change occurs in the existing vehicle data.
When the app is live, it will request [api] every 20 seconds using coroutine's `Dispatchers.IO` context.
[Kotlin Flow] is used for data streaming. Hence, with the change syncing in `vehicles` table, `paging-3` library have `PagingSource` API which maps data from `Flow` and stream it to [VehicleRecyclerViewAdapter][5] which is a `PagingDataAdapter`
[VehicleListViewModel][4] holds the state of all the data so that the list page survives the configuration changes.

## Show all vehicle as markers in the map
In the [MapFragment][3] all vehicle is shown as markers. Here, a `carIcon` is represented as custom marker which is created using [bitmap-helper]. 
Response from the [VehicleApi][12] is fetched and mapped into `List` of [VehicleMarker][8] which is set on the map as markers according to their `LatLng`.
All the basic gestures(e.g., zoom, pan) are enabled in the map. When a [SingleVehicle][7] is selected from [VehiclesFragment][2], [MapFragment][3] opens and zoom in towards the selected [VehicleMarker][8].
[MarkerInfoWindowAdapter][6] is used to show the details of a marker in the map when tapped.

## Update data
It occurred to me that the [VehicleApi][12] response is always changing. Hence, I developed [VehicleDataSource][10] to load data from `API` and then sync with [VehicleDB][16]. 
Upon receiving data from `API` the app is deleting all vehicle information that are not in the latest data set. Then, it updates any existing vehicle information and add new vehicle information in `VerhiclrDB`.
`DELETE FROM vehicles WHERE vehicleId NOT IN (:list)` is the query to delete all vehicle information not in the latest data set.
`VehicleDataSource` repeats the whole process every 20 seconds in a `Dispatchers.IO` coroutine context.
This enables zero loading time and a very neat user experience.

### Libraries
* [Google Maps SDK for Android][maps-sdk] for map and marker
* [Android Support Library][support-lib]
* [Android Architecture Components][arch] for MVVM and clean architecture
* [Bottom Navigation View][bottom-nav] for bottom menu
* [Navigation Components][nav] for hosting fragments
* [Jetpack Paging Library][paging-3] for loading data in recycler view
* [Kotlin Flow][flow] for asynchronous data transfer
* [Retrofit][retrofit] for REST api communication
* [Room DB][room] for local storage and caching
* [Dagger-hilt][hilt-android] for dependency injection
* [SDP][sdp] for dynamic size unit
* [Truth][truth] for Unit tests assertion
* [Turbine][turbine] for testing flow
* [espresso][espresso] for UI tests
* [GeoCoder][geoCoder] for getting address from coordinates

[1]: app/src/main/java/com/george/freenowassessment/ui/MainActivity.kt
[2]: app/src/main/java/com/george/freenowassessment/ui/fragments/VehiclesFragment.kt
[3]: app/src/main/java/com/george/freenowassessment/ui/fragments/MapFragment.kt
[4]: app/src/main/java/com/george/freenowassessment/ui/VehicleListViewModel.kt
[5]: app/src/main/java/com/george/freenowassessment/ui/adapters/VehicleRecyclerViewAdapter.kt
[6]: app/src/main/java/com/george/freenowassessment/ui/adapters/MarkerInfoWindowAdapter.kt
[7]: app/src/main/java/com/george/freenowassessment/ui/vo/SingleVehicle.kt
[8]: app/src/main/java/com/george/freenowassessment/ui/vo/VehicleMarker.kt
[9]: app/src/main/java/com/george/freenowassessment/repositories/VehicleListRepository.kt
[10]: app/src/main/java/com/george/freenowassessment/repositories/VehicleDataSource.kt
[11]: app/src/main/java/com/george/freenowassessment/repositories/VehicleDataSource.kt
[12]: app/src/main/java/com/george/freenowassessment/data/remote/VehicleApi.kt
[13]: app/src/main/java/com/george/freenowassessment/data/remote/responses/VehicleList.kt
[14]: app/src/main/java/com/george/freenowassessment/data/local/Vehicle.kt
[15]: app/src/main/java/com/george/freenowassessment/data/local/VehicleDao.kt
[16]: app/src/main/java/com/george/freenowassessment/data/local/VehicleDB.kt
[17]: app/src/main/java/com/george/freenowassessment/other/connectivity/ConnectivityObserver.kt
[18]: app/src/main/java/com/george/freenowassessment/other/BitmapHelper.kt
[19]: app/src/main/java/com/george/freenowassessment/other/Constants.kt
[20]: app/src/main/java/com/george/freenowassessment/other/Ext.kt
[support-lib]: https://developer.android.com/topic/libraries/support-library/index.html
[arch]: https://developer.android.com/arch
[nav]: https://developer.android.com/guide/navigation/navigation-getting-started
[espresso]: https://google.github.io/android-testing-support-library/docs/espresso/
[retrofit]: http://square.github.io/retrofit
[paging-3]: https://developer.android.com/topic/libraries/architecture/paging/v3-overview
[api]: https://fake-poi-api.mytaxi.com/?p1Lat=53.694865&p1Lon=9.757589&p2Lat=53.394655&p2Lon=10.099891
[bottom-nav]: https://material.io/components/bottom-navigation/android#using-bottom-navigation
[maps-sdk]: https://developers.google.com/maps/documentation/android-sdk/
[bitmap-helper]: https://github.com/googlecodelabs/maps-platform-101-android/blob/main/solution/app/src/main/java/com/google/codelabs/buildyourfirstmap/BitmapHelper.kt
[room]: https://developer.android.com/jetpack/androidx/releases/room
[hilt-android]: https://developer.android.com/training/dependency-injection/hilt-android
[sdp]: https://github.com/intuit/sdp
[flow]: https://developer.android.com/kotlin/flow
[truth]: https://truth.dev/
[turbine]: https://github.com/cashapp/turbine
[geoCoder]: https://developer.android.com/reference/android/location/Geocoder
