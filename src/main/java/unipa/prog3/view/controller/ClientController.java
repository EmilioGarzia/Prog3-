package unipa.prog3.view.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import unipa.prog3.MainApplication;
import unipa.prog3.controller.service.CenterService;
import unipa.prog3.controller.service.ClientService;
import unipa.prog3.controller.service.ServiceProvider;
import unipa.prog3.model.relation.Centro;
import unipa.prog3.model.relation.Cliente;
import unipa.prog3.model.relation.util.ClientBuilder;

import java.util.Vector;

public class ClientController extends Controller {
    @FXML
    private TextField nameField, surnameField;
    @FXML
    private ComboBox<String> countryChooser, townChooser;
    @FXML
    private TextField CAPField, addressField, houseNumberField;
    @FXML
    private TextField emailField, phoneField;
    @FXML
    private Label errorLabel;

    private final CenterService centerService;

    public ClientController() {
        centerService = (CenterService) ServiceProvider.getService(Centro.class);
    }

    public void initialize() {
        Vector<Centro> centers = centerService.selectAll();
        Vector<String> countries = new Vector<>();
        centers.forEach(centro -> {
            if (!countries.contains(centro.getStato()))
                countries.add(centro.getStato());
        });
        countryChooser.setItems(FXCollections.observableList(countries));
        countryChooser.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Vector<String> towns = new Vector<>();
            centers.forEach(centro -> {
                if (!towns.contains(centro.getCittà()) && centro.getStato().equals(newValue))
                    towns.add(centro.getCittà());
            });
            townChooser.setItems(FXCollections.observableList(towns));
        });
    }

    @FXML
    public void aggiungiIndirizzo() {
        if (nameField.getText().isEmpty()) {
            errorLabel.setText("Il campo nome è vuoto!");
            return;
        } else if (surnameField.getText().isEmpty()) {
            errorLabel.setText("Il campo cognome è vuoto!");
            return;
        } else if (countryChooser.getValue().isEmpty()) {
            errorLabel.setText("Devi selezionare il tuo Stato di appartenenza!");
            return;
        } else if (townChooser.getValue().isEmpty()) {
            errorLabel.setText("Devi selezionare la tua città di appartenenza!");
            return;
        } else if (CAPField.getText().isEmpty()) {
            errorLabel.setText("Il campo CAP è vuoto!");
            return;
        } else if (addressField.getText().isEmpty()) {
            errorLabel.setText("Il campo indirizzo è vuoto!");
            return;
        } else if (houseNumberField.getText().isEmpty()) {
            errorLabel.setText("Il campo numero civico è vuoto!");
            return;
        } else if (phoneField.getText().isEmpty()) {
            errorLabel.setText("Il campo telefono è vuoto!");
            return;
        }

        Centro centro = centerService.selectByLocation(townChooser.getValue(), countryChooser.getValue());

        ClientBuilder builder = new ClientBuilder();
        builder.setNome(nameField.getText())
                .setCognome(surnameField.getText())
                .setCap(Integer.parseInt(CAPField.getText()))
                .setCentro(centro)
                .setIndirizzo(addressField.getText())
                .setCivico(Integer.parseInt(houseNumberField.getText()))
                .setTelefono(phoneField.getText());

        if (emailField.getText().isEmpty())
            builder.setEmail(null);

        ClientService service = (ClientService) ServiceProvider.getService(Cliente.class);
        if (service.findRecord(builder.getCliente()) == null)
            service.insert(builder.getCliente());

        MainApplication.getMainController().navigateBack();
    }

    @Override
    public void onResume() {}

    @FXML
    public void annulla() {
        MainApplication.getMainController().navigateBack();
    }
}
