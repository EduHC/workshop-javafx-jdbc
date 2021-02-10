package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Seller;
import model.services.SellerService;

public class SellerListController implements Initializable, DataChangeListener {
	
	private SellerService service;

	@FXML
	private TableView<Seller> tableViewSeller;
	
	@FXML
	private TableColumn<Seller, Integer> tableColumnId;
	
	@FXML
	private TableColumn<Seller, String> tableColumnName;
	
	@FXML
	private TableColumn<Seller, String> tableColumnEmail;
	
	@FXML
	private TableColumn<Seller, Date> tableColumnBirthDate;
	
	@FXML
	private TableColumn<Seller, Double> tableColumnBaseSalary;
	
	@FXML
	private TableColumn<Seller, Seller> tableColumnEdit;
	
	@FXML
	private TableColumn<Seller, Seller> tableColumnDelete;
	
	@FXML
	private Button buttonNewSeller;
	
	private ObservableList<Seller> obsList;
	
	
	@FXML
	public void onButtonNewSellerAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Seller department = new Seller();
		createDialogForm(department, "/gui/SellerForm.fxml", parentStage);
	}
	
	
	@Override
	public void initialize(URL url, ResourceBundle resource) {
		initializeNodes();
	}

	public void setSellerService(SellerService service) {
		this.service = service;
	}
	
	private void initializeNodes() {
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("name"));
		tableColumnEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
		tableColumnBirthDate.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy");
		tableColumnBaseSalary.setCellValueFactory(new PropertyValueFactory<>("baseSalary"));
		Utils.formatTableColumnDouble(tableColumnBaseSalary, 2);
		
		// Fazendo com que o TableView acompanhe a altura da janela, pegando pela referência à cena principal no método Main.
		Stage stage = (Stage) Main.getMainScene().getWindow();
		tableViewSeller.prefHeightProperty().bind(stage.heightProperty());
	}
	
	public void updateTableView() {
		if(service == null) {
		throw new IllegalStateException("Service was null");
		}
		
		List<Seller> list = service.findAll();
		obsList = FXCollections.observableArrayList(list);
		tableViewSeller.setItems(obsList);
		initEditButtons();
		initdeleteButtons();
	}
	
	private void createDialogForm(Seller department, String absouluteName, Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absouluteName));
			Pane pane = loader.load();
			
			SellerFormController controller = loader.getController();
			controller.setSeller(department);
			controller.setSellerSerice(new SellerService());
			controller.subscribeDatChangeListener(this);
			controller.updateFormData();
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Seller data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		} 
		catch (IOException e) {
			Alerts.showAlert("IO Exceptio", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	} 

	private void initEditButtons() {
		tableColumnEdit.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnEdit.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("edit");
			
			@Override
			protected void updateItem(Seller department, boolean empty) {
				super.updateItem(department, empty);
				
				if (department == null) {
					setGraphic(null);
					return;
				}
				
				setGraphic(button);
				button.setOnAction(
					event -> createDialogForm(
						department, "/gui/SellerForm.fxml", Utils.currentStage(event)));
			}
		});
	}
	
	
	private void initdeleteButtons() {
		tableColumnDelete.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumnDelete.setCellFactory(param -> new TableCell<Seller, Seller>() {
			private final Button button = new Button("delete");
			
			@Override
			protected void updateItem(Seller department, boolean empty) {
				super.updateItem(department, empty);
				
				if (department == null) {
					setGraphic(null);
					return;
				}
				
				setGraphic(button);
				button.setOnAction(
					event -> removeEntity(department));
			}
		});
	}
	
	private void removeEntity(Seller department) {
		Optional<ButtonType> result =  Alerts.showConfirmation("Confirmation", "Are you sure you want to delete?");
		
		if (result.get() == ButtonType.OK) {
			if (service == null) 
				throw new IllegalStateException("Service was null");
			
			try {
				service.deleteSeller(department);
				updateTableView();
			}		
			catch (DbIntegrityException e) {
				Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);
			}
		}
	}


	@Override
	public void onDataChanged() {
		updateTableView();
		
	}
}
