package com.pkce.poc.pkcepocfrontend.controller;

import com.pkce.poc.pkcepocfrontend.auth.OnyxIntegratorAuthClient;
import com.pkce.poc.pkcepocfrontend.constants.OnyxIntegratorConstants;
import com.pkce.poc.pkcepocfrontend.model.Client;
import com.pkce.poc.pkcepocfrontend.model.NewClient;
import com.pkce.poc.pkcepocfrontend.service.CreateClientIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Map;

@Controller
//@RequiredArgsConstructor
class UiController {

	@Autowired
	OnyxIntegratorAuthClient authClient;

	@Autowired
	CreateClientIdService createClientIdService;

	public UiController() {
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

	// Add service to create client here
	@PostMapping("/admin/create-client")
	public String createClientForm(@ModelAttribute Client client) {

		NewClient newClient = new NewClient();
		newClient.setClientId(client.getClientId());
		System.out.println(client.getClientId());

		//Get Token move to UI
		String token = authClient.getToken();
		System.out.println("token: " + token);

		Object returnClient = createClientIdService.createClientId(token, newClient);

		client.setClientId(((Map<String, String>)returnClient).get(OnyxIntegratorConstants.BODY_CLIENTID));
		client.setClientSecret(((Map<String, String>)returnClient).get(OnyxIntegratorConstants.BODY_CLIENTSECRET));

		return "result";
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
