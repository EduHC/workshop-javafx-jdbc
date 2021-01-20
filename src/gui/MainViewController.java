package gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;

public class MainViewController implements Initializable{

	@FXML
	private MenuItem menuItemSeller;
	
	@FXML
	private MenuItem menuItemDepartment;
	
	@FXML
	private MenuItem menuItemAbout;
	
	@FXML
	public void onMenuItemSellerActon() {
		System.out.println("onMenuItemSellerActon");
	}
	
	@FXML
	public void onMenuItemDepartmentActon() {
		System.out.println("onMenuItemDepartmentActon");
	}
	
	@FXML
	public void onMenuItemAboutActon() {
		System.out.println("onMenuItemAboutActon");
	}
	
	
	@Override
	public void initialize(URL uri, ResourceBundle resource) {
	}

}
