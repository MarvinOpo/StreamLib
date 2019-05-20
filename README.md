# StreamingLib

A library that provides FACEBOOK or YOUTUBE streamer key.

## Setup

Import STREAMAPI library module to your android projects then implement it to you app module

```groovy
dependencies {
    implementation project(path: ':streamapi')
}
```

## Implementation

Request Constants

```java
//for facebook key request
Constants.FACEBOOK_KEY

//for youtube key request
Constants.YOUTUBE_KEY
```

StreamApi instance, requires context

```java
StreamApi streamApi = new StreamApi(this);
```

Event handling with KeyCallback

```java
streamApi.createStreamKey(Constants.FACEBOOK_KEY, new KeyCallback() {
  @Override
  public void onSuccess(String key) {
    //TODO
  }
});
```

## Limitations

1. Facebook Console is in development mode, contact developer to allow your account for testing.
2. Youtube will provide error response if account is not enable for streaming.
You need to manually enable your account in youtube.com/features
3. No error handling
