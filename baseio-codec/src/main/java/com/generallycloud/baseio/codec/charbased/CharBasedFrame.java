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
package com.generallycloud.baseio.codec.charbased;

import com.generallycloud.baseio.protocol.AbstractFrame;
import com.generallycloud.baseio.protocol.TextFrame;

public class CharBasedFrame extends AbstractFrame implements TextFrame {

    private String readText;

    public CharBasedFrame() {}

    public CharBasedFrame(String readText) {
        this.readText = readText;
    }

    @Override
    public String getReadText() {
        return readText;
    }

    @Override
    public String toString() {
        return getReadText();
    }

}
