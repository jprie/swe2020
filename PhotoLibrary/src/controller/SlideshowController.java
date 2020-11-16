package controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Window;
import model.Photo;
import javafx.scene.control.ProgressIndicator;

/**
 * Slideshow controller implements the logic for showing all the photos of the
 * currently selected photographer. An automated slideshow is started at the
 * beginning. It can be paused and re-started. When paused next and previous
 * picture can be loaded manually.
 * 
 * @author jprie
 *
 */
public class SlideshowController extends CommonPropertyController {

	private List<Photo> slideShowImages = null; //selectedPhotographer.get().getPhotos();

	private IntegerProperty currentIndex = new SimpleIntegerProperty(0);

	private Task<Integer> slideShowTask;

	private BooleanProperty isPlaying = new SimpleBooleanProperty(true);

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private ImageView imageView;

	@FXML
	private Button rewindButton;

	@FXML
	private Button playPauseButton;

	@FXML
	private Button forwardButton;

	@FXML
	ProgressIndicator progressIndicator;

	// Action handlers for the button-based UI
	@FXML
	void handleForwardButtonAction(ActionEvent event) {

		loadNextImage();

	}

	@FXML
	void handlePlayPauseAction(ActionEvent event) {

		if (isPlaying.get()) {
			playPauseButton.setText("Play");
			isPlaying.set(false);
			slideShowTask.cancel();
		} else {
			playPauseButton.setText("||");
			isPlaying.set(true);
			startSlideShow();
		}
	}

	@FXML
	void handleRewindButtonAction(ActionEvent event) {

		loadPreviousImage();
	}

	private void loadImageAtIndex(int index) {
		imageView.setImage(new Image(getClass().getResourceAsStream(slideShowImages.get(index).getUrl())));
	}

	@FXML
	void initialize() {
		assert imageView != null : "fx:id=\"imageView\" was not injected: check your FXML file 'Slideshow.fxml'.";
		assert rewindButton != null : "fx:id=\"rewindButton\" was not injected: check your FXML file 'Slideshow.fxml'.";
		assert playPauseButton != null : "fx:id=\"playPauseButton\" was not injected: check your FXML file 'Slideshow.fxml'.";
		assert forwardButton != null : "fx:id=\"forwardButton\" was not injected: check your FXML file 'Slideshow.fxml'.";

		// load images in image list
		try {
			slideShowImages = photographerDAO.getPhotosFromPhotographer(selectedPhotographer.get());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// load the first image
		loadImageAtIndex(currentIndex.get());

		// when should which button be enabled?
		forwardButton.disableProperty().bind(currentIndex.greaterThanOrEqualTo(slideShowImages.size() - 1));
		rewindButton.disableProperty().bind(currentIndex.lessThan(1));

		progressIndicator.setProgress(-1);
		progressIndicator.setVisible(false);

		// Since scene and stage are not ready while we are in initializable(), we need
		// to wait till that happens.
		// Therefore we add listeners to the sceneProperty() and stageProperty() to get
		// notified, when they are ready.
		imageView.sceneProperty().addListener(this::handleSceneChanged);

		// start the automated slide show in a new task
		startSlideShow();
	}

	/**
	 * Creates a new Task and starts it in a new thread. The task (worker-thread)
	 * can be cancelled. Change to the UI have to be made on the Application Thread,
	 * thus Platform.runLater(...) which puts the UI-Action on the the Application
	 * thread.
	 */
	private void startSlideShow() {

		// we need a new task each time, since a cancelled task cannot be re-started!
		slideShowTask = new Task<Integer>() {

			@Override
			protected Integer call() throws Exception {

				// we could use true, since this requirement always holds, but if somebody from
				// the outside sets currentIndex to an out-of-range value, we are on the safe
				// side.
				while (currentIndex.get() < slideShowImages.size()) {
					try {

						// show image for 2 seconds
						Thread.sleep(2000);

						// show progress indicator
						Platform.runLater(() -> progressIndicator.setVisible(true));

						// "loading" is shown for half a second
						Thread.sleep(500);

						// load new image on the application thread
						Platform.runLater(() -> loadNextImage());


					} catch (InterruptedException e) {
						System.out.println("Task was woken up/cancelled from outside!");
						
						// check if task was cancelled from the outside
						if (isCancelled()) {
							break;
						}

					}

				}

				return null;
			}

		};

		// create and start thread for automated slideShow
		Thread t = new Thread(slideShowTask);
		t.start();

	}

	/**
	 * Loads the next image in the slideShowImages. If end is reached, starts from 0
	 * again!
	 */
	private void loadNextImage() {

		if (currentIndex.get() < slideShowImages.size() - 1) {
			currentIndex.set(currentIndex.get() + 1);

		} else {
			currentIndex.set(0);
		}
		loadImageAtIndex(currentIndex.get());
		progressIndicator.setVisible(false);

	}

	/**
	 * Loads the previous image, as long as there is one. Does NOT go back to the
	 * end of the list.
	 */
	private void loadPreviousImage() {

		if (currentIndex.get() > 0) {
			currentIndex.set(currentIndex.get() - 1);
		}
		loadImageAtIndex(currentIndex.get());
		progressIndicator.setVisible(false);

	}

	// Change listeners for cleaning running task when window gets closed

	/**
	 * Listening for the Scene to change
	 * 
	 * @param observable
	 * @param oldValue
	 * @param scene
	 */
	public void handleSceneChanged(ObservableValue<? extends Scene> observable, Scene oldValue, Scene scene) {

		if (scene != null) {

			// set listener for stage being set
			scene.windowProperty().addListener(this::handleStageChanged);
		}

	}

	/**
	 * Listening for the Window to change
	 * 
	 * @param observable
	 * @param oldValue
	 * @param scene
	 */
	public void handleStageChanged(ObservableValue<? extends Window> observable, Window oldValue,
			Window slideShowStage) {

		slideShowStage.setOnCloseRequest((WindowEvent) ->

		// cancel running Task if still running
		slideShowTask.cancel());

	}
}
