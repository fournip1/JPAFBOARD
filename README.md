# Ze PAF Board

This small Java application is a bespoke media player designed for **sound stage managers working on improv' shows**.

> In an improv' show, the sound manager should be able to play the right track at the right moment instantaneously, depending on what's happening on stage.

Until now, this could be achieved either with a live musician or with a standard music board. Obviously, a live musician is the ideal solution.  Alternatively, if you want to rely on a very small set of tracks, a simple music board can do the job too.

But in my case, I wanted to pick up my song from a large music library. That's why I created this tool.

**The basic principle underlying this tool is to associate keywords to tracks.**

> Let's illustrate! Suppose that you have a track with the sound of a chainsaw running. You can set the keywords *fear*, *forest*, *accident* for this track. The song will then be accessible via the buttons corresponding to these keywords.

## Licence

This software is published under GPL3 license. Please see the [details here](LICENSE.md).

It uses the excellent Manjari font, which is published under [this license](OFL.txt).

## Features

The board allows the user to:

* find music based on keywords, title, artist, using the search bar;
* quickly access the list of keywords on a grid and get the songs associated to a given keyword by pressing the corresponding button;
* add songs to the music database by simply dragging and dropping;
* play a song by double-clicking on it;
* edit a song and modify the title, artist and list of keywords;
* edit a keyword;
* remove a song or a keyword.

## Download and install

All the files needed  to run the application are in [this directory](/installers).

### For Debian-based distributions (ubuntu, mint, raspbian...)

You may use a ready made installer for debian (.deb). Simply run the command:

> sudo dpkg -i jpafboard_2.3_amd64.deb

This will create a ***JPafBoard*** shortcut  in your applications menu.

If you want to uninstall it, run:

> sudo apt remove jpafboard

### For Windows 

You may download and double-click on the msi installer and follow a quite standard procedure.

### For Mac OS.

1. Make sure that Java 17 is set on your computer by downloading and installing [jdk 17](https://download.oracle.com/java/17/archive/jdk-17.0.7_macos-x64_bin.dmg).
2. Download and double click on the JPafBoard... jar to run the application.

> If it does not work, please check that you have downloaded the correct files for your operating system.
> You may as well try the *open with* command and check that the jar file is correctly opened with Java 17.
> If it still doesn't work, please [drop me an email](mailto:fournip1@hotmail.com).

## How to use the board?

### Music library features

![](/screenshots/directory.png)

On the top of the screen you will see the directory used by the board. You can change this default directory and point to any other one.

If you do not want to start from scratch, you may download and extract [this sample directory](/installers/sample.zip) and change the music directory to point to it.

When you click on *Refresh*, the PAF board will scan the music directory and udpate the music database accordingly. You only need to refresh if you are installing for the first time or if you modify the music directory externally.

**This tool only works with mp3 files. You may use a music converter if you wish to upload other types of audio files.**

> **What's behind the scene?**
> The software reads the MP3 tags. The keywords are read as a coma separated list from the *comment* tag. Therefore, you may use an external tool to update accordingly the mp3 tags. I recommend **easytag**.

### Keywords' buttons

![](/screenshots/buttons.png)

Each keyword can be associated with one or more tracks.

You can delete or rename a keyword, using the right-click context menu.

In order to avoid the *too many buttons issue*, you can merge two keywords.

> Let's assume that you find the keywords *fear* and *panic* too close to stay distinct. Then you can choose to merge *fear* into *panic*. To do so, edit the *panic* keyword and rename it to *fear*.

You cannot add a keyword button directly to the grid. Instead you should modify the related tracks. Please see the next section for details.

### Tracks list

![](/screenshots/trackslist.png)

It is on the right side of the panel. This list is automatically updated when hitting a keyword button, or when updating the research field.

If you want to play a track, just double click on it.

Of course you may edit or delete a track, using the right-click contextual menu.

![](/screenshots/trackedition.png)

To add an mp3 track from another directory, **simply drag and drop it onto the tracks list**.

> When editing a track, you can assign a set of keywords to it. The keywords should be entered as a coma separated list in the related field. ***If a keyword does not already exist, it is automatically created upon saving the track.***
>
> ***Important note***: editing or deleting a track perform in parallel the operation on the actual mp3 file.

### Player

![](/screenshots/player.png)

The player is displayed at the bottom of the board. It has all the usual basic functions that you may expect.

On the bottom right side, you can see which track is loaded. **Be careful, if you press the play button, this loaded track will be played or paused. To change the loaded track, you should instead double click on an item of the track list.**

### Search bar

![](/screenshots/searchbar.png)

It works as a usual search field except that you can choose the search mode, exclusive (and) or inclusive (or).

If you choose "or", it means that the tracks' list will contain all the tracks which match at least one of the word written.

If you choose "and", the application will return all the tracks which match all the words simultaneously.

> The words are searched amongst the following track fields: keywords, artist, title.
> If you write in the search bar "Beethoven fear" with the exclusive search mode, there is a good chance that the tracks' list will contain all the scary Beethoven pieces.
> On the other hand, if you write the same words with the inclusive search mode, the tracks list should contain both the Beethoven tracks and the tracks with the keyword *fear*.

## For developpers

This is a ready to code Maven project. You can find all the informations in the ***pom.xml*** file.

The graphical interface and the media player was built with javafx framework.

To manage MP3 tags, I used the [MP3agic library](https://github.com/mpatric/mp3agic).

Last but not least, data are stored in an sqlite database located in the ***.jpafboard*** directory installed in the user's home directory.

I used [ormlite package](https://ormlite.com/) to access and modify the data in a quite straightforward way.

### Issues and bugs encountered

There was a bug invoking modules **ormlite.core** et and **ormlite.jdbc** simultaneously.

I solved it using a [taylored made library](https://jitpack.io/#com.gitlab.grrfe/ormlitebuild/5.1.1)

As well, it was complex to debug the project since it runs javafx instead of java.

I followed this [explanation on stack exchange](https://stackoverflow.com/questions/56197372/i-cant-debug-an-application-using-netbeans-11-with-javafx-12/56207033#56207033).

I found a bug with the loadfont instruction inside my css. I solved it by directly loading the font from the controller, which is not very coherent with the MVC coding method...

### Next steps

Make an installer for Mac OS. If you have a Mac developper license and wish to help me on that, please [tell me](mailto:fournip1@hotmail.com).

### Want to contribute?

If you are a developper and wish to contribute, provide advises or suggest modifications, feel free to [drop me an email](mailto:fournip1@hotmail.com).
