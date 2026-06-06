# iOS 导入产物

当前仓库通过聚合模块 `:shared` 提供 iOS 导入产物流水线，目标是产出一个可直接被 Xcode 导入的 `XCFramework` 单入口产物。

## 覆盖模块

当前默认产出以下模块的 iOS 导入产物：

- `shared`

`shared` 内部聚合 `core:data`，并通过 `core:data` 的公开依赖向上暴露 `core:database`、`core:datastore`、`core:network` 等能力。iOS 工程应优先只导入这一份产物，而不是分别拖入多个 `core:*` 框架。

## 本地构建

构建产物：

```bash
./gradlew :shared:assembleASSharedReleaseXCFramework
```

产物目录：

```text
shared/build/XCFrameworks/release/
```

当前默认会生成一个聚合 `XCFramework` 目录：

- `ASShared.xcframework`

## Xcode 工程接入

iOS 工程位于：

```text
iosApp/BILIBILIAS.xcodeproj
```

该工程采用“共享逻辑、SwiftUI 自绘 UI”接入方式：

- iOS 包名、Team、版本号集中在 `iosApp/Configuration/Config.xcconfig`。
- 当前 Bundle Identifier 是 `com.imcys.bilibilias`；如需替换包名，优先改 `PRODUCT_BUNDLE_IDENTIFIER`，不要直接散改 `project.pbxproj`。
- Target 的 `Build ASShared` 脚本阶段会从 Xcode 工程目录回到仓库根目录，执行 `./gradlew :shared:embedAndSignAppleFrameworkForXcode`。
- Swift 侧直接 `import ASShared`，当前 `ContentView` 只验证 shared 逻辑产物可导入；Swift 友好的业务 bridge 应从 `shared` 暴露简单类型和函数，不要直接暴露 Koin 容器对象。
- iOS UI 暂不使用 Compose Multiplatform，由 SwiftUI 自己实现；`ASShared` 只作为共享逻辑入口。
- Xcode 工程显式使用 `iosApp/BILIBILIAS/Info.plist`，并关闭 `ENABLE_USER_SCRIPT_SANDBOXING`，避免构建阶段调用 Gradle 时被 Xcode 脚本沙箱拦截。
- Xcode scheme 提交在 `iosApp/BILIBILIAS.xcodeproj/xcshareddata/xcschemes/BILIBILIAS.xcscheme`，不要提交 `xcuserdata` 和 `UserInterfaceState.xcuserstate` 这类本地状态文件。

`embedAndSignAppleFrameworkForXcode` 是 Kotlin Multiplatform Gradle 插件为 iOS framework 自动生成的任务。该任务必须由 Xcode Run Script 调用，因为它依赖 Xcode 注入的 `SDK_NAME`、`CONFIGURATION`、`TARGET_BUILD_DIR`、`FRAMEWORKS_FOLDER_PATH`、`ARCHS` 等环境变量来判断构建真机还是模拟器产物，并把 `ASShared.framework` 嵌入到：

```text
$TARGET_BUILD_DIR/$FRAMEWORKS_FOLDER_PATH
```

因此 `ASShared.framework` 通常不会出现在 `iosApp/BILIBILIAS/` 源码目录或 Xcode 左侧项目树里，而是在构建后的 `.app/Frameworks` 目录中。

在 Xcode 运行 iOS：

```bash
open iosApp/BILIBILIAS.xcodeproj
```

选择 `BILIBILIAS` scheme 和 iOS 模拟器/真机运行即可。Xcode 构建时会自动触发 shared framework 构建和签名嵌入。

在 Android Studio 运行 iOS：

- 先重新 Sync Gradle，让 IDE 识别根项目中的 KMP iOS target 和 `iosApp/BILIBILIAS.xcodeproj`。
- 运行配置下拉中应能看到 `BILIBILIAS` iOS App/scheme，并可选择 iOS Simulator。
- 这依赖 Xcode 工程、scheme、`iosArm64/iosSimulatorArm64` target 和 `embedAndSignAppleFrameworkForXcode` 构建脚本；不是通过 `.idea` 私有配置固定出来的。

在 Android Studio 运行 Android：

```bash
./gradlew :app:assembleAlphaDebug
```

日常开发仍优先使用 `alpha` flavor；iOS 侧通过同一个 Gradle 根工程复用 `:shared` 产物。

## CI 流水线

GitHub Actions 工作流文件：

```text
.github/workflows/build-core-ios-artifacts.yml
```

流水线运行在 `macos-latest`，执行：

```bash
./gradlew :shared:assembleASSharedReleaseXCFramework --stacktrace --info
```

随后直接上传 `shared/build/XCFrameworks/release/ASShared.xcframework` 作为构建产物。

## 说明

- 当前流水线默认面向 iOS 单入口产物，优先通过 `ASShared.xcframework` 交付。
- Kotlin 工程内部仍保持 `core:*` 多模块结构；`shared` 只负责对 iOS 聚合导出。
- 不再额外把产物复制到 `core/build/ios-artifacts`；默认直接使用 `shared` 模块原生输出目录。
- iOS 侧初始化和 bridge 入口应优先从 `shared` 暴露，面向 Swift 提供简单返回值、回调或 KMP-NativeCoroutines 这类桥接后的异步接口。
- 若后续新增需要暴露给 iOS 的 KMP 模块，优先评估是否应作为 `shared` 的依赖导出，并同步更新该文档。
