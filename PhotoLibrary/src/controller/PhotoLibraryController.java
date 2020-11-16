package controller;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import exception.ServiceException;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener.Change;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.FileChooser;
import javafx.util.converter.LocalDateStringConverter;
import model.Photo;
import model.Photographer;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PhotoLibraryController extends CommonPropertyController {

	// define the de_AT date format to be used
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	
	@FXML // ResourceBundle can be used for internationalization!!
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private TableView<Photo> photoTable;

	@FXML
	private TableColumn<Photo, String> nameTableColumn;

	@FXML
	private TextField nameText;

	@FXML
	private TextField urlText;

	@FXML
	private Button selectButton;

	@FXML
	private ComboBox<Photographer> photographerComboBox;

	@FXML
	private TextField locationText;

	@FXML
	private Button clearButton;

	@FXML
	private Button deleteButton;

	@FXML
	private Button updateButton;

	@FXML
	private Button newButton;

	@FXML
	private ImageView photoImageView;

	@FXML
	private TableColumn<Photo, String> locationTableColumn;

	@FXML
	private TextField dateTextField;

	@FXML
	private TableColumn<Photo, LocalDate> dateTableColumn;

	/**
	 * Initialize gets called from the FXMLLoader automatically and is therefore a
	 * good place to initialize the mechanisms and bindings in the controller.
	 */
	@FXML
	void initialize() {
		assert photoTable != null : "fx:id=\"photoTable\" was not injected: check your FXML file 'PhotoLibrary.fxml'.";
		assert nameTableColumn != null : "fx:id=\"nameColumn\" was not injected: check your FXML file 'PhotoLibrary.fxml'.";
		assert nameText != null : "fx:id=\"nameText\" was not injected: check your FXML file 'PhotoLibrary.fxml'.";
		assert urlText != null : "fx:id=\"urlText\" was not injected: check your FXML file 'PhotoLibrary.fxml'.";
		assert selectButton != null : "fx:id=\"selectButton\" was not injected: check your FXML file 'PhotoLibrary.fxml'.";
		assert photographerComboBox != null : "fx:id=\"photographerComboBox\" was not injected: check your FXML file 'PhotoLibrary.fxml'.";
		assert locationText != null : "fx:id=\"locationText\" was not injected: check your FXML file 'PhotoLibrary.fxml'.";
		assert clearButton != null : "fx:id=\"clearButton\" was not injected: check your FXML file 'PhotoLibrary.fxml'.";
		assert deleteButton != null : "fx:id=\"deleteButton\" was not injected: check your FXML file 'PhotoLibrary.fxml'.";
		assert updateButton != null : "fx:id=\"updateButton\" was not injected: check your FXML file 'PhotoLibrary.fxml'.";
		assert newButton != null : "fx:id=\"newButton\" was not injected: check your FXML file 'PhotoLibrary.fxml'.";

		// init photos from DB
		try {
			photoList.addAll(photoDAO.getAll());
		} catch (ServiceException e) {

			System.out.println("Could not load photos from DB");
			e.printStackTrace();
		}

		// init photographers from DB
		try {
			photographerList.addAll(photographerDAO.getAll());
		} catch (ServiceException e) {

			System.out.println("Could not load photographers from DB");
			e.printStackTrace();
		}

		// configure formatter for dateTextField
		TextFormatter<LocalDate> dateFormatter = new TextFormatter<>(new LocalDateStringConverter(formatter, formatter));

		dateTextField.setTextFormatter(dateFormatter);

		// configure photographer choice box
		photographerComboBox.setItems(photographerList);

		// setup the table
		photoTable.setItems(photoList);
		photoTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		// configure cells of table with the cell factory
		// the factory gets the photos properties as input
		// and creates String representations of its values
		nameTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
		locationTableColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLocation()));

		// the date needs special treatment since it cannot be converted to String
		// automatically
		dateTableColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<LocalDate>(cellData.getValue().getDate()));
		dateTableColumn.setCellFactory(this::createTableCellForLocalDateColumn);

		// add listener for handling selection changes in the table
		photoTable.getSelectionModel().selectedItemProperty().addListener(this::handlePhotoTableSelectionChanged);

		// add listener for handling the selected photo property being changed
		selectedPhoto.addListener(this::handleSelectedPhotoChanged);

		// keep photoList and photographerList synchronized when photographer of photo
		// was changed
		photoList.addListener(this::handlePhotoListChanged);

		// bindings define the logic when buttons are to be enabled
		newButton.disableProperty()
				.bind(nameText.textProperty().isEmpty().or(urlText.textProperty().isEmpty())
						.or(photographerComboBox.getSelectionModel().selectedItemProperty().isNull())
						.or(locationText.textProperty().isEmpty()).or(selectedPhoto.isNotNull()));

		updateButton.disableProperty()
				.bind(nameText.textProperty().isEmpty().or(urlText.textProperty().isEmpty())
						.or(photographerComboBox.getSelectionModel().selectedItemProperty().isNull())
						.or(locationText.textProperty().isEmpty()).or(selectedPhoto.isNull()));

		deleteButton.disableProperty().bind(selectedPhoto.isNull());

	}

	/**
	 * Creates a table cell for a LocalDate column in photo table Since we need to
	 * format the cell for our LocalDate, the usual Cell-from-property mechanism
	 * does not work.
	 */
	private TableCell<Photo, LocalDate> createTableCellForLocalDateColumn(TableColumn<Photo, LocalDate> column) {

		return new TableCell<Photo, LocalDate>() {
			@Override
			protected void updateItem(LocalDate item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
				} else {
					// format date as dd.MM.yyyy
					setText(formatter.format(item));
				}
			}

		};

	}

	/**
	 * Updates the selected photo property
	 * 
	 * @param observable
	 * @param oldValue
	 * @param newValue
	 */
	public void handlePhotoTableSelectionChanged(ObservableValue<? extends Photo> observable, Photo oldValue,
			Photo newValue) {

		selectedPhoto.set(newValue);
	}

	/**
	 * Adds and removes photos from the photographer in the photographer list
	 * keeping track of changes in the photo list
	 * 
	 * @param c
	 */
	public void handlePhotoListChanged(Change<? extends Photo> c) {

		while (c.next()) {
			if (c.wasAdded()) {
				c.getAddedSubList();
				for (Photo p : c.getAddedSubList()) {
					for (Photographer ph : photographerList) {
						if (ph.equals(p.getPhotographer())) {
							ph.getPhotos().add(p);
						}
					}
				}
			}
			if (c.wasRemoved()) {
				for (Photo p : c.getRemoved()) {
					for (Photographer ph : photographerList) {
						if (ph.getPhotos().contains(p)) {
							ph.getPhotos().remove(p);
						}
					}
				}
			}
		}

	}

	/**
	 * Updates the data in the photo form when a new photo was selected
	 * 
	 * @param arg0
	 * @param arg1
	 * @param newPhoto
	 */
	public void handleSelectedPhotoChanged(ObservableValue<? extends Photo> arg0, Photo arg1, Photo newPhoto) {

		// show url in webview
		if (newPhoto != null) {
			Image image = new Image(getClass().getResourceAsStream(newPhoto.getUrl()));
			photoImageView.setImage(image);

			// set values for photo
			nameText.setText(newPhoto.getName());
			locationText.setText(newPhoto.getLocation());
			dateTextField.setText(newPhoto.getDate().format(formatter));
			photographerComboBox.getSelectionModel().select(newPhoto.getPhotographer());
			urlText.setText(newPhoto.getUrl());

		}

	}

	/**
	 * Add a photo with the given attributes from the form to the list
	 * Try to store them in the database and if not possible remove the
	 * photo from the list.
	 * @param event
	 * @throws ServiceException
	 */
	
	@FXML
	void handleAddPhotoAction(ActionEvent event) throws ServiceException {

		Photo photo = new Photo(nameText.getText(), urlText.getText(),
				photographerComboBox.getSelectionModel().getSelectedItem(),
				LocalDate.parse(dateTextField.getText(), formatter), locationText.getText());
		photoList.add(photo);

		try {
			photoDAO.add(photo);
		} catch (ServiceException e) {
			System.out.println("Photo could not be persisted!");
			photoList.remove(photo);
		}

		resetSelectedPhoto();
	}

	/**
	 * Clear the information of the currently selected photo
	 * @param event
	 */
	@FXML
	void handleClearPhotoAction(ActionEvent event) {

		resetSelectedPhoto();
	}
	
	/**
	 * Reset the currently selected photo to allow for entering new data or 
	 * selecting another photo from the library
	 */
	private void resetSelectedPhoto() {
		
		clearForm();
		selectedPhoto.set(null);
	}

	/**
	 * Clears the text fields of the form
	 */
	private void clearForm() {

		nameText.clear();
		urlText.clear();
		photographerComboBox.getSelectionModel().clearSelection();
		locationText.setText("");
		dateTextField.clear();
		photoImageView.setImage(null);
		
	}

	/**
	 * Deletes the currently selected photographer from the list and the database
	 * @param event
	 */
	@FXML
	public void handleDeletePhotoAction(ActionEvent event) {
		Photo selectedPhoto = photoTable.getSelectionModel().getSelectedItem();

		// remove photo immediately to please the user
		photoList.remove(selectedPhoto);
		
		try {
			photoDAO.delete(selectedPhoto);
			
		} catch (ServiceException e) {
			System.out.println(e.getMessage());
			
			// photo could not be deleted from DB, add to photoList to keep in sync
			photoList.add(selectedPhoto);
			
		}

		resetSelectedPhoto();
	}

	/**
	 * Updates the currently selected photo in the list (add/remove) and updates its
	 * entity in the database.
	 * 
	 * @param event
	 */
	@FXML
	void handleUpdatePhotoAction(ActionEvent event) {
		Photo oldPhoto = photoTable.getSelectionModel().getSelectedItem();

		// create a copy with identical id
		Photo newPhoto = new Photo(oldPhoto);

		// update values
		newPhoto.setLocation(locationText.getText());
		newPhoto.setName(nameText.getText());
		newPhoto.setDate(LocalDate.parse(dateTextField.getText(), formatter));
		newPhoto.setUrl(urlText.getText());
		newPhoto.setPhotographer(photographerComboBox.getSelectionModel().getSelectedItem());

		try {
			Photo updatedPhoto = photoDAO.update(newPhoto);

			// remove and add is simpler
			photoList.remove(oldPhoto);
			photoList.add(updatedPhoto);
		} catch (ServiceException e) {
			System.out.println(e.getMessage());
		}

		photoTable.refresh();
		photoTable.getSelectionModel().clearSelection();

		resetSelectedPhoto();
	}

	/**
	 * Shows a file chooser to select photo files (i.e. their location) from the
	 * file system Restriction: only files in the images-Folder of the Resources of
	 * the project are supported. Otherwise the relative path "/images/..." wont be
	 * found. TODO: Store the absolute path s.t. images from anywhere in the file
	 * system can be used.
	 * 
	 * @param event
	 * @throws URISyntaxException
	 */
	@FXML
	void handleSelectFileAction(ActionEvent event) throws URISyntaxException {
		FileChooser fileChooser = new FileChooser();

		//fileChooser.setInitialDirectory(new File(getClass().getResource("/" + "images").toURI()));

		File file = fileChooser.showOpenDialog(selectButton.getScene().getWindow());
		if (file != null) {

			Path p = Paths.get(file.getPath());

			// create path relative from resources, prepending '/' necessary for
			// getResource(...)
			urlText.setText("/images/" + p.getFileName().toString());
			Image image = new Image(getClass().getResourceAsStream(urlText.getText()));
			photoImageView.setImage(image);

		}
	}

}
