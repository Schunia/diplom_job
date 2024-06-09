package com.alexandra.Astro.Services;

import com.alexandra.Astro.Models.Subscription;
import com.alexandra.Astro.Repositories.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionService {
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    public void setSubscriptionRepository(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }
    public List<Subscription> getActiveSubscriptions() {
        return subscriptionRepository.findByActiveTrue();
    }

    public Subscription getSubscriptionById(Long id) {
        return subscriptionRepository.findById(id).get();
    }

    public void insertSubscription(Subscription subscription) {
        subscriptionRepository.save(subscription);
    }

    public void deleteSubscriptionById(Long id) {
        subscriptionRepository.deleteById(id);
    }

}
