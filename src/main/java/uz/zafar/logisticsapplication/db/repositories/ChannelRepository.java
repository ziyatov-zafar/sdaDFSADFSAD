package uz.zafar.logisticsapplication.db.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uz.zafar.logisticsapplication.db.domain.Channel;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChannelRepository extends CrudRepository<Channel, Long> {

    Optional<Channel> getChannelById(long id);

    Page<Channel> findAllByOrderById(Pageable pageable);

    List<Channel> findAllByActiveIsTrueAndStatusOrderById(String status);
    Channel findByStatus(String status);
}
