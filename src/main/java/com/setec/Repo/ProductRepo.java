package com.setec.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.setec.Entities.Product;

public interface ProductRepo extends JpaRepository<Product, Integer> {

	List<Product> findByName(String name);

}
