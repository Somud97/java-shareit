package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i WHERE i.owner.id = :ownerId")
    Collection<Item> findByOwner(@Param("ownerId") Long ownerId);

    @Query("""
        SELECT i FROM Item i
        WHERE i.available = true
          AND (LOWER(i.name) LIKE LOWER(CONCAT('%', :text, '%'))
               OR LOWER(i.description) LIKE LOWER(CONCAT('%', :text, '%')))
        """)
    Collection<Item> searchByText(@Param("text") String text);

    @Query("SELECT i FROM Item i WHERE i.request.id = :requestId ORDER BY i.id ASC")
    Collection<Item> findByRequest_Id(@Param("requestId") Long requestId);
}
