package com.github.rusichpt.catalogue.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class ProductsRestControllerIT {
    @Autowired
    MockMvc mockMvc;

    @Test
    @Sql("/sql/products.sql")
    @DisplayName("findProducts вернет список товаров")
    void findProducts_ReturnsProductsList() throws Exception {
        // given
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/catalogue-api/products")
                .param("filter", "товар")
                .with(jwt().jwt(builder -> builder.claim("scope", "view_catalogue")));

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                [
                                    {"id": 1,"title": "Товар №1", "details": "Описание товара №1"},
                                    {"id": 3,"title": "Товар №3", "details": "Описание товара №3"}
                                ]
                                """)
                );

    }

    @Test
    @DisplayName("createProduct создаст товар если запрос валиден")
    void createProduct_RequestIsValid_ReturnsNewProduct() throws Exception {
        // given
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/catalogue-api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                                "title": "Новый Товар №1",
                                "details": "Описание товара №1"
                            }
                        """)
                .with(jwt().jwt(builder -> builder.claim("scope", "edit_catalogue")));

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isCreated(),
                        header().string(HttpHeaders.LOCATION, "http://localhost/catalogue-api/products/1"),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        content().json("""
                                    {
                                        "id": 1,
                                        "title": "Новый Товар №1",
                                        "details": "Описание товара №1"
                                    }
                                """)
                );
    }

    @Test
    @DisplayName("createProduct вернет ProblemDetails если запрос невалиден")
    void createProduct_RequestIsInValid_ReturnsProblemDetail() throws Exception {
        // given
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/catalogue-api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                                "title": "  ",
                                "details": null
                            }
                        """)
                .locale(Locale.of("ru", "RU"))
                .with(jwt().jwt(builder -> builder.claim("scope", "edit_catalogue")));

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json("""
                                    {
                                        "errors": [
                                            "Название товара должно быть указано",
                                            "Название товара должно быть от 3 до 50 символов"
                                        ]
                                    }
                                """) // нестрогая проверка, остальные поля игнорируются, если хочешь строгую проверку то content().json(...,true)
                );
    }

    @Test
    @DisplayName("createProduct вернет 401 если пользователь не авторизован")
    void createProduct_UserIsNotAuthorized_ReturnsForbidden() throws Exception {
        // given
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/catalogue-api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                            {
                                "title": "Новый Товар №1",
                                "details": "Описание товара №1"
                            }
                        """)
                .locale(Locale.of("ru", "RU"))
                .with(jwt().jwt(builder -> builder.claim("scope", "view_catalogue")));

        // when
        mockMvc.perform(requestBuilder)
                // then
                .andDo(print())
                .andExpectAll(
                        status().isForbidden()
                );
    }
}