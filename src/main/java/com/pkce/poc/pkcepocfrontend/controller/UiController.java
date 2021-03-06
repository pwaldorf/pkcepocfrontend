package com.pkce.poc.pkcepocfrontend.controller;

import com.pkce.poc.pkcepocfrontend.utils.PkceUtil;
import com.pkce.poc.pkcepocfrontend.constants.OnyxIntegratorConstants;
import com.pkce.poc.pkcepocfrontend.model.Client;
import com.pkce.poc.pkcepocfrontend.model.NewClient;
import com.pkce.poc.pkcepocfrontend.service.CreateClientIdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

@Controller
@SessionAttributes("client")
public class UiController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	CreateClientIdService createClientIdService;

	@Autowired
	PkceUtil pkceUtil;

	public UiController() {
	}

	@PostMapping("/admin/create-client")
	public String createClient(@ModelAttribute Client client) {

		String authorizationLocation = createClientIdService.getAuthenticationUrl(client);

		return "redirect:" + authorizationLocation;
	}

	@GetMapping("/callback")
	public String callback(HttpServletRequest request, HttpServletResponse response, @ModelAttribute Client client) {

		logger.info("Code Verifier: " + client.getCodeVerifier());
		String accessToken = createClientIdService
				      .getTokenFromAuthCode(request.getParameter("code"), client.getCodeVerifier());

		logger.info("token: " + accessToken);
		logger.info(client.getClientId());

		NewClient newClient = new NewClient();
		newClient.setClientId(client.getClientId());

		Object returnClient = createClientIdService.createClientId(accessToken, newClient);

		client.setClientId(((Map<String, String>)returnClient).get(OnyxIntegratorConstants.BODY_CLIENTID));
		client.setClientSecret(((Map<String, String>)returnClient).get(OnyxIntegratorConstants.BODY_CLIENTSECRET));

		return "result";
	}

	@GetMapping("/")
	public String index() {
		return "home";
	}

	@GetMapping("/admin/setup")
	public String setup(Model model) {
		model.addAttribute("client", new Client());
		return "setup";
	}

	@GetMapping("/relogin")
	public String relogin() {

		return "relogin";
	}

	@GetMapping("/result")
	public String result() {

		return "result";
	}

	@GetMapping("/home")
	//public String home(Model model, @AuthenticationPrincipal Principal currentUser) {
	public String home() {

		return "home";
	}
}
