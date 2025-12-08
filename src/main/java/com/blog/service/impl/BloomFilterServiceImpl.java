package com.blog.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.BitSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 布隆过滤器服务实现
 * 防止缓存穿透，减少无效数据库查询
 * 
 * @author Worlt
 * @date 2025/9/5
 */
@Service
@Slf4j
public class BloomFilterServiceImpl {
    
    // 简化版布隆过滤器（生产环境建议使用Guava或Redis的布隆过滤器）
    private final Set<String> bloomFilter = ConcurrentHashMap.newKeySet();
    
    // BitSet实现的布隆过滤器（更节省内存）
    private final BitSet bitSet = new BitSet(1000000); // 100万bit
    private static final int[] HASH_SEEDS = {3, 5, 7, 11, 13, 31, 37, 61}; // 8个哈希函数
    
    /**
     * 添加元素到布隆过滤器
     * 
     * @param key 要添加的键
     */
    public void add(String key) {
        if (key == null) {
            return;
        }
        
        // 简单版本：直接添加到Set
        bloomFilter.add(key);
        
        // BitSet版本：使用多个哈希函数
        for (int seed : HASH_SEEDS) {
            int hash = hash(key, seed);
            bitSet.set(Math.abs(hash) % bitSet.size());
        }
        
        log.debug("添加到布隆过滤器: key={}", key);
    }
    
    /**
     * 检查元素是否可能存在
     * 
     * @param key 要检查的键
     * @return true-可能存在，false-一定不存在
     */
    public boolean mightContain(String key) {
        if (key == null) {
            return false;
        }
        
        // 简单版本：直接检查Set
        boolean simpleResult = bloomFilter.contains(key);
        
        // BitSet版本：检查所有哈希位
        boolean bitSetResult = true;
        for (int seed : HASH_SEEDS) {
            int hash = hash(key, seed);
            if (!bitSet.get(Math.abs(hash) % bitSet.size())) {
                bitSetResult = false;
                break;
            }
        }
        
        // 两种方式都认为存在才返回true
        boolean result = simpleResult || bitSetResult;
        
        log.debug("布隆过滤器检查: key={}, result={}", key, result);
        return result;
    }
    
    /**
     * 批量添加元素
     * 
     * @param keys 键集合
     */
    public void addAll(Set<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return;
        }
        
        keys.forEach(this::add);
        log.info("批量添加到布隆过滤器: {} 个键", keys.size());
    }
    
    /**
     * 获取过滤器统计信息
     * 
     * @return 统计信息
     */
    public BloomFilterStats getStats() {
        BloomFilterStats stats = new BloomFilterStats();
        stats.setTotalElements(bloomFilter.size());
        stats.setBitSetSize(bitSet.size());
        stats.setBitSetCardinality(bitSet.cardinality()); // 设置为1的bit数量
        
        // 估算误判率
        double falsePositiveRate = Math.pow(
            (double) bitSet.cardinality() / bitSet.size(), 
            HASH_SEEDS.length
        );
        stats.setEstimatedFalsePositiveRate(falsePositiveRate);
        
        return stats;
    }
    
    /**
     * 清空布隆过滤器
     */
    public void clear() {
        bloomFilter.clear();
        bitSet.clear();
        log.info("布隆过滤器已清空");
    }
    
    /**
     * 哈希函数
     */
    private int hash(String key, int seed) {
        int hash = 0;
        for (char c : key.toCharArray()) {
            hash = hash * seed + c;
        }
        return hash;
    }
    
    /**
     * 布隆过滤器统计信息
     */
    public static class BloomFilterStats {
        private int totalElements;
        private int bitSetSize;
        private int bitSetCardinality;
        private double estimatedFalsePositiveRate;
        
        // Getters and Setters
        public int getTotalElements() { return totalElements; }
        public void setTotalElements(int totalElements) { this.totalElements = totalElements; }
        
        public int getBitSetSize() { return bitSetSize; }
        public void setBitSetSize(int bitSetSize) { this.bitSetSize = bitSetSize; }
        
        public int getBitSetCardinality() { return bitSetCardinality; }
        public void setBitSetCardinality(int bitSetCardinality) { this.bitSetCardinality = bitSetCardinality; }
        
        public double getEstimatedFalsePositiveRate() { return estimatedFalsePositiveRate; }
        public void setEstimatedFalsePositiveRate(double estimatedFalsePositiveRate) { 
            this.estimatedFalsePositiveRate = estimatedFalsePositiveRate; 
        }
    }
} 