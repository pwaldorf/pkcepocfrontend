package com.pkce.poc.pkcepocfrontend.controller;

import com.pkce.poc.pkcepocfrontend.model.Client;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
//@RequiredArgsConstructor
class UiController {

	//@Autowired
	//private final TodoClient todoClient;

	public UiController() {
	}

	@GetMapping("/")
	public String index() {
		return "home";
	}

	@GetMapping("/setup")
	public String setup(Model model) {
		model.addAttribute("client", new Client());
		return "setup";
	}

	// Add service to create client here
	@PostMapping("/create-client")
	public String createClientForm(@ModelAttribute Client client) {

		System.out.println(client.getClientId());
		return "setup";
	}

	@GetMapping("/relogin")
	public String relogin() {

		return "relogin";
	}

	@GetMapping("/home")
	//public String home(Model model, @AuthenticationPrincipal Principal currentUser) {
	public String home() {

		return "home";
	}
}
