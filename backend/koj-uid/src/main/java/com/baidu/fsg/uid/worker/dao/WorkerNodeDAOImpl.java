package com.baidu.fsg.uid.worker.dao;

import com.baidu.fsg.uid.worker.entity.WorkerNodeEntity;
import com.kairlec.koj.dao.DSLAccess;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.kairlec.koj.dao.tables.UidWorkerNode.UID_WORKER_NODE;

/**
 * @author : Kairlec
 * @since : 2022/1/10
 **/
@Service
public class WorkerNodeDAOImpl implements WorkerNodeDAO {
    private final DSLAccess access;

    public WorkerNodeDAOImpl(DSLAccess access) {
        this.access = access;
    }

    @Override
    public Mono<WorkerNodeEntity> getWorkerNodeByHostPort(@NotNull String host, @NotNull String port) {
        return access.mono(create -> Mono.from(create.select(UID_WORKER_NODE.ID, UID_WORKER_NODE.HOST_NAME, UID_WORKER_NODE.PORT, UID_WORKER_NODE.TYPE, UID_WORKER_NODE.LAUNCH_DATE, UID_WORKER_NODE.UPDATE_TIME, UID_WORKER_NODE.CREATE_TIME)
                        .from(UID_WORKER_NODE)
                        .where(UID_WORKER_NODE.HOST_NAME.eq(host))
                        .and(UID_WORKER_NODE.PORT.eq(port)))
                .map(r -> r.into(WorkerNodeEntity.class)));
    }

    @Override
    public Mono<Integer> addWorkerNode(@NotNull WorkerNodeEntity workerNodeEntity) {
        return access.mono(create -> Mono.from(create.insertInto(UID_WORKER_NODE,
                        UID_WORKER_NODE.HOST_NAME,
                        UID_WORKER_NODE.PORT,
                        UID_WORKER_NODE.TYPE,
                        UID_WORKER_NODE.LAUNCH_DATE
                )
                .values(
                        workerNodeEntity.getHostName(),
                        workerNodeEntity.getPort(),
                        workerNodeEntity.getType(),
                        workerNodeEntity.getLaunchDate()
                )));
    }
}
