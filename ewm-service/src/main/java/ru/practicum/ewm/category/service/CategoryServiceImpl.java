package ru.practicum.ewm.category.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.storage.CategoryRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.pageable.FromSizePageable;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;

    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository repository, CategoryMapper categoryMapper) {
        this.repository = repository;
        this.categoryMapper = categoryMapper;
    }

    @Transactional
    @Override
    public CategoryDto createCategory(CategoryDto category) {
        if (categoryMapper.categoryDtoToCategory(category) == null) {
            return null;
        }
        return categoryMapper.categoryToCategoryDto(repository.save(categoryMapper.categoryDtoToCategory(category)));
    }

    @Transactional
    @Override
    public void delete(Long id) {
        Category category = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Категория с ид %s не найдена", id)));
        repository.deleteById(id);
    }

    @Transactional
    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto, Long id) {
        Category category = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Категория с ид %s не найдена", id)));
        category.setName(categoryDto.getName());
        return categoryMapper.categoryToCategoryDto(category);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryDto> getСategories(Integer from, Integer size) {
        FromSizePageable page = FromSizePageable.of(from, size, Sort.unsorted());
        return repository.findAll(page).stream()
                .map(e -> categoryMapper.categoryToCategoryDto(e))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public CategoryDto getСategoryById(Long catId) {
        Category category = repository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Категория с ид %s не найдена", catId)));
        return categoryMapper.categoryToCategoryDto(category);
    }
}
