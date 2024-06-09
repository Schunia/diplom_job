package com.alexandra.Astro.Controllers;

import com.alexandra.Astro.Models.Subscription;
import com.alexandra.Astro.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.security.Principal;

@Controller
public class SubscriptionController {

    private SubscriptionService subscriptionService;

    @Autowired
    public void setServices(SubscriptionService subscriptionService){
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/subscription/adding")
    public String insertObservation(Subscription subscription) throws IOException {

        subscription.setActive(true);
        subscriptionService.insertSubscription(subscription);

        return "redirect:/";
    }
}
