package ru.practicum.ewm.category;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/categories")
@Slf4j
public class AdminCategoryController {
    private final CategoryService categoryService;

    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody @Valid CategoryDto category) {
        log.info("Начинаем добавлять категорию: {}", category);
        CategoryDto categoryDto = categoryService.createCategory(category);
        log.info("Категория добавлена: {}", category);
        return categoryDto;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        log.info("Удаление категории с ид {}", id);
        categoryService.delete(id);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto updateUser(@RequestBody @Valid CategoryDto category, @PathVariable Long id) {
        log.info("Начинаем изменять категорию: {}", category);
        CategoryDto categoryDto = categoryService.updateCategory(category, id);
        log.info("Категория изменен: {}", category);
        return categoryDto;
    }
}
