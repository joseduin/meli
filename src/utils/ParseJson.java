package utils;

import java.io.IOException;
import java.util.ArrayList;

import java.util.List;

import modelo.Answer;
import modelo.From;
import modelo.Producto;
import modelo.Question;
import modelo.UserCurrent;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public final class ParseJson {
	
	public static Producto item(String json) throws IOException {
		System.out.println(json);
			
		JsonElement obj = stringToJsonElement(json);
		
		String precio = (buscarJson(obj, "price").equals("null")) 
		          		? "0" : buscarJson(obj, "price");
		String condicion = (buscarJson(obj, "condition").equals("null")) 
				           ? "Sin definir": buscarJson(obj, "condition");
		
		return new Producto(buscarJson(obj, "title"), 
							buscarJson(obj, "thumbnail"), 
							 condicion, 
							 Double.parseDouble(precio), 
							 Producto.CURRENCY.get(buscarJson(obj, "currency_id")),
							 buscarJson(obj, "currency_id"),
							 Integer.parseInt(buscarJson(obj, "sold_quantity")),
							 Integer.parseInt(buscarJson(obj, "available_quantity")));
	}
	
	public static String username(String json) throws IOException {
		System.out.println(json);
			
		JsonElement obj = stringToJsonElement(json);
		return buscarJson(obj, "nickname");				
	}
	
	public static List<Question> questions(String json) {
		System.out.println(json);		
			
		List<Question> list = new ArrayList<>();
		JsonObject obj = stringToJsonObject(json);
		JsonArray questions = obj.getAsJsonArray("questions");
		for (JsonElement question : questions) {
			Answer answer = null;
				
			if (!buscarJson(question, "answer").equals("null")) {
				JsonObject answerObj = question.getAsJsonObject();
				JsonElement answerJ = answerObj.get("answer");
									
				answer = new Answer(buscarJson(answerJ, "text"), 
									Answer.STATUS.get( buscarJson(answerJ, "status") ), 
									ZkUtils.dateFormat( buscarJson(question, "date_created") ));
			}
			
			JsonObject fromObj = question.getAsJsonObject();
			JsonElement fromJ = fromObj.get("from");
			
			From from = new From(buscarJson(fromJ, "id"), 
								Integer.valueOf( buscarJson(fromJ, "answered_questions") ));
			
			list.add(new Question(buscarJson(question, "id"), 
								  answer, 
								  ZkUtils.dateFormat( buscarJson(question, "date_created") ), 
								  buscarJson(question, "item_id"), 
								  buscarJson(question, "seller_id"), 
								  Question.STATUS.get( buscarJson(question, "status") ), 
								  buscarJson(question, "text"),
								  Boolean.valueOf( buscarJson(question, "deleted_from_listing") ),
								  Boolean.valueOf( buscarJson(question, "hold") ),
								  from));
		}
		return list;
	}
	
	public static UserCurrent me(String json, String accessToken, String refreshToken) throws IOException {
		System.out.println(json);
			
		JsonElement obj = stringToJsonElement(json);
			
		return new UserCurrent(buscarJson(obj, "id"),
								buscarJson(obj, "nickname"),
								buscarJson(obj, "first_name"),
								buscarJson(obj, "last_name"),
								accessToken,
								refreshToken);
	}

	public static JsonElement stringToJsonElement(String cadena) {
		JsonParser jp = new JsonParser (); 
		return jp.parse(cadena);
	}
	public static JsonObject stringToJsonObject(String cadena) {
		JsonParser jp = new JsonParser (); 
		JsonElement je = jp.parse(cadena);
		return JsonElementToJsonObject(je);
	}
	public static JsonObject JsonElementToJsonObject(JsonElement je) {
		return je.getAsJsonObject();
	}
	public static String buscarJson(JsonElement je, String buscar) {
		JsonObject jo = JsonElementToJsonObject(je);
		return validarJsonObjToString(jo.get(buscar));
	}
	public static String validarJsonObjToString(JsonElement jsonElement) {
		String a = jsonElement.toString();
		return a.replaceAll("\"", "");
	}
	public static String buscarJson(JsonElement je, String array, String buscar) {
		JsonObject jo = JsonElementToJsonObject(je);
		return buscarJson(jo.get(array), buscar);
	}
 
}