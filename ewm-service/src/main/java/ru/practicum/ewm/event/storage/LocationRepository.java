package ru.practicum.ewm.event.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.event.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long>  {
}
