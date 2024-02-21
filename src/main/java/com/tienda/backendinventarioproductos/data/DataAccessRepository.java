package com.tienda.backendinventarioproductos.data;

import com.tienda.backendinventarioproductos.model.db.Product;
import com.tienda.backendinventarioproductos.model.response.AggregationDetails;
import com.tienda.backendinventarioproductos.model.response.ProductsQueryResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder.Type;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class DataAccessRepository {

    @Value("${server.gatewayAddres}")
    private String gatewayAddres;

    // Esta clase (y bean) es la unica que usan directamente los servicios para
    // acceder a los datos.
    private final ProductRepository productRepository;
    private final ElasticsearchOperations elasticClient;

    private final String[] descriptionSearchFields = {"caracteristicas", "caracteristicas._2gram", "caracteristicas._3gram"};
    private final String[] nombreSearchFields = {"nombre", "nombre._2gram", "nombre._3gram"};

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public Boolean delete(Product product) {
        productRepository.delete(product);
        return Boolean.TRUE;
    }

	public Optional<Product> findById(String id) {
		return productRepository.findById(id);
	}

    @SneakyThrows
    public ProductsQueryResponse findProducts(String nombre, String caracteristicas, String categoria, Boolean aggregate) {

        BoolQueryBuilder querySpec = QueryBuilders.boolQuery();

        if (!StringUtils.isEmpty(categoria)) {
            querySpec.must(QueryBuilders.termQuery("categoria", categoria));
        }

        if (!StringUtils.isEmpty(nombre)) {
            querySpec.should(QueryBuilders.multiMatchQuery(nombre, nombreSearchFields).type(Type.BOOL_PREFIX));

            //querySpec.must(QueryBuilders.matchQuery("nombre", nombre));
        }

        if (!StringUtils.isEmpty(caracteristicas)) {
            querySpec.should(QueryBuilders.multiMatchQuery(caracteristicas, descriptionSearchFields).type(Type.BOOL_PREFIX));
        }

        //Si no he recibido ningun parametro, busco todos los elementos.
        if (!querySpec.hasClauses()) {
            querySpec.must(QueryBuilders.matchAllQuery());
        }

        //Filtro implicito
        //No le pido al usuario que lo introduzca pero lo aplicamos proactivamente en todas las peticiones
        //En este caso, que los productos sean visibles (estado correcto de la entidad)
       // querySpec.must(QueryBuilders.termQuery("visible", true));

        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder().withQuery(querySpec);

        if (aggregate) {
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("Categoria").field("categoria").size(1000));
            nativeSearchQueryBuilder.withMaxResults(0);
        }

        //Opcionalmente, podemos paginar los resultados
        //nativeSearchQueryBuilder.withPageable(PageRequest.of(0, 10));

        Query query = nativeSearchQueryBuilder.build();
        SearchHits<Product> result = elasticClient.search(query, Product.class);

        List<AggregationDetails> responseAggs = new LinkedList<>();

        if (result.hasAggregations()) {

            Map<String, Aggregation> aggs = result.getAggregations().asMap();
            ParsedStringTerms countryAgg = (ParsedStringTerms) aggs.get("Categoria");

            //Componemos una URI basada en serverFullAddress y query params para cada argumento, siempre que no viniesen vacios
            String queryParams = getQueryParams(nombre, caracteristicas, categoria);
            countryAgg.getBuckets()
                    .forEach(
                            bucket -> responseAggs.add(
                                    new AggregationDetails(
                                            bucket.getKey().toString(),
                                            (int) bucket.getDocCount(),
                                            gatewayAddres + "/products?categoria=" + bucket.getKey() + queryParams)));
        }
        return new ProductsQueryResponse(result.getSearchHits().stream().map(SearchHit::getContent).toList(), responseAggs);
    }

    /**
     * Componemos una URI basada en serverFullAddress y query params para cada argumento, siempre que no viniesen vacios
     *
     * @param nombre        - nombre del producto
     * @param caracteristicas - descripcion del producto
     * @param categoria     - pais del producto
     * @return
     */
    private String getQueryParams(String nombre, String caracteristicas, String categoria) {
        String queryParams = (StringUtils.isEmpty(nombre) ? "" : "&nombre=" + nombre)
                + (StringUtils.isEmpty(caracteristicas) ? "" : "&caracteristicas=" + caracteristicas);
        // Eliminamos el ultimo & si existe
        return queryParams.endsWith("&") ? queryParams.substring(0, queryParams.length() - 1) : queryParams;
    }
}
