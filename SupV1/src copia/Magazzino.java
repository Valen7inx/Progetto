import java.util.ArrayList;

public class Magazzino {
	
private ArrayList <Articolo> ElencoArticoli;

public ArrayList <Articolo> getElencoArticoli() {
	return ElencoArticoli;
}

public Magazzino() {
	ElencoArticoli = new ArrayList <Articolo>();
}
public void setElencoArticoli(ArrayList <Articolo> elencoArticoli) {
	ElencoArticoli = elencoArticoli;
}

public boolean add(Articolo a) {
	return ElencoArticoli.add(a);
}

public boolean remove(Articolo a) {
	return ElencoArticoli.remove(a);	
}

public boolean contains(Articolo a) {
	return ElencoArticoli.contains(a); 
}

public int getSize() {
	return ElencoArticoli.size();
}

public void clear() {
	ElencoArticoli.clear();
}
}
