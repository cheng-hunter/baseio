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
package com.generallycloud.baseio.common;

import java.util.UUID;

public class UUIDGenerator {

    public static String random() {
        UUID uuid = UUID.randomUUID();
        byte[] array = new byte[16];
        MathUtil.long2Byte(array, uuid.getMostSignificantBits(), 0);
        MathUtil.long2Byte(array, uuid.getLeastSignificantBits(), 8);
        return MathUtil.bytes2HexString(array);
    }

    public static String randomMostSignificantBits() {
        UUID uuid = UUID.randomUUID();
        byte[] array = new byte[8];
        MathUtil.long2Byte(array, uuid.getMostSignificantBits(), 0);
        return MathUtil.bytes2HexString(array);
    }

    public static String randomLeastSignificantBits() {
        UUID uuid = UUID.randomUUID();
        byte[] array = new byte[8];
        MathUtil.long2Byte(array, uuid.getLeastSignificantBits(), 0);
        return MathUtil.bytes2HexString(array);
    }
    
    
    public static void main(String[] args) {
        
        int count = 1024 * 1024;
        long start = System.currentTimeMillis();
        String str = null;
        for (int i = 0; i < count; i++) {
            str = random();
//            str = UUID.randomUUID().toString();
        }
        System.out.println(str);
        System.out.println(System.currentTimeMillis() - start);
        
    }
    

}