package main;

import java.util.ArrayList;
import java.util.List;

public class AnimalFarm {

	public static void main(String[] args) {

		List<Animal> animals = new ArrayList<Animal>(List.of(new Animal("Geopard", 40, 100.2, "Mammal"),
															new Animal("Katze", 10, 5.4, "Mammal"),
															new Animal("Hund", 15, 10.5, "Mammal")));
		
		// implemented new feature
		animals.forEach(System.out::println);

		// new version of feature stream api

		//colleague changed same line

		// yet another version

	}

}
