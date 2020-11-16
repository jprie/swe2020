package dao;

import java.util.List;
import java.util.Optional;

import exception.ServiceException;
import model.Photo;

public interface PhotoDAO {
	
	public void add(Photo photo) throws ServiceException;
	
	public Photo update(Photo photo) throws ServiceException;

	public List<Photo> getAll() throws ServiceException;

	public Optional<Photo> get(long id) throws ServiceException;

	public void delete(Photo photo) throws ServiceException;
}
