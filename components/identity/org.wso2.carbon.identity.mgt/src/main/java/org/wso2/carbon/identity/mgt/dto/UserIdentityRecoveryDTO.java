package org.wso2.carbon.identity.mgt.dto;

/**
 * This object contains the information of the created user account. This
 * information can be sent to the user to complete the user registration
 * process. Information are such as the temporary password, confirmation code
 * etc
 * 
 * @author sga
 * 
 */
public class UserIdentityRecoveryDTO {

	private String userId;
	private String temporaryPassword;
	private String confirmationCode;
	
	public UserIdentityRecoveryDTO(String userName) {
		this.userId = userName;
	}

	/**
	 * Returns the temporary password of the created account
	 * @return
	 */
	public String getTemporaryPassword() {
		return temporaryPassword;
	}

	public UserIdentityRecoveryDTO setTemporaryPassword(String temporaryPassword) {
		this.temporaryPassword = temporaryPassword;
		return this;
	}

	/**
	 * Returns the confirmation code for the created account
	 * @return
	 */
	public String getConfirmationCode() {
		return confirmationCode;
	}

	public UserIdentityRecoveryDTO setConfirmationCode(String confirmationCode) {
		this.confirmationCode = confirmationCode;
		return this;
	}

	public String getUserId() {
	    return userId;
    }

	public UserIdentityRecoveryDTO setUserId(String userId) {
	    this.userId = userId;
	    return this;
    }

}