package com.alexandra.Astro.Repositories;

import com.alexandra.Astro.Models.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findByActiveTrue();
}
