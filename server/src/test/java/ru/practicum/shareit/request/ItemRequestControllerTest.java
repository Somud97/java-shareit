package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ru.practicum.shareit.request.ItemRequestController.class)
@Import(ErrorHandler.class)
class ItemRequestControllerTest {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void create_returnsCreatedRequest() throws Exception {
        ItemRequestDto request = new ItemRequestDto();
        request.setDescription("Нужна дрель");
        ItemRequestDto response = new ItemRequestDto();
        response.setId(1L);
        response.setDescription("Нужна дрель");
        response.setCreated(LocalDateTime.now());
        response.setItems(List.of());
        when(itemRequestService.create(eq(1L), any(ItemRequestDto.class))).thenReturn(response);

        mvc.perform(post("/requests")
                        .header(USER_HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"description\":\"Нужна дрель\",\"items\":[]}"));

        verify(itemRequestService).create(eq(1L), any(ItemRequestDto.class));
    }

    @Test
    void getMyRequests_returnsList() throws Exception {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);
        dto.setDescription("Нужна дрель");
        dto.setItems(List.of());
        when(itemRequestService.getMyRequests(1L)).thenReturn(List.of(dto));

        mvc.perform(get("/requests").header(USER_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"description\":\"Нужна дрель\",\"items\":[]}]"));

        verify(itemRequestService).getMyRequests(1L);
    }

    @Test
    void getAllOtherRequests_returnsList() throws Exception {
        when(itemRequestService.getAllOtherRequests(1L)).thenReturn(List.of());

        mvc.perform(get("/requests/all").header(USER_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(itemRequestService).getAllOtherRequests(1L);
    }

    @Test
    void getById_returnsRequest() throws Exception {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);
        dto.setDescription("Нужна дрель");
        dto.setItems(List.of());
        when(itemRequestService.getById(1L, 1L)).thenReturn(dto);

        mvc.perform(get("/requests/1").header(USER_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"description\":\"Нужна дрель\",\"items\":[]}"));

        verify(itemRequestService).getById(1L, 1L);
    }

    @Test
    void getById_returns404WhenNotFound() throws Exception {
        when(itemRequestService.getById(1L, 999L)).thenThrow(new NotFoundException("Запрос не найден."));

        mvc.perform(get("/requests/999").header(USER_HEADER, 1))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"error\":\"Запрос не найден.\"}"));

        verify(itemRequestService).getById(1L, 999L);
    }
}
