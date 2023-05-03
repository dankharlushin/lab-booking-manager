package ru.github.dankharlushin.lbmlib.data.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.github.dankharlushin.lbmlib.data.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findUserByOsUsername(final String osUsername);

    User findUserByChatId(final Long chatId);

    @Query("select u from User u")
    Slice<User> streamAll(final Pageable page);

    boolean existsByOsUsername(final String osUsername);

    void deleteByOsUsername(final String osUsername);
}
