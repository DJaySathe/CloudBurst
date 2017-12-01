package com.dssathe.cloudburst.repository;

import com.dssathe.cloudburst.model.VmInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VmInfoRepository extends JpaRepository<VmInfo, Long>{
	 VmInfo findByVmId(String id);
}

