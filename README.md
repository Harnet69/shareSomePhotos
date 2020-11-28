# SHARE SOME PHOTO [![Quality gate](https://sonarcloud.io/api/project_badges/quality_gate?project=Harnet69_shareSomePhotos)](https://sonarcloud.io/dashboard?id=Harnet69_shareSomePhotos)
Photo service for sharing and storing images.
According to Google plans to make its Google Photos service limited up to 15GB.

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
Self-configured virtual machine with:
- Ubuntu 20.04
- Node.js
- MongoDB
- Parse Server with Dashboard

## APP FEATURES
- works both in a portrait and a landscape orientation as well

## PROFILE SCREEN
- Focus sets to user name field automatically 
- Two modes switcher(Login and Signup)
- Click to free screen place makes keyboard disappear
- Prevention against empty or white spaces consisting data
- Prevention against duplicated accounts
- E-mail's format checking
- When user filled the login form and push DONE button, a data is sending automatically without clicking on sending button
- Email field hides in Login mode
- Adding user image ask for permission to image library, with rationale(explanation of reasons)

## USERS LIST
- displaying user image(if it was added) and name
- owner of the device haven't shown in user list
- separation line between users items
- spinner for loading
- list refreshes by swiping down
- back arrow to return

## FEEDS LIST
- displaying not a users profile pictures with the author name
- images loads dynamically
- waiting spinner for better UX
- preventing against clicking to 2+ images at the same time

## IMAGE PAGE
- zooming an image by pinching and clicking
