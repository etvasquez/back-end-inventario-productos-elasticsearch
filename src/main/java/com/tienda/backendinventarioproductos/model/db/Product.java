package com.tienda.backendinventarioproductos.model.db;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "producto", createIndex = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Product {
	
	@Id
	private Integer id;
	
	@Field(type = FieldType.Search_As_You_Type, name = "nombre")
	private String nombre;
	
	@Field(type = FieldType.Search_As_You_Type, name = "caracteristicas")
	private String caracteristicas;
	
	@Field(type = FieldType.Text, name = "codigo")
	private String codigo;
	
	@Field(type = FieldType.Double, name = "precio")
	private Double precio;

	@Field(type = FieldType.Binary, name = "imagen")
	private String imagen;

	@Field(type = FieldType.Integer, name = "stock")
	private Integer stock;

	@Field(type = FieldType.Keyword, name = "categoria")
	private String categoria;
}
