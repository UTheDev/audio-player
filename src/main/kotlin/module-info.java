module com.uthedev.audioplayer.audioplayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
                requires kotlin.stdlib;
    
                            
    opens com.uthedev.audioplayer to javafx.fxml;
    exports com.uthedev.audioplayer;
}