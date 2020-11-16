package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import exception.ServiceException;
import model.Photo;
import model.Photographer;

public class PhotographerDAO_JDBC implements PhotographerDAO {

	@Override
	public void add(Photographer photographer) throws ServiceException {
		
		try (Connection con = DriverManager.getConnection("jdbc:derby:~/DB/NewPhotoDB; user=test; password=test;");
				Statement stmt = con.createStatement()) {
			
			stmt.execute("INSERT INTO Photographer(first_name, last_name) VALUES('" + photographer.getFirstName() + "', '" +
												photographer.getLastName() + "')");
			
		} catch(Exception e) {
			throw new ServiceException(e.getMessage());
		}

	}

	@Override
	public Photographer update(Photographer photographer) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Photographer> getAll() throws ServiceException {
		ArrayList<Photographer> photographers = new ArrayList<Photographer>();
		
		try (Connection con = DriverManager.getConnection("jdbc:derby:~/DB/NewPhotoDB; user=test; password=test;");
				Statement stmt = con.createStatement()) {
			
			ResultSet res = stmt.executeQuery("SELECT * FROM Photographer");
			
			Photographer photographer;
			
			while(res.next()) {
				photographer = new Photographer(res.getInt(1), res.getString(2), res.getString(3));
				photographers.add(photographer);
			}
			
		} catch(Exception e) {
			throw new ServiceException(e.getMessage());
		}
		return photographers;
	}

	@Override
	public Optional<Photographer> get(long id) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Photographer photographer) throws ServiceException {
		// TODO Auto-generated method stub

	}
	
	public List<Photo> getPhotosFromPhotographer(Photographer p) throws ServiceException {
		ArrayList<Photo> photos = new ArrayList<Photo>();
		
		try (Connection con = DriverManager.getConnection("jdbc:derby:~/DB/NewPhotoDB; user=test; password=test;");
				Statement stmt = con.createStatement()) {
			
			ResultSet res = stmt.executeQuery("SELECT * FROM Photo where photographer_id=" + p.getId());
			
			Photo photo;
			
			while(res.next()) {
				photo = new Photo(res.getInt(1), res.getString(2), res.getString(3), p, res.getDate(5).toLocalDate(), res.getString(6));
				photos.add(photo);
			}
			
		} catch(Exception e) {
			throw new ServiceException(e.getMessage());
		}
		return photos;
		
	}

}
