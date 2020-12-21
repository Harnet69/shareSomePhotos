# SHARE SOME PHOTO [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Harnet69_shareSomePhotos&metric=alert_status)](https://sonarcloud.io/dashboard?id=Harnet69_shareSomePhotos)

## ABOUT APP
Photo service for sharing and storing images. According to Google plans to make its Google Photos service paid up to 15GB

## Application video:
![Game process](https://github.com/Harnet69/shareSomePhotos/blob/main/app/GitHubMediaFiles/video.gif)

## Application installation:
- scan this QR code by an Android phone, download and install the app 
![QR](https://github.com/Harnet69/shareSomePhotos/blob/main/app/GitHubMediaFiles/apk_QR.png)
- download .apk [ShareSomePhoto v.1.1 installer](https://drive.google.com/file/d/1Dc3stkSjsX5LR1Ptn5v1ssLiKGSRLIDm/view?usp=sharing) and install it
- clone a project code from this repo to your computer and run it via Android studio or another Android emulator

## Application pdf presentation: 
[DevNewsRadar presentation]()

## TECHNOLOGIES
- Kotlin
- Google Material Design
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
- bottom navigation pannel
- all lists are prevented against crash by clicking to 2+ items at the same time

### SIGNUP/LOGIN SCREEN
- Two modes switcher(SignUp an LoginIn)
- Hiding Email field in Login mode
- Click to a free screen place makes keyboard disappear
- Animated floating hints for user inputs 
- Switching between input fields by 'NEXT' keyboard button
- More than 10 characters usernames validation with signs counter
- 'Clear a field' icon on an Username field
- Prevention against empty or white spaces consisting user data
- 'Hide/reveal' icon on an Password field
- E-mail's format validation
- User with the same username existence validation
- Error validation messages under an error field
- Keyboard 'DONE' button sends data without clicking on the sending button

### PROFILE SCREEN 
- short click to a Profile picture redirect to image preview screen 
- long click - change profile page through a preview screen (ask for the permission to access to an image library with rationale (explanation of reasons))
- top app bar with user icon image and name
- statistic block with counters:
  - feeds by short click redirect to all user feeds
  - followers --> to following users list
  - following  --> to following users list 

### USERS LIST
- displaying user image(or default if it haven't been added) and user name
- by a short click redirect to the user details screen 
- owner of the device haven't shown in users list
- separation line between users items
- spinner for loading
- list refreshes by swiping down
- error message is shown if something goes wrong

### USER DETAILS
- user images gallery
- short click on image redirect to an image viewer page for full screen view
- top app bar with 
  = thumbnail icon of user profile image
  = user's name
  = follow/unfollow icon

### FEEDS
- displaying users feeds with an image and the author name
- short click to an author name redirect to the user profile page
- short click on an image redirect to an image viewer page
- floating button add a new feed(with asking permission if it haven't been granted already)
- images load dynamically
- feeds sorted by descending order by its date of adding
- waiting spinner for better UX

### IMAGE VIEWER PAGE
- zooming image by tapping or pinching
- share, comment and delete buttons(last two not implemented yet)
- error message if something goes wrong with loading or saving

### IMAGE PAGE
- zooming an image by pinching and clicking
- waiting spinner
