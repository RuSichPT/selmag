package com.github.rusichpt.managerapp.controller;

import com.github.rusichpt.managerapp.entity.Product;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)// можно так или mockMvc.perform(...).andDo(print())
@WireMockTest(httpPort = 54321)
class ProductsControllerIT {
    @Autowired
    MockMvc mockMvc;

    @Test
    @WithMockUser(username = "manager", roles = "MANAGER")
    @DisplayName("getProductsList вернет страницу со списком товара")
    void getProductsList_ReturnsProductsListPage() throws Exception {
        // given
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/catalogue/products/list")
                .queryParam("filter", "товар");

        WireMock.stubFor(WireMock.get(WireMock.urlPathMatching("/catalogue-api/products"))
                .withQueryParam("filter", WireMock.equalTo("товар"))
                .willReturn(WireMock.ok("""
                                [
                                    {"id": 1, "title": "Товар №1", "details": "Описание товара №1"},
                                    {"id": 2, "title": "Товар №2", "details": "Описание товара №2"}
                                ]
                                """)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));


        // when
        mockMvc.perform(requestBuilder)
                // then
                .andExpectAll(
                        status().isOk(),
                        view().name("catalogue/products/list"),
                        model().attribute("filter", "товар"),
                        model().attribute("products", List.of(
                                new Product(1, "Товар №1", "Описание товара №1"),
                                new Product(2, "Товар №2", "Описание товара №2")
                        ))
                );

        WireMock.verify(WireMock.getRequestedFor(WireMock.urlPathMatching("/catalogue-api/products"))
                .withQueryParam("filter", WireMock.equalTo("товар")));
    }

    @Test
    @DisplayName("getNewProductPage вернет страницу товара")
    void getNewProductPage_ReturnsProductPage() throws Exception {
        // given
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/catalogue/products/create")
                .with(user("manager").roles("MANAGER"));// можно через @WithMockUser()

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andExpectAll(
                        status().isOk(),
                        view().name("catalogue/products/new_product")
                );
    }
}
