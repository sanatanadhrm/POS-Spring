package com.example.cashier.applications.usecase.product;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.cashier.applications.dto.params.product.GetListProductParams;
import com.example.cashier.applications.dto.response.product.GetListProductResponse;
import com.example.cashier.applications.mapper.ProductMapper;

import com.example.cashier.domains.commons.PagedResult;
import com.example.cashier.domains.product.ProductRepository;
import com.example.cashier.domains.product.entity.Product;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GetListProductUsecase {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public GetListProductUsecase(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public PagedResult<GetListProductResponse> execute(GetListProductParams params) {

        PagedResult<Product> productPage = productRepository.findByStoreIdWithPagination(params.getStoreId(),
                params.getLimit(), params.getPage());

        List<Product> products = productPage.getData();
        List<GetListProductResponse> getListProductResponses = productMapper.toGetListProductResponse(products);

        return PagedResult.<GetListProductResponse>builder()
                .data(getListProductResponses)
                .page(productPage.getPage())
                .limit(productPage.getLimit())
                .totalRows(productPage.getTotalRows())
                .build();
    }
}
