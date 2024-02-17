package com.tienda.backendinventarioproductos.service;

import com.tienda.backendinventarioproductos.data.DataAccessRepository;
import com.tienda.backendinventarioproductos.model.db.Product;
import com.tienda.backendinventarioproductos.model.request.CreateProductRequest;
import com.tienda.backendinventarioproductos.model.response.ProductsQueryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductsServiceImpl implements ProductsService {

	private final DataAccessRepository repository;

	@Override
	public ProductsQueryResponse getProducts(String nombre, String caracteristicas, String categoria, Boolean aggregate) {
		//Ahora por defecto solo devolvera productos visibles
		return repository.findProducts(nombre, caracteristicas, categoria, aggregate);
	}

	@Override
	public Product getProduct(String productId) {
		return repository.findById(productId).orElse(null);
	}

	@Override
	public Boolean removeProduct(String productId) {

		Product product = repository.findById(productId).orElse(null);
		if (product != null) {
			repository.delete(product);
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	@Override
	public Product createProduct(CreateProductRequest request) {

		return null;
	}

}
