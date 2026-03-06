package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ru.practicum.shareit.item.ItemController.class)
@Import(ErrorHandler.class)
class ItemControllerTest {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;

    @Test
    void create_returnsCreatedItem() throws Exception {
        ItemDto request = new ItemDto();
        request.setName("Дрель");
        request.setDescription("Аккумуляторная");
        request.setAvailable(true);
        ItemDto createdDto = new ItemDto();
        createdDto.setId(1L);
        createdDto.setName("Дрель");
        createdDto.setDescription("Аккумуляторная");
        createdDto.setAvailable(true);
        when(itemService.create(eq(1L), any(Item.class), eq(null))).thenReturn(createdDto);

        mvc.perform(post("/items")
                        .header(USER_HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"name\":\"Дрель\",\"description\":\"Аккумуляторная\",\"available\":true}"));

        verify(itemService).create(eq(1L), any(Item.class), eq(null));
    }

    @Test
    void update_returnsUpdatedItem() throws Exception {
        ItemDto request = new ItemDto();
        request.setName("Обновлённая дрель");
        ItemDto updatedDto = new ItemDto();
        updatedDto.setId(1L);
        updatedDto.setName("Обновлённая дрель");
        updatedDto.setDescription("Аккумуляторная");
        updatedDto.setAvailable(true);
        when(itemService.update(eq(1L), eq(1L), any(Item.class))).thenReturn(updatedDto);

        mvc.perform(patch("/items/1")
                        .header(USER_HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"name\":\"Обновлённая дрель\",\"available\":true}"));

        verify(itemService).update(eq(1L), eq(1L), any(Item.class));
    }

    @Test
    void findById_returnsItemWithDetails() throws Exception {
        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setName("Дрель");
        dto.setDescription("Описание");
        dto.setAvailable(true);
        dto.setComments(List.of());
        when(itemService.getItemWithDetails(1L, 1L)).thenReturn(dto);

        mvc.perform(get("/items/1").header(USER_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"name\":\"Дрель\",\"description\":\"Описание\",\"available\":true,\"comments\":[]}"));

        verify(itemService).getItemWithDetails(1L, 1L);
    }

    @Test
    void findById_returns404WhenNotFound() throws Exception {
        when(itemService.getItemWithDetails(1L, 999L))
                .thenThrow(new NotFoundException("вещь не найдена"));

        mvc.perform(get("/items/999").header(USER_HEADER, 1))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"error\":\"вещь не найдена\"}"));

        verify(itemService).getItemWithDetails(1L, 999L);
    }

    @Test
    void findByOwner_returnsItems() throws Exception {
        ItemDto dto = new ItemDto();
        dto.setId(1L);
        dto.setName("Дрель");
        dto.setComments(List.of());
        when(itemService.getItemsWithDetailsForOwner(1L)).thenReturn(List.of(dto));

        mvc.perform(get("/items").header(USER_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"name\":\"Дрель\",\"comments\":[]}]"));

        verify(itemService).getItemsWithDetailsForOwner(1L);
    }

    @Test
    void search_returnsItems() throws Exception {
        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Описание");
        item.setAvailable(true);
        when(itemService.search("дрель")).thenReturn(List.of(item));

        mvc.perform(get("/items/search").param("text", "дрель").header(USER_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"name\":\"Дрель\",\"description\":\"Описание\",\"available\":true}]"));

        verify(itemService).search("дрель");
    }

    @Test
    void addComment_returnsComment() throws Exception {
        CommentDto request = new CommentDto();
        request.setText("Отличная вещь!");
        CommentDto response = new CommentDto();
        response.setId(1L);
        response.setText("Отличная вещь!");
        when(itemService.addComment(eq(1L), eq(1L), any(CommentDto.class))).thenReturn(response);

        mvc.perform(post("/items/1/comment")
                        .header(USER_HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"text\":\"Отличная вещь!\"}"));

        verify(itemService).addComment(eq(1L), eq(1L), any(CommentDto.class));
    }
}
