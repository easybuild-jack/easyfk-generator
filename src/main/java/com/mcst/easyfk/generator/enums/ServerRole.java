package com.mcst.easyfk.generator.enums;

/**
 * 服务角色，仅 SINGLE + RPC 时生效
 *
 * @author eb-jack
 */
public enum ServerRole {
    /**
     * NONE
     */
    NONE,
    /**
     * SERVER (服务端/提供者)
     */
    SERVER,
    /**
     * PRD (消费端/表现层)
     */
    PRD
}
