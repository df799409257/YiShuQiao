package com.yishuqiao.dto;

import java.io.Serializable;

/**
 * 图片对象
 */
public class ImageItem implements Serializable {

    private static final long serialVersionUID = -7188270558443739436L;
    public String imageId;
    public String thumbnailPath;
    public String sourcePath;
    public String key;
    public boolean isSelected = false;
}
