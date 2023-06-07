# Welcome to the PAF Board
This small Java application is a bespoke media player taylored for sound stage manager working on improv' show.
On such a show, the sound manager should be able to play the right track at the right moment instantaneously.
Therefore, this tool is based on the following principle:
 
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
eyJoaXN0b3J5IjpbMTc2NjYyODU2Myw0ODQ5MTI3MzFdfQ==
-->