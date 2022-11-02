package it.polito.tdp.borders.model;

import java.util.ArrayList;

public class TestSimulatore {

	public static void main(String[] args) {
		
		Model m = new Model();
		m.creaGrafo(2000);
		m.simula(new ArrayList<Country>(m.getCountries()).get(0));

	}

}
