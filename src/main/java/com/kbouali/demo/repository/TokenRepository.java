package com.kbouali.demo.repository;

import com.kbouali.demo.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {

    @Query("""
    select t from Token t inner join AccountEntity a on t.account.id = a.id
    where a.id = :accountId and (t.expired = false or t.revoked = false)
""")
    List<Token> findAllValidTokensByUser(Long accountId);

    Optional<Token> findByToken(String token);
}
