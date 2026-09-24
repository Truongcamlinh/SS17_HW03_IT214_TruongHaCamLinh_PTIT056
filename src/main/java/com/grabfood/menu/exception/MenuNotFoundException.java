package com.grabfood.menu.exception;

public class MenuNotFoundException extends RuntimeException {
    public MenuNotFoundException(Long id) { super("Không tìm thấy menu id=" + id); }
}
