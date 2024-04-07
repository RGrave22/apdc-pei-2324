package pt.unl.fct.di.apdc.firstwebapp.util;

import java.io.InputStream;

import com.google.cloud.datastore.Key;


public class ChangeAttributesData {

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
	
	public ChangeAttributesData() {	
		//tem mesmo que existir um construtor vazio porque esta classe vai ter que ser instanciada automaticamente quando recebe um JSON
		//do cliente que tem de ser convertido para uma instancia java
	}
	
/*	public ChangeAttributesData(String username, String password, String confirmation, String email, String name, String phone,
			String profile, String ocupation, String workPlace, String houseHold, String CP, String NIF,String foto, String role, String state) {
		this.username = username;
		this.email = email; // tem de ter formato string@string.DOM
		this.name = name;
		this.phone = phone;
		this.password = password; // pode ter uma regra a escolha
		this.profile = profile;
		this.ocupation = ocupation;
		this.workPlace = workPlace;
		this.houseHold = houseHold;
		this.CP = CP;
		this.NIF = NIF;
		this.foto = foto;
		this.role = role;
		this.state = state;
		
	}*/
	
	public ChangeAttributesData(String username, String password, String confirmation, String email, String name, String phone,
			String profile, String ocupation, String workPlace, String houseHold, String CP, String NIF, String foto, String role, String state) {
		this.username = username;
		this.email = email; // tem de ter formato string@string.DOM
		this.name = name;
		this.phone = phone;
		this.password = password; // pode ter uma regra a escolha
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
	
	
}
