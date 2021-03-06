/*
 * Copyright 2015-2017 GenerallyCloud.com
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.generallycloud.baseio.container.http11;

import java.io.File;

import com.generallycloud.baseio.common.DateUtil;

/**
 * @author wangkai
 *
 */
public class HttpEntity {

    private String contentType;
    private File   file;
    private long   lastModify;
    private long   lastModifyGTMTime;
    private byte[] binary;
    private String lastModifyGTM;
    private byte[] lastModifyGTMBytes;
    private byte[] contentTypeBytes;

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
        this.contentTypeBytes = contentType.getBytes();
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public long getLastModify() {
        return lastModify;
    }

    public void setLastModify(long lastModify) {
        DateUtil format = DateUtil.get();
        this.lastModify = lastModify;
        this.lastModifyGTMBytes = format.formatHttpBytes(lastModify);
        this.lastModifyGTM = new String(lastModifyGTMBytes);
        this.lastModifyGTMTime = format.parseHttp(lastModifyGTM).getTime();
    }

    public byte[] getBinary() {
        return binary;
    }

    public void setBinary(byte[] binary) {
        this.binary = binary;
    }

    public String getLastModifyGTM() {
        return lastModifyGTM;
    }

    public long getLastModifyGTMTime() {
        return lastModifyGTMTime;
    }
    
    public byte[] getLastModifyGTMBytes() {
        return lastModifyGTMBytes;
    }
    
    public byte[] getContentTypeBytes() {
        return contentTypeBytes;
    }
    
}
