# DotaMote

A mobile application that communicates with a desktop client to allow the user to accept or decline matches of the popular MOBA, DOTA 2. 

DotaMote contains many complex components. A SQLite database to hold different IP address information. Foreground Service with Notifications to keep connection. An AsyncTask to connect to the desktop client. User Interface integration with SQLite database, and real-time updates from desktop client. The notification contains a custom layout and in addition a background animation shown in the main application. Also sound and vibration alerts are 
used to inform the user. 

## States 

![Example Image](/website/static/states2.gif?raw=true)

Dotamote receives the different states of matchingmaking process from the Desktop Client.
  
  * Connected
  * Looking For Match
  * Game Found
  * Accept Or Decline
  * Checking...
  * Looking for Server
  * Checking If In Game
  * In Game
  
The application also checks for any restarts in the search or for matchmaking error.

  * Decline 
  * Failed To Ready Up
  * Returning To Search...
  * Disconnect

## User Interface


![Example Image](/website/static/create.gif?raw=true)

### Add Dialog
Dotamote connects to the desktop client by connecting to the desktop's local IP Address. The saved IP information
is saved inside an SQLite database, accessible by a series of AlertDialogs. 

The saved IP Addresses are called from the SQLite Database on creation of the AlertDialog.  The selected choice is the IP Address that will be used to connect to the Desktop Client. Options to Cancel, Edit, and Add are available. Cancel will reset the default IP Address to the previous choice.

![Example Image](/website/static/add3.gif?raw=true)

The Add and other Dialogs only accept properly formatted IP addresses, and they have validity checks for the Name and Port Number. This information is saved in the SQLite database and sets the newly added IP address to default.


### Edit Options with Delete
![Example Image](/website/static/edit.gif?raw=true)  _________________________   ![Example Image](/website/static/delete.gif?raw=true)

The Edit allows for changed to be made to the selected IP address records from the database. Also in the option for Delete is located here. 


## Notifications

![Example Image](/website/static/notification2.gif?raw=true)

The communication between the application and the Desktop client is handled by a Service. The Service is initialized as a Foreground Service, allowing it to persist in the case of low system memory or if the user leaves the application. The Service uses an Asynctask (Background Thread) to send and receive the data from the Desktop Client. Once the service is running, the application can rebind itself to the service and continue receiving information. The application, Service, and Asynctask communicate by an series of Interfaces, which are connected when the Service is created, and when the application  binds itself to the service.

# Important Information

This project allowed me to gain knowledge and experience of higher level Object Oriented Programming. I plan to use these skills in my other projects and to allow me to adapt to other programming languages and development concepts. 

PS. To show my experience of the Android Lifecycle, All of the Dialogs and Game information are full adaptable on orientation 
changes and other user inputs. 

![Example Image](/website/static/accept_destroy.gif?raw=true)______________![Example Image](/website/static/reload.gif?raw=true)



##3rd Party Libraries Used


###AutoFitTextView

  https://github.com/grantland/android-autofittextview

  A TextView that automatically resizes text to fit perfectly within its bounds.
  
###Android Form EditText

  https://github.com/vekexasia/android-edittext-validator

  Android form edit text is an extension of EditText that brings data validation facilities to the edittext. (Used in Dialogs for
  validation. )
  
###MultiStateAnimation

https://github.com/KeepSafe/MultiStateAnimation

Animation Handler. Used to have Animation States coincided with Status changes of the game.

