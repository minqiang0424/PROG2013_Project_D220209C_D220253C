import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Main application class for H&L Hotel Reservation System.
 * Demonstrates the use of OOP concepts, file handling, and GUI.
 */
public class HotelReservationApp extends Application {

    private Stage primaryStage;
    private ComboBox<String> roomTypeComboBox;
    private Label priceLabel;
    private Label availabilityLabel;
    private TextField nameTextField;
    private TextField emailTextField;
    private TextField phoneTextField;
    private ComboBox<Integer> nightsComboBox;
    private ComboBox<Integer> checkInHourComboBox;
    private ComboBox<Integer> checkInMinuteComboBox;
    private ComboBox<Integer> roomQuantityComboBox;
    private DatePicker checkInDatePicker;

    private List<Room> rooms = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("H&L Hotel Reservation");

        // Initialize rooms
        initializeRooms();

        VBox mainLayout = new VBox(10);
        mainLayout.setStyle("-fx-padding: 20px;");

        Label welcomeLabel = new Label("Welcome to H&L Hotel Reservation!");
        welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        mainLayout.getChildren().add(welcomeLabel);

        mainLayout.getChildren().addAll(
                createRoomTypeSection(),
                createPersonalInfoSection(),
                createControlButtons()
        );

        Scene scene = new Scene(mainLayout, 700, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        checkInDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isBefore(LocalDate.now())) {
                showAlert("Invalid Booking Date", "Please select a date after the current day", Alert.AlertType.WARNING);
                checkInDatePicker.setValue(oldValue);
            }
        });
    }

    /**
     * Initializes room data.
     */
    private void initializeRooms() {
        rooms.add(new StandardRoom(100.0, true));
        rooms.add(new DeluxeRoom(150.0, true));
        rooms.add(new SuiteRoom(200.0, true));
    }

    /**
     * Creates the section for room type selection.
     * @return VBox containing room type selection UI components.
     */
    private VBox createRoomTypeSection() {
        VBox roomTypeBox = new VBox(5);

        roomTypeComboBox = new ComboBox<>();
        for (Room room : rooms) {
            roomTypeComboBox.getItems().add(room.getType());
        }
        roomTypeComboBox.setOnAction(event -> updatePriceAndAvailability());

        roomQuantityComboBox = new ComboBox<>();
        roomQuantityComboBox.getItems().addAll(1, 2, 3, 4, 5);
        roomQuantityComboBox.getSelectionModel().selectFirst();

        nightsComboBox = new ComboBox<>();
        nightsComboBox.getItems().addAll(1, 2, 3, 4, 5);
        nightsComboBox.getSelectionModel().selectFirst();

        priceLabel = new Label();
        availabilityLabel = new Label();
        availabilityLabel.setStyle("-fx-font-weight: bold;");

        HBox checkInDateTimeBox = new HBox(10);

        Label checkInLabel = new Label("Check-in:");
        checkInHourComboBox = new ComboBox<>();
        checkInHourComboBox.getItems().addAll(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23);
        checkInHourComboBox.getSelectionModel().selectFirst();

        checkInMinuteComboBox = new ComboBox<>();
        checkInMinuteComboBox.getItems().addAll(0, 15, 30, 45);
        checkInMinuteComboBox.getSelectionModel().selectFirst();

        checkInDatePicker = new DatePicker();
        checkInDatePicker.setValue(LocalDate.now());

        checkInDateTimeBox.getChildren().addAll(
                checkInLabel,
                checkInHourComboBox,
                new Label(":"),
                checkInMinuteComboBox,
                new Label("Date:"),
                checkInDatePicker
        );

        roomTypeBox.getChildren().addAll(
                new Label("Select Room Type:"),
                roomTypeComboBox,
                new Label("Select Quantity:"),
                roomQuantityComboBox,
                new Label("Number of Nights:"),
                nightsComboBox,
                priceLabel,
                availabilityLabel,
                checkInDateTimeBox
        );

        return roomTypeBox;
    }

    /**
     * Updates the price and availability labels based on the selected room type.
     */
    private void updatePriceAndAvailability() {
        int selectedRoomIndex = roomTypeComboBox.getSelectionModel().getSelectedIndex();
        if (selectedRoomIndex >= 0 && selectedRoomIndex < rooms.size()) {
            Room selectedRoom = rooms.get(selectedRoomIndex);
            priceLabel.setText("Price per night: RM" + selectedRoom.getPrice());
            availabilityLabel.setText("Availability: " + (selectedRoom.isAvailable() ? "Available" : "Not Available"));
            availabilityLabel.setStyle(selectedRoom.isAvailable() ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
        }
    }

    /**
     * Creates the section for personal information input.
     * @return VBox containing personal information input UI components.
     */
    private VBox createPersonalInfoSection() {
        VBox personalInfoBox = new VBox(5);

        nameTextField = new TextField();
        emailTextField = new TextField();
        phoneTextField = new TextField();

        personalInfoBox.getChildren().addAll(
                new Label("Enter Personal Information:"),
                new Label("Name:"),
                nameTextField,
                new Label("Email:"),
                emailTextField,
                new Label("Phone Number:"),
                phoneTextField
        );

        return personalInfoBox;
    }

    /**
     * Creates the control buttons for reservation and quitting the application.
     * @return HBox containing control buttons.
     */
    private HBox createControlButtons() {
        HBox buttonsBox = new HBox(10);

        Button reserveButton = new Button("Reserve Room");
        reserveButton.setOnAction(event -> makeReservation());

        Button quitButton = new Button("Quit");
        quitButton.setOnAction(event -> primaryStage.close());

        buttonsBox.getChildren().addAll(reserveButton, quitButton);
        return buttonsBox;
    }

    /**
     * Handles the reservation process by validating input, calculating fees,
     * and saving the reservation.
     */
    private void makeReservation() {
        int selectedRoomIndex = roomTypeComboBox.getSelectionModel().getSelectedIndex();
        if (selectedRoomIndex >= 0 && selectedRoomIndex < rooms.size()) {
            Room selectedRoom = rooms.get(selectedRoomIndex);
            if (selectedRoom.isAvailable()) {
                String customerName = nameTextField.getText();
                String customerEmail = emailTextField.getText();
                String customerPhone = phoneTextField.getText();

                if (customerName.isEmpty() || customerEmail.isEmpty() || customerPhone.isEmpty()) {
                    showAlert("Incomplete Information", "Please fill in all the required information before proceeding.", Alert.AlertType.WARNING);
                    return;
                }

                double roomPrice = selectedRoom.getPrice();
                int nights = nightsComboBox.getValue();
                double totalFee = roomPrice * nights;

                int roomQuantity = roomQuantityComboBox.getValue();
                double totalRoomFee = totalFee * roomQuantity;

                int checkInHour = checkInHourComboBox.getValue();
                int checkInMinute = checkInMinuteComboBox.getValue();
                LocalTime checkInTime = LocalTime.of(checkInHour, checkInMinute);
                LocalDate checkInDate = checkInDatePicker.getValue();

                String receipt = generateReceipt(
                        customerName,
                        customerEmail,
                        customerPhone,
                        selectedRoom.getType(),
                        nights,
                        roomPrice,
                        totalRoomFee,
                        checkInTime,
                        roomQuantity,
                        checkInDate
                );

                showAlert("Booking Successful", receipt, Alert.AlertType.INFORMATION);
                saveReservationToFile(customerName, customerEmail, customerPhone, receipt);
            } else {
                showAlert("Room Unavailable", "Selected room is not available.", Alert.AlertType.WARNING);
            }
        }
    }

    /**
     * Generates a receipt with booking details.
     * @param name Customer's name.
     * @param email Customer's email.
     * @param phone Customer's phone number.
     * @param roomType Type of the room.
     * @param numberOfNights Number of nights for the stay.
     * @param roomPrice Price per night.
     * @param totalRoomFee Total fee for the rooms.
     * @param checkInTime Check-in time.
     * @param roomQuantity Number of rooms.
     * @param checkInDate Check-in date.
     * @return Formatted receipt string.
     */
    private String generateReceipt(String name, String email, String phone, String roomType, int numberOfNights, double roomPrice, double totalRoomFee, LocalTime checkInTime, int roomQuantity, LocalDate checkInDate) {
        return String.format(
                "Customer Information:\nName: %s\nEmail: %s\nPhone Number: %s\n\nBooking Details:\nRoom Type: %s\nNumber of Nights: %d\nCheck-in Date: %s\nCheck-in Time: %s\nNumber of Rooms: %d\n\nPayment Details:\nPrice per Night: RM%.2f\nTotal Fee: RM%.2f",
                name, email, phone, roomType, numberOfNights, checkInDate.format(DateTimeFormatter.ISO_DATE), checkInTime.format(DateTimeFormatter.ofPattern("hh:mm a")), roomQuantity, roomPrice, totalRoomFee
        );
    }

    /**
     * Saves the reservation receipt to a file.
     * @param name Customer's name used for file naming.
     * @param email Customer's email.
     * @param phone Customer's phone number.
     * @param receipt Receipt details to be saved.
     */
    private void saveReservationToFile(String name, String email, String phone, String receipt) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(name + "_booking_receipt.txt"))) {
            writer.write(receipt);
            showAlert("Save Successful", "Booking receipt saved to file successfully.", Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            showAlert("Save Failed", "Failed to save the booking receipt to file.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Displays an alert dialog with the specified message.
     * @param title Title of the alert.
     * @param message Message to be displayed.
     * @param alertType Type of the alert.
     */
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Abstract class representing a room in the hotel.
     * Encapsulates the properties and methods related to a room.
     */
    abstract class Room {
        private double price;
        private boolean available;

        public Room(double price, boolean available) {
            this.price = price;
            this.available = available;
        }

        public double getPrice() {
            return price;
        }

        public boolean isAvailable() {
            return available;
        }

        public abstract String getType();
    }

    /**
     * Class representing a standard room in the hotel.
     * Inherits from Room and specifies the type of room.
     */
    class StandardRoom extends Room {
        public StandardRoom(double price, boolean available) {
            super(price, available);
        }

        @Override
        public String getType() {
            return "Standard Room";
        }
    }

    /**
     * Class representing a deluxe room in the hotel.
     * Inherits from Room and specifies the type of room.
     */
    class DeluxeRoom extends Room {
        public DeluxeRoom(double price, boolean available) {
            super(price, available);
        }

        @Override
        public String getType() {
            return "Deluxe Room";
        }
    }

    /**
     * Class representing a suite room in the hotel.
     * Inherits from Room and specifies the type of room.
     */
    class SuiteRoom extends Room {
        public SuiteRoom(double price, boolean available) {
            super(price, available);
        }

        @Override
        public String getType() {
            return "Suite Room";
        }
    }
}
