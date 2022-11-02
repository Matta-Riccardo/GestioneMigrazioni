package it.polito.tdp.borders.model;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;

public class Simulatore {

//	CODA DEGLI EVENTI
	
//	es. di evento ----> Evento (T, Country, Quantità di persone)
//					   Evento (1, Italia, 1000)
//					   		500 staziali in italia
//							4 stati confinanti ---> 125 persone per stato 
//
//							crea eventi di ingresso e quando li estraggo li prendo una alla volta
//
//					   Evento (2, Francia, 125)
//							62 diventano stanziali in Francia
//							63 si dividono tra 5 stati confinanti: di cui 12 per stato e i rimamenti 3 diventano stanziali
//							
//	  				   Evento (2, Svizzera, 125)
//							62 diventano stanziali in Svizzera
//							63 si dividono tra 4 stati confinanti: di cui 15 per stato e i rimamenti 3 diventano stanziali
//
//	   				   Evento (2, Austria, 125)
//	   				   Evento (2, Slovenia, 125)
//					   Evento (3, Spagna, 12)
//					   Evento (3, Italia, 12)
//					   Evento (3, Germania, 12)
//	   				   Evento (3, Belgio, 12)
//	   				   Evento (3, Olanda, 12)
//					   Evento (3, Francia, 15)
//	  				   Evento (3, Italia, 15)
//	  				   Evento (3, Germania, 15)
//	  				   Evento (3, Austria, 15)
	
	private PriorityQueue<Event> queue; 		
	
//	PARAMETRI DI SIMULAZIONE
	private int nInizialeMigranti;
	private Country nazioneIniziale; // Questi sono i due parametri che mi servono per avviare la simulazione
	
//	OUTPUT DELLA SIMULAZIONE = numero di passi della simulazione ed elenco per stato di numero di persone presenti
	private int nPassi; 
	private Map<Country, Integer> persone; // Oppure posso usare una lista: List<CountryAndNumber> personeStazianli;
	
//	STATO DEL MONDO SIMULATO
	private Graph<Country, DefaultEdge> grafo;
	// Qui mi serve anche la mappa persone, Country --> Integer

	public Simulatore(Graph<Country, DefaultEdge> grafo) {
		super();
		this.grafo = grafo;
	}
	
	public void init(Country partenza, int migranti) {
		this.nazioneIniziale = partenza;
		this.nInizialeMigranti = migranti;
		
		this.persone = new HashMap<Country, Integer>();
		for(Country c : this.grafo.vertexSet()) { // Se faccio diverse simulazioni ogni volta la mappa deve partire da zero e non devo portarmi dietri i residui della simulazione precedente
			this.persone.put(c, 0);
		}
		
		this.queue = new PriorityQueue<Event>();
		this.queue.add(new Event(1, this.nazioneIniziale, this.nInizialeMigranti));
		
	}
	
	public void run() {
		while(!this.queue.isEmpty()) {
			Event e = this.queue.poll();
//			System.out.println(e);
			processEvent(e);
		}
	}

	private void processEvent(Event e) {
		int stanziali = e.getPersone() / 2;
		int migranti = e.getPersone() - stanziali;
		int confinanti = this.grafo.degreeOf(e.getNazione());
		int gruppiMigranti = migranti / confinanti;
		stanziali += migranti % confinanti;
		
//		Gli stanziali che calcolo li devo sommare eventualmente agli stanziali che già erano presenti in quello stato
		this.persone.put(e.getNazione(), this.persone.get(e.getNazione())+stanziali);

//		Ogni volta che elaboro un evento aggiorno il numero di T, in modo che quando arrivo alla fine ho traccia del numero di passi che ho dovuto eseguire
		this.nPassi = e.getTime();
		
//		Ho aggiornato lo stato degli eventi ma sto anche generando nuovi eventi per questi gruppi verso tutti gli stati confinanti con e.getNazione()
		if(gruppiMigranti != 0) {	
			for(Country vicino : Graphs.neighborListOf(this.grafo, e.getNazione())) { //Metodo che dato un vertice mi permette di risalire a tutti i vertici confinanti
				this.queue.add(new Event(e.getTime()+1, vicino, gruppiMigranti));
			}
		}
	}

	public int getnPassi() {
		return nPassi;
	}

	public Map<Country, Integer> getPersone() {
		return persone;
	}
	
}
