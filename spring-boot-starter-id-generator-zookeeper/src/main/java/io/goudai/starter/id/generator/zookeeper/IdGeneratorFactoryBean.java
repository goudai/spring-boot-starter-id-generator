package io.goudai.starter.id.generator.zookeeper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.apache.zookeeper.CreateMode.EPHEMERAL;
import static org.apache.zookeeper.Watcher.Event.KeeperState.SyncConnected;
import static org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE;

/**
 * Created by freeman on 17/2/15.
 */
@Slf4j
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IdGeneratorFactoryBean implements FactoryBean<IdGenerator>, InitializingBean, DisposableBean {

    private String zkAddress;
    private String root;

    private int sessionTimeout;
    private long connectTimeout;

    private ZooKeeper zooKeeper;
    private IdGenerator idGenerator;


    @Override
    public void afterPropertiesSet() throws Exception {
        Objects.requireNonNull(zkAddress);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        zooKeeper = new ZooKeeper(zkAddress, sessionTimeout, event -> {
            if (event.getState() == SyncConnected) {
                countDownLatch.countDown();
            }
        });
        boolean await = countDownLatch.await(connectTimeout, TimeUnit.SECONDS);
        if (await) {
            log.info("connected zookeeper success ~");
        } else {
            throw new Exception("connected zookeeper failed ~");
        }

        Stat exists = zooKeeper.exists(root, false);
        if (exists == null) {
            zooKeeper.create(root, new byte[0], OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        long index = 0;
        for (; ; ) {
            String path = root + "/" + index;
            exists = zooKeeper.exists(path, false);
            if (exists == null) {
                zooKeeper.create(path, new byte[0], OPEN_ACL_UNSAFE, EPHEMERAL);
                break;
            } else {
                log.info(String.format("work id %s existed in zookeeper storage", index));
            }
            index = index + 1;
        }
        this.idGenerator = new IdGenerator(index % 1024 % 32, (index % 1024) / 32);
        log.info(String.format("id generator created success at %s ,workId : %s dataCenterId: %s ", this.idGenerator, index % 1024 % 32, (index % 1024) / 32));
    }

    @Override
    public void destroy() throws Exception {
        zooKeeper.close();
    }

    @Override
    public IdGenerator getObject() throws Exception {
        return idGenerator;
    }

    @Override
    public Class<IdGenerator> getObjectType() {
        return IdGenerator.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }


}
