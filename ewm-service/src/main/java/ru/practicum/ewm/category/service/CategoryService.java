package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.CategoryDto;
import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CategoryDto category);

    void delete(Long id);

    CategoryDto updateCategory(CategoryDto categoryDto, Long id);

    List<CategoryDto> getСategories(Integer from, Integer size);

    CategoryDto getСategoryById(Long catId);
}
