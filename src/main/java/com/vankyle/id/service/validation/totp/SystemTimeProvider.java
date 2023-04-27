package com.vankyle.id.service.validation.totp;

public class SystemTimeProvider implements TimeProvider {
    @Override
    public long getTime() {
        return System.currentTimeMillis();
    }
}
