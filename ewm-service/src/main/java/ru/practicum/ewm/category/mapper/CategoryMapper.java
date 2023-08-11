package ru.practicum.ewm.category.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.model.Category;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {
    Category categoryDtoToCategory(CategoryDto category);

    CategoryDto categoryToCategoryDto(Category category);
}
