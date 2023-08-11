package ru.practicum.ewm.compilation.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.model.CompilationEvents;

import java.util.List;

public interface CompilationEventsRepository extends JpaRepository<CompilationEvents, Long> {
    List<CompilationEvents> findByCompilation(Compilation compilation);


}
