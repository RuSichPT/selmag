package com.github.rusichpt.feedback.service;

import com.github.rusichpt.feedback.entity.FavouriteProduct;
import com.github.rusichpt.feedback.repository.FavouriteProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultFavouriteProductServiceTest {

    @Mock
    FavouriteProductRepository favouriteProductRepository;

    @InjectMocks
    DefaultFavouriteProductService service;

    @Test
    void addProductToFavourites_ReturnsCreatedFavouriteProduct() {
        // given
        doAnswer(invocation -> Mono.justOrEmpty(invocation.getArguments()[0]))
                .when(favouriteProductRepository).save(any());

        // when
        StepVerifier.create(service.addProductToFavourites(1, "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c"))
                // then
                .expectNextMatches(favouriteProduct -> favouriteProduct.getProductId() == 1 &&
                        favouriteProduct.getUserId().equals("5f1d5cf8-cbd6-11ee-9579-cf24d050b47c") &&
                        favouriteProduct.getId() != null)
                .verifyComplete();

        verify(favouriteProductRepository).save(argThat(favouriteProduct -> favouriteProduct.getProductId() == 1 &&
                favouriteProduct.getUserId().equals("5f1d5cf8-cbd6-11ee-9579-cf24d050b47c") &&
                favouriteProduct.getId() != null));
    }

    @Test
    void removeProductFromFavourites_ReturnsEmptyMono() {
        // given
        doReturn(Mono.empty()).when(favouriteProductRepository)
                .deleteByProductIdAndUserId(1, "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c");

        // when
        StepVerifier.create(service.removeProductFromFavourites(1, "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c"))
                // then
                .verifyComplete();

        verify(favouriteProductRepository)
                .deleteByProductIdAndUserId(1, "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c");
    }

    @Test
    void findFavouriteProductByProduct_ReturnsFavouriteProduct() {
        // given
        doReturn(Mono.just(new FavouriteProduct(UUID.fromString("fe87eef6-cbd7-11ee-aeb6-275dac91de02"),
                1, "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c")))
                .when(favouriteProductRepository)
                .findByProductIdAndUserId(1, "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c");

        // when
        StepVerifier.create(service.findFavouriteProductByProductId(1, "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c"))
                // then
                .expectNext(new FavouriteProduct(UUID.fromString("fe87eef6-cbd7-11ee-aeb6-275dac91de02"),
                        1, "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c"))
                .verifyComplete();
    }

    @Test
    void findFavouriteProducts_ReturnsFavouriteProducts() {
        // given
        doReturn(Flux.fromIterable(List.of(
                new FavouriteProduct(UUID.fromString("fe87eef6-cbd7-11ee-aeb6-275dac91de02"), 1,
                        "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c"),
                new FavouriteProduct(UUID.fromString("23ff1d58-cbd8-11ee-9f4f-ef497a4e4799"), 3,
                        "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c")
        ))).when(favouriteProductRepository).findAllByUserId("5f1d5cf8-cbd6-11ee-9579-cf24d050b47c");

        // when
        StepVerifier.create(service.findFavouriteProducts("5f1d5cf8-cbd6-11ee-9579-cf24d050b47c"))
                // then
                .expectNext(
                        new FavouriteProduct(UUID.fromString("fe87eef6-cbd7-11ee-aeb6-275dac91de02"), 1,
                                "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c"),
                        new FavouriteProduct(UUID.fromString("23ff1d58-cbd8-11ee-9f4f-ef497a4e4799"), 3,
                                "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c")
                )
                .verifyComplete();
    }
}