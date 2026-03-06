package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ActiveProfiles("test")
class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> bookingJson;
    @Autowired
    private JacksonTester<BookingRequestDto> requestJson;

    @Test
    void bookingRequestDto_serializesAndDeserializesLocalDateTime() throws Exception {
        LocalDateTime start = LocalDateTime.of(2025, 6, 15, 10, 0);
        LocalDateTime end = LocalDateTime.of(2025, 6, 20, 12, 0);
        BookingRequestDto dto = new BookingRequestDto();
        dto.setItemId(1L);
        dto.setStart(start);
        dto.setEnd(end);

        String json = requestJson.write(dto).getJson();
        assertThat(json).contains("2025-06-15");
        assertThat(json).contains("2025-06-20");
        assertThat(json).contains("\"itemId\":1");

        BookingRequestDto parsed = requestJson.parse(json).getObject();
        assertThat(parsed.getItemId()).isEqualTo(1L);
        assertThat(parsed.getStart()).isEqualTo(start);
        assertThat(parsed.getEnd()).isEqualTo(end);
    }

    @Test
    void bookingDto_serializesAndDeserializesWithStatus() throws Exception {
        LocalDateTime start = LocalDateTime.of(2025, 6, 15, 10, 0);
        LocalDateTime end = LocalDateTime.of(2025, 6, 20, 12, 0);
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStart(start);
        dto.setEnd(end);
        dto.setStatus(BookingStatus.WAITING);
        BookerShortDto booker = new BookerShortDto();
        booker.setId(2L);
        dto.setBooker(booker);
        ItemShortDto item = new ItemShortDto();
        item.setId(1L);
        item.setName("Дрель");
        dto.setItem(item);

        String json = bookingJson.write(dto).getJson();
        assertThat(json).contains("\"status\":\"WAITING\"");
        assertThat(json).contains("2025-06-15");
        assertThat(json).contains("\"booker\":{\"id\":2}");
        assertThat(json).contains("\"item\":{\"id\":1,\"name\":\"Дрель\"}");

        BookingDto parsed = bookingJson.parse(json).getObject();
        assertThat(parsed.getId()).isEqualTo(1L);
        assertThat(parsed.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(parsed.getBooker().getId()).isEqualTo(2L);
        assertThat(parsed.getItem().getName()).isEqualTo("Дрель");
    }
}
