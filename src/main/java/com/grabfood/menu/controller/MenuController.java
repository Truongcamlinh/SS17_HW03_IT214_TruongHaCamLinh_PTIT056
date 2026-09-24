package com.grabfood.menu.controller;

import com.grabfood.menu.entity.Menu;
import com.grabfood.menu.service.MenuService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/menu")
public class MenuController {
    private final MenuService service;
    public MenuController(MenuService service) { this.service = service; }
    @GetMapping("/{id}") Menu get(@PathVariable Long id) { return service.getMenuById(id); }
    @PutMapping Menu update(@Valid @RequestBody Menu menu) { return service.updateMenu(menu); }
}
