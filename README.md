# GSCodeChallenge
Code Challenge

# Used Third party libraries
----------------------------
* retrofit2 - https://square.github.io/retrofit/
* okhttp3 - https://square.github.io/okhttp/4.x/okhttp/okhttp3/
* Glide - https://github.com/bumptech/glide
* APIs  - https://api.nasa.gov/ 

Compatibility
-------------
 * **Minimum Android SDK**: GSCodeChallenge requires a minimum API level of 21.
 * **Compile Android SDK**: GSCodeChallenge requires you to compile against API 30 or later.

Development
-----------
* Clone code
* To open the project in Android Studio:

1. Go to *File* menu or the *Welcome Screen*
2. Click on *Open...*
3. Navigate to GSCodeChallenge's root directory.
4. Select `setting.gradle`


Functionality:
-----------
 Calendar à select the date to fetch the image of the day

Options:
-----------
-          Day: will only display image of selected date
-          From day: will display image of selected date till current date (optional feature – yet to finalize limits)

Image can be selected as fav by clicking on star symbol. When we click on fav list, it will list all stared images

-          When offline, all the downloaded image will be shown from cache
-          When online, images are fetched from api
-          Cache limit is 5MB
-          App works on any resolution screen


