package com.app.food.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.food.model.DonorView;

/**
 * Spring Data JPA repository for the {@link DonorView} entity.
 * Provides access to the local, replicated view of donor data.
 *
 * @author Radhakrishnan
 * @version 1.0
 */
@Repository
public interface DonorViewRepository extends JpaRepository<DonorView, Long> {}