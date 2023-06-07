# Welcome to the PAF Board project
This small Java application is a bespoke media player taylored for **sound stage manager working on improv' shows**.

> In an improv' show, the sound manager should be able to play the right track at the right moment instantaneously, depending on what's happening on stage.

Up to now, this could either be achieved with a live musician or with a standard music board. Of course a live musician is the ideal solution.  Alternatively, if you want to rely on a very small set of tracks, a straightforward music board can do the job too.
 
 But in my case, I wanted to pick-up my song amongst a wide music library. That's why I designed this tool. 
 
 **The basic principle underlying this tool is to associate keywords to tracks.**
 
 > Let's illustrate! Suppose that you have a track with the sound of a running jigsaw. You can set the keywords ***fear***, ***forest***, ***accident*** for this track. Doing so the song will be accessible behind the buttons corresponding to these keywords.  
 
 ## Features
 
 The board allows the user to:
 * find music based on keywords, title, artist, using the search bar;
 * quickly access the list of keywords on a grid and get the songs associated to a given keyword by pressing the related button;
 * play a song by double clicking on it;
 * remove a song;
 *  edit a song and modify the title, artist and list of keywords associated to it;
 * edit a keyword;
 * remove a song or a keyword.
> 
## Download and install

All the files for a ready to use experience are in [this directory](/installers).

### For debian-based distributions
You may use a ready made installer for debian (.deb). Simply run the command:

> sudo dpkg -i jpafboard_2.1-1_amd64.deb

If you want to desinstall it, run:

> sudo apt remove JPafBoard

### For windows and Mac OS.

 1. Install java 17 by downloading and double clicking on the installer in the [very same folder](/installers).
2. Download and double click on the Java-PafBoard... jar to launch the application.
3. 
> If it does not work, please check that you have downloaded the files corresponding to your operating systems.
> You may as well try the *open with* command and check that the jar file is correctly opened with Java 17.
> If it still doesn't work, please [drop me an email](mailto:fournip1@hotmail.com).

## How to use the board?
### Music library features

On the top of the screen you will see the directory used by the board. You can change this default directory and point to any other one. 

If you do not want to start from scratch, you may download and extract [this sample directory](/installers/sample.zip) and make the music directory to point on this one.

When you click on *Refresh*, the PAF board scans the music directory and udpates the music database accordingly.

> **What's behind the scene?**
> The software reads the MP3 tags. The keywords are read as a coma separated list from the tag *comment*. Therefore, you may use an external tool to update accordingly the mp3 tags. I recommend **easytag**.

### Keywords' buttons

Each keyword can be associated with one or several tracks.

You can delete or rename a keyword, using the right click contextual menu.

In order to avoid the *too many buttons issue*, you can merge two keywords.

> Let's illustrate! Let's assume that you find the keywords *fear* and *panic* too close to stay distinct. Then you can choose to merge *fear* into *panic*. To do so, edit the *panic* keyword and rename it to *fear*.

You cannot add a keyword directly to the grid. Instead you should modify the related tracks. Please see the next section for details.

### Tracks list

It is on the right side of the panel. This list is automatically updated when hitting a keyword button, or when updating the research field.

If you want to play a track, just double click on it.

Of course you may edit or delete a track, using the right-click contextual menu.

> When editing a track, you can assign a set of keywords to it. The keywords should be entered as a coma separated list in the related field. ***If a keyword does not already exist, it is automatically created upon saving the track.***
> 
> ***Important note***: editing or deleting a track perform in parallel the operation on the actual file. 

### Player

The player is displayed on the bottom of the board. It has all the usual basic functions that you may expect. 

On the bottom right side, you can see which track is loaded. **Be careful, if you press the play button, this loaded track will be played or paused. To change the loaded track, you should instead double click on an item of track list.**

## For developpers

This is a ready to code Maven project. You can find all the informations in the ***pom.xml*** file.

The graphical interface and the media player was built with javafx framework.

To manage MP3 tags, I used the [MP3agic library](https://github.com/mpatric/mp3agic).

Last but not least, data are stored in an sqlite database located in the *.jpafboard* directory installed in the user's home directory. I used ormlite package to access and modify the data. Pleas


### Dependencies and modules



There was a bug invoking modules **ormlite.core** et and **ormlite.jdbc** simultaneously.
I solved it using a [taylored made library](https://jitpack.io/#com.gitlab.grrfe/ormlitebuild/5.1.1)
As well, it was complex to debug the project since it runs javafx instead of java.
I followed this [explanation on stack exchange](https://stackoverflow.com/questions/56197372/i-cant-debug-an-application-using-netbeans-11-with-javafx-12/56207033#56207033).
<!--stackedit_data:
eyJoaXN0b3J5IjpbMTY0MzkxODY2NiwzMDAwMTcyMzcsNjEzNz
g1NTAwLC0xMjcxMDY5NTMxLDk4Mzc0OTkzMCwxOTk0NzE0MzA0
LDQ4NDkxMjczMV19
-->