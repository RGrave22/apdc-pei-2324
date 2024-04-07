package pt.unl.fct.di.apdc.firstwebapp.util;

public class PasswordData {

	public String actualPassword;
	public String newPassword;
	public String newPasswordConfirmation;
	
	
	public PasswordData() {}
	
	public PasswordData(String actualPassword, String newPassword, String newPasswordConfirmation) {
		this.actualPassword = actualPassword;
		this.newPassword = newPassword;
		this.newPasswordConfirmation = newPasswordConfirmation;
	}
	
}
