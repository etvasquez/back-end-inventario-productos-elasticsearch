package com.tienda.backendinventarioproductos.service;
import com.tienda.backendinventarioproductos.model.response.ProductsQueryResponse;

public interface ProductsService {

	ProductsQueryResponse getProducts(String nombre, String caracteristicas, String categoria, Boolean aggregate);

}
