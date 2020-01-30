
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import javax.swing.text.JTextComponent;
import java.awt.Color;
import java.awt.Component;

import javax.swing.UIManager;

public class NegozioController {
	static Magazzino MagazzinoTransazionale;
	static Magazzino MagazzinoTemporaneo;
	CarrelloUtente CarrelloUtente;
	CarrelloFrame CarrelloFrame;
	static HomePage HomePageFrame;
	MagazzinoFrame MagazzinoFrame;
	EliminaDaMagazzinoFrame EliminaDaMagazzinoFrame;
	InserimentoArticoloInMagazzinoFrame InserimentoArticoloInMagazzinoFrame;
	AggiungiAlCarrelloFrame AggiungiAlCarrelloFrame;
	RimuoviDalCarrelloFrame RimuoviDalCarrelloFrame;
	VetrinaFrame VetrinaFrame;
	PagamentoFrame PagamentoFrame;
	static Connection connessione;
	static MagazzinoDAO MagazzinoDAO;
	static ArticoloDAO ArticoloDAO;
	
	

	public NegozioController() throws Exception {
		MagazzinoTransazionale = new Magazzino();
		MagazzinoTemporaneo = new Magazzino();
		CarrelloUtente = new CarrelloUtente();
		
		connessione = getConnection();
		MagazzinoDAO MagazzinoDAO = new MagazzinoDAO(connessione);
		ArticoloDAO ArticoloDAO = new ArticoloDAO(connessione);
		
		MagazzinoDAO.creaTabellaMagazzinoSQL();
		ArticoloDAO.creaTabellaArticoloSQL();
		riempiMagazzinoDaDB();
		
		MagazzinoTemporaneo = MagazzinoTransazionale;
		
		EliminaDaMagazzinoFrame = new EliminaDaMagazzinoFrame(this);
		AggiungiAlCarrelloFrame = new AggiungiAlCarrelloFrame(this);
		CarrelloFrame = new CarrelloFrame(this);
		VetrinaFrame = new VetrinaFrame(this);
		MagazzinoFrame = new MagazzinoFrame(this);
		
		
	}
	
	public static void main(String[] args) throws Exception {
		NegozioController TheController = new NegozioController();
		HomePageFrame = new HomePage(TheController);
		HomePageFrame.setVisible(true);
		MagazzinoDAO = new MagazzinoDAO(connessione);
		ArticoloDAO = new ArticoloDAO(connessione);
		InserimentoArticoloInMagazzinoFrame InserimentoArticoloInMagazzinoFrame = new InserimentoArticoloInMagazzinoFrame(TheController);
		EliminaDaMagazzinoFrame EliminaDaMagazzinoFrame = new EliminaDaMagazzinoFrame (TheController);
		MagazzinoFrame MagazzinoFrame = new MagazzinoFrame(TheController);
		RimuoviDalCarrelloFrame RimuoviDalCarrelloFrame = new RimuoviDalCarrelloFrame(TheController);
		VetrinaFrame VetrinaFrame= new VetrinaFrame(TheController);
		AggiungiAlCarrelloFrame AggiungiAlCarrelloFrame = new AggiungiAlCarrelloFrame(TheController);

	}
	
	

	private static void riempiMagazzinoDaDB() throws SQLException, IOException {
		ArticoloDAO = new ArticoloDAO(connessione);
		String sql = "SELECT * FROM Magazzino";
		PreparedStatement getArticolo = connessione.prepareStatement(sql);
		ResultSet result = getArticolo.executeQuery();
		while(result.next()) {
			Integer quantita = new Integer(result.getString(7));
			while(quantita>0) {
				String nome = (result.getString(1));
				String id = (result.getString(2));
				Double prezzo = new Double(result.getString(3));
				String pathfoto = "";
				String taglia = (result.getString(5));
				String colore = (result.getString(6));
				Articolo ArticoloTrovato = new Articolo (nome, id, prezzo, pathfoto, taglia, colore);
				ArticoloDAO.JdbcReadImage(ArticoloTrovato);
				MagazzinoTransazionale.add(ArticoloTrovato);
				MagazzinoTemporaneo.add(ArticoloTrovato);
				quantita--;
			}
		}
	}


	
	public static Connection getConnectionLocale() throws Exception{
		try {
			String driver = "com.mysql.jdbc.Driver";
			String url = "jdbc:mysql://localhost:3306/giraffe";
			String username = "root";
			String password = "password";
			Class.forName(driver);
			Connection conn = DriverManager.getConnection(url, username, password);
			System.out.println ("connected");
			return conn;
		} catch (Exception e) { System.out.println(e); }
		return null;
		
	}
	

	public static Connection getConnection() throws Exception{
	     try{ 
	    	 String driver = "com.mysql.cj.jdbc.Driver";
	    	 Class.forName(driver);
	    	 	try{
	    	 		String url ="jdbc:mysql://sql7.freesqldatabase.com:3306/sql7317801";
	    	 		String username = "sql7317801";
	    	 		String password = "JSfK3lPNgq";
	    	 		Connection connection = DriverManager.getConnection(url, username ,password);
	    	 		return connection;
	    	 	}
	    	 	catch (SQLException e) {
	    	 		creaMessaggioErroreDuranteOperazione("CONNESSIONE AL DATABASE FALLITA", "RIPROVARE");
	    	 		chiudiProgramma();
	    	 	}
	     } 
	     catch(ClassNotFoundException e) {
	     	creaMessaggioErroreDuranteOperazione("CONNESSIONE AL DATABASE FALLITA", "JDBC DRIVER NON TROVATO");
	     	chiudiProgramma();
	     	return null;
	     } 
	return null;
	}
		

	
	public void aggiungiArticoloAlMagazzino(String Nome, String Codice, String prezzo, String fotoPath, String Taglia, String Colore) throws SQLException {
		Double Prezzo = new Double(prezzo);
		Articolo ArticoloDaAggiungere = new Articolo(Nome, Codice, Prezzo, fotoPath, Taglia, Colore);
		try{ 
			ArticoloDAO.CreaArticolo(ArticoloDaAggiungere);
			try {
				if(MagazzinoDAO.checkQuantitaArticoloMagazzinoSQL(ArticoloDaAggiungere)==null 
						&& (MagazzinoDAO.AggiungiArticoloAlMagazzinoSQL(ArticoloDaAggiungere))) {
					creaMessaggioOperazioneEffettuataConSuccesso("Articoli aggiunti correttamente");
				} else { 
					MagazzinoDAO.incrementaQuantitaArticoloMagazzinoDB(ArticoloDaAggiungere);
					creaMessaggioOperazioneEffettuataConSuccesso("Quantità articolo incrementata di 1!");
				}
				MagazzinoTransazionale.add(ArticoloDaAggiungere);
			} catch (Exception e) { //catch per creazione articolo nel database
				creaMessaggioErroreDuranteOperazione("ERRORE: ID DUPLICATO", "RIPROVARE"); 
			}
		} catch (Exception e) { //catch per inserimento articolo in magazzino
			creaMessaggioErroreDuranteOperazione("ERRORE: ID NON VALIDO, INSERIRE SOLO NUMERI", "RIPROVARE"); 
		}
		if(fotoPath!=null)ArticoloDAO.JdbcWriteImage(fotoPath, Codice);
	}
		
	public static void creaMessaggioErroreDuranteOperazione(String testoDaMostrare, String titolo) {
		JOptionPane.showMessageDialog(new JFrame(), testoDaMostrare, titolo,
		        JOptionPane.ERROR_MESSAGE);
		
	}
	public void creaMessaggioOperazioneEffettuataConSuccesso(String testoDaMostrare) {
		JFrame parent = new JFrame();
		JOptionPane.showMessageDialog(parent, testoDaMostrare);
	}
	
	public void rimuoviArticoliDalCarrello (Articolo ArticoloDaRimuovere, int quantita) {
		while(quantita>0) {
			if(CarrelloUtente.remove(ArticoloDaRimuovere)) {
				MagazzinoTemporaneo.add(ArticoloDaRimuovere);
				aggiornaLabelCarrello();
				AggiungiAlCarrelloFrame = new AggiungiAlCarrelloFrame(this);
				quantita--;
				}
			if(isCarrelloEmpty()) {
				chiudiTutteLeFinestre();
				CarrelloFrame = new CarrelloFrame(this);
				CarrelloFrame.setVisible(true);
			}	
		}
		RimuoviDalCarrelloFrame = new RimuoviDalCarrelloFrame(this);
	}
	
	public boolean isCarrelloEmpty () {
		if (CarrelloUtente.isEmpty()) {
			creaMessaggioErroreDuranteOperazione("Carrello Vuoto", "Carrello vuoto");
			CarrelloFrame.setVisible(true);
		}
		return CarrelloUtente.isEmpty();
	}
		
	public void riempiComboAggiungiAlCarrello (JComboBox<Articolo> articoloBox) {
		for (Articolo a: MagazzinoTemporaneo.getElencoArticoli()) {
			articoloBox.addItem(a);
		}		
	}
	
	public void riempiComboRimuoviDalCarrello (JComboBox<Articolo> articoloBox) {
		for (Articolo a: CarrelloUtente.getElencoArticoli()) {
			articoloBox.addItem(a);
		}
	}
	
	public void openAggiungiAlCarrello () {
		AggiungiAlCarrelloFrame = new AggiungiAlCarrelloFrame(this);
		AggiungiAlCarrelloFrame.setVisible(true);
	}
	
	public void openRimuoviDalCarrello() {
		if(!isCarrelloEmpty()) {
			RimuoviDalCarrelloFrame = new RimuoviDalCarrelloFrame(this);
			RimuoviDalCarrelloFrame.setVisible(true);
		}
	}
	
	public void terminaInserimentoArticoli() {
		creaVetrina();
		VetrinaFrame.setVisible(false);
		HomePageFrame.setVisible(true);
	}
	
	public void aggiungiAlCarrello (Articolo articoloSelezionato, int quantitaSelezionata) throws SQLException{
		int quantitaDisponibileInMagazzino = MagazzinoDAO.checkQuantitaArticoloMagazzinoSQL(articoloSelezionato);
		if(quantitaDisponibileInMagazzino >= quantitaSelezionata && MagazzinoTemporaneo.contains(articoloSelezionato)) {
			while(quantitaSelezionata>0) {
				CarrelloFrame.setVisible(false);
				aggiungiArticoloAlCarrelloUtente(articoloSelezionato);
				aggiornaLabelCarrello();
				quantitaSelezionata--;
				MagazzinoTemporaneo.remove(articoloSelezionato);
			}
			CarrelloFrame.setVisible(true);
			AggiungiAlCarrelloFrame.setVisible(false);
			AggiungiAlCarrelloFrame = new AggiungiAlCarrelloFrame(this);
		} else creaMessaggioErroreDuranteOperazione("Quantità in magazzino non disponibile",
				"Riduci la quantità");
	}
	
	public void aggiungiArticoloAlCarrelloUtente (Articolo articoloSelezionato) {
		if(!checkCarrelloPieno()) {
			CarrelloUtente.add(articoloSelezionato);
		}
	}
		
	public double eseguiTotale () {
		double Totale=0.0;
		for (Articolo a: CarrelloUtente.getElencoArticoli())
			Totale = Totale + a.getPrice();
		
		return Totale;
	}
	
	public void aggiornaLabelCarrello () {
		CarrelloFrame.setVisible(false);
		CarrelloFrame = new CarrelloFrame(this);
		creaLabelTabella();
		int y=107, max=450, x=1; 
		for(Articolo a: CarrelloUtente.getElencoArticoli()) {
			if(y>=max){
				y=107;
				x=x+300; 
			}
			creaLabelArticoloCarrello (x, y, a);
			y=y+17;
		}
		aggiornaFrameCarrello();
	}
	
	public void creaLabelTabella () {
		int y=90;
		int max=900;
		for (int x=1; x<max; x=x+300) {
			JLabel LabelNome = new JLabel();
			LabelNome.setText("Nome ");
			LabelNome.setBounds(x, y, 360, 18);
			CarrelloFrame.contentPane.add(LabelNome);
		
			JLabel LabelPrezzo = new JLabel();
			LabelPrezzo.setText(" -  $");
			LabelPrezzo.setBounds(x+80, y, 360, 18);
			CarrelloFrame.contentPane.add(LabelPrezzo);
		}	
	}
	
	public void aggiornaFrameCarrello () {
		CarrelloFrame.setVisible(false);
		CarrelloFrame.setVisible(true);
	}
	
	public void aggiornaFrameVetrina() {
		VetrinaFrame.setVisible(false);
		VetrinaFrame.setVisible(true);
	}
		
	public void creaLabelArticoloCarrello (int x, int y, Articolo a) {
		JLabel LabelNome = new JLabel();
		LabelNome.setText(a.getName());
		LabelNome.setBounds(x, y, 360, 18);
		CarrelloFrame.contentPane.add(LabelNome);
		
		JLabel LabelPrezzo = new JLabel();
		Double d = new Double(a.getPrice());
		String prezzo = Double.toString(d);
		LabelPrezzo.setText(" - " +prezzo);
		LabelPrezzo.setBounds(x+80, y, 360, 18);
		
		CarrelloFrame.contentPane.add(LabelPrezzo);
		creaPulsanteVisualizzaArticolo(x, y, a);
		
		SwingUtilities.updateComponentTreeUI(CarrelloFrame);
		
	}
	
	public void creaPulsanteVisualizzaArticolo(int x, int y,Articolo articoloCliccato){
		JButton btnVisualizzaArticolo = new JButton("Visualizza");
		ArticoloDaVisualizzare articoloVisualizzato = new ArticoloDaVisualizzare(articoloCliccato, this);
		btnVisualizzaArticolo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				articoloVisualizzato.setVisible(true);
			}
		});
		btnVisualizzaArticolo.setBounds(x+147, y+2, 130, 15);
		CarrelloFrame.contentPane.add(btnVisualizzaArticolo);
	}
	
	public void creaArticoloPerVetrina(int x, int y, Articolo articoloCliccato) {
		ArticoloDaVisualizzare ArticoloVisualizzato = new ArticoloDaVisualizzare (articoloCliccato, this);
		JLabel fotoLabel = ArticoloVisualizzato.getFotoLabel();
		JLabel articoloLabel = new JLabel(articoloCliccato.getName()+"-"+ articoloCliccato.getTaglia() +"-"+
			articoloCliccato.getPrice()+"$");
		JButton BottoneAggiungi = ArticoloVisualizzato.getBottone();
		fotoLabel.setBounds(x, y, 100, 100);
		articoloLabel.setBounds(x, y+105, 360, 18);
		BottoneAggiungi.setBounds(x, y+125, 100, 15);
		VetrinaFrame.aggiungiInVetrina(fotoLabel, articoloLabel, BottoneAggiungi);
		
		SwingUtilities.updateComponentTreeUI(VetrinaFrame);
		
	}
	
	public void creaVetrina() {
		int x=30, max=350, y=30; 
		for(Articolo a: MagazzinoTransazionale.getElencoArticoli()) {
			if(y>=max){
				y=30;
				x=x+115; 
				}
			creaArticoloPerVetrina(x, y, a);
			y=y+150;
			}
		aggiornaFrameVetrina();
	}
	
	public boolean checkCarrelloPieno () {
		if (CarrelloUtente.getSize() >= 63) {
			creaMessaggioErroreDuranteOperazione("Carrello Pieno!", "Carrello Pieno!");
			return true;
		}
		return false;
			
	}


	public void apriSchermataPagamento() {
		if (CarrelloUtente.getSize()==0) 
			creaMessaggioErroreDuranteOperazione("Il carrello è vuoto!", "Carrello vuoto!");
		else {	
		PagamentoFrame = new PagamentoFrame(this);
		PagamentoFrame.setVisible(true);
		}
	}


	public void effettuaTransazione() throws SQLException {
		for (Articolo a: CarrelloUtente.getElencoArticoli())
		rimuoviArticoloDalMagazzino(a);
		
		creaMessaggioOperazioneEffettuataConSuccesso("Pagamento effettuato!");
		chiudiProgramma();
	}
	 
	
	public static void chiudiProgramma() {
		System.exit(0);	
	}




	protected ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = getClass().getResource(path);
		return new ImageIcon(imgURL, description);		
	}


	public void apriSchermataVetrina() {
		chiudiTutteLeFinestre();
		VetrinaFrame = new VetrinaFrame(this);
		creaVetrina();
		VetrinaFrame.setVisible(true);
	}


	public void apriSchermataCarrello() {
		if(MagazzinoTransazionale.getSize()!=0) {
			chiudiTutteLeFinestre();
			CarrelloFrame = new CarrelloFrame(this);
			CarrelloFrame.setVisible(true);
		}
		else creaMessaggioErroreDuranteOperazione("Il magazzino è vuoto, inserire un articolo", "Riprovare");
	}


	public void apriSchermataMagazzino() {
		if(MagazzinoTransazionale.getSize()==0) 
			creaMessaggioErroreDuranteOperazione("Magazzino vuoto!", "Magazzino vuoto!");
			else {
				chiudiTutteLeFinestre();
				MagazzinoFrame = new MagazzinoFrame(this);
				riempiMagazzinoFrame();
				MagazzinoFrame.setVisible(true);
			}
	}


	public void apriSchermataAggiungiAlMagazzino() {
		InserimentoArticoloInMagazzinoFrame = new InserimentoArticoloInMagazzinoFrame(this);
		InserimentoArticoloInMagazzinoFrame.setVisible(true);
	}


	public void apriSchermataEliminaDaMagazzino() {
		if(MagazzinoTransazionale.getSize()==0)
			creaMessaggioErroreDuranteOperazione("Magazzino vuoto!", "Magazzino vuoto!");
			else{
				EliminaDaMagazzinoFrame = new EliminaDaMagazzinoFrame(this);
				EliminaDaMagazzinoFrame.setVisible(true);
			}	
	}
	
		
	public void cancellaDatiMagazzino() throws SQLException {
		if(MagazzinoTransazionale.getSize()==0) creaMessaggioErroreDuranteOperazione("Magazzino vuoto!", "Magazzino vuoto!");
			else {
				for (Articolo a: MagazzinoTransazionale.getElencoArticoli()) {
					MagazzinoDAO.eliminaArticoloDalMagazzinoSQL(a.getId());
				}
				MagazzinoTransazionale.clear();
				creaMessaggioOperazioneEffettuataConSuccesso("Magazzino resettato con successo!");
			}
	}
		
	private void chiudiTutteLeFinestre() {
		java.awt.Window win[] = java.awt.Window.getWindows(); 
		for(int i=0;i<win.length;i++){ 
			win[i].dispose(); 
		} 
	}


	public void apriSchermataHome() {
		chiudiTutteLeFinestre();
		HomePageFrame = new HomePage(this);
		HomePageFrame.setVisible(true);	
	}


	public void riempiComboEliminaDaMagazzino(JComboBox<Articolo> articoloBox) {
		for (Articolo a: MagazzinoTemporaneo.getElencoArticoli()) {
			articoloBox.addItem(a);
		}	
	}


	public void rimuoviArticoloDalMagazzino(Articolo articoloSelezionato) throws SQLException {
		if(MagazzinoTransazionale.getSize()!=0) {
			MagazzinoTransazionale.remove(articoloSelezionato);
			MagazzinoTemporaneo.remove(articoloSelezionato);
			int quantita = MagazzinoDAO.checkQuantitaArticoloMagazzinoSQL(articoloSelezionato);
			if(quantita>1) {
				MagazzinoDAO.decrementaQuantitaArticoloMagazzinoDB(articoloSelezionato);
			}
			else MagazzinoDAO.eliminaArticoloDalMagazzinoSQL(articoloSelezionato.getId());
		}
		EliminaDaMagazzinoFrame.setVisible(false);
		VetrinaFrame.setVisible(false);
		HomePageFrame.setVisible(true);
	}

	public void riempiMagazzinoFrame() {
		int y=15, max=480, x=10; 
		for(Articolo a: MagazzinoTransazionale.getElencoArticoli()) {
			if(y>=max){
				y=15;
				x=x+255; 
			}
			creaLabelArticoloMagazzino(x, y, a);
			y=y+15;
		}
		MagazzinoFrame.setVisible(false);
		MagazzinoFrame.setVisible(true);
	}



	private void creaLabelArticoloMagazzino(int x, int y, Articolo articoloDaMostrare) {
		JLabel articoloLabel = new JLabel(articoloDaMostrare.toString());
		articoloLabel.setBounds(x, y+10, 360, 18);
		MagazzinoFrame.AggiungiInMagazzinoFrame(articoloLabel);
		SwingUtilities.updateComponentTreeUI(MagazzinoFrame);
	}
			
			
	


	
}
	
	


