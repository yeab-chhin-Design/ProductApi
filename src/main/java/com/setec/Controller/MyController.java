package com.setec.Controller;


import java.io.File;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.setec.DAO.PostProductDAO;
import com.setec.DAO.PutProductDAO;
import com.setec.Entities.Product;
import com.setec.Repo.ProductRepo;

import lombok.experimental.var;

@RestController
@RequestMapping("/api/product")
public class MyController {
	@Autowired
	private ProductRepo productRepo;
	
	@SuppressWarnings("deprecation")
	@GetMapping
	public Object getAll() {
		var products = productRepo.findAll();
		if(products.size()==0) {
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
				    .body(Map.of("message", "Empty product"));
		}else {
			return products;
		}
	}
	
	@GetMapping("{id}")
	public Object getById(@PathVariable("id") Integer id) {
		var pro = productRepo.findById(id);
		if(pro.isPresent()) {
			return pro.get();
		}else {
			return ResponseEntity.status(404).body(Map.of("message","product id ="+id+" not found"));
		}
	}
	
	@GetMapping("name/{name}")
	public Object getByName(@PathVariable("name") String name) {
	    java.util.List<Product> pros = productRepo.findByName(name);
		if(pros.size()==0) {
			return ResponseEntity.status(404).body(Map.of("message","product name = "+name+" not found"));
		}else {
			return pros;
		}
	}
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Object postproduct(@ModelAttribute PostProductDAO product)throws Exception {
		var file = product.getImageFile();
		String uploadDir = new File("myApp/static").getAbsolutePath();	
		File dir = new File(uploadDir);
		if(!dir.exists()) {
			dir.mkdirs();
		}
		String extension = Objects.requireNonNull(file.getOriginalFilename());
		String fileName = UUID.randomUUID()+"-"+extension;
		String filePath = Paths.get(uploadDir, fileName).toString();
		
		file.transferTo(new File(filePath));
		Product pro = new Product();
		pro.setName(product.getName());
		pro.setQty(product.getQty());
		pro.setPrice(product.getPrice());
		pro.setImageUrl("/static/"+fileName);
		
		productRepo.save(pro);
		
		
		return ResponseEntity.status(201).body(pro);
	}
	
	@DeleteMapping("/{id}")
	public Object deletById(@PathVariable("id") Integer id) {
		var p = productRepo.findById(id);
		if(p.isPresent()) {
			new File("myApp/"+p.get().getImageUrl()).delete();
			productRepo.delete(p.get());
			return ResponseEntity.status(HttpStatus.ACCEPTED)
				 .body(Map.of("message","Product id= "+id+" has been deleted"));
		
		}
		    return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(Map.of("message","Product id= "+id+" not found"));
	}
	
	@PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Object putProduct(@ModelAttribute PutProductDAO product)throws Exception {
		Integer id = product.getId();
		var p = productRepo.findById(id);
		if(p.isPresent()) {
			var update = p.get();
			update.setName(product.getName());
			update.setPrice(product.getPrice());
			update.setQty(product.getQty());
			if(product.getImageFile() != null) {
				var file = product.getImageFile();
				String uploadDir = new File("myApp/static").getAbsolutePath();	
				File dir = new File(uploadDir);
				if(!dir.exists()) {
					dir.mkdirs();
				}
				String extension = Objects.requireNonNull(file.getOriginalFilename());
				String fileName = UUID.randomUUID()+"-"+extension;
				String filePath = Paths.get(uploadDir, fileName).toString();
				
				new File("myApp/"+update.getImageUrl()).delete();
				file.transferTo(new File(filePath));
				update.setImageUrl("/static/"+fileName);
				
			}
			productRepo.save(update);
			return ResponseEntity.status(HttpStatus.ACCEPTED)
					.body(Map.of("message","product update successfull","product",update));
		}
		
		return ResponseEntity.status(404).body(Map.of("message","product id = "+id+" not found"));
	}
}


