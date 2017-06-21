package com.retail.services;


import java.util.UUID;

import org.springframework.stereotype.Service;

import com.retail.domains.Response;
import com.retail.entities.UserAuth;
import com.retail.repositories.UserAuthRepository;
import com.retail.util.SignUpMailer;

@Service
public class UserService {
	
	public Response userSignUp(UserAuth user, UserAuthRepository userAuthRepository) {
		Response response = new Response();
		String verifyToken = UUID.randomUUID().toString();
		UserAuth existingUser = userAuthRepository.findByEmail(user.getEmail());
		if (existingUser == null) {
			user.setVerified(false);
			user.setVerifyToken(verifyToken);
			UserAuth createdUser = userAuthRepository.save(user);
			boolean mailStatus = sendVerificationMail(user, verifyToken);
			if (mailStatus) {
				response.setStatus("201");
				response.setUserMessage("User Created with EmailId :: " + createdUser.getEmail() + 
						"  please check your mail for account activation link");	
			}else {
				response.setStatus("500");
				response.setUserMessage("invalid email..! Please check your mail once");	
			}
					
		}else {
			response.setStatus("500");
			response.setUserMessage("User Already exists with EmailId :: " + existingUser.getEmail());
		}
		return response;
		
	}
	
	
	public Response verifyUser(String token, UserAuthRepository authRepository) {
		Response response = new Response();
		String accessToken = UUID.randomUUID().toString();
		UserAuth auth = authRepository.findByVerifyToken(token);
		if (auth != null) {
			auth.setVerified(true);
			auth.setAccessToken(accessToken);
			response.setUserMessage("Account verified.. Thanks for you time");
			response.setStatus("200");
		} else {
			response.setStatus("401");
			response.setUserMessage("Sorry ! Bad try..");
		}
		
		return response;
	}
	
	
	private boolean sendVerificationMail(UserAuth user,String verifyToken ) {
		SignUpMailer mailer = new SignUpMailer(); 
		boolean status = mailer.send(user.getEmail(), verifyToken);
		return status;
	}
	
	

}