package com.kbouali.demo.repository;

import com.kbouali.demo.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("""
                select t.balance from Transaction t inner join AccountEntity a on t.account.id = a.id
                where a.id = :accountId order by t.dateTime desc limit 1
            """)
    Optional<Integer> findLatestBalanceByAccount(Long accountId);

    @Query("""
                select t from Transaction t inner join AccountEntity a on t.account.id = a.id
                where a.id = :accountId order by t.dateTime desc
            """)
    List<Transaction> findSortedTransactionsByAccount(Long accountId);
}
