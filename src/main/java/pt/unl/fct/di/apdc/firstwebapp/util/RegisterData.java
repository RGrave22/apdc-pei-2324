package pt.unl.fct.di.apdc.firstwebapp.util;

import com.google.cloud.datastore.Key;

public class RegisterData {

	public String username;
	public String password;
	public String confirmation;
	public String email;
	public String name;
	public String phone;
	public String profile;
	public String ocupation;
	public String workPlace;
	public String houseHold;
	public String CP;
	public String NIF;
	public String foto;
	public String role;
	public String state;
	
	public RegisterData() {	
		//tem mesmo que existir um construtor vazio porque esta classe vai ter que ser instanciada automaticamente quando recebe um JSON
		//do cliente que tem de ser convertido para uma instancia java
	}
	
	public RegisterData(String username, String password, String confirmation, String email, String name, String phone,
			String profile, String ocupation, String workPlace, String houseHold, String CP, String NIF,String foto, String role, String state) {
		this.username = username;
		this.email = email; // tem de ter formato string@string.DOM
		this.name = name;
		this.phone = phone;
		this.password = password; // pode ter uma regra a escolha
		this.confirmation = confirmation;
		this.profile = profile;
		this.ocupation = ocupation;
		this.workPlace = workPlace;
		this.houseHold = houseHold;
		this.CP = CP;
		this.NIF = NIF;
		this.foto = foto;
		this.role = role;
		this.state = state;
		
	}
	
	
	public boolean isValid() {
		return(password != null && username != null && email != null && name != null && phone != null);
	}
	
	
	/*
	 * verifies if the email is valid like this <String>@<String>.<TLS>
	 */
	public boolean validEmail() {
		String emailPattern= "[A-Za-z0-9\\._%+\\-]+@[A-Za-z0-9\\.\\-]+\\.[A-Za-z]{2,}";
		return email.matches(emailPattern);
	}
	
	public boolean validPassword() {
		
		/* Verifies if the password contains upper and lower cases, numbers, special caracters and at least 8 caracters
		 * 
		 */
		String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).{8,}$";
		return password.matches(passwordPattern);
	}
	
	
	
	
	
}