package test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import model.Photo;

class PhotoTest {

	@Test
	void test_photo_set_id_simple() {
		
		// input
		Photo testPhoto = new Photo("Toscana", "/images/IMG1234.jpg", null, LocalDate.now(), "Volterra, Toscana");
		
		// expected output
		Photo outputPhoto = new Photo(1, "Toscana", "/images/IMG1234.jpg", null, LocalDate.now(), "Volterra, Toscana");
		
		// method under test
		testPhoto.setId(1);
		
		
		// compare result to expected result
		assertEquals(testPhoto, outputPhoto);
		
		
	}
	
	@Test
	void test_photo_copy_photo() {
		
		// input
		Photo testPhoto = new Photo("Toscana", "/images/IMG1234.jpg", null, LocalDate.now(), "Volterra, Toscana");
		
		// expected output
		Photo outputPhoto = new Photo(1, "Toscana", "/images/IMG1234.jpg", null, LocalDate.now(), "Volterra, Toscana");
		
		// method under test
		Photo copy = new Photo(testPhoto);
		
		// compare result to expected result
		assertEquals(copy, outputPhoto);
		
		
				
		
	}

}
