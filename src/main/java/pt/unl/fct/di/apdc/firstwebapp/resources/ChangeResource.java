package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;

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
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.util.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.ChangeAttributesData;
import pt.unl.fct.di.apdc.firstwebapp.util.LoginData;
import pt.unl.fct.di.apdc.firstwebapp.util.PasswordData;
import pt.unl.fct.di.apdc.firstwebapp.util.RegisterData;
import pt.unl.fct.di.apdc.firstwebapp.util.RoleData;
import pt.unl.fct.di.apdc.firstwebapp.util.StateData;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Path("/change")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ChangeResource {
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
	
	
	//private static String roles = "USER, GBO, GA, SU";
	//private static String states = "INATIVO, ATIVO";
	private static final String SU = "SU";
	private static final String GA = "GA";
	private static final String GBO = "GBO";
	private static final String USER = "USER";
	private static String INATIVO = "INATIVO";
	private static String ATIVO = "ATIVO";
	
	private static String USER_DONT_EXIST = "User doesn't exist.";
	private static String NO_PERMITION = "You dont have permition for that.";
	
	private static String COOKIE_EXPIRED = "You lost your session, login please";
	
	//Role change
	private static String USER_HAS_ROLE = " already has this role ";
	private static String UPDATED_ROLE = " role changed.";
	
	//State change
	private static String USER_HAS_STATE = " already has the state ";
	private static String UPDATED_STATE = " state updated.";
	
	//Password change
	private static String INCORRECT_PASSWORD = "Your actual password is incorrect.";
	
	//Attributes change
	private static String NO_PERMISSION_TO_CHANGE_USER = "You dont have permittions to change this user.";
	private static String NO_PERMISSION_TO_CHANGE_ATTRIBUTES = "You dont have permittions to change this attributes.";
	private static String NO_ATTRIBUTES_CHANGED = "No attributes got changed.";
	private static String ATTRIBUTES_CHANGED = " attributes got changed.";
	
	public  ChangeResource() {}
	
	@POST
	@Path("/role")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response ChangeRole(@CookieParam("session::apdc") Cookie cookie, RoleData data) {
		LOG.fine("Attempt to change users " + data.username.toString() + " role");
		
		Transaction txn = datastore.newTransaction();
		
		String userRole = getUserRole(cookie);
		
		try {
			
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
			Entity user = txn.get(userKey);
			
			if(!LoginResource.checkCookie(cookie)) {
				return Response.status(Status.GONE).entity(COOKIE_EXPIRED).build();
			}
			
			if(txn.get(userKey) == null) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity(USER_DONT_EXIST).build();
				
			}else if(!canChangeRole(userRole, user.getString("Role"), data.role)) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity(NO_PERMITION).build();	
			
			}else if(user.getString("Role").equals(data.role)) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity(data.username + USER_HAS_ROLE + data.role).build();
			
			}else {	
				Entity update = Entity.newBuilder(user)
						.set("Role", data.role)
						.build();
				
				txn.update(update);
				LOG.fine("User's role sucessfully changed");
				txn.commit();
				return Response.ok().entity(data.username + UPDATED_ROLE).build();
			}
				
		} finally {
			if(txn.isActive()) {
				txn.rollback();
			}
		}
	}
	
	
	@POST
	@Path("/state")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response ChangeState(@CookieParam("session::apdc") Cookie cookie, StateData data) {
		LOG.fine("Attempt to change user " + data.username + " state");
		
		//TODO teremos que conseguir verificar se o user que esta a realizar a operação esta a fazelo bem
		
		Transaction txn = datastore.newTransaction();
		
		String userRole = LoginResource.getUserRole(cookie);
		
		try {
			
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
			Entity user = txn.get(userKey);
			
			if(!LoginResource.checkCookie(cookie)) {
				return Response.status(Status.GONE).entity(COOKIE_EXPIRED).build();
			}
			
			if(txn.get(userKey) == null) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity(USER_DONT_EXIST).build();
			
			}else if(!canChangeState(userRole,user.getString("Role"))) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity(NO_PERMITION).build();
				
			}else if(user.getString("State").equals(data.state)) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity(USER_HAS_STATE + data.state).build();
			}else {
				Entity update = Entity.newBuilder(user)
						.set("State", data.state)
						.build();
				txn.update(update);
				LOG.fine("User's role sucessfully changed");
				txn.commit();
				return Response.ok().entity(data.username.toString() + UPDATED_STATE).build();
			}
				
		} finally {
			if(txn.isActive()) {
				txn.rollback();
			}
		}
	}
	
	@POST
	@Path("/password")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response ChangePassword(@CookieParam("session::apdc") Cookie cookie, PasswordData data) {
		LOG.fine("Attempt to change the users password");
		
		Transaction txn = datastore.newTransaction();
		
		String username = LoginResource.getUsername(cookie);
		
		try {
			
			if(!LoginResource.checkCookie(cookie)) {
				return Response.status(Status.GONE).entity(COOKIE_EXPIRED).build();
			}
			
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(username);
			Entity user = txn.get(userKey);
			
			if(txn.get(userKey) == null) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity(USER_DONT_EXIST).build();
				
			}if(!data.actualPassword.equals(user.getString("Password"))) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity(INCORRECT_PASSWORD).build();
				
			}else {	
				Entity update = Entity.newBuilder(user)
						.set("Password", data.newPassword)
						.build();
				txn.update(update);
				LOG.fine("User's role sucessfully changed");
				txn.commit();
				return Response.ok(username + " password updated").build();
			}
				
		} finally {
			if(txn.isActive()) {
				txn.rollback();
			}
		}
	}
	
	 	@POST
		 @Path("/attribute")
		 @Consumes(MediaType.APPLICATION_JSON)
		 public Response changeAttributes(@CookieParam("session::apdc") Cookie cookie, ChangeAttributesData data) {
			
		 	Transaction txn = datastore.newTransaction();
			
		 	String username = LoginResource.getUsername(cookie);
		 	String userRole = LoginResource.getUserRole(cookie);
			
		 	try {
		 		Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
		 		Entity user = txn.get(userKey);
		 		
		 		if(!LoginResource.checkCookie(cookie)) {
					return Response.status(Status.GONE).entity(COOKIE_EXPIRED).build();
				}
				
		 		if(txn.get(userKey) == null) {
		 			txn.rollback();
		 			return Response.status(Status.FORBIDDEN).entity(USER_DONT_EXIST).build();
		 		}else {	
					
		 			if(!canChangeAtributtes(userRole, user.getString("Role"), username, user.getString("Username"))) {
		 				txn.rollback();
		 				return Response.status(Status.FORBIDDEN).entity(NO_PERMISSION_TO_CHANGE_USER).build();
		 			}
		 			
		 			if(userRole.equals(USER)) {
		 				if(!userHavePermission(data, user)){
		 					txn.rollback();
		 					return Response.status(Status.FORBIDDEN).entity(NO_PERMISSION_TO_CHANGE_ATTRIBUTES).build();
		 				}
						
		 				if(attributeGotChanged(data, user)) {
							
		 					Entity.Builder updateAtt = Entity.newBuilder(user);	
						
		 					if(!data.phone.equals(user.getString("Phone_number"))&& !data.phone.equals("")) {
		 						updateAtt.set("Phone_number", data.phone);
		 					}
		 					else if(!data.password.equals(user.getString("Password"))&& !data.password.equals("")) {
		 						updateAtt.set("Password", data.password);
		 					}
		 					else if(!data.profile.equals(user.getString("Profile"))&& !data.profile.equals("")) {
		 						updateAtt.set("Profile", data.profile);
		 					}
		 					else if(!data.ocupation.equals(user.getString("Ocupation"))&& !data.ocupation.equals("")) {
		 						updateAtt.set("Ocupation", data.ocupation);
		 					}
		 					else if(!data.workPlace.equals(user.getString("Work_Place"))&& !data.workPlace.equals("")) {
		 						updateAtt.set("Work_Place", data.workPlace);
		 					}
		 					else if(!data.houseHold.equals(user.getString("Household"))&& !data.houseHold.equals("")) {
		 						updateAtt.set("Household", data.houseHold);
		 					}
		 					else if(!data.CP.equals(user.getString("CP"))&& !data.CP.equals("")) {
		 						updateAtt.set("CP", data.CP);
		 					}
		 					else if(!data.NIF.equals(user.getString("NIF"))&& !data.NIF.equals("")) {
		 						updateAtt.set("NIF", data.NIF);
		 					}
		 					else if(!data.foto.equals("")) {
		 						updateAtt.set("foto", data.foto);
		 					}
							
		 					Entity update = updateAtt.build();
							
		 					txn.update(update);
		 					txn.commit();
							
		 				}else {
		 					txn.rollback();
		 					return Response.status(Status.FORBIDDEN).entity(NO_ATTRIBUTES_CHANGED).build();
		 				}
						
		 			} else {
						
		 				if(attributeGotChanged(data, user)) {
							
		 					Entity.Builder updateAtt = Entity.newBuilder(user);	
							
		 					if(!data.email.equals(user.getString("Email")) && !data.email.equals("")) {
		 						updateAtt.set("Email", data.email);
		 					}
		 					else if(!data.name.equals(user.getString("Name")) && !data.name.equals("")) {
		 						updateAtt.set("Name", data.name);
		 					}
		 					else if(!data.phone.equals(user.getString("Phone_number"))&& !data.phone.equals("")) {
		 						updateAtt.set("Phone_number", data.phone);
		 					}
		 					else if(!data.password.equals(user.getString("Password"))&& !data.password.equals("")) {
		 						updateAtt.set("Password", data.password);
		 					}
		 					else if(!data.profile.equals(user.getString("Profile"))&& !data.profile.equals("")) {
		 						updateAtt.set("Profile", data.profile);
		 					}
		 					else if(!data.ocupation.equals(user.getString("Ocupation"))&& !data.ocupation.equals("")) {
		 						updateAtt.set("Ocupation", data.ocupation);
		 					}
		 					else if(!data.workPlace.equals(user.getString("Work_Place"))&& !data.workPlace.equals("")) {
		 						updateAtt.set("Work_Place", data.workPlace);
		 					}
		 					else if(!data.houseHold.equals(user.getString("Household"))&& !data.houseHold.equals("")) {
		 						updateAtt.set("Household", data.houseHold);
		 					}
		 					else if(!data.CP.equals(user.getString("CP"))&& !data.CP.equals("")) {
		 						updateAtt.set("CP", data.CP);
		 					}
		 					else if(!data.NIF.equals(user.getString("NIF"))&& !data.NIF.equals("")) {
		 						updateAtt.set("NIF", data.NIF);
		 					}
		 					else if(!data.foto.equals("")) {
		 						updateAtt.set("foto", data.foto);
		 					}
		 					else if(!data.role.equals(user.getString("Role"))&& !data.role.equals("")) {
		 						updateAtt.set("Role", data.role);
		 					}
		 					else if(!data.state.equals(user.getString("State"))&& !data.state.equals("")) {
		 						updateAtt.set("State", data.state);
		 					}
							
		 					Entity update = updateAtt.build();
							
		 					txn.update(update);
		 					txn.commit();
							
		 				}else {
		 					txn.rollback();
		 					return Response.status(Status.FORBIDDEN).entity(NO_ATTRIBUTES_CHANGED).build();
		 				}
		 			}
		 			return Response.ok().entity(username + ATTRIBUTES_CHANGED).build();
		 		}
					
		 	} finally {
		 		if(txn.isActive()) {
		 			txn.rollback();
		 		}
		 	}	
		 }
	
	
	/*
	 * verifies if the user logged can perform the requested role change
	 */
	public boolean canChangeRole(String userRole, String lastRole, String newRole) {
		switch(userRole) {
			case SU:
				return true;
			case GA:
				if((lastRole.equals(GBO) && newRole.equals(USER)) || (lastRole.equals(USER) && newRole.equals(GBO))) {
					return true;
				}else {
					return false;
				}
			case GBO:
				return false;
			case USER:
				return false;
			default:
				return false;
		}
	}
	
	/*
	 * Returns true if the user have permitions to change the role
	 */
	public boolean canChangeState(String userRole, String roleToChange) {
		switch(userRole) {
		case SU:
			return true;
		case GA:
			if((roleToChange.equals(GBO) || roleToChange.equals(USER))) {
				return true;
			}else {
				return false;
			}
		case GBO:
			if(roleToChange.equals(USER)) {
				return true;
			}else {
				return false;
			}
		case USER:
			return false;
		default:
			return false;
		}
	}
	
	
	public boolean canChangeAtributtes(String userRole, String roleToChange, String username, String usernameToCHange) {
		switch(userRole) {
		case SU:
			return true;
		case GA:
			if((roleToChange.equals(GBO) || roleToChange.equals(USER))) {
				return true;
			}else {
				return false;
			}
		case GBO:
			if(roleToChange.equals(USER)) {
				return true;
			}else {
				return false;
			}
		case USER:
			if(username.equals(usernameToCHange)) {
				return true;
			}else {
				return false;
			}
		default:
			return false;
		}
	}
	
	public boolean userHavePermission(ChangeAttributesData data, Entity user ) {
		if(data.name.equals("") && data.email.equals("") && data.role.equals("") && data.state.equals("")){
						return true;
					}else {
						return false;
					}
	}
	
	public boolean attributeGotChanged(ChangeAttributesData data, Entity user) {
		if((!data.email.equals(user.getString("Email")) && !data.email.equals("")) || (!data.name.equals(user.getString("Name")) &&!data.name.equals(""))
				|| (!data.phone.equals(user.getString("Phone_number")) && !data.phone.equals("")) || (!data.password.equals(user.getString("Password")) && !data.password.equals(""))
				|| (!data.name.equals(user.getString("Name")) && !data.name.equals("")) || (!data.name.equals(user.getString("Name")) && !data.name.equals(""))
				|| (!data.profile.equals(user.getString("Profile")) && !data.profile.equals(""))
				|| (!data.ocupation.equals(user.getString("Ocupation")) && !data.ocupation.equals("")) || (!data.workPlace.equals(user.getString("Work_Place")) && !data.workPlace.equals(""))
				|| (!data.houseHold.equals(user.getString("Household"))&& !data.houseHold.equals("")) || (!data.CP.equals(user.getString("Codigo_Postal")) && !data.CP.equals(""))
				||(!data.NIF.equals(user.getString("NIF")) && !data.NIF.equals("")) || (!data.foto.equals(""))
				|| (!data.role.equals(user.getString("Role"))&& !data.role.equals("")) || (!data.state.equals(user.getString("State"))) && !data.state.equals("")) {
			return true;
		}else {
			return false;
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
	
	
	
	
	
}
