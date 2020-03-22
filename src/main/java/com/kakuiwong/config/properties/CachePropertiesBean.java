package com.kakuiwong.config.properties;

import com.kakuiwong.bean.ThanosCacheTypeE;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

/**
 * @author: gaoyang
 * @Description:
 */
@ConfigurationProperties(prefix = "cache-thanos")
public class CachePropertiesBean {

    private Long localMaxSize;
    private Boolean doubleDelete;
    private String type;

    public String getType() {
        if (StringUtils.isEmpty(type)) {
            return ThanosCacheTypeE.L1.getType();
        }
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getLocalMaxSize() {
        if (this.localMaxSize == null) {
            return 0L;
        }
        return localMaxSize;
    }

    public Boolean getDoubleDelete() {
        if (this.doubleDelete == null) {
            return false;
        }
        return doubleDelete;
    }

    public void setDoubleDelete(Boolean doubleDelete) {
        this.doubleDelete = doubleDelete;
    }

    public void setLocalMaxSize(Long localMaxSize) {
        this.localMaxSize = localMaxSize;
    }
}
