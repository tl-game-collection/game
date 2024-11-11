package com.xiuxiu.app.server;

import com.xiuxiu.core.utils.StringUtil;
import org.redisson.api.*;
import org.redisson.client.codec.Codec;
import org.redisson.config.Config;

public class Redisson extends org.redisson.Redisson {
    protected String keyPreFix;

    public static RedissonClient create(Config config) {
        Redisson redisson = new Redisson(config);
        if (config.isReferenceEnabled()) {
            redisson.enableRedissonReferenceSupport();
        }
        return redisson;
    }


    protected Redisson(Config config) {
        super(config);
    }

    @Override
    public <K, V> RStream<K, V> getStream(String name) {
        return super.getStream(this.getKey(name));
    }

    @Override
    public <K, V> RStream<K, V> getStream(String name, Codec codec) {
        return super.getStream(this.getKey(name), codec);
    }

    @Override
    public RBinaryStream getBinaryStream(String name) {
        return super.getBinaryStream(this.getKey(name));
    }

    @Override
    public <V> RGeo<V> getGeo(String name) {
        return super.getGeo(this.getKey(name));
    }

    @Override
    public <V> RGeo<V> getGeo(String name, Codec codec) {
        return super.getGeo(this.getKey(name), codec);
    }

    @Override
    public <V> RBucket<V> getBucket(String name) {
        return super.getBucket(this.getKey(name));
    }

    @Override
    public RRateLimiter getRateLimiter(String name) {
        return super.getRateLimiter(this.getKey(name));
    }

    @Override
    public <V> RBucket<V> getBucket(String name, Codec codec) {
        return super.getBucket(this.getKey(name), codec);
    }

    @Override
    public <V> RHyperLogLog<V> getHyperLogLog(String name) {
        return super.getHyperLogLog(this.getKey(name));
    }

    @Override
    public <V> RHyperLogLog<V> getHyperLogLog(String name, Codec codec) {
        return super.getHyperLogLog(this.getKey(name), codec);
    }

    @Override
    public <V> RList<V> getList(String name) {
        return super.getList(this.getKey(name));
    }

    @Override
    public <V> RList<V> getList(String name, Codec codec) {
        return super.getList(this.getKey(name), codec);
    }

    @Override
    public <K, V> RListMultimap<K, V> getListMultimap(String name) {
        return super.getListMultimap(this.getKey(name));
    }

    @Override
    public <K, V> RListMultimap<K, V> getListMultimap(String name, Codec codec) {
        return super.getListMultimap(this.getKey(name), codec);
    }

    @Override
    public <K, V> RLocalCachedMap<K, V> getLocalCachedMap(String name, LocalCachedMapOptions<K, V> options) {
        return super.getLocalCachedMap(this.getKey(name), options);
    }

    @Override
    public <K, V> RLocalCachedMap<K, V> getLocalCachedMap(String name, Codec codec, LocalCachedMapOptions<K, V> options) {
        return super.getLocalCachedMap(this.getKey(name), codec, options);
    }

    @Override
    public <K, V> RMap<K, V> getMap(String name) {
        return super.getMap(this.getKey(name));
    }

    @Override
    public <K, V> RMap<K, V> getMap(String name, MapOptions<K, V> options) {
        return super.getMap(this.getKey(name), options);
    }

    @Override
    public <K, V> RSetMultimap<K, V> getSetMultimap(String name) {
        return super.getSetMultimap(this.getKey(name));
    }

    @Override
    public <K, V> RSetMultimapCache<K, V> getSetMultimapCache(String name) {
        return super.getSetMultimapCache(this.getKey(name));
    }

    @Override
    public <K, V> RSetMultimapCache<K, V> getSetMultimapCache(String name, Codec codec) {
        return super.getSetMultimapCache(this.getKey(name), codec);
    }

    @Override
    public <K, V> RListMultimapCache<K, V> getListMultimapCache(String name) {
        return super.getListMultimapCache(this.getKey(name));
    }

    @Override
    public <K, V> RListMultimapCache<K, V> getListMultimapCache(String name, Codec codec) {
        return super.getListMultimapCache(this.getKey(name), codec);
    }

    @Override
    public <K, V> RSetMultimap<K, V> getSetMultimap(String name, Codec codec) {
        return super.getSetMultimap(this.getKey(name), codec);
    }

    @Override
    public <V> RSetCache<V> getSetCache(String name) {
        return super.getSetCache(this.getKey(name));
    }

    @Override
    public <V> RSetCache<V> getSetCache(String name, Codec codec) {
        return super.getSetCache(this.getKey(name), codec);
    }

    @Override
    public <K, V> RMapCache<K, V> getMapCache(String name) {
        return super.getMapCache(this.getKey(name));
    }

    @Override
    public <K, V> RMapCache<K, V> getMapCache(String name, MapOptions<K, V> options) {
        return super.getMapCache(this.getKey(name), options);
    }

    @Override
    public <K, V> RMapCache<K, V> getMapCache(String name, Codec codec) {
        return super.getMapCache(this.getKey(name), codec);
    }

    @Override
    public <K, V> RMapCache<K, V> getMapCache(String name, Codec codec, MapOptions<K, V> options) {
        return super.getMapCache(this.getKey(name), codec, options);
    }

    @Override
    public <K, V> RMap<K, V> getMap(String name, Codec codec) {
        return super.getMap(this.getKey(name), codec);
    }

    @Override
    public <K, V> RMap<K, V> getMap(String name, Codec codec, MapOptions<K, V> options) {
        return super.getMap(this.getKey(name), codec, options);
    }

    @Override
    public RLock getLock(String name) {
        return super.getLock(this.getKey(name));
    }

    @Override
    public RLock getFairLock(String name) {
        return super.getFairLock(this.getKey(name));
    }

    @Override
    public RReadWriteLock getReadWriteLock(String name) {
        return super.getReadWriteLock(this.getKey(name));
    }

    @Override
    public <V> RSet<V> getSet(String name) {
        return super.getSet(this.getKey(name));
    }

    @Override
    public <V> RSet<V> getSet(String name, Codec codec) {
        return super.getSet(this.getKey(name), codec);
    }

    @Override
    public RScheduledExecutorService getExecutorService(String name) {
        return super.getExecutorService(this.getKey(name));
    }

    @Override
    public RScheduledExecutorService getExecutorService(String name, ExecutorOptions options) {
        return super.getExecutorService(this.getKey(name), options);
    }

    @Override
    public RScheduledExecutorService getExecutorService(String name, Codec codec) {
        return super.getExecutorService(this.getKey(name), codec);
    }

    @Override
    public RScheduledExecutorService getExecutorService(String name, Codec codec, ExecutorOptions options) {
        return super.getExecutorService(this.getKey(name), codec, options);
    }

    @Override
    public RRemoteService getRemoteService(String name) {
        return super.getRemoteService(this.getKey(name));
    }

    @Override
    public RRemoteService getRemoteService(String name, Codec codec) {
        return super.getRemoteService(this.getKey(name), codec);
    }

    @Override
    public <V> RSortedSet<V> getSortedSet(String name) {
        return super.getSortedSet(this.getKey(name));
    }

    @Override
    public <V> RSortedSet<V> getSortedSet(String name, Codec codec) {
        return super.getSortedSet(this.getKey(name), codec);
    }

    @Override
    public <V> RScoredSortedSet<V> getScoredSortedSet(String name) {
        return super.getScoredSortedSet(this.getKey(name));
    }

    @Override
    public <V> RScoredSortedSet<V> getScoredSortedSet(String name, Codec codec) {
        return super.getScoredSortedSet(this.getKey(name), codec);
    }

    @Override
    public RLexSortedSet getLexSortedSet(String name) {
        return super.getLexSortedSet(this.getKey(name));
    }

    @Override
    public <M> RTopic<M> getTopic(String name) {
        return super.getTopic(this.getKey(name));
    }

    @Override
    public <M> RTopic<M> getTopic(String name, Codec codec) {
        return super.getTopic(this.getKey(name), codec);
    }

    @Override
    public <M> RPatternTopic<M> getPatternTopic(String pattern) {
        return super.getPatternTopic(this.getKey(pattern));
    }

    @Override
    public <M> RPatternTopic<M> getPatternTopic(String pattern, Codec codec) {
        return super.getPatternTopic(this.getKey(pattern), codec);
    }

    @Override
    public <V> RQueue<V> getQueue(String name) {
        return super.getQueue(this.getKey(name));
    }

    @Override
    public <V> RQueue<V> getQueue(String name, Codec codec) {
        return super.getQueue(this.getKey(name), codec);
    }

    @Override
    public <V> RBlockingQueue<V> getBlockingQueue(String name) {
        return super.getBlockingQueue(this.getKey(name));
    }

    @Override
    public <V> RBlockingQueue<V> getBlockingQueue(String name, Codec codec) {
        return super.getBlockingQueue(this.getKey(name), codec);
    }

    @Override
    public <V> RBoundedBlockingQueue<V> getBoundedBlockingQueue(String name) {
        return super.getBoundedBlockingQueue(this.getKey(name));
    }

    @Override
    public <V> RBoundedBlockingQueue<V> getBoundedBlockingQueue(String name, Codec codec) {
        return super.getBoundedBlockingQueue(this.getKey(name), codec);
    }

    @Override
    public <V> RDeque<V> getDeque(String name) {
        return super.getDeque(this.getKey(name));
    }

    @Override
    public <V> RDeque<V> getDeque(String name, Codec codec) {
        return super.getDeque(this.getKey(name), codec);
    }

    @Override
    public <V> RBlockingDeque<V> getBlockingDeque(String name) {
        return super.getBlockingDeque(this.getKey(name));
    }

    @Override
    public <V> RBlockingDeque<V> getBlockingDeque(String name, Codec codec) {
        return super.getBlockingDeque(this.getKey(name), codec);
    }

    @Override
    public RAtomicLong getAtomicLong(String name) {
        return super.getAtomicLong(this.getKey(name));
    }

    @Override
    public RLongAdder getLongAdder(String name) {
        return super.getLongAdder(this.getKey(name));
    }

    @Override
    public RDoubleAdder getDoubleAdder(String name) {
        return super.getDoubleAdder(this.getKey(name));
    }

    @Override
    public RAtomicDouble getAtomicDouble(String name) {
        return super.getAtomicDouble(this.getKey(name));
    }

    @Override
    public RCountDownLatch getCountDownLatch(String name) {
        return super.getCountDownLatch(this.getKey(name));
    }

    @Override
    public RBitSet getBitSet(String name) {
        return super.getBitSet(this.getKey(name));
    }

    @Override
    public RSemaphore getSemaphore(String name) {
        return super.getSemaphore(this.getKey(name));
    }

    @Override
    public RPermitExpirableSemaphore getPermitExpirableSemaphore(String name) {
        return super.getPermitExpirableSemaphore(this.getKey(name));
    }

    @Override
    public <V> RBloomFilter<V> getBloomFilter(String name) {
        return super.getBloomFilter(this.getKey(name));
    }

    @Override
    public <V> RBloomFilter<V> getBloomFilter(String name, Codec codec) {
        return super.getBloomFilter(this.getKey(name), codec);
    }

    @Override
    public <V> RPriorityQueue<V> getPriorityQueue(String name) {
        return super.getPriorityQueue(this.getKey(name));
    }

    @Override
    public <V> RPriorityQueue<V> getPriorityQueue(String name, Codec codec) {
        return super.getPriorityQueue(this.getKey(name), codec);
    }

    @Override
    public <V> RPriorityBlockingQueue<V> getPriorityBlockingQueue(String name) {
        return super.getPriorityBlockingQueue(this.getKey(name));
    }

    @Override
    public <V> RPriorityBlockingQueue<V> getPriorityBlockingQueue(String name, Codec codec) {
        return super.getPriorityBlockingQueue(this.getKey(name), codec);
    }

    @Override
    public <V> RPriorityBlockingDeque<V> getPriorityBlockingDeque(String name) {
        return super.getPriorityBlockingDeque(this.getKey(name));
    }

    @Override
    public <V> RPriorityBlockingDeque<V> getPriorityBlockingDeque(String name, Codec codec) {
        return super.getPriorityBlockingDeque(this.getKey(name), codec);
    }

    @Override
    public <V> RPriorityDeque<V> getPriorityDeque(String name) {
        return super.getPriorityDeque(this.getKey(name));
    }

    @Override
    public <V> RPriorityDeque<V> getPriorityDeque(String name, Codec codec) {
        return super.getPriorityDeque(this.getKey(name), codec);
    }

    protected String getKey(String key) {
        if (StringUtil.isEmptyOrNull(this.keyPreFix)) {
            return key;
        }
        return this.keyPreFix + "_" + key;
    }

    public void setKeyPreFix(String keyPreFix) {
        this.keyPreFix = keyPreFix;
    }
}
