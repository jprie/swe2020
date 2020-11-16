package dao;

import java.util.List;
import java.util.Optional;

import exception.ServiceException;
import model.Photo;
import model.Photographer;

public interface PhotographerDAO {
	
	public void add(Photographer photographer) throws ServiceException;
	
	public Photographer update(Photographer photographer) throws ServiceException;

	public List<Photographer> getAll() throws ServiceException;

	public Optional<Photographer> get(long id) throws ServiceException;

	public void delete(Photographer photographer) throws ServiceException;
	
	public List<Photo> getPhotosFromPhotographer(Photographer p) throws ServiceException;
}
