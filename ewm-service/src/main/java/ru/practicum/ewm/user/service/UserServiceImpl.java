package ru.practicum.ewm.user.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.pageable.FromSizePageable;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.storage.UserRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    private final UserMapper userMapper;

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
        if (userMapper.userDtoToUser(userDto) == null) {
            return null;
        }
        return userMapper.userToUserDto(repository.save(userMapper.userDtoToUser(userDto)));
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        FromSizePageable page = FromSizePageable.of(from, size, Sort.by("id"));
        if (ids == null) {
            return repository.findAll(page).stream()
                    .map(e -> userMapper.userToUserDto(e))
                    .collect(Collectors.toList());
        } else {
            return repository.findByIdIn(ids, page).stream()
                    .map(e -> userMapper.userToUserDto(e))
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
