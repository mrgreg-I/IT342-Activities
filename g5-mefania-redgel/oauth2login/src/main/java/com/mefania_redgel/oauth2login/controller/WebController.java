package com.quindaojredgar.oauth2login.controller;

import com.quindaojredgar.oauth2login.service.GoogleContactsService;
import com.google.api.services.people.v1.model.Person;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/")
public class WebController {

    private final GoogleContactsService googleContactsService;

    public WebController(GoogleContactsService googleContactsService) {
        this.googleContactsService = googleContactsService;
    }

    @GetMapping
    public String home() {
        return "index";
    }

    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal OidcUser principal, Model model) {
        if (principal == null) {
            return "redirect:/";
        }
        model.addAttribute("user", principal);
        return "profile"; 
    }

    @GetMapping("/contacts")
    public String contacts(Model model, OAuth2AuthenticationToken authentication) {
        try {
            List<Person> contacts = googleContactsService.getContacts(authentication);
            
            // Debugging: Print contacts to check data
            if (contacts != null) {
                for (Person contact : contacts) {
                    System.out.println("Contact: " + contact.getNames());
                    System.out.println("Phone Numbers: " + contact.getPhoneNumbers());
                }
            } else {
                System.out.println("No contacts retrieved.");
            }
            
            model.addAttribute("contacts", contacts != null ? contacts : List.of());
        } catch (IOException e) {
            model.addAttribute("error", "Failed to fetch contacts.");
            e.printStackTrace();
        }
        return "contacts";
    }
    

    //Add Contact
    @PostMapping("/contacts/add")
    public String addContact(@RequestParam String name, @RequestParam String email, @RequestParam String phone, OAuth2AuthenticationToken authentication) {
        try {
            googleContactsService.createContact(authentication, name, email, phone);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/contacts";
    }

    //Update Contact
    @PostMapping("/contacts/update")
    public String updateContact(@RequestParam String resourceName, @RequestParam String name, @RequestParam String email, @RequestParam String phone, OAuth2AuthenticationToken authentication) {
        try {
            googleContactsService.updateContact(authentication, resourceName, name, email, phone);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/contacts";
    }

    //Delete Contact
    @PostMapping("/contacts/delete")
    public String deleteContact(@RequestParam String resourceName, OAuth2AuthenticationToken authentication) {
        try {
            googleContactsService.deleteContact(authentication, resourceName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/contacts";
    }
}
