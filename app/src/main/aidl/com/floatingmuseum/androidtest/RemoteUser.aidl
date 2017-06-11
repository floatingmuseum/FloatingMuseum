// RemoteUser.aidl
package com.floatingmuseum.androidtest;

// Declare any non-default types here with import statements

interface RemoteUser {
            void sendMessage(String message);
            void receiveMessage(String message);
}
