# Welcome to the PAF Board project
This small Java application is a bespoke media player taylored for sound stage manager working on improv' show.
In an improv' show, the sound manager should be able to play the right track at the right moment instantaneously.
Up to now, this could either be achieved with a live musician  or with a standard music board. 
Of course a live musician is the ideal solution. 
Alternatively, if you want to rely on a very small set of tracks, a plain music board can work too.
 But in my case, I wanted to pick-up my song amongst a wide music library.
 That's why I designed this tool.
 
 ## Features
 The board allows the user to:
 * find music based on keywords, title, artist, using the search bar;
 * quickly access the list of keywords on a grid and get the songs associated to a given keyword by pressing the related button;
 * play a song by double clicking on it;
 * remove a song;
 *  edit a song and modify the title, artist and list of keywords associated to it;
 * remove a song or a keyword;
 * 
## Download and install
All the files for a ready to use experience are in [this directory](/target).
### For debian-based distributions
You may use a ready made installer for debian (.deb). Simply run the command:
> sudo dpkg -i jpafboard_2.1-1_amd64.deb
If you want to desinstall it, run:
> sudo apt remove JPafBoard
### For windows and Mac OS.
First install java 17 version by double clicking on the installer in the very same folder.
Once Java 17 is installed, you can double click on the Java-PafBoard... jar file to launch the application. 
## You are an enduser

## You are a developper
There was a bug invoking modules **ormlite.core** et and **ormlite.jdbc** simultaneously.
I solved it using a [taylored made library](https://jitpack.io/#com.gitlab.grrfe/ormlitebuild/5.1.1)
As well, it was complex to debug the project since it runs javafx instead of java.
I followed this [explanation on stack exchange](https://stackoverflow.com/questions/56197372/i-cant-debug-an-application-using-netbeans-11-with-javafx-12/56207033#56207033).
<!--stackedit_data:
eyJoaXN0b3J5IjpbNDUwOTczMDk1LDk4Mzc0OTkzMCwxOTk0Nz
E0MzA0LDQ4NDkxMjczMV19
-->