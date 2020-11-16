package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ListIterator;
import java.util.ResourceBundle;

import application.Constants;
import exception.ServiceException;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener.Change;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Photo;
import model.Photographer;
import javafx.scene.control.ListView;

/**
 * PhotographerController implements all the functionality reachable in the PhotographerView
 * i.e. CRUD operations for a photographer and showing the slideshow view for presentation of
 * the selected photographer's photos.
 * @author jprie
 *
 */
public class PhotographerController extends CommonPropertyController {

	private BooleanProperty selectedPhotographerHasNoPhotos = new SimpleBooleanProperty(true);
	
	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private TextField firstNameTextField;

	@FXML
	private TextField lastNameTextField;

	@FXML
	private Button addButton;

	@FXML
	private Button updateButton;

	@FXML
	private Button deleteButton;

	@FXML
	private Button clearButton;

	@FXML
	private ListView<Photographer> listView;

	@FXML
	private Button slideShowButton;

	/**
	 * Adds a non-empty photographer to the list and stores it in the database
	 * This photographer can be used in the photoLibraryView (selected in the combobox) 
	 * immediately.
	 * @param event
	 */
	@FXML
	void handleAddAction(ActionEvent event) {

		String firstName = firstNameTextField.getText();
		String lastName = lastNameTextField.getText();

		if (!firstName.equals("") && !lastName.equals("")) {

			Photographer photographer = new Photographer(firstName, lastName);

			photographerList.add(photographer);

			try {
				photographerDAO.add(photographer);
			} catch (Exception e) {
				e.printStackTrace();
			}

			resetSelectedPhotographer();
		}
	}

	/**
	 * Clear the information of the currently selected photographer
	 * @param event
	 */
	@FXML
	void handleClearAction(ActionEvent event) {

		resetSelectedPhotographer();
	}

	/**
	 * Reset the currently selected photographer to allow for entering new data or 
	 * selecting another photographer from the list
	 */
	private void resetSelectedPhotographer() {
		
		clearForm();
		selectedPhotographer.set(null);
	}

	/**
	 * Clears the text fields of the form
	 */
	private void clearForm() {

		firstNameTextField.clear();
		lastNameTextField.clear();

	}

	/**
	 * Deletes the currently selected photographer from the list and the database
	 * @param event
	 */
	@FXML
	void handleDeleteAction(ActionEvent event) {

		Photographer photographer = selectedPhotographer.get();
		photographerList.remove(photographer);
		
		try {
			photographerDAO.delete(photographer);
			
		} catch (ServiceException e) {
			// could not be deleted from DB
			System.out.println(e.getMessage());
			photographerList.add(photographer);
		}
		
		
		listView.getSelectionModel().clearSelection();
		resetSelectedPhotographer();
	}

	/**
	 * Updates the currently selected photographer in the list (add/remove) and updates
	 * its entity in the database.
	 * @param event
	 */
	@FXML
	void handleUpdateAction(ActionEvent event) {

		Photographer photographer = selectedPhotographer.get();
		
		photographerList.remove(photographer);
		
		photographer.setFirstName(firstNameTextField.getText());
		photographer.setLastName(lastNameTextField.getText());
		
		try {
			photographer = photographerDAO.update(photographer);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
		photographerList.add(photographer);
		
		resetSelectedPhotographer();
		
	}

	/**
	 * Initializes the photographer view.
	 * Sets up bindings for usability (when should which button be enabled).
	 * Sets up the listeners for common properties to keep photographers and photos synchronized between 
	 */
	@FXML
	void initialize() {
		assert firstNameTextField != null : "fx:id=\"firstNameTextField\" was not injected: check your FXML file 'PhotographerView.fxml'.";
		assert lastNameTextField != null : "fx:id=\"lastNameTextField\" was not injected: check your FXML file 'PhotographerView.fxml'.";
		assert addButton != null : "fx:id=\"addButton\" was not injected: check your FXML file 'PhotographerView.fxml'.";
		assert updateButton != null : "fx:id=\"updateButton\" was not injected: check your FXML file 'PhotographerView.fxml'.";
		assert deleteButton != null : "fx:id=\"deleteButton\" was not injected: check your FXML file 'PhotographerView.fxml'.";
		assert clearButton != null : "fx:id=\"clearButton\" was not injected: check your FXML file 'PhotographerView.fxml'.";

		listView.setItems(photographerList);

		listView.getSelectionModel().selectedItemProperty().addListener(this::handlePhotographerListSelectionChanged);

		selectedPhotographer.addListener(this::handleSelectedPhotographerChanged);

		// disable when photographer selected OR one of the text fields is empty
		addButton.disableProperty().bind(selectedPhotographer.isNotNull()
				.or(firstNameTextField.textProperty().isEmpty().or(lastNameTextField.textProperty().isEmpty())));
		updateButton.disableProperty().bind(selectedPhotographer.isNull()
				.or(firstNameTextField.textProperty().isEmpty().or(lastNameTextField.textProperty().isEmpty())));
		deleteButton.disableProperty().bind(selectedPhotographer.isNull());
		slideShowButton.disableProperty().bind(selectedPhotographer.isNull().or(selectedPhotographerHasNoPhotos));
		
		// keep photographersList and photoList synchronized
		photographerList.addListener(this::handlePhotographerListChanged);
		
	}
	
	/**
	 * Updates the selected photographer property and the selectedPhotographerHasNoPhotos property
	 * @param observable
	 * @param oldValue
	 * @param newValue
	 */
	public void handlePhotographerListSelectionChanged(ObservableValue<? extends Photographer> observable, Photographer oldValue,
			Photographer newValue) {

		selectedPhotographer.set(newValue);
		if (newValue != null) {
			selectedPhotographerHasNoPhotos.set(newValue.getPhotos().isEmpty());
		}

	}

	/**
	 * Updates the text fields of the form if a photographer was selected
	 * @param observable
	 * @param oldValue
	 * @param newValue
	 */
	public void handleSelectedPhotographerChanged(ObservableValue<? extends Photographer> observable, Photographer oldValue,
			Photographer newValue) {
		
		if (newValue != null) {
			firstNameTextField.setText(newValue.getFirstName());
			lastNameTextField.setText(newValue.getLastName());
		}

	}

	/**
	 * Removes all the photos from the photo list if the photographer was removed.
	 * Otherwise the photos in the list remain there and are no longer in sync with
	 * the database.
	 * (Adding a new photographer is not handled since a new photographer has no 
	 * photos yet)
	 * @param c
	 */
	public void handlePhotographerListChanged(Change<? extends Photographer> c) {
		
		while (c.next()) {
			
			if (c.wasRemoved()) {
				for (Photographer ph : c.getRemoved()) {
				
					ListIterator<Photo> iter = photoList.listIterator();
					
					while (iter.hasNext()) {
						
						Photo p = iter.next();
						if (p.getPhotographer().equals(ph)) {
							iter.remove();
						}
						
					}
				}
			}
		}
		
	}

	/**
	 * Opens a new model stage for presentation of a photo slide show
	 * @param event
	 * @throws IOException
	 */
	@FXML
	public void handleStartSlideShowAction(ActionEvent event) throws IOException {
		
		FXMLLoader loader = new FXMLLoader(getClass().getResource(Constants.PATH_TO_SLIDESHOW_VIEW_FXML));
		Parent root = loader.load();
		
		Stage slideshowStage = new Stage();
		slideshowStage.initModality(Modality.APPLICATION_MODAL);
		
		Scene scene = new Scene(root);
		
		slideshowStage.setScene(scene);
		slideshowStage.showAndWait();
	}
}
