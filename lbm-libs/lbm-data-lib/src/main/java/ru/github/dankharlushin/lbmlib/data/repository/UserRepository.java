package ru.github.dankharlushin.lbmlib.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.github.dankharlushin.lbmlib.data.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findUserByOsUsername(final String osUsername);
}
