module com.paf.jpafboard {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires mp3agic;
    // requires javafx.swing;
    // requires java.base;
    // requires java.logging;
    requires ormlitebuild;
//    requires ormlite.jdbc;
    requires java.sql;
    requires java.base;

    opens com.paf.jpafboard to javafx.fxml, ormlitebuild;
    exports com.paf.jpafboard;
}
