package com.gooroomees.neulbomgil_backend.domain.auth.repository;

import com.gooroomees.neulbomgil_backend.domain.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface UserChatRepository  extends JpaRepository<User, Long> {

   // Optional<UserAuth> findFirstByRole(Role role);

    @Query("""
    select cr.roomId
    from ChatRoom cr
    where cr.user.userId = :userId
""")
    Integer findRoomIdByUserId(@Param("userId") Long userId);
}
