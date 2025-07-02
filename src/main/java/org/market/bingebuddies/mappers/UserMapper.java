package org.market.bingebuddies.mappers;

import org.mapstruct.Mapper;
import org.market.bingebuddies.domain.security.User;
import org.market.bingebuddies.dtos.UserDTO;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserDTO userDTO);
    UserDTO toUserDTO(User user);
}
