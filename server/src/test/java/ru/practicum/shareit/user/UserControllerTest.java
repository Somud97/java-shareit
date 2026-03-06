package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ru.practicum.shareit.user.UserController.class)
@Import(ErrorHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;

    @Test
    void create_returnsCreatedUser() throws Exception {
        UserDto request = new UserDto();
        request.setName("Test");
        request.setEmail("test@mail.ru");
        User user = new User();
        user.setId(1L);
        user.setName("Test");
        user.setEmail("test@mail.ru");
        when(userService.create(any(User.class))).thenReturn(user);

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"name\":\"Test\",\"email\":\"test@mail.ru\"}"));

        verify(userService).create(any(User.class));
    }

    @Test
    void update_returnsUpdatedUser() throws Exception {
        UserDto request = new UserDto();
        request.setName("Updated");
        User user = new User();
        user.setId(1L);
        user.setName("Updated");
        user.setEmail("test@mail.ru");
        when(userService.update(eq(1L), any(User.class))).thenReturn(user);

        mvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"name\":\"Updated\",\"email\":\"test@mail.ru\"}"));

        verify(userService).update(eq(1L), any(User.class));
    }

    @Test
    void findAll_returnsListOfUsers() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("user@mail.ru");
        when(userService.findAll()).thenReturn(List.of(user));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"name\":\"User\",\"email\":\"user@mail.ru\"}]"));

        verify(userService).findAll();
    }

    @Test
    void findById_returnsUser() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("user@mail.ru");
        when(userService.findById(1L)).thenReturn(user);

        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"name\":\"User\",\"email\":\"user@mail.ru\"}"));

        verify(userService).findById(1L);
    }

    @Test
    void findById_returns404WhenNotFound() throws Exception {
        when(userService.findById(999L)).thenThrow(new NotFoundException("Пользователь не найден."));

        mvc.perform(get("/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"error\":\"Пользователь не найден.\"}"));

        verify(userService).findById(999L);
    }

    @Test
    void delete_callsServiceAndReturnsOk() throws Exception {
        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService).deleteById(1L);
    }
}
