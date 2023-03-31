package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Long> {

  User findByUsername(String username);
  User findByEmail(String email);
  User findUserById(Long id);
  User findUserByToken(String token);
  @Transactional
  @Modifying
  @Query("UPDATE User u SET u.points = :points WHERE u.id = :id")
  void updatePoints(@Param("points") long points, @Param("id") Long id);
}
