# SHARE SOME PHOTO [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Harnet69_shareSomePhotos&metric=alert_status)](https://sonarcloud.io/dashboard?id=Harnet69_shareSomePhotos)

## ABOUT APP
Photo service for sharing and storing images. According to Google plans to make its Google Photos service paid up to 15GB

## Application video:
![Game process](https://github.com/Harnet69/shareSomePhotos/blob/main/app/GitHubMediaFiles/video.gif)

## TECHNOLOGIES
- Kotlin
- Coroutines instead AsyncTask
- Fragments
- Navigation
- DataBinding
- Permissions
- KTX
- Glide
- PhotoView library for zooming images

## BACKEND(SERVER) SIDE
Self-configured remote virtual machine with:
- Ubuntu 20.04
- Node.js
- MongoDB
- Parse Server with Dashboard

## ANOTHER APP FEATURES
- works both in a portrait and a landscape orientation as well

### PROFILE SCREEN
- Focus sets to user name field automatically 
- Two modes switcher(Login and Signup)
- Click to a free screen place makes keyboard disappear
- Prevention against empty or white spaces consisting data
- Prevention against duplicated accounts
- E-mail's format validation
- When user filled the login form and push DONE button, a data is sending automatically without clicking on sending button
- Email field hides in Login mode
- Adding an image ask for the permission to access to an image library, with rationale (explanation of reasons)
- preview Profile's image by a short click on it
- redirecting to the saving image preview screen by a long click

### USERS LIST
- displaying user image(if it was added) and name
- owner of the device haven't shown in users list
- separation line between users items
- spinner for loading
- list refreshes by swiping down
- back arrow to return

### FEEDS LIST
- displaying not a users profile pictures with the author name
- floating button to adding new image to feeds
- images load dynamically
- feeds sorted by descending order by its date of adding
- waiting spinner for better UX
- preventing against clicking to 2+ images at the same time

### IMAGE VIEWER PAGE
- zooming image
- share, comment and delete buttons
- error message if something goes wrong with loading or saving

### IMAGE PAGE
- zooming an image by pinching and clicking
- waiting spinner
