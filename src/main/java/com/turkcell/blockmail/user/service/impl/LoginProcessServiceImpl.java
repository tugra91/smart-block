package com.turkcell.blockmail.user.service.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.turkcell.blockmail.document.AccessTokenInformationDocument;
import com.turkcell.blockmail.model.BlockInfoOutput;
import com.turkcell.blockmail.model.GetAccessTokenOutput;
import com.turkcell.blockmail.model.RTEGetLoginInformationInput;
import com.turkcell.blockmail.model.RTEGetLoginInformationOutput;
import com.turkcell.blockmail.model.UserInformationModel;
import com.turkcell.blockmail.user.dao.LoginProcessDao;
import com.turkcell.blockmail.user.service.LoginProcessService;
import com.turkcell.blockmail.util.mail.service.BlockSendMailService;


@Service
public class LoginProcessServiceImpl implements LoginProcessService {


	@Autowired
	@Qualifier("restTemplateBean")
	private RestTemplate restTemplate;
	
	@Autowired
	private LoginProcessDao loginPageDao;
	
	@Autowired
	private BlockSendMailService blockSendMailService;


	private static final String OAuth2URL = "http://localhost:8090/oauth/authorize";
	private static final String CLIENT_ID = "blockMail";
	private static final String RESPONSE_TYPE = "code";
	private static final String SCOPE = "openid";
	private static final String REDIRECT_URI = "http://localhost:8090/getAuthCode";

	@Override
	public GetAccessTokenOutput getAccessToken(String username, String password) {
		GetAccessTokenOutput output = new GetAccessTokenOutput();
		try {
			URI url = new URI(OAuth2URL+"?client_id="+CLIENT_ID+
					"&response_type="+RESPONSE_TYPE+
					"&scope="+SCOPE+
					"&redirect_uri="+REDIRECT_URI);
			
			
			HttpHeaders body = new HttpHeaders();
			body.add("username", username);
			body.add("password", password);

			HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(getHttpHeaderForOAuthCode(username, password));

			ResponseEntity<String> response = 
					restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
			
			if(response.getStatusCode().is2xxSuccessful()) {
				output = getAccessTokenFromAuthServer(response.getBody());
			} else {
				output.setResult("Login servisine bağlanamıyorum sanırım hatalı bir cevap dönüyor. Lütfen Tuğra Er'e bilgi verin.");
				output.setStatus(false);
			}
		}
		catch (URISyntaxException e) {
			output.setResult("Login servisine bağlanamıyorum sanırım servis adresleri karışmış. Lütfen Tuğra Er'e bilgi verin.");
			output.setStatus(false);
		}
		
		return output;
	}
	
	

	@Override
	public BlockInfoOutput saveUser(UserInformationModel userInformation) {
		BlockInfoOutput output = new BlockInfoOutput();
		UserInformationModel existUser = loginPageDao.findByUsername(userInformation.getUsername().trim());
		UserInformationModel existEmailUser = loginPageDao.findByEmail(userInformation.getEmail().trim());
		List<String> roles = new ArrayList<>();
		if(existUser != null && existEmailUser != null) {
			output.setStatus(false);
			output.setResult("Daha önce böyle bir kullanıcı adı ve E-mail ile hesap açılmış. Lütfen başka bir kullanıcı adı ve E-Mail ile deneyin.");
		} else if(existUser != null) {
			output.setStatus(false);
			output.setResult("Daha önce böyle bir kullanıcı adı ile hesap açılmış. Lütfen başka bir kullanıcı adı deneyin.");
		} else if(existEmailUser != null) {
			output.setStatus(false);
			output.setResult("Daha önce böyle bir E-mail ile hesap açılmış. Lütfen başka bir E-mail ile deneyin");
		} else {
			output.setStatus(true);
			output.setResult("Tebrikler Başarıyla Kaydoldunuz. Üyeliğiniz onaylandıktan sonra giriş yaparak, sistemi kullanabilirsiniz");
			userInformation.setPassword(new BCryptPasswordEncoder().encode(userInformation.getPassword()));
			userInformation.setApproved(false);
			roles.add("ROLE_USER");
			userInformation.setRoles(roles);
			userInformation.setCreatedDate(System.currentTimeMillis());
			try {
				userInformation.setEmail(userInformation.getEmail().trim());
				userInformation.setUsername(userInformation.getUsername().trim());
				loginPageDao.saveUser(userInformation);
			} catch(Exception e) {
				output.setStatus(false);
				output.setResult("Sizi Veritabanımıza kaydedemedik. Lütfen Tuğra Er'e bilgi verin gereğini yapacaktır. Aksaklık için özür dileriz. ");
			}
			try {
				List<UserInformationModel> adminList = loginPageDao.findAllAdmin();
				blockSendMailService.sendRegisterUserMail(userInformation);
				blockSendMailService.sendAdminMailForInformation(userInformation, adminList);
			} catch (Exception e) {
				output.setStatus(true);
				output.setResult("Başarıyla kayıt oldunuz fakat sistem yöneticilerine bilgilendirme maili göndermeyle ilgili sıkıntılar yaşıyorum. Lütfen TEAM-SQUAD-TERFI'yi bilgilendirin ve sorun yaşadığınızı bildirin. ");
			}
			
		}
		return output;
	}
	

	@Override
	public UserInformationModel updateUser(UserInformationModel userInformation) {
		try {
			Update update = new Update();
			update.set("email", userInformation.getEmail());
			update.set("name", userInformation.getName());
			update.set("surname", userInformation.getSurname());
			if(!StringUtils.isEmpty(userInformation.getPassword())) {
				userInformation.setPassword(new BCryptPasswordEncoder().encode(userInformation.getPassword()));
				update.set("password", userInformation.getPassword());
			}
			UserInformationModel result = loginPageDao.updateUser(userInformation, update);
			result.setPassword("");
			return result;
		} catch(Exception e) {
			userInformation = null;
			return userInformation;
		}
	}

	@Override
	public UserInformationModel getUserInformation(String username) {
		UserInformationModel userInformation = new UserInformationModel();
		userInformation = loginPageDao.findByUsername(username);
		userInformation.setPassword("");
		return userInformation;
	}

	@Override
	public UserInformationModel getUserInformationWithId(ObjectId id) {
		UserInformationModel userInformation = new UserInformationModel();
		userInformation = loginPageDao.findById(id);
		userInformation.setPassword("");
		return userInformation;
	}

	@Override
	public String checkUserExpire() {
		return "OK";
	}

	@Override
	public GetAccessTokenOutput getAccessTokenViaRefreshToken(String accessToken) {
		GetAccessTokenOutput output = new GetAccessTokenOutput();
		try {
			AccessTokenInformationDocument accessTokenInformation = loginPageDao.getAccessToken(accessToken);
			if(accessTokenInformation == null) {
				output.setStatus(false);
				return output;
			}
			URI url = new URI("http://localhost:8090/getLoginInformationForRT");
			RTEGetLoginInformationInput input = new RTEGetLoginInformationInput();
			input.setCode(accessTokenInformation.getRefreshToken());
			RequestEntity<RTEGetLoginInformationInput> requestEntity = new RequestEntity<RTEGetLoginInformationInput>(input, HttpMethod.POST, url);
			
			ResponseEntity<RTEGetLoginInformationOutput> response = restTemplate.exchange(requestEntity, RTEGetLoginInformationOutput.class);
			
			if(response.getStatusCode().is2xxSuccessful()) {
				output.setAccessToken(response.getBody().getAccessToken());
				output.setStatus(true);
				try {
					loginPageDao.deleteOldAccessToken(accessToken);
					saveAccessTokenToDB(response.getBody().getAccessToken(), response.getBody().getRefreshToken());
				} catch (Exception e) {
					output.setStatus(false);
				}
			} else {
				output.setStatus(false);
			}
			
		}catch (Exception e) {
			output.setStatus(false);
		}
		return output;
	}
	
	private GetAccessTokenOutput getAccessTokenFromAuthServer(String code) {
		GetAccessTokenOutput output = new GetAccessTokenOutput();
		try {
			URI url = new URI("http://localhost:8090/getLoginInformation");
			
			RTEGetLoginInformationInput input = new RTEGetLoginInformationInput();
			input.setCode(code);
			
			RequestEntity<RTEGetLoginInformationInput> requestEntity = new RequestEntity<RTEGetLoginInformationInput>(input, HttpMethod.POST, url);
			
			ResponseEntity<RTEGetLoginInformationOutput> response = restTemplate.exchange(requestEntity, RTEGetLoginInformationOutput.class);
			
			if(response.getStatusCode().is2xxSuccessful()) {
				output.setAccessToken(response.getBody().getAccessToken());
				output.setStatus(true);
				try {
					saveAccessTokenToDB(response.getBody().getAccessToken(), response.getBody().getRefreshToken());
				} catch (Exception e) {
					output.setResult("Login servisi veritabanına bağlanırken hata aldı. Lütfen Tuğra Er'e bilgi verin");
					output.setStatus(false);
				}
			} else {
				output.setResult("Login servisine bağlanamıyorum sanırım hatalı bir cevap dönüyor. Lütfen Tuğra Er'e bilgi verin.");
				output.setStatus(false);
			}
		} catch (Exception e) {
			output.setResult("Login servisine bağlanamıyorum sanırım servis'e bağlantı konfigrasyonunda hata var. Lütfen Tuğra Er'e bilgi verin.");
			output.setStatus(false);
		}
		return output;
	}
	
	private void saveAccessTokenToDB(String accessToken, String refreshToken) throws Exception {
		try {
			AccessTokenInformationDocument accessTokenInformation = loginPageDao.getAccessToken(accessToken);
			if(accessTokenInformation == null) {
				accessTokenInformation = new AccessTokenInformationDocument();
				accessTokenInformation.setAccessToken(accessToken);
				accessTokenInformation.setRefreshToken(refreshToken);
				loginPageDao.saveAccessToken(accessTokenInformation);
			}
		} catch (Exception e) {
			throw e;
		}
	}


	private HttpHeaders getHttpHeaderForOAuthCode(String username, String password) {
		return new HttpHeaders() {
			private static final long serialVersionUID = 8998352702764652594L;

			{
				String auth = username + ":" + password;
				byte[] encodedAuth = Base64.getEncoder().encode( 
						auth.getBytes(Charset.forName("UTF-8")) );
				String authHeader = "Basic " + new String( encodedAuth );
				set( "Authorization", authHeader );
			}};
	}



	
	 


}
