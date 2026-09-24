package com.grabfood.menu.service;

import com.grabfood.menu.entity.Menu;
import com.grabfood.menu.exception.MenuNotFoundException;
import com.grabfood.menu.repository.MenuRepository;
import org.slf4j.*;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MenuService {
    private static final Logger log = LoggerFactory.getLogger(MenuService.class);
    private final MenuRepository menuRepository;
    public MenuService(MenuRepository menuRepository) { this.menuRepository = menuRepository; }

    @Transactional(readOnly = true)
    @Cacheable(value = "menuCache", key = "#id")
    public Menu getMenuById(Long id) {
        if (id == null) throw new IllegalArgumentException("id không được null");
        log.info("Cache miss - đọc menu id={} từ database", id);
        return menuRepository.findById(id).orElseThrow(() -> new MenuNotFoundException(id));
    }

    @Transactional
    @CacheEvict(value = "menuCache", key = "#menu.id")
    public Menu updateMenu(Menu menu) {
        validate(menu);
        if (!menuRepository.existsById(menu.getId())) throw new MenuNotFoundException(menu.getId());

        // Cache-Aside: ghi và flush DB trước; Spring chỉ evict sau khi method thành công.
        Menu saved = menuRepository.save(menu);
        menuRepository.flush();
        log.info("Đã cập nhật DB menu id={}, price={}; tiếp theo evict menuCache::{}",
                saved.getId(), saved.getPrice(), saved.getId());
        return saved;
    }

    private void validate(Menu menu) {
        if (menu == null || menu.getId() == null) throw new IllegalArgumentException("menu.id không được null");
        if (menu.getDishName() == null || menu.getDishName().isBlank())
            throw new IllegalArgumentException("dish_name không được rỗng");
        if (menu.getPrice() == null || menu.getPrice().signum() < 0)
            throw new IllegalArgumentException("price phải lớn hơn hoặc bằng 0");
    }
}
