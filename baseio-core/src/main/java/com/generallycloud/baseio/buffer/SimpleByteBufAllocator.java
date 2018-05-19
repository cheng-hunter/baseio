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
package com.generallycloud.baseio.buffer;

import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleByteBufAllocator extends PooledByteBufAllocator {

    public SimpleByteBufAllocator(int capacity, int unitMemorySize, boolean isDirect) {
        super(capacity, unitMemorySize, isDirect);
    }

    private int[]           blockEnds;
    private boolean[]       frees; // try bitmap

    //FIXME 判断余下的是否足够，否则退出循环
    @Override
    protected PooledByteBuf allocate(ByteBufNew byteBufNew, int limit, int start, int end,int size) {
        int freeSize = 0;
        for (; start < end;) {
            int blockEnd = start;
            if (!frees[blockEnd]) {
                start = blockEnds[blockEnd];
                freeSize = 0;
                continue;
            }
            if (++freeSize == size) {
                int blockEnd1 = blockEnd + 1; //blockEnd1=blockEnd+1
                int blockStart = blockEnd1 - size;
                frees[blockStart] = false;
                blockEnds[blockStart] = blockEnd1;
                mask = blockEnd1;
                return byteBufNew.newByteBuf(this).produce(blockStart, blockEnd1, limit, bufVersions++);
            }
            start++;
        }
        return null;
    }
    
    @Override
    protected void doStart() throws Exception {
        super.doStart();
        this.blockEnds = new int[capacity];
        this.frees = new boolean[capacity];
        Arrays.fill(frees, true);
    }

    @Override
    public void release(ByteBuf buf) {
        release((PooledByteBuf) buf, true);
    }

    protected void release(PooledByteBuf buf, boolean recycle) {
        ReentrantLock lock = this.lock;
        lock.lock();
        try {
            frees[buf.getBeginUnit()] = true;
            if (recycle) {
                bufFactory.freeBuf(buf);
            }
        } finally {
            lock.unlock();
        }
    }

    //FIXME ..not correct
    private int fillBusy() {
        int free = 0;
        for(boolean f : frees){
            if (f) {
                free++;
            }
        }
        return free;
    }

    @Override
    public synchronized String toString() {
        int free = fillBusy();
        StringBuilder b = new StringBuilder();
        b.append(this.getClass().getSimpleName());
        b.append("[free=");
        b.append(free);
        b.append(",memory=");
        b.append(capacity);
        b.append(",isDirect=");
        b.append(isDirect);
        b.append("]");
        return b.toString();
    }

}
