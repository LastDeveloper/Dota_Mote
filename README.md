# DotaMote

A mobile application that communicates with a desktop client to allow the user to accept or decline matches.
SQLite database to hold different IP address information. Foreground Service with Notifications to keep 
connection. User Interface integration with SQLite, and which reacts to desktop client.

## States 

![Example Image](/website/static/states.gif?raw=true)

Dotamote receives the different states of matchingmaking process from the Desktop Client.
  
  * Connected
  * Looking For Match
  * Game Found
  * Accept Or Decline
  * Checking...
  * Looking for Server
  * Checking If In Game
  * In Game
  and also checks for any restarts in the search or matchmaking error.

  * Decline 
  * Failed To Ready Up
  * Returning To Search...
  * Disconnect

## User Interface


![Example Image](/website/static/create.gif?raw=true)

### Add Dialog
Dotamote connects to the desktop client by connecting to the desktop's local IP Address. The saved IP information
is saved inside an SQLite database, accessible by a series of AlertDialogs. The AlertDialogs are accessed in the Navigation Drawer
The saved IP Addresses are displayed in a SingleChoiceList which is called from the SQLite Database on creation.  The selected choice is the IP Address that will be used to connect to the Desktop Client. Options to Cancel, Edit, and Add are available. Cancel will reset the default IP Address to the previous choice.

![Example Image](/website/static/add3.gif?raw=true)

The Add Dialog accepts only properly formated IP addresses, and has emptyness checks for the Name and Port Number. This information is 
saved in the SQLite database and sets the newly added IP address to default.


### Edit Options with Delete
![Example Image](/website/static/edit.gif?raw=true)                   /t         ![Example Image](/website/static/delete.gif?raw=true)

The Edit allows for changed to be made to the slected IP address records. Also in the Option for Deleted is located here. 
