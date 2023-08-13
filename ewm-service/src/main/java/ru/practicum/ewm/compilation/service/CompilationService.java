package ru.practicum.ewm.compilation.service;

import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    CompilationDto createCompilation(NewCompilationDto compilationDto);

    void delete(Long compId);

    CompilationDto updateCompilation(UpdateCompilationRequest compilationDto, Long compId);

    List<CompilationDto> getCompilation(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilationById(Long compId);
}
