package com.grabfood.menu.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.grabfood.menu.entity.Menu;
import com.grabfood.menu.repository.MenuRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.annotation.CacheEvict;

@ExtendWith(MockitoExtension.class)
class MenuServiceTest {
    @Mock MenuRepository repository;
    @InjectMocks MenuService service;

    @Test void updateHasExactRequiredCacheEvictConfiguration() throws Exception {
        CacheEvict annotation = MenuService.class.getMethod("updateMenu", Menu.class)
                .getAnnotation(CacheEvict.class);
        assertThat(annotation).isNotNull();
        assertThat(annotation.value()).containsExactly("menuCache");
        assertThat(annotation.key()).isEqualTo("#menu.id");
        assertThat(annotation.beforeInvocation()).isFalse();
    }

    @Test void savesDatabaseBeforeMethodReturnsForCacheEviction() {
        Menu menu = new Menu(1L, "Phở Bò Đặc Biệt", new BigDecimal("75000"));
        when(repository.existsById(1L)).thenReturn(true);
        when(repository.save(menu)).thenReturn(menu);

        Menu result = service.updateMenu(menu);

        assertThat(result.getPrice()).isEqualByComparingTo("75000");
        InOrder order = inOrder(repository);
        order.verify(repository).existsById(1L);
        order.verify(repository).save(menu);
        order.verify(repository).flush();
    }

    @Test void rejectsNegativePriceWithoutWritingDatabase() {
        Menu menu = new Menu(1L, "Phở", new BigDecimal("-1"));
        assertThatThrownBy(() -> service.updateMenu(menu))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("price");
        verifyNoInteractions(repository);
    }
}
