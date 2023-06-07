# Welcome to the PAF Board tool
## Download and install
All the files for a ready to use experience are in [this directory](/target).
### For Linux debian-based distribution users
You may use a ready made installer for debian (.deb). Simply run the command:
> sudo dpkg
## You are an enduser

## You are a developper
There was a bug invoking modules **ormlite.core** et and **ormlite.jdbc** simultaneously.
I solved it using a [taylored made library](https://jitpack.io/#com.gitlab.grrfe/ormlitebuild/5.1.1)
As well, it was complex to debug the project since it runs javafx instead of java.
I followed this [explanation on stack exchange](https://stackoverflow.com/questions/56197372/i-cant-debug-an-application-using-netbeans-11-with-javafx-12/56207033#56207033).
<!--stackedit_data:
eyJoaXN0b3J5IjpbLTU0NjIxODA2Nl19
-->