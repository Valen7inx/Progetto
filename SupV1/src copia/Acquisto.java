import java.util.Date;

public class Acquisto {
private Date data;
private String idArticolo;
private int quantita;
private int codiceAcquisto;

public Acquisto(Date data, String id, int quantita, int codiceAcquisto) {
	this.setData(data);
	this.setIdArticolo(id);
	this.setQuantita(quantita);
	this.setCodiceAcquisto(codiceAcquisto);
}
public Date getData() {
	return data;
}
public void setData(Date data) {
	this.data = data;
}
public String getIdArticolo() {
	return idArticolo;
}
public void setIdArticolo(String idArticolo) {
	this.idArticolo = idArticolo;
}
public int getQuantita() {
	return quantita;
}
public void setQuantita(int quantita) {
	this.quantita = quantita;
}
public int getCodiceAcquisto() {
	return codiceAcquisto;
}
public void setCodiceAcquisto(int codiceAcquisto) {
	this.codiceAcquisto = codiceAcquisto;
}
}

