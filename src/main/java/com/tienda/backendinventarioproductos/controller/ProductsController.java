package com.tienda.backendinventarioproductos.controller;


import com.tienda.backendinventarioproductos.model.response.ProductsQueryResponse;
import com.tienda.backendinventarioproductos.service.ProductsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ProductsController {

	private final ProductsService service;

	@GetMapping("/products")
	public ResponseEntity<ProductsQueryResponse> getProducts(
			@RequestHeader Map<String, String> headers,
			@RequestParam(required = false) String caracteristicas,
			@RequestParam(required = false) String nombre,
			@RequestParam(required = false) String categoria,
			@RequestParam(required = false, defaultValue = "false") Boolean aggregate) {

		log.info("headers: {}", headers);
		ProductsQueryResponse products = service.getProducts(nombre, caracteristicas, categoria, aggregate);
		return ResponseEntity.ok(products);
	}


}
