package HomeworkM06Files;

import java.io.*;
import java.util.*;
import javafx.application.*;
import javafx.event.*;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.stage.*;

public class CustomerInput extends Application {

    private Stage primaryStage;
    private Text statusText, resultText;
    private Button uploadButton;

    private final static Font RESULT_FONT = Font.font("Helvetica", 24);
    private final static Font INPUT_FONT = Font.font("Helvetica", 20);

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        VBox primaryBox = new VBox();
        primaryBox.setAlignment(Pos.CENTER);
        primaryBox.setSpacing(20);
        primaryBox.setStyle("-fx-background-color: white");

        VBox uploadBox = new VBox();
        uploadBox.setAlignment(Pos.CENTER);
        uploadBox.setSpacing(20);
        Text uploadLabel = new Text("Upload a comma-separated file with customer data.");
        uploadLabel.setFont(INPUT_FONT);
        uploadButton = new Button("Upload data");
        uploadButton.setOnAction(this::processDataUpload);

        uploadBox.getChildren().add(uploadLabel);
        uploadBox.getChildren().add(uploadButton);
        primaryBox.getChildren().add(uploadBox);

        VBox resultsBox = new VBox();
        resultsBox.setAlignment(Pos.CENTER);
        resultsBox.setSpacing(20);
        statusText = new Text("");
        statusText.setVisible(false);
        statusText.setFont(RESULT_FONT);
        statusText.setFill(Color.RED);
        resultText = new Text("");
        resultText.setVisible(false);
        resultText.setFont(RESULT_FONT);
        resultsBox.getChildren().add(statusText);
        resultsBox.getChildren().add(resultText);
        primaryBox.getChildren().add(resultsBox);

        Scene scene = new Scene(primaryBox, 475, 200, Color.TRANSPARENT);
        primaryStage.setTitle("Customer Data Upload");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void processDataUpload(ActionEvent event) {
        statusText.setVisible(false);
        resultText.setVisible(false);
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(primaryStage);
        parseFile(file);

    }

    private void parseFile(File file) {
        
        String customerID = null;
        String ordersString = null;

        try (Scanner fileScan = new Scanner(new FileReader(file))) {

            List <Customer> customerList = new ArrayList<>();
            int totalOrders = 0;
            int numOfOrders = 0;

            while (fileScan.hasNextLine())  {
                Scanner lineScan = new Scanner(fileScan.nextLine());
                lineScan.useDelimiter(",");

                customerID = lineScan.next();
                if (customerID.contains("@"))   {
                    throw new InvalidIDException();
                }

                ordersString = lineScan.next();
                numOfOrders = Integer.parseInt(ordersString);

                customerList.add(new Customer(customerID, numOfOrders));
                totalOrders += numOfOrders;

            }

            updateStatus("Customers added: " + customerList.size());


            resultText.setText("Total number of orders: " + totalOrders);
            resultText.setVisible(true);
            uploadButton.setDisable(true);
            
        } catch (IOException | NullPointerException ex) {
            updateStatus("Invalid File");
        } catch (NumberFormatException ex)  {
            updateStatus("Data error: " + ordersString + " is not an integer");
        } catch (InvalidIDException ex) {
            updateStatus("Invalid id: " + customerID);
        }
   
    }
    
    private void updateStatus(String text)  {
        statusText.setText(text);
        statusText.setVisible(true);
    }
    
    public static void main(String[] args) {
        launch(args);
    }

    private static class InvalidIDException extends Exception  {
        private static final String MESSAGE = "Invalid customer id";
        
        private InvalidIDException()    {
            super(MESSAGE);
        }
        
        private InvalidIDException(String message)  {
            super(message);
        }
    }
}