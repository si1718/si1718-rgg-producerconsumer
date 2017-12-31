package data.streaming.test;

import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import data.streaming.dto.TweetDTO;
import data.streaming.utils.Utils;

import java.util.ArrayList;
import java.util.List;

	
public class SinkMongoDB implements SinkFunction<String> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7754848691580777265L;
	
	private List<Document> aux = new ArrayList <> ();
	
	public void invoke(String arg0) throws Exception {
		// TODO Auto-generated method stub
		//aux.add(org.bson.Document.parse(arg0));
		
		TweetDTO tdto = Utils.createTweetDTO(arg0);
		
		Document userData = new Document().append("idStr", tdto.getUser().getIdStr())
				.append("name", tdto.getUser().getName()).append("screenName", tdto.getUser().getScreenName())
				.append("friends", tdto.getUser().getFriends()).append("followers", tdto.getUser().getFollowers());
		Document tweet = new Document().append("creationDate", tdto.getCreatedAt())
				.append("language", tdto.getLanguage()).append("text", tdto.getText())
				.append("userData", userData);
		
		aux.add(tweet);
		
		
		if (aux.size() == 40) {
			final MongoClientURI uri = new MongoClientURI(
					"mongodb://rafa:rafa@ds159845.mlab.com:59845/si1718-rgg-groups");
			MongoClient client = new MongoClient(uri);
			MongoDatabase database = client.getDatabase("si1718-rgg-groups");
			MongoCollection collection = database.getCollection("tweets");
			
			collection.insertMany(aux);
			client.close();
			aux.clear();
			System.out.println("40 documents inserted");
		}
	}
	
}