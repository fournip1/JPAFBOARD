There was a bug invoking modules ormlite.core et and ormlite.jdbc simultaneously.
I solved it using a taylored made library:
https://jitpack.io/#com.gitlab.grrfe/ormlitebuild/5.1.1
As well, it was complex to debug the project since it runs javafx instead of java.
I followed this explanation:
https://stackoverflow.com/questions/56197372/i-cant-debug-an-application-using-netbeans-11-with-javafx-12/56207033#56207033


<!--stackedit_data:
eyJoaXN0b3J5IjpbLTQ1MjQzMDEzMl19
-->