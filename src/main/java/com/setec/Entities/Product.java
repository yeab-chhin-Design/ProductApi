package com.setec.Entities;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "tbl_product")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String name;
	private double price;
	
	@JsonIgnore
	  private String imageUrl;
	
	private int qty;
	public double getAmount( ) {
		return price * qty;
	}
	
	public String getFullImageUrl() {
		return ServletUriComponentsBuilder.fromCurrentContextPath().build().toString() + imageUrl;
	}
}
