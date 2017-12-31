package data.streaming.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.fasterxml.jackson.databind.ObjectMapper;
/*import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;*/

import data.streaming.dto.TweetDTO;

public class Utils {
	
	private static final ObjectMapper mapper = new ObjectMapper();
	static List<Document> aux = new ArrayList <Document> ();

	public static TweetDTO createTweetDTO(String x) {
		TweetDTO result = null;

		try {
			result = mapper.readValue(x, TweetDTO.class);
		} catch (IOException e) {

		}
		return result;
	}

	public static boolean isValid(String x) {
		// TODO Auto-generated method stub
		boolean result = true;
		if (createTweetDTO(x) == null) {
			result = false;
		}
		return result;
	}

	/*public static TweetDTO insertaDB (TweetDTO tdto) {
		
		Document userData = new Document().append("idStr", tdto.getUser().getIdStr())
										 .append("name", tdto.getUser().getName())
										 .append("screenName", tdto.getUser().getScreenName())
										 .append("friends", tdto.getUser().getFriends())
										 .append("followers", tdto.getUser().getFollowers());
		Document tweet = new Document().append("creationDate", tdto.getCreatedAt())
										 .append("language", tdto.getLanguage())
										 .append("text", tdto.getText()).append("userData", userData);
		
		aux.add(tweet);
		
		if (aux.size() == 40) {
			// Conection to database
			MongoClientURI uri = new MongoClientURI(
					"mongodb://rafa:rafa@ds159845.mlab.com:59845/si1718-rgg-groups");
			MongoClient client = new MongoClient(uri);
			MongoDatabase database = client.getDatabase("si1718-rgg-groups");
			MongoCollection<Document> collection = database.getCollection("tweets");
			
			collection.insertMany(aux);
			client.close();
		}
		collection.insertOne(tweet);
		client.close();
		return tdto;
	}*/
}
