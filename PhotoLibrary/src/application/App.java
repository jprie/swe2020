package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class App extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		try {
			// object-tree
			
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(Constants.PATH_TO_TAB_VIEW_FXML));
			Parent root = loader.load();	
			
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void init() throws Exception {
	
		super.init();
		
		// Create DB connection
		//DatabaseContext.setupDatabase();
		
	}
	
	@Override
	public void stop() throws Exception {
	 
		// Close DB connection
		//DatabaseContext.bringDownDatabase();
		
		super.stop();
	}

}
