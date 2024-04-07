package pt.unl.fct.di.apdc.firstwebapp.resources;

import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;
import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Transaction;
import com.google.gson.Gson;

import pt.unl.fct.di.apdc.firstwebapp.util.AuthToken;
import pt.unl.fct.di.apdc.firstwebapp.util.LoginData;
import pt.unl.fct.di.apdc.firstwebapp.util.RegisterData;

@Path("/register")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RegisterResource {

	/*
	 * DataStorage
	 */
	private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
	
	/*
	 * Logger Object
	 */
	private static final Logger LOG = Logger.getLogger(RegisterResource.class.getName());
	
	private final Gson g = new Gson();
	
	private static String REGIST_ATTEMPT = "Attempt to register user: ";
	private static String REGIST_SUCESSFULL = "User sucessfully registered ";
	
	private static String MANDATORY_NOT_PROVIDED = "Mandatory requirements not provided.";
	private static String INVALID_EMAIL = "Invalid email.";
	private static String USERNAME_ALREADY_EXISTS = "Username already exists.";
	private static String INVALID_PASSWORD = "Invalid password, password need to contain upper case, lower case, numbers, a special caracter and at least 8 characters";
	private static String PASSWORD_CONFIRMATION_WRONG = "Password and his confirmations are different";
	private static String SUCESFULL_LOGIN = " sucessfully registed.";
	
	private static final String ROOT = "root";
	private static boolean rootAlreadyCreated = false;
	
	private static String SUPER_USER = "SU";
	private static String USER = "USER";
	
	private static String ACTIVE_STATE = "ATIVO";
	private static String INATIVE_STATE = "INATIVO";
	
	
	public RegisterResource() { } //Nothing to be done here
	
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response doRegist(RegisterData data) { 
		LOG.fine(REGIST_ATTEMPT + data.username);
		
		Transaction txn = datastore.newTransaction();
		
		try {
																	
			Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.username);
			
			//Por muito que avalie no client-side se os valores obrigatorios enviados sao "", para ter a certeza absoluta que nenhum valor e defenido como null mantenho esta condiçao
			if(!data.isValid()) {
				txn.rollback(); 
				return Response.status(Status.FORBIDDEN).entity(MANDATORY_NOT_PROVIDED).build();
				
			}else if(!data.validEmail()) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity(INVALID_EMAIL).build();
				
			}else if(!data.validPassword()) {
				txn.rollback();
				return Response.status(Status.FORBIDDEN).entity(INVALID_PASSWORD).build();
			
//			}else if (!data.password.equals(data.confirmation)) {
//				txn.rollback();
//				return Response.status(Status.FORBIDDEN).entity(PASSWORD_CONFIRMATION_WRONG).build();
				
			}else if(datastore.get(userKey) != null || (data.username.toLowerCase().equals(ROOT) && rootAlreadyCreated)) {
				txn.rollback();
				return Response.status(Status.NOT_ACCEPTABLE).entity(USERNAME_ALREADY_EXISTS).build();
				
			}else {
				Entity.Builder user = Entity.newBuilder(userKey);
				
					user
					.set("Username", data.username)
					.set("Email", data.email)
					.set("Name", data.name)
					.set("Phone_number", data.phone)
					.set("Password", DigestUtils.sha512Hex(data.password))//passwords devem sempre ser guardadas de maneira encriptada (hash)
					.set("Profile", data.profile)
					.set("Ocupation", data.ocupation)
					.set("Work_Place", data.workPlace)
					.set("Household", data.houseHold)
					.set("NIF", data.NIF)
					.set("CP", data.CP)
					.set("foto", data.foto)
					.set("user_creation_time", Timestamp.now());
					
				if(data.username.toLowerCase().equals(ROOT)) {
						user
						.set("Role", SUPER_USER)
						.set("State", ACTIVE_STATE);
						rootAlreadyCreated = true;

				}else {
						user
						.set("Role", USER)
						.set("State", INATIVE_STATE);
						
				}
					Entity userToAdd = user.build();
					txn.add(userToAdd);
					LOG.info(REGIST_SUCESSFULL);
					txn.commit();
					return Response.ok().entity(data.username + SUCESFULL_LOGIN).build();
			}
		} finally {
			if(txn.isActive()) {
				txn.rollback();
			}
		}
		
	}
}
