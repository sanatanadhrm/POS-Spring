package com.example.cashier.interfaces.product;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.cashier.applications.dto.params.product.GetListProductParams;
import com.example.cashier.applications.dto.request.product.CreateProductRequest;
import com.example.cashier.applications.dto.request.product.DeductBatchProductRequest;
import com.example.cashier.applications.dto.request.product.DeductProductRequest;
import com.example.cashier.applications.dto.request.product.GetBatchProductRequest;
import com.example.cashier.applications.dto.response.product.CreateProductResponse;
import com.example.cashier.applications.dto.response.product.DeductProductResponse;
import com.example.cashier.applications.dto.response.product.GetBatchProductResponse;
import com.example.cashier.applications.dto.response.product.GetListProductResponse;
import com.example.cashier.applications.usecase.product.CreateProductUsecase;
import com.example.cashier.applications.usecase.product.DeductBatchProductUsecase;
import com.example.cashier.applications.usecase.product.DeductProductUsecase;
import com.example.cashier.applications.usecase.product.GetBatchProductUsecase;
import com.example.cashier.applications.usecase.product.GetListProductUsecase;
import com.example.cashier.commons.dto.PaginationResponse;
import com.example.cashier.commons.dto.WebResponse;
import com.example.cashier.domains.commons.PagedResult;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@RestController
@RequestMapping("/api/v1/products")
@Slf4j
public class ProductHandler {
        private final CreateProductUsecase createProductUsecase;
        private final DeductProductUsecase deductProductUsecase;
        private final GetListProductUsecase getListProductUsecase;
        private final GetBatchProductUsecase getBatchProductUsecase;
        private final DeductBatchProductUsecase deductBatchProductUsecase;

        public ProductHandler(CreateProductUsecase createProductUsecase, DeductProductUsecase deductProductUsecase,
                        GetListProductUsecase getListProductUsecase, GetBatchProductUsecase getBatchProductUsecase,
                        DeductBatchProductUsecase deductBatchProductUsecase) {
                this.createProductUsecase = createProductUsecase;
                this.deductProductUsecase = deductProductUsecase;
                this.getListProductUsecase = getListProductUsecase;
                this.getBatchProductUsecase = getBatchProductUsecase;
                this.deductBatchProductUsecase = deductBatchProductUsecase;
        }

        @PostMapping({ "", "/" })
        public WebResponse<CreateProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
                CreateProductResponse response = this.createProductUsecase.execute(request);
                return WebResponse.<CreateProductResponse>builder()
                                .data(response)
                                .message("success")
                                .status("success")
                                .build();
        }

        @PutMapping("deduct/{id}")
        public WebResponse<DeductProductResponse> deductProduct(@PathVariable String id,
                        @Valid @RequestBody DeductProductRequest request) {

                DeductProductResponse response = this.deductProductUsecase.execute(request, id);

                return WebResponse.<DeductProductResponse>builder()
                                .data(response)
                                .message("success")
                                .status("success")
                                .build();
        }

        @GetMapping({ "", "/" })
        public WebResponse<List<GetListProductResponse>> getListProduct(
                        @Valid @ModelAttribute GetListProductParams params) {
                PagedResult<GetListProductResponse> result = this.getListProductUsecase.execute(params);
                PaginationResponse pagination = PaginationResponse.builder()
                                .page(result.getPage())
                                .limit(result.getLimit())
                                .totalRows(result.getTotalRows())
                                .totalPages(result.getTotalPages())
                                .nextPage(result.getNextPage())
                                .prevPage(result.getPrevPage())
                                .build();

                return WebResponse.<List<GetListProductResponse>>builder()
                                .data(result.getData())
                                .pagination(pagination)
                                .message("success")
                                .status("success")
                                .build();

        }

        @PostMapping("batch")
        public WebResponse<List<GetBatchProductResponse>> batchProduct(
                        @Valid @RequestBody GetBatchProductRequest request) {
                log.info("masukkkkkkk");
                List<GetBatchProductResponse> response = this.getBatchProductUsecase.execute(request);
                return WebResponse.<List<GetBatchProductResponse>>builder()
                                .data(response)
                                .message("success")
                                .status("success")
                                .build();
        }

        @PutMapping("batch-deduct")
        public WebResponse<List<DeductProductResponse>> deductBatchProduct(
                        @Valid @RequestBody DeductBatchProductRequest request) {
                List<DeductProductResponse> response = this.deductBatchProductUsecase.execute(request);
                return WebResponse.<List<DeductProductResponse>>builder()
                                .data(response)
                                .message("success")
                                .status("success")
                                .build();
        }

}
