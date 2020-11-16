package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import exception.ServiceException;
import model.Photo;

public class PhotoDAO_JDBC implements PhotoDAO {

	
	@Override
	public void add(Photo photo) throws ServiceException {

		try (Connection con = DriverManager.getConnection("jdbc:derby:~/DB/NewPhotoDB; user=test; password=test;");
				Statement stmt = con.createStatement()) {
			
			stmt.execute("INSERT INTO Photo(name, location, date, url, photographer_id) VALUES("
												+ "'" + photo.getName() + "', "
												+ "'" + photo.getLocation() + "', "
												+ "'" + java.sql.Date.valueOf(photo.getDate()) + "', " 
												+ "'" + photo.getUrl() + "', " +
												photo.getPhotographer().getId() +
												 ")");
			
		} catch(Exception e) {
//			throw new ServiceException(e.getMessage());
			e.printStackTrace();
		}
		
	}

	@Override
	public Photo update(Photo photo) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Photo> getAll() throws ServiceException {
		// TODO Auto-generated method stub
		return new ArrayList<Photo>();
	}

	@Override
	public Optional<Photo> get(long id) throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Photo photo) throws ServiceException {
		// TODO Auto-generated method stub
		
	}

}
