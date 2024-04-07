package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;

import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Entity.Builder;
import com.google.cloud.datastore.StructuredQuery.CompositeFilter;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.KeyFactory;
import com.google.cloud.datastore.PathElement;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.Transaction;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.util.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.LoginData;
import pt.unl.fct.di.apdc.firstwebapp.Authentication.SignatureUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Path("/login")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")//truque para evitar que os clients tenham problemas com o encoding
public class LoginResource {

	/*
	 * DataStorage
	 */
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	/*
	 * Logger Object
	 */
	private static final Logger LOG = Logger.getLogger(LoginResource.class.getName());
	
	//usado para manipular objetos json!
	private final Gson g = new Gson();
	
	private static final String key = "dhsjfhndkjvnjdsdjhfkjdsjfjhdskjhfkjsdhfhdkjhkfajkdkajfhdkmc";
	
	private static final String INATIVO = "INATIVO";
	
	public LoginResource() { } //Nothing to be done here
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	//@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response doLogin(LoginData data) {
		LOG.fine("Login attempt by user: " + data.username);
	
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
			Entity user = datastore.get(userKey);
			
			//String HashedPass = user.getString("user_password");
			
			if(datastore.get(userKey) == null) {
				return Response.status(Status.FORBIDDEN).entity("Username doesn't exist").build();
			
			}else if(user.getString("State").equals(INATIVO)) {
				return Response.status(Status.FORBIDDEN).entity("User is inative").build();
			
			} else if(!user.getString("Password").equals(DigestUtils.sha512Hex(data.password))) {
				return Response.status(Status.FORBIDDEN).entity("Wrong password").build();
			
			} else {
				
				String id = UUID.randomUUID().toString();
				long currentTime = System.currentTimeMillis();
				String fields = data.username+"."+ id +"."+user.getString("Role")+"."+currentTime+"."+1000*60*60*2;
				
				String signature = SignatureUtils.calculateHMac(key, fields);
					
				if(signature == null) {
					return Response.status(Status.INTERNAL_SERVER_ERROR).entity("Error while signing token. See logs.").build();
				}
				
				String value =  fields + "." + signature;
				NewCookie cookie = new NewCookie("session::apdc", value, "/", null, "comment", 1000*60*60*2, false, true);
									
				LOG.fine("sucessfull login welcome back " + data.username);
				
				return Response.ok().cookie(cookie).entity(cookie.getValue()).build();	
				// return Response.ok().cookie(cookie).build();	
			}
	}
	
	
	public static String getUserRole(Cookie cookie) {
		String value = cookie.getValue();
		String[] values = value.split("\\.");
		
		return values[2];
	}
	
	public static String getUsername(Cookie cookie) {
		String value = cookie.getValue();
		String[] values = value.split("\\.");
		
		return values[0];
	}
	
	public static boolean checkCookie(Cookie cookie) {
		if (cookie == null || cookie.getValue() == null) {
			return false;
		}

		String value = cookie.getValue();
		String[] values = value.split("\\.");
	
		String signatureNew = SignatureUtils.calculateHMac(key, values[0]+"."+values[1]+"."+values[2]+"."+values[3]+"."+values[4]);
		String signatureOld = values[5];
					
		if(!signatureNew.equals(signatureOld)) {
			return false;
		}
		
		if(System.currentTimeMillis() > (Long.valueOf(values[3]) + Long.valueOf(values[4])*1000)) {
			
			return false;
		}
		
			
		return true;
	}
	
	
	
}
