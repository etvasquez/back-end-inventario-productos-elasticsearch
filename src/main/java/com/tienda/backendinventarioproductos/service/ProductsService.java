package com.tienda.backendinventarioproductos.service;

import com.tienda.backendinventarioproductos.model.db.Product;
import com.tienda.backendinventarioproductos.model.request.CreateProductRequest;
import com.tienda.backendinventarioproductos.model.response.ProductsQueryResponse;

public interface ProductsService {

	ProductsQueryResponse getProducts(String nombre, String caracteristicas, String categoria, Boolean aggregate);
	
	Product getProduct(String productId);
	
	Boolean removeProduct(String productId);
	
	Product createProduct(CreateProductRequest request);

}
