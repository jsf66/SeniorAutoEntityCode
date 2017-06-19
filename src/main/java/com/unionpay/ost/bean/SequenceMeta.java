package com.unionpay.ost.bean;

/**
 * Created by jsf on 16/8/11..
 */
public class SequenceMeta {

    private String sequenceName;//序列名
    private String sequenceStrategy;//序列生成策略
    private Long sequenceInitialValue;//序列的初始值
    private Long sequenceAllocationSize;//序列对应的主键每次自动增长的值
    public static final String AUTO_STRATEGY = "GenerationType.AUTO";//根据底层数据库自动选择生成
    public static final String IDENTITY_STRATEGY = "GenerationType.IDENTITY";//主键由数据库自动生成(自动增长型)
    public static final String SEQUENCE_STRATEGY = "GenerationType.SEQUENCE";//根据底层数据库的序列来生成主键
    public static final String TABLE_STRATEGY = "GenerationType.TABLE";//使用一个特定的数据库表格来存储主键


    public Long getSequenceInitialValue() {
        return sequenceInitialValue;
    }

    public Long getSequenceAllocationSize() {
        return sequenceAllocationSize;
    }

    public String getSequenceName() {
        return sequenceName;
    }

    public String getSequenceStrategy() {
        return sequenceStrategy;
    }


    public void setSequenceInitialValue(Long sequenceInitialValue) {
        this.sequenceInitialValue = sequenceInitialValue;
    }

    public void setSequenceAllocationSize(Long sequenceAllocationSize) {
        this.sequenceAllocationSize = sequenceAllocationSize;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    public void setSequenceStrategy(String sequenceStrategy) {
        this.sequenceStrategy = sequenceStrategy;
    }
}
