package data.streaming.test;

//import java.io.BufferedReader;ç
//import java.io.InputStreamReader;
//import java.net.MalformedURLException;
//import java.net.URL;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

import dbase.MongoConnection;
import dbase.Utils;

public class Batch {
	
	private final static MongoClientURI uri = new MongoClientURI(Utils.URL_DATABASE);
    private static MongoClient client;
    private static MongoDatabase database;
    private static DB db;
    
    
	public static void ratingGroups () /*throws MalformedURLException*/ {
		// TODO Auto-generated method stub
		client = new MongoClient(uri);
		database = client.getDatabase("si1718-rgg-groups");
		db = client.getDB("si1718-rgg-groups");
		MongoCollection<org.bson.Document> collection = database.getCollection("groups");
		
		//List <Document> ratingsList = new ArrayList <> ();
		Set <DBObject> ratingsList = new HashSet <> ();
		List <Document> groupList = new ArrayList <> ();
		//int soncero = 0;
		//int nosoncero = 0;
		
		// Paso los grupos de la coleccion a una lista
		FindIterable<Document> findIter = collection.find();
		MongoCursor<Document> cursor = findIter.iterator();
		while (cursor.hasNext()) {
			groupList.add(cursor.next());
		}
		/*for (int i = 0; i < groupList.size(); i++) {
			Object nameLeader = groupList.get(i).get("components");
			for (int a = 0; a < nameLeader.toString().split(", ").length; a++) {
				System.out.println(nameLeader.toString().split(", ")[a]);
			}
			
		}*/
		//URL url = new URL("https://si1718-dfr-researchers.herokuapp.com/api/v1/researchers?search=");
		
		// Comparo parejas de keywords y creo rating según las coincidencias
		for (int i = 0; i < groupList.size(); i++) {
			Object doc1 = groupList.get(i).get("keywords");
			String[] newdoc1 = doc1.toString().split(", ");
			/*System.out.println("....................................");
			System.out.println(doc1.toString().replace("[", "").replace("]", "") + " length=" + newdoc1.length);*/
			
			for (int j = 1; j < groupList.size(); j++) {
				//Document document = new Document ();
				DBObject document = new BasicDBObject();
				
				Object doc2 = groupList.get(j).get("keywords");
				String[] newdoc2 = doc2.toString().split(", ");
				//System.out.println(doc2.toString().replace("[", "").replace("]", "") + " length=" + newdoc2.length);
				
				document.put("id1", groupList.get(i).get("idGroup").toString());
				document.put("id2", groupList.get(j).get("idGroup").toString());
				
				int cont = 0;
				for (int k = 0; k < newdoc1.length; k++) {
					for (int l = 0; l < newdoc2.length; l++) {
						String elem1 = newdoc1[k].replace("[", "").replace("]", "");
						String elem2 = newdoc2[l].replace("[", "").replace("]", "");
						/*if ((newdoc1[k].replace("[", "").replace("]", "")).equals(newdoc2[l].replace("[", "").replace("]", "")) == true) {
							System.out.println("::::::::::::::::::::::::");
							System.out.println("newdoc1:" + elem1 + " ....... " + groupList.get(i).get("idGroup").toString());
							System.out.println("newdoc2:" + elem2 + " ....... " + groupList.get(j).get("idGroup").toString());
							System.out.println((newdoc1[k].replace("[", "").replace("]", "")).equals(newdoc2[l].replace("[", "").replace("]", "")));
							System.out.println("::::::::::::::::::::::::");
							System.out.println("....................................");
						}*/
						
						if (( elem1 ).equals( elem2 )) {
							cont++;
						}
					}
				}
				
				if (cont != 0) {
					//nosoncero++;
					double var = 5.0; // The size of the arrays are the same
					
					if ( (newdoc1.length) != (newdoc2.length) ) {
						if ((newdoc1.length) > (newdoc2.length)) {
							var = (cont * var) / newdoc1.length;
						}
						else {
							var = (cont * var) / newdoc2.length;
						}
					}
					else {
						if (cont != newdoc1.length) {
							var = (cont * var) / newdoc1.length;
						}
					}
					
					document.put("rating", Double.toString(var));
					ratingsList.add(document);
					//System.out.println(document.toString());
				}
				else{
					//soncero++;
					//System.out.println(document.toString());
				}
			}
		}
		
		/* PRUEBAS */
		/*System.out.println("+++++++++++++++++++++++++++++");
		System.out.println("+++++++++++++++++++++++++++++");
		System.out.println("son cero: " + soncero);
		System.out.println("no son cero: " + nosoncero);
		System.out.println("+++++++++++++++++++++++++++++");
		System.out.println("+++++++++++++++++++++++++++++");*/
		/* ********* */
		
		// Inserto los ratios en la base de datos
		//MongoCollection<org.bson.Document> collectionRatings = database.getCollection("ratings");
		DBCollection collectionRatings = db.getCollection("ratings");
		WriteResult documentsRemoved = collectionRatings.remove(new BasicDBObject());
		System.out.println("(RATINGS) Number of documents are deleted: " + documentsRemoved.getN());
		
		collectionRatings.insert(new ArrayList<>(ratingsList));
		System.out.println("INFORMATION: New ratings inserted into the database");
		client.close();
    }
	
	
	public static void createDocChart(List <Document> dataList, Object date, String keyword) {
		int tam = dataList.size();
		
		for (int j = 0; j < tam; j++) {
			String existentDate = dataList.get(j).get("creationDate").toString();
			
			if (existentDate.equals(date.toString()) && (dataList.get(j).get("keyword").toString()).equals(keyword)) {
				Object numt = Integer.parseInt(dataList.get(j).get("numTweets").toString()) + 1;
				Document actDoc = new Document("creationDate", existentDate)
						.append("keyword", dataList.get(j).get("keyword")).append("numTweets", numt);
				dataList.remove(j);
				dataList.add(j, actDoc);
			}
			else if (j == tam-1){
				Document doc = new Document("creationDate", date)
						.append("keyword", keyword).append("numTweets", 1);
				dataList.add(doc);
				
				if ((dataList.get(0).get("creationDate").toString()).equals("00-00-0000")) {
					dataList.remove(0);
				}
			}
		}
	}
	
	
	public static void grantsData () {
		client = new MongoClient(uri);
		database = client.getDatabase("si1718-rgg-groups");
		MongoCollection<org.bson.Document> collectionTweets = database.getCollection("tweets");
		
		// Conexion y obtencion de keywords
		MongoConnection mongoConnect = new MongoConnection();
		SortedSet<String> keywords = mongoConnect.getKeywords();
		final String[] stringKEYWORDS = keywords.toArray(new String[keywords.size()]);
		
		List <Document> tweetsList = new ArrayList <> ();
		List <Document> dataList = new ArrayList <> ();
		
		// Paso los grupos de la coleccion a una lista
		FindIterable<Document> findIter = collectionTweets.find();
		MongoCursor<Document> cursor = findIter.iterator();
		while (cursor.hasNext()) {
			tweetsList.add(cursor.next());
		}
		
		if (dataList.isEmpty()) {
			Document dIni = new Document("creationDate", "00-00-0000")
					.append("numTweets", 0);
			dataList.add(dIni);
		}
		
		for (int i = 0; i < stringKEYWORDS.length; i++) {
			
			for (int j = 0; j < tweetsList.size(); j++) {
				Object doc1 = tweetsList.get(j).get("creationDate");
				String[] newdoc1 = doc1.toString().split(" ");
				String year = newdoc1[newdoc1.length-1];
				String month = newdoc1[1];
				String day = newdoc1[2];
				Object date = day + "-" + month + "-" + year;
				
				if (tweetsList.get(j).get("text").toString().contains(stringKEYWORDS[i])) {
					String keyword = stringKEYWORDS[i];
					createDocChart(dataList, date, keyword);
				}
			}
		}
		
		/*System.out.println(":::::::::::::::::::::::::::::::::::::::::");
		System.out.println("LIST OF DATA FOR THE CHARTS::");
		for (int k = 0; k < dataList.size(); k++) {
			System.out.println(dataList.get(k));
		}
		System.out.println(":::::::::::::::::::::::::::::::::::::::::");*/
		
		// Database insertion
		if (dataList.size() > 0) {
			MongoCollection collectionChartsData = database.getCollection("chartsData");
			collectionChartsData.insertMany(dataList);
			dataList.clear();
			System.out.println("INFORMATION: Documents with charts data inserted");
		}
		
		client.close();
	}
	
	public static void main (String [] args) throws IOException {
		ratingGroups();
		grantsData();
	}

}
