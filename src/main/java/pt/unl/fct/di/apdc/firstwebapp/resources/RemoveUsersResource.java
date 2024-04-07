package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
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
import pt.unl.fct.di.apdc.firstwebapp.util.RemoveUserData;
import pt.unl.fct.di.apdc.firstwebapp.util.RoleData;
import pt.unl.fct.di.apdc.firstwebapp.util.StateData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Path("/remove")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RemoveUsersResource {
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

	private static final String SU = "SU";
	private static final String GA = "GA";
	private static final String GBO = "GBO";
	private static final String USER = "USER";
	
	private static String USER_DONT_EXIST = "Username doesn't exist.";
	private static String DELETED = " deleted.";
	private static String NO_PERMITION = "You dont have permition to delete this user";
	
	private static String COOKIE_EXPIRED = "You lost your session, login";

	public RemoveUsersResource() {}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response removeUser(@CookieParam("session::apdc") Cookie cookie, RemoveUserData data) {
		
		Transaction txn = datastore.newTransaction();
		
		String userRole = LoginResource.getUserRole(cookie);
		String username = LoginResource.getUsername(cookie);
		
		try {
			
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
			Entity user = txn.get(userKey);
			
			if(!LoginResource.checkCookie(cookie)) {
				return Response.status(Status.GONE).entity(COOKIE_EXPIRED).build();
			}
			
			if(txn.get(userKey) == null) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity(USER_DONT_EXIST).build();
				
			} else if(!canRemoveUser(userRole, user.getString("Role"), username, data.username)) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity(NO_PERMITION).build();	
				
			}else {	
				txn.delete(userKey);
				LOG.fine("User's sucessfully deleted");
				txn.commit();
				return Response.ok().entity(data.username + DELETED).build();
			}
		} finally {
			if(txn.isActive()) {
				txn.rollback();
			}
		}
		
	}
	
	public boolean canRemoveUser(String userRole, String userToRemove, String username, String usernameToRemove) {
		switch(userRole) {
		case SU:
			return true;
		case GA:
			if((userToRemove.equals(GBO) || userToRemove.equals(USER))) {
				return true;
			}else {
				return false;
			}
		case GBO:
				return false;
		case USER:
			if(usernameToRemove.equals(username)) {
				return true;
			}else {
				return false;
			}
		default:
			return false;
		}
	}
}
