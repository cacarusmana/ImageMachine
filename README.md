# ImageMachine
A simple app that created to complete the recruitment process in Prospace. The app uses Room as the database to store any data related to the machine information locally, and the images associated with the machine will be stored locally on Internal Storage so that only the app have the access to the image files.

This application has some features as follows :
- Show List of machine
- Sort the machines alphabetically by machine name or machine type
- Create, Update and Delete machine
- Add or removes images that associated with the machine
- Scan QR Code to find the desired machine
- View image on Zoom Mode

Tech Stack :
- Java 11
- MVVM (Design Pattern)
- Dagger Hilt (Dependecy Injection)
- Android Architecture Component (ViewModel, LiveData, Room)
- Jetpack Libraries (AndroidX)
- RxJava3 (Multithreading)
- StfalconImageViewer (Image Viewer)
