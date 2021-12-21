# Technical Report 

## Splash view
Application shows a splash screen when opened. The splash screen shows for 2 seconds and then launches the log in view. 
Log in Activity
Log in view is used to authenticate users. It uses Google firebase authentication backend to authenticate users by basic email and password. In the login view the user can log in and create an account. Once the user has entered the right credentials, the application allows the user to proceed to the list view.
## List Fragment
The list view shows  a list of cards for the parkings uploaded to google firebase realtime database. The card shows the parking’s title, the email of the parking’s owner, an icon showing the category of the parking, green tree for a nature parking, blue P for a public parking, red P for a private parking, and a brown house for a camping. The card it will also show an image of the parking that was previously uploaded to google firebase storage. Images are loaded using Picasso library. A rating bar is also shown, the value of the rating bar is the average off all parking’s reviews. Parkings can be deleted by swiping left and can be edited by swiping right. Only the owner of the parking can edit or delete it. There is a toggle button on the toolbar that switches between user’s parkings and all parkings. Navigation to other fragments can be performed via nav drawer, floating button or menu. In order to add a new parking the user can press the floating button or select add in the menu or nav drawer. This action brings the user to the add view.
## Add Fragment
The add fragment provide the user with the functionality of adding a new parking the the list. The elements to be filled in this view are the title, a further description, a radio button to select the category of the parking which can be nature, public, private or camping.  The view also lets the user to pick an image for the parking and a location.
Title and description are text inputs. Material Design library has been used to implement this feature. Image can be loaded from the gallery by selecting image in the menu. Image is displayed using picaso library. Once the parking is saved, the image is uploaded to google firebase storage for further retrieving. First time the user access the add view, the app will ask for permission to access current location. Map shows current location when first access the add view and updates the locations as the device moves using google libraries “play-services-map” and “play-services-location”. If the user decides to edit the location, it can access the edit location view by pressing the location selection on the toolbar menu. This brings the user to another view where the marker can be dragged as desired. By pressing the back button, the application returns to the add view but with the new location on the map. When all the details have been filled, the user can select save on the menu to upload the information to Firebase realtime database and return to the list view. The new parking will show on the list with all the details shown in the card and with zero stars on the rating bar as the parking does not have reviews yet.  
From the list view, if the user swipe to the right on a parking, the application starts the add view but in this case the functionality is to edit the parking instead of creating a new parking. However, if the user clicks on a parking card, the application will open a screen to visualize parking.
## View fragment
In this screen, the user can check the parking details such as title, description, image, map location, category icon but it can also make a review. The review consists of a comment, a user, a date and a rating. The rating can be done with a rating bar from zero to 5 stars. When a review is done, review is uploaded to firebase realtime database. There is a rating element attach to the parking which is calculated with the average of all the parking’s reviews.
## Map fragment
The map view shows the parkings locations in a google map. Parkings will show on the map with a different icon depending on the category of the parking. User can zoom in and out and move the map as desired to locate all locations. Markers can be click to show further at the top of the screen. There is a toggle switch on the toolbar menu to switch between all parkings an user’s parking. 
## UX/DX approach adopted
Navigation via Nav drawer to navigate between main fragments (Map, Add, List)
Swipe left to delete parking in a recycler view and swipe right to edit parking in a recycler view.
Material design elements used to give the user a better experience. 
MVVM design pattern followed with two way data binding.
## Git Repository
Git branching model approach adopted. 
Master branch where new versions are released.
Develop branch with is the main branch where all de new features are merged. Develop is merge into master when a new released is ready.
Feature branches to develop new features. The develop branch splits into a new feature branch when a new feature needs to be implemented and is merged back to develop when the feature works as expected. 



## UML diagram

 <img src="https://github.com/AlvaroSanchezDomingo/camper-parking-android-a2/blob/master/images/UML_camper_parking.png">



## References
### Radio button
https://stackoverflow.com/questions/39427178/how-to-bind-method-on-radiogroup-on-checkchanged-event-with-data-binding
### Location Service
https://developers.google.com/android/reference/com/google/android/gms/location/LocationServices#getFusedLocationProviderClient(android.content.Context)
### Map on fragment
https://www.it-swarm-es.com/es/android/getmapasync-en-fragment/824270913/
### Menu
https://developer.android.com/guide/fragments/appbar
### Fragment arguments
https://stackoverflow.com/questions/55781830/check-if-arguments-exists-when-navigating-to-fragment-using-safeargs
### Rating bar
https://www.develou.com/ratingbar-en-android/
### Resource to bitmap
https://www.it-swarm-es.com/es/android/marcador-personalizado-en-los-mapas-de-google-en-android-con-el-icono-de-activo-vectorial/829632466/
