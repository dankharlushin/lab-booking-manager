package ru.github.dankharlushin.lbmlib.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.github.dankharlushin.lbmlib.database.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
}
