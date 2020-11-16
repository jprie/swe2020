module PhotoLibrary {
	requires javafx.controls;
	requires javafx.base;
	requires javafx.web;
	requires javafx.fxml;
	requires java.sql;
	requires junit;
	requires org.junit.jupiter.api;
	
	opens application to javafx.graphics, javafx.fxml;
	opens controller to javafx.fxml;
}
