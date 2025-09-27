package com.mcst.easyfk.generator.enums;

/**
 * @author liu yijun
 * @version 1.0
 */
public enum DbType {
    /** MySql数据库 */
    MYSQL("mysql", "MySql数据库"),
    /** MariaDB数据库 */
    MARIADB("mariadb", "MariaDB数据库"),
    /** Oracle11g及以下数据库(高版本推荐使用ORACLE_NEW) */
    ORACLE("oracle", "Oracle11g及以下数据库(高版本推荐使用ORACLE_NEW)"),
    /** Oracle12c+数据库 */
    ORACLE_12C("oracle12c", "Oracle12c+数据库"),
    /** DB2数据库 */
    DB2("db2", "DB2数据库"),
    /** H2数据库 */
    H2("h2", "H2数据库"),
    /** HSQL数据库 */
    HSQL("hsql", "HSQL数据库"),
    /** SQLite数据库 */
    SQLITE("sqlite", "SQLite数据库"),
    /** Postgre数据库 */
    POSTGRE_SQL("postgresql", "Postgre数据库"),
    /** SQLServer2005数据库 */
    SQL_SERVER2005("sqlserver2005", "SQLServer2005数据库"),
    /** SQLServer数据库 */
    SQL_SERVER("sqlserver", "SQLServer数据库"),
    /** 达梦数据库 */
    DM("dm", "达梦数据库"),
    /** 虚谷数据库 */
    XU_GU("xugu", "虚谷数据库"),
    /** 人大金仓数据库 */
    KINGBASE_ES("kingbasees", "人大金仓数据库"),
    /** Phoenix HBase数据库 */
    PHOENIX("phoenix", "Phoenix HBase数据库"),
    /** Gauss 数据库 */
    GAUSS("zenith", "Gauss 数据库"),
    /** clickhouse 数据库 */
    CLICK_HOUSE("clickhouse", "clickhouse 数据库"),
    /** 南大通用数据库 */
    GBASE("gbase", "南大通用数据库"),
    /** 神通数据库 */
    OSCAR("oscar", "神通数据库"),
    /** Sybase ASE 数据库 */
    SYBASE("sybase", "Sybase ASE 数据库"),
    /** OceanBase 数据库 */
    OCEAN_BASE("oceanbase", "OceanBase 数据库"),
    /** Firebird 数据库 */
    FIREBIRD("Firebird", "Firebird 数据库"),
    /** 其他数据库 */
    OTHER("other", "其他数据库");

    private final String db;
    private final String desc;

    public static DbType getDbType(String dbType) {
        DbType[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            DbType type = var1[var3];
            if (type.db.equalsIgnoreCase(dbType)) {
                return type;
            }
        }

        return OTHER;
    }

    public String getDb() {
        return this.db;
    }

    public String getDesc() {
        return this.desc;
    }

    private DbType(final String db, final String desc) {
        this.db = db;
        this.desc = desc;
    }
}
