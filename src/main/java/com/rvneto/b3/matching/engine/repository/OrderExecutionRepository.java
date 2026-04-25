package com.rvneto.b3.matching.engine.repository;

import com.rvneto.b3.matching.engine.model.OrderExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderExecutionRepository extends JpaRepository<OrderExecution, Long> {

}
