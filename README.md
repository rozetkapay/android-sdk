<img src="https://github.com/user-attachments/assets/6319f2c7-bdc8-4381-b866-5609bacc6e6c" alt="drawing" width="400"/>

# RozetkaPay SDK for Android
![GitHub Release](https://img.shields.io/github/v/release/rozetkapay/android-sdk)

> The RozetkaPay SDK makes adding a smooth payment experience to your mobile application easy. Our SDK offers customizable UI components for securely collecting card details and supports complete payment flows, including Google Pay/Apple Pay, for seamless transactions.

You can find all documentation here [RozetkaPay Android SDK Documentation](https://responsible-jupiter-c73.notion.site/Android-SDK-Documentation-108d2e0ae34280669532fa19eee2fe03)

## Installation
To integrate the RozetkaPay SDK into your Android app, follow the steps below. Ensure your project meets the minimum requirements and add the necessary dependencies to your project.

### Requirements

Before integrating RozetkaPay, make sure your project meets the following requirements:

- **Minimum SDK Version:** 23 (Android 6.0)

### Dependencies

#### 1. Preconditions

We use GitHub Packages to store our dependencies. You should add this repository to your project's repositories to gain access to these dependencies. 

This repository is available only for authorized GitHub users, but since the project is public, any GitHub account will suffice. Here are instructions on how to create a personal access token for your GitHub account: [Managing your personal access tokens](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens). Your token should have permission for `read:packages`.

> [!IMPORTANT]
> We do not recommend storing your username and token in the project in plain view. Instead, you can use environment variables or a properties file, or a combination of both, as shown in the example below:
> ```kotlin
> val githubProperties = File(rootDir, "github.properties").let { file ->
>     Properties().apply {
>         if (file.exists()) file.inputStream().use { load(it) }
>     }
> }
> credentials {
>     username = githubProperties["github_user"] as String? ?: System.getenv("GITHUB_USER")
>     password = githubProperties["github_token"] as String? ?: System.getenv("GITHUB_TOKEN")
> }
> ```
> 
> Ensure you add your `github.properties` file to `.gitignore`.

#### 2. Add Repository to Your Project

Add the following code to your project's `settings.gradle` (or `settings.gradle.kts`) file in the `repositories` section:

```kotlin
repositories {
	maven {
		url "https://maven.pkg.github.com/rozetkapay/android-sdk"
		credentials {
			username = "<YOUR_GITHUB_USERNAME>"
			password = "<YOUR_PERSONAL_ACCESS_TOKEN>"
		}
	}
}
```

#### 3. Add dependency

Add the following dependency to your application's `build.gradle` (or `build.gradle.kts`) file or use `toml`:

```kotlin
implementation("com.rozetkapay:sdk:{version}")
```

Number of the last version of the library you can find here  https://github.com/rozetkapay/android-sdk/releases
