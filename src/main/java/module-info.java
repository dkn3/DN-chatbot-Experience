module com.example.chatbot_java {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.google.gson;


    opens com.example.chatbot_java to javafx.fxml;
    exports com.example.chatbot_java;
}