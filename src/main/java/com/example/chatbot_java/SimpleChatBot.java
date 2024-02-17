package com.example.chatbot_java;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SimpleChatBot extends Application {

    private TextArea chatArea = new TextArea();
    private TextField userInputField = new TextField();

    @Override
    public void start(Stage primaryStage) {
        VBox vbox = new VBox(chatArea, userInputField, createSendButton());

        Scene scene = new Scene(vbox, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Chat Bot");
        primaryStage.show();
    }

    private Button createSendButton() {
        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> {
            String userInput = userInputField.getText();
            chatArea.appendText("User: " + userInput + "\n");
            String botResponse = getBotResponse(userInput);
            chatArea.appendText("Bot: " + botResponse + "\n");
            userInputField.clear();
        });
        return sendButton;
    }

    private String getBotResponse(String userInput) {
        HttpClient client = HttpClient.newHttpClient();
        JsonObject json = new JsonObject();
        json.addProperty("prompt", userInput);
        json.addProperty("max_tokens", 60);
        json.addProperty("temperature", 0.2);
        String jsonString = json.toString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/engines/davinci-002/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer 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")
                .POST(HttpRequest.BodyPublishers.ofString(jsonString))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                return "Error: Received status code " + response.statusCode();
            }
            JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
            if (jsonResponse.getAsJsonArray("choices") == null) {
                return "Error: 'choices' not found in the response";
            }
            return jsonResponse.getAsJsonArray("choices").get(0).getAsJsonObject().get("text").getAsString().trim();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }



    public static void main(String[] args) {
        launch(args);
    }
}
