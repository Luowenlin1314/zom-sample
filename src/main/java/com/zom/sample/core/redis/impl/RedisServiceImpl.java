package com.zom.sample.core.redis.impl;

import com.zom.sample.core.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * 当事务没有开启，redisTemplate.execute会主动释放连接，
 * 当事务支持开启，当事务结束后，需要调用xxx释放连接
 * TODO
 * 批量操作使用管道技术 redisTemplate.executePipelined()

 */
@Service(value = "redisService")
public class RedisServiceImpl implements RedisService {

    private static String redisCode = "utf-8";

    private RedisTemplate<String, String> redisTemplate;

    /**
     * @param keys
     */
    public long del(final String... keys) {
        return (long) redisTemplate.execute(new RedisCallback() {
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                long result = 0;
                for (int i = 0; i < keys.length; i++) {
                    result = connection.del(keys[i].getBytes());
                }
                return result;
            }
        });
    }

    /**
     * @param key
     * @param value
     * @param liveTime
     */
    public void set(final byte[] key, final byte[] value, final long liveTime) {
        redisTemplate.execute(new RedisCallback() {
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                connection.set(key, value);
                if (liveTime > 0) {
                    connection.expire(key, liveTime);
                }
                return 1L;
            }
        });
    }

    /**
     * @param key
     * @param value
     * @param liveTime
     */
    public void set(String key, String value, long liveTime) {
        this.set(key.getBytes(), value.getBytes(), liveTime);
    }

    /**
     * @param key
     * @param value
     */
    public void set(String key, String value) {
        this.set(key, value, 0L);
    }

    /**
     * @param key
     * @param value
     */
    public void set(byte[] key, byte[] value) {
        this.set(key, value, 0L);
    }

    /**
     * @param key
     * @return
     */
    public String get(final String key) {
        return (String) redisTemplate.execute(new RedisCallback() {
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                try {
                    return new String(connection.get(key.getBytes()), redisCode);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return "";
            }
        });
    }

    public Set<String> Setkeys(String pattern) {
        return redisTemplate.keys(pattern);
    }


    /**
     * @param key
     * @return
     */
    public boolean exists(final String key) {
        return (boolean) redisTemplate.execute(new RedisCallback() {
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.exists(key.getBytes());
            }
        });
    }

    /**
     * @return
     */
    public String flushDB() {
        return (String) redisTemplate.execute(new RedisCallback() {
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                connection.flushDb();
                return "ok";
            }
        });
    }

    /**
     * @return
     */
    public long dbSize() {
        return (long) redisTemplate.execute(new RedisCallback() {
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.dbSize();
            }
        });
    }

    /**
     * @return
     */
    public String ping() {
        return (String) redisTemplate.execute(new RedisCallback() {
            public String doInRedis(RedisConnection connection) throws DataAccessException {

                return connection.ping();
            }
        });
    }

    /**
     * @param key
     * @param value
     * @return
     */
    @Override
    public Long leftPush(String key, String value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * @param key
     * @return
     */
    @Override
    public String leftPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    /**
     * @param key
     * @param value
     * @return
     */
    @Override
    public Long rightPush(String key, String value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /**
     * @param key
     * @return
     */
    @Override
    public String rightPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    /**
     * @param key
     * @return
     */
    @Override
    public Long listLength(String key) {
        return redisTemplate.opsForList().size(key);
    }

    /**
     * @param key
     * @param start
     * @param end
     * @return
     */
    @Override
    public List<String> range(String key, int start, int end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * @param key
     * @param i
     * @param value
     */
    @Override
    public void remove(String key, long i, String value) {
        redisTemplate.opsForList().remove(key, i, value);
    }

    /**
     * @param key
     * @param index
     * @return
     */
    @Override
    public String index(String key, long index) {
        return redisTemplate.opsForList().index(key, index);
    }

    /**
     * @param key
     * @param index
     * @param value
     */
    @Override
    public void set(String key, long index, String value) {
        redisTemplate.opsForList().set(key, index, value);
    }

    /**
     * @param key
     * @param start
     * @param end
     */
    @Override
    public void trim(String key, long start, int end) {
        redisTemplate.opsForList().trim(key, start, end);
    }

    /**
     * @param key
     * @param value
     * @return
     */
    @Override
    public Long addSet(String key, String... value) {
        return redisTemplate.opsForSet().add(key, value);
    }

    /**
     * @param key
     * @return
     */
    @Override
    public String popSet(String key) {
        return redisTemplate.opsForSet().pop(key);
    }

    @Override
    public Set<String> membersSet(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    @Override
    public Long removeSet(String key, String value) {
        return redisTemplate.opsForSet().remove(key, value);
    }

    @Override
    public Boolean isMember(final String key, final String member) {
        return redisTemplate.opsForSet().isMember(key, member);
    }

    @Override
    public List<Object> isMemberPipeLine(final String key, final List<String> members) {
        return redisTemplate.executePipelined(new RedisCallback() {
            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {

                byte[] keyByte = key.getBytes();
                for (String member : members) {
                    member = "\"" + member + "\"";
                    connection.sIsMember(keyByte, member.getBytes());
                }

                return null;
            }
        });
    }

    @Override
    public void hashMapPutAll(String key, HashMap<String, String> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    @Override
    public void hashMapPut(String key, String hashKey, String value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    @Override
    public Object getHashMapValue(String key, String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    @Override
    public List<Object> multiGetHashMap(String key, Collection<Object> hashKey) {
        return redisTemplate.opsForHash().multiGet(key, hashKey);
    }

    /**
     * 发布
     *
     * @param channel 频道
     * @param msg     消息
     * @return
     */
    @Override
    public Long publish(final String channel, final String msg) {

        return (Long) redisTemplate.execute(new RedisCallback() {
            public Long doInRedis(RedisConnection connection) throws DataAccessException {

                return connection.publish(channel.getBytes(), msg.getBytes());
            }
        });
    }

    @Override
    public void expire(String key, Long liveTime) {
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                if (liveTime > 0) {
                    connection.expire(key.getBytes(), liveTime);
                }
                return 1L;
            }
        });
    }

    private RedisServiceImpl() {

    }


    public RedisTemplate<String, String> getRedisTemplate() {
        return redisTemplate;
    }

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

}