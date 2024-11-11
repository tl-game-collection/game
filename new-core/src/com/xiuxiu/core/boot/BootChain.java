package com.xiuxiu.core.boot;

import com.xiuxiu.core.log.Logs;

public class BootChain {
    private final BootJob first = new BootJob() {
        @Override
        protected void start() {
            // log
            BootChain.this.startTime = System.currentTimeMillis();
            this.startNext();
        }

        @Override
        protected void stop() {
            this.stopNext();
            // log
            Logs.CORE.info("关闭完成, 耗时:%d ms", System.currentTimeMillis() - BootChain.this.stopTime);
        }

        @Override
        public String getName() {
            return "FirstBoot";
        }
    };

    private BootJob last = first;
    private long startTime;
    private long stopTime;

    public static BootChain chain() {
        return new BootChain();
    }

    public void start() {
        this.first.start();
    }

    public void stop() {
        this.first.stop();
    }

    public void end() {
        this.next(new BootJob() {
            @Override
            protected void start() {
                // last
                Logs.CORE.info("启动完成, 耗时:%d ms", System.currentTimeMillis() - BootChain.this.startTime);
            }

            @Override
            protected void stop() {
                // last
                BootChain.this.stopTime = System.currentTimeMillis();
            }

            @Override
            public String getName() {
                return "LastBoot";
            }
        });
    }

    public BootChain next(BootJob next) {
        this.last = this.last.next(next);
        return this;
    }
}
