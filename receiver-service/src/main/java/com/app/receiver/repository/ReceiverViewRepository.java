package com.app.receiver.repository;

import com.app.receiver.model.ReceiverView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link ReceiverView} entity.
 * Provides access to the local, replicated view of receiver data.
 *
 * @author Radhakrishnan
 * @version 1.0
 */
@Repository
public interface ReceiverViewRepository extends JpaRepository<ReceiverView, Long> {}