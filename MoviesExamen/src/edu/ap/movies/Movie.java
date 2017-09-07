package edu.ap.movies;

import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.Document;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import redis.clients.jedis.Jedis;


@Path("/movies")
public class Movie {
	@GET
	@Path("{title}")
	@Produces({"text/html"})
	public String searchMovie(@PathParam("title") String title) throws ParseException{
		
		
		Jedis jedis = new Jedis("localhost", 6379);
		
		jedis.connect();
		jedis.select(0);
		
		List<String> movies = jedis.lrange("movies", 0, -1);
				
		Iterator iterator = movies.iterator();
		
		while (iterator.hasNext()) {
			movies.add((String) iterator.next());
		}
		
		
		Boolean notInDB = true;
		String returnJSON = "";
		
		for(String movie: movies){

			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(movie);
			if ((json.get("title").toString()).equals(title)){
				
		        JsonObjectBuilder builder = Json.createObjectBuilder();
		        builder.add("title", json.get("title").toString());
		        builder.add("year", json.get("year").toString());
		        System.out.println("gevonden in db!");
		        JsonObject newJSON = builder.build();
		        notInDB = false;
		        returnJSON = newJSON.toString();

				break;
			}
		}

		if(notInDB){
			
			Response response = ClientBuilder.newClient()
					.target("http://www.omdbapi.com/?t=" + title + "&apikey=plzBanMe")
					.request(MediaType.APPLICATION_JSON)
					.get();
			
			String jsonString = response.readEntity(String.class);
			
			JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
			JsonObject object = jsonReader.readObject();
			jsonReader.close();
			
	        Document movie = new Document();
	        movie.append("title", object.getString("movie"));
	        movie.append("year", object.getString("year"));
	   
	        jedis.lset("movies", Long.parseLong("-1"),movie.toString());
	        
	        
	        returnJSON = object.toString();
	        System.out.println("opgehaald en opgeslagen");
			
		}
		return returnJSON;
	}

}
