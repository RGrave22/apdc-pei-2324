package pt.unl.fct.di.apdc.firstwebapp.util;

import com.google.cloud.datastore.Key;

public class LoginData {

	public String username;
	public String password;
	
	public LoginData() {	
		//tem mesmo que existir um construtor vazio porque esta classe vai ter que ser instanciada automaticamente quando recebe um JSON
		//do cliente que tem de ser convertido para uma instancia java
	}
	
	public LoginData(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	
	
	
	
}
