package com.example.project2.repository;

import com.example.project2.model.ServerPool;
import org.springframework.data.aerospike.repository.AerospikeRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServersPoolRepository extends AerospikeRepository<ServerPool,Integer> {
}
