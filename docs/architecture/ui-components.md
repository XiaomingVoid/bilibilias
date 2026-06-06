# UI 组件与使用约定

本文档说明 BILIBILIAS 当前自定义 Compose 组件的分层、适用场景和选型方式。目标不是重复组件源码，而是帮助开发时快速判断“该用哪个组件”，并保持现有页面的一致交互。

## 总体原则

- 页面优先复用 `core/ui` 中的公共组件，避免在 feature 或业务组件里重复封装通用控件。
- `core/ui` 只放脱离 BILIBILIAS 业务语义后仍成立的 Compose 组件、主题和 UI 工具。
- `shared/ui/component` 放 BILIBILIAS 业务组件，例如下载任务、分集选择、登录平台筛选、存储展示、用户/稿件信息等。
- `shared/platform/component` 放需要平台 expect/actual 或平台能力支撑的 UI 组件，例如 WebView、BackHandler、HTMLText。
- 能用现有组件表达的场景，不直接落回原始 Material 3 组件；除非现有组件能力明显不够。

## 组件分层

当前仓库中的 UI 组件主要分三层：

| 位置 | 包名 | 职责 |
| --- | --- | --- |
| `core/ui/src/commonMain/kotlin/com/imcys/bilibilias/ui/component` | `com.imcys.bilibilias.ui.component` | 跨页面、跨业务可复用的公共 Compose 组件 |
| `shared/src/commonMain/kotlin/com/imcys/bilibilias/shared/ui/component` | `com.imcys.bilibilias.shared.ui.component` | shared 页面使用的业务组件 |
| `shared/src/commonMain/kotlin/com/imcys/bilibilias/shared/platform/component` | `com.imcys.bilibilias.shared.platform.component` | 平台相关 UI 组件 |

`core/ui` 还包含：

- `ui.component.tip`：错误、警告、信息提示组件。
- `ui.component.shimmer`：骨架屏和 shimmer modifier。
- `ui.component.dialog`：公共对话框组件。
- `ui.component.reorderable`：可重排列表/网格能力。
- `ui.theme`：Compose 主题、颜色、字体和形状。
- `ui.utils`：尺寸等级、屏幕适配等 UI 工具。

新增组件时优先判断：

- 如果它脱离某个具体业务页面后仍然成立，放 `core/ui`。
- 如果它强依赖下载、解析、播放器、登录、B 站业务语义，放 `shared/ui/component` 或对应 `shared/feature/*/components`。
- 如果它需要平台实现或平台能力，放 `shared/platform/component`。
- 如果它只服务单个页面，优先放在对应 `shared/feature/<feature>/components`，不要提前上升为公共组件。

## `core/ui` 公共组件

`core/ui` 的包名已经统一为 `com.imcys.bilibilias.ui.component`。历史上的 `ui.weight` 命名不再使用，新代码不要继续引用或新增 `weight` 包。

### 页面骨架

`ASTopAppBar` 是对 Material 3 `TopAppBar`、`LargeTopAppBar`、`CenterAlignedTopAppBar` 的统一封装，适合大多数有标题、返回按钮、操作菜单的标准页面。

它额外处理：

- `Small / Large / CenterAligned` 三种样式切换。
- 基于屏幕尺寸等级的宽屏显示控制。
- `contentPadding` 和 `windowInsets` 的统一管理。

普通设置页、列表页优先使用 `Small`；首页区块或层级较高页面可使用 `Large`；对称操作或标题需要居中时使用 `CenterAligned`。

`AsBackIconButton` 用于标准返回行为，优先配合 `ASTopAppBar` 使用。

### 基础交互

`ASIconButton` 在 `IconButton` 外统一增加点击触感反馈，适合顶栏按钮、卡片角标按钮、删除/更多等轻量操作。

`ASTextButton` 是 `TextButton` 的统一封装，适合对话框确认/取消、重试、复制、查看更多等次级操作。

`ASCheckThumbSwitch` 和 `ASCheckBox` 用于布尔状态选择。设置页布尔开关优先使用 `ASCheckThumbSwitch`，短时筛选状态优先考虑 chip 或 toggle 语义的组件。

### 弹窗

`ASAlertDialog` 是对 Material 3 `AlertDialog` 的轻封装，用 `showState` 控制显示，并支持 `clickBlankDismiss` 和 `DialogSortBuilder`。

适合确认删除、确认退出、权限提示、风险确认等标准弹窗。破坏性操作建议关闭 `clickBlankDismiss`，避免误触关闭。

`PermissionRequestTipDialog` 已迁入 `core/ui`，用于通用权限提示。它不应承载具体业务请求逻辑，业务侧只负责决定何时展示和如何响应用户选择。

### 图片与媒体展示

`ASAsyncImage` 基于 Coil 3，统一处理网络图片、Preview 占位和可点击封装。适合封面、头像、海报、视频缩略图等场景。

如果只需要本地静态图标，直接使用 `Icon` 或 `Image`；图片裁剪、圆角、缩放方式优先通过参数传入，不要在页面层复制一套相同包装。

### 表单与输入

`ASCardTextField` 是面向搜索/解析输入场景的卡片式单行输入框，内置搜索图标、自动聚焦、清空按钮和触感反馈。

适合 BV / AV / EP / SS 号输入、搜索框、链接粘贴框、关键字输入框。不适合多行文本编辑或复杂表单校验。

`ASCommonExposedDropdownMenu` 适合普通下拉选择。仅业务选项和展示文案变化时，优先复用它，不要在页面里重复拼 Material 3 下拉菜单。

`ASCommonSelectGrid` 和 `ASCommonLazyVerticalGrid` 适合选择型网格和通用懒加载网格。它们已经放在 `core/ui`，shared 页面应从 `com.imcys.bilibilias.ui.component` 引入。

### 设置页组件体系

设置页优先使用 `SettingsItem` 体系，不要每个页面自己拼 `Row + Icon + Text + Switch`。

常用组件：

- `SwitchSettingsItem`：布尔开关首选。
- `RadioSettingsItem`：多选一配置项首选。
- `SliderSettingsItem`：数值区间配置首选。
- `ColorSettingsItem`：颜色配置或颜色预览入口。
- `CategorySettingsItem`：设置页分组标题。
- `TipSettingsItem`：设置页说明提示。
- `BannerItem`：比普通设置项更强调的信息块。

`BaseSettingsItem` 适合“左侧图标 + 中间标题/描述 + 右侧状态/控件”的标准行。如果内容高度不固定、含复杂富文本或网格布局，不要强行套设置项组件。

### 卡片与组合容器

`SurfaceColorCard` 适合作为统一的表面色卡片容器。页面里需要标准卡片外观时优先使用它，而不是重复写 `CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)`。

`ASCardGroups` 用于把多个卡片块横向并列成一组，并自动处理首尾圆角差异。只是普通横向排布时，直接使用 `Row`。

### 提示与状态

`ASErrorTip`、`ASInfoTip`、`ASWarringTip` 用于错误、说明、警告状态。页面不要散落自定义配色提示块。

`ASCommonLoadingScreen` 适合大面积加载状态。局部列表或卡片占位优先使用 `shimmer` 体系。

`AsAutoError` 留在 `shared/ui/component`，因为它和 shared 页面错误展示方式绑定，不作为纯公共 UI 组件。

### 页面结构与动效

以下组件已经迁入 `core/ui`，可在 shared 页面和后续公共页面中复用：

- `ASAnimatedContent`
- `ASCollapsingToolbar`
- `ASColumnAutoFolding`
- `ASPrimaryTabRow`
- `ModifierExt`
- `Shapes`
- `reorderable/*`

这些组件只负责通用 UI 行为，不应反向依赖 shared、data、network、database、datastore 等业务或数据层模块。

## `shared/ui/component` 业务组件

`shared/ui/component` 放 BILIBILIAS 业务语义明确的组件。它们可以依赖 shared 页面上下文、资源、业务 model 或平台能力，但不应被继续下沉到 `core/ui`。

当前典型组件：

- `ASAgreePrivacyPolicy`：隐私授权展示。
- `ASEpisodeSelection`、`ASEpisodeTitle`：分集、合集和视频标题选择。
- `ASLoginPlatformFilterChipRow`：登录平台筛选。
- `ASStorageRing`：存储空间展示。
- `AsAutoError`：shared 页面错误展示。
- `AsUserInfoRow`：用户信息行。
- `DownloadTaskCard`：下载任务卡片。
- `Konfetti`：业务场景中的动效组件。
- `WorkCard`：稿件卡片。
- `copyright/VideoCopyrightApply`：版权/合规提示组件。

如果组件只服务某个 feature，优先放在 `shared/feature/<feature>/components`。只有多个 feature 复用且仍带 BILIBILIAS 业务语义时，才提升到 `shared/ui/component`。

## `shared/platform/component` 平台组件

`shared/platform/component` 用于 Compose UI 中需要平台实现的组件，例如：

- WebView。
- BackHandler。
- HTMLText。

这类组件通常需要 `expect/actual`、Android/iOS API 或平台运行时能力。新增时应保持平台边界清晰：公共页面只调用抽象组件，不直接在 commonMain 中访问 Android 或 iOS API。

## 新增页面时的选型顺序

建议按这个顺序判断：

1. 先看 `core/ui` 有没有现成组件能直接表达需求。
2. 如果页面属于下载、解析、分集、登录、隐私、存储等 BILIBILIAS 特有业务，再看 `shared/ui/component`。
3. 如果组件需要平台能力，再看 `shared/platform/component`。
4. 如果只是差一点点，优先扩展现有组件参数，而不是复制一份近似组件。
5. 只有当现有组件确实无法覆盖，并且具备跨页复用价值时，才新增公共组件。

## 新增公共组件的约定

- 命名优先沿用 `AS*` 前缀。
- 能包进统一交互就包进去，例如点击触感、Preview 兼容、宽屏适配、主题色默认值。
- 参数设计优先暴露行为和插槽，不要让页面层传大量内部实现细节。
- 纯公共 UI 不依赖 `shared`、`data`、`network`、`database`、`datastore`。
- 新增后应补充到本文档，至少说明“它替代什么、什么时候用、什么时候别用”。
