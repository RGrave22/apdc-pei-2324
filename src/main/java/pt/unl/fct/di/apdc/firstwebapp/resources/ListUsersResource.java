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

@Path("/list")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class ListUsersResource {
	
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
	
	private static String SU = "SU";
	private static String GA = "GA";
	private static String GBO = "GBO";
	private static String USER = "USER";
	private static String INATIVO = "INATIVO";
	private static String ATIVO = "ATIVO";
	
	private static String COOKIE_EXPIRED = "You lost your session, login please";
	
	public ListUsersResource() {}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
	public Response listUsers(@CookieParam("session::apdc") Cookie cookie) {
		
		String userRole = LoginResource.getUserRole(cookie);
		
		if(!LoginResource.checkCookie(cookie)) {
			return Response.status(Status.GONE).entity(COOKIE_EXPIRED).build();
		}
		
		
	Query<Entity> usersQuery = Query.newEntityQueryBuilder()
			.setKind("User")
			.build();
		
	QueryResults<Entity> usersResult = datastore.run(usersQuery);	
		
	List<List<String>> usersList = new ArrayList<>();
	
	usersResult.forEachRemaining(user -> {
		
		if(userRole.equals(USER)) {
			/*
			 * A verificação a baixo, apenas serve para listar os utilizadores que de facto pertencem à DB do projeto, visto que ao longo das aulas utilizamos outros user sem a maioria dos atributos, nao faria sentido lista-los
			 */
			if(user.contains("Role")) {
				if(user.getString("Role").equals(USER) 
						&& user.getString("State").equals(ATIVO) 
							&& user.getString("Profile").equals("Publico")) {
					List<String> userInfo = new ArrayList<>();
					userInfo.add(user.getString("Username"));
					userInfo.add(user.getString("Email"));
					userInfo.add(user.getString("Name"));
					
					usersList.add(userInfo);
				}
			}
		}else if(userRole.equals(GBO) ) {
			if(user.contains("Role")) {
				if(user.getString("Role").equals(USER)) {
					List<String> userInfo = new ArrayList<>();
					userInfo.add(user.getString("Username"));
					userInfo.add(user.getString("Email"));
					userInfo.add(user.getString("Name"));
					userInfo.add(user.getString("Phone_number"));
					userInfo.add(user.getString("Password"));
					userInfo.add(user.getString("Profile"));
					userInfo.add(user.getString("Ocupation"));
					userInfo.add(user.getString("Work_Place"));
					userInfo.add(user.getString("Household"));
					//userInfo.add(user.getString("Codigo_Postal"));
					userInfo.add(user.getString("NIF"));
					userInfo.add(user.getString("CP"));
					userInfo.add(user.getString("foto"));
					userInfo.add(user.getString("Role"));
					userInfo.add(user.getString("State"));
					
					usersList.add(userInfo);
				}
			}
		
		}else if(userRole.equals(GA)) {
			if(user.contains("Role")) {
				if(user.getString("Role").equals(USER) || user.getString("Role").equals(GBO)|| user.getString("Role").equals(GA)) {
					List<String> userInfo = new ArrayList<>();
					userInfo.add(user.getString("Username"));
					userInfo.add(user.getString("Email"));
					userInfo.add(user.getString("Name"));
					userInfo.add(user.getString("Phone_number"));
					userInfo.add(user.getString("Password"));
					userInfo.add(user.getString("Profile"));
					userInfo.add(user.getString("Ocupation"));
					userInfo.add(user.getString("Work_Place"));
					userInfo.add(user.getString("Household"));
					//userInfo.add(user.getString("Codigo_Postal"));
					userInfo.add(user.getString("NIF"));
					userInfo.add(user.getString("CP"));
					userInfo.add(user.getString("foto"));
					userInfo.add(user.getString("Role"));
					userInfo.add(user.getString("State"));
					
					usersList.add(userInfo);
				}
			}
		}else if(userRole.equals(SU)) {
			//if por ter a base com mais users que nao estao criados da mesma maneira, so para teste
			if(user.contains("Role")) {
				List<String> userInfo = new ArrayList<>();
				userInfo.add(user.getString("Username"));
				userInfo.add(user.getString("Email"));
				userInfo.add(user.getString("Name"));
				userInfo.add(user.getString("Phone_number"));
				userInfo.add(user.getString("Password"));
				userInfo.add(user.getString("Profile"));
				userInfo.add(user.getString("Ocupation"));
				userInfo.add(user.getString("Work_Place"));
				userInfo.add(user.getString("Household"));
				//userInfo.add(user.getString("Codigo_Postal"));
				userInfo.add(user.getString("NIF"));
				userInfo.add(user.getString("CP"));
				userInfo.add(user.getString("foto"));
				userInfo.add(user.getString("Role"));
				userInfo.add(user.getString("State"));
				
				usersList.add(userInfo);
			}
		}
		
	});
		
	return Response.ok().entity(usersList).build();

	}
}
