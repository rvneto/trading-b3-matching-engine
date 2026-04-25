package com.rvneto.b3.matching.engine.repository;

import com.rvneto.b3.matching.engine.model.ExecutionStatus;
import com.rvneto.b3.matching.engine.model.OrderExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderExecutionRepository extends JpaRepository<OrderExecution, Long> {

    Optional<OrderExecution> findByOrderId(String orderId);

    List<OrderExecution> findByTicker(String ticker);

    List<OrderExecution> findByStatus(ExecutionStatus status);

}
