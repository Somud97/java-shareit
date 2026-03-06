package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Collection<Booking> findAllByBookerIdOrderByStartDesc(Long bookerId);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.booker.id = :userId
          AND b.start <= :now AND b.end >= :now
        ORDER BY b.start DESC
        """)
    Collection<Booking> findCurrentByBooker(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.booker.id = :userId AND b.end < :now
        ORDER BY b.start DESC
        """)
    Collection<Booking> findPastByBooker(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.booker.id = :userId AND b.start > :now
        ORDER BY b.start DESC
        """)
    Collection<Booking> findFutureByBooker(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.booker.id = :userId AND b.status = :status
        ORDER BY b.start DESC
        """)
    Collection<Booking> findByBookerAndStatus(@Param("userId") Long userId, @Param("status") BookingStatus status);

    Collection<Booking> findAllByItem_Owner_IdOrderByStartDesc(Long ownerId);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.item.owner.id = :ownerId
          AND b.start <= :now AND b.end >= :now
        ORDER BY b.start DESC
        """)
    Collection<Booking> findCurrentByOwner(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.item.owner.id = :ownerId AND b.end < :now
        ORDER BY b.start DESC
        """)
    Collection<Booking> findPastByOwner(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.item.owner.id = :ownerId AND b.start > :now
        ORDER BY b.start DESC
        """)
    Collection<Booking> findFutureByOwner(@Param("ownerId") Long ownerId, @Param("now") LocalDateTime now);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.item.owner.id = :ownerId AND b.status = :status
        ORDER BY b.start DESC
        """)
    Collection<Booking> findByOwnerAndStatus(@Param("ownerId") Long ownerId, @Param("status") BookingStatus status);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.item.id = :itemId
          AND b.start <= :now AND b.status = :status
        ORDER BY b.start DESC
        """)
    Booking findLastBooking(@Param("itemId") Long itemId,
                            @Param("now") LocalDateTime now,
                            @Param("status") BookingStatus status);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.item.id = :itemId
          AND b.start > :now AND b.status = :status
        ORDER BY b.start ASC
        """)
    Booking findNextBooking(@Param("itemId") Long itemId,
                            @Param("now") LocalDateTime now,
                            @Param("status") BookingStatus status);

    @Query("""
        SELECT COUNT(b) FROM Booking b
        WHERE b.item.id = :itemId
          AND b.booker.id = :bookerId
          AND b.end < :before
          AND b.status = :status
        """)
    long countCompletedBooking(@Param("itemId") Long itemId,
                               @Param("bookerId") Long bookerId,
                               @Param("before") LocalDateTime before,
                               @Param("status") BookingStatus status);

    List<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);
}
