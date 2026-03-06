package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookerShortDto;
import ru.practicum.shareit.booking.dto.ItemShortDto;
import ru.practicum.shareit.exception.NotFoundException;

import java.time.LocalDateTime;
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

@WebMvcTest(ru.practicum.shareit.booking.BookingController.class)
@Import(ErrorHandler.class)
class BookingControllerTest {

    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingService;

    @Test
    void create_returnsBooking() throws Exception {
        BookingRequestDto request = new BookingRequestDto();
        request.setItemId(1L);
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(2));
        BookingDto response = new BookingDto();
        response.setId(1L);
        response.setStatus(BookingStatus.WAITING);
        when(bookingService.create(eq(1L), any(BookingRequestDto.class))).thenReturn(response);

        mvc.perform(post("/bookings")
                        .header(USER_HEADER, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"status\":\"WAITING\"}"));

        verify(bookingService).create(eq(1L), any(BookingRequestDto.class));
    }

    @Test
    void approve_returnsUpdatedBooking() throws Exception {
        BookingDto response = new BookingDto();
        response.setId(1L);
        response.setStatus(BookingStatus.APPROVED);
        when(bookingService.approve(1L, 1L, true)).thenReturn(response);

        mvc.perform(patch("/bookings/1").param("approved", "true").header(USER_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"status\":\"APPROVED\"}"));

        verify(bookingService).approve(1L, 1L, true);
    }

    @Test
    void getById_returnsBooking() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStatus(BookingStatus.APPROVED);
        BookerShortDto booker = new BookerShortDto();
        booker.setId(2L);
        dto.setBooker(booker);
        ItemShortDto item = new ItemShortDto();
        item.setId(1L);
        item.setName("Дрель");
        dto.setItem(item);
        when(bookingService.getById(1L, 1L)).thenReturn(dto);

        mvc.perform(get("/bookings/1").header(USER_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"status\":\"APPROVED\",\"booker\":{\"id\":2},\"item\":{\"id\":1,\"name\":\"Дрель\"}}"));

        verify(bookingService).getById(1L, 1L);
    }

    @Test
    void getById_returns404WhenNotFound() throws Exception {
        when(bookingService.getById(1L, 999L)).thenThrow(new NotFoundException("Бронирование не найдено."));

        mvc.perform(get("/bookings/999").header(USER_HEADER, 1))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"error\":\"Бронирование не найдено.\"}"));

        verify(bookingService).getById(1L, 999L);
    }

    @Test
    void getForBooker_returnsList() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        when(bookingService.getForBooker(1L, BookingState.ALL)).thenReturn(List.of(dto));

        mvc.perform(get("/bookings").header(USER_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1}]"));

        verify(bookingService).getForBooker(1L, BookingState.ALL);
    }

    @Test
    void getForBooker_withStateParam() throws Exception {
        when(bookingService.getForBooker(1L, BookingState.CURRENT)).thenReturn(List.of());

        mvc.perform(get("/bookings").param("state", "CURRENT").header(USER_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(bookingService).getForBooker(1L, BookingState.CURRENT);
    }

    @Test
    void getForOwner_returnsList() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        when(bookingService.getForOwner(1L, BookingState.ALL)).thenReturn(List.of(dto));

        mvc.perform(get("/bookings/owner").header(USER_HEADER, 1))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1}]"));

        verify(bookingService).getForOwner(1L, BookingState.ALL);
    }
}
