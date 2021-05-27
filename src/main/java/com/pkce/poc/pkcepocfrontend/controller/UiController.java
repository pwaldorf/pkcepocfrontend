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
	public String setup(Model model, HttpServletRequest request) {
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

	@GetMapping("/account")
	public String account() {

		//String todoUri = linkTo(getClass()).toUriComponentsBuilder().path("/home").toUriString();
		//return "redirect:" + keycloakLinkGenerator.createAccountLinkWithBacklink(todoUri);
		return null;
	}

	@GetMapping("/home")
	public String home(Model model, @AuthenticationPrincipal Principal currentUser) {

		//Resources<Resource<Todo>> todos = todoClient.fetchTodos();
		//model.addAttribute("todos", todos.getContent());

//		KeycloakAuthenticationToken keycloakUser = (KeycloakAuthenticationToken) currentUser;
//		System.out.printf("Current user roles: %s%n", keycloakUser.getAuthorities());

		return "home";
	}
}
