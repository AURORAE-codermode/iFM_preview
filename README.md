# iFM_preview
 播客应用 Android 版交接手册
撰写人：AI Assistant（项目代码生成者）  
目标读者：结对项目成员  
撰写目的：跑通代码、看懂架构、扩展



 1. 项目一句话定位  
"类喜马拉雅"播客应用：  
 单列播客浏览 + 分类搜索  
 音频播放 + 订阅管理 + 虚拟数据模拟  
 全部基于 Kotlin + Android原生开发，使用 MVVM 架构，便于后续功能扩展  



 2. 跑通
bash
 1. 用 Android Studio 打开项目
File → Open → 选择项目根目录

 2. 同步 Gradle（自动）
等待右下角进度条完成，下载依赖

 3. 连接设备或启动模拟器
确保 Android 5.0+ (API 21+)

 4. 运行
点击绿色运行按钮 或 Shift+F10
 应用将安装并启动




 3. 文件地图（核心文件）

iFM_preview/
├── manifests/AndroidManifest.xml           应用配置 + 权限 + Activity注册
├── build.gradle.kts (Module :app)          依赖配置 + 编译设置
├── kotlin+java/com.zjgsu.ifm_preview/
│   ├── presentation/activity/
│   │   ├── LoginActivity.kt                登录注册页
│   │   ├── MainActivity.kt                 首页播客列表
│   │   └── PlayerActivity.kt               播放详情页
│   ├── data/
│   │   ├── model/Podcast.kt                数据模型定义
│   │   └── repository/PodcastRepository.kt  虚拟数据仓库
│   ├── presentation/viewmodel/
│   │   └── PodcastViewModel.kt             首页业务逻辑
│   ├── presentation/adapter/
│   │   ├── PodcastAdapter.kt               播客列表适配器
│   │   └── EpisodeAdapter.kt               剧集列表适配器
│   └── service/
│       └── PodcastPlayerService.kt         后台播放服务
├── res/layout/
│   ├── activity_login.xml                  登录页布局
│   ├── activity_main.xml                   首页布局
│   ├── activity_player.xml                 播放页布局
│   ├── item_podcast.xml                    播客项布局
│   └── item_episode.xml                    剧集项布局
└── res/values/
    ├── strings.xml                         字符串资源
    └── colors.xml                          颜色资源




 4. 核心逻辑一张图
| 事件 | 实现位置 | 关键变量/函数 |
||||
| 登录/注册 | LoginActivity.kt | validateInput() + performLogin() |
| 播客列表加载 | MainActivity.kt + PodcastViewModel.kt | loadPodcasts() + LiveData 观察 |
| 上下滑动浏览 | RecyclerView 内置 | LinearLayoutManager + PodcastAdapter |
| 点击播放 | MainActivity.navigateToPodcastDetail() | Intent 传递 Parcelable 对象 |
| 播放控制 | PlayerActivity.kt + PodcastPlayerService.kt | MediaPlayer + Service 绑定 |
| 订阅管理 | PodcastViewModel.toggleSubscription() | 更新 isSubscribed 状态 |
| 搜索过滤 | MainActivity.setupSearchView() | SearchView.OnQueryTextListener |



 5. 后续常见扩展点（已留接口）

| 需求 | 推荐做法 | 预估工作量 |
||||
| 真实后端接入 | Retrofit + 真实 API 替换 PodcastRepository | 前端 3h（接口已抽象） |
| 用户系统完善 | SharedPreferences 存用户信息 + 登录状态 | 1h（已有 AppConstants.Preferences） |
| 下载功能 | WorkManager 后台下载 + 本地存储 | 4h（需处理存储权限） |
| 播放列表 | Room 数据库存播放记录 + 播放队列 | 6h（数据结构已定义） |
| 推送通知 | Firebase Cloud Messaging | 3h（通知渠道已创建） |
| 深色模式 | DayNight 主题 + 颜色资源适配 | 2h |



 6. 依赖与版本锁定
kotlin
// build.gradle.kts 核心依赖
dependencies {
    implementation("androidx.core:corektx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.lifecycle:lifecycleviewmodelktx:2.7.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
}

Android Studio ≥ 2022.3 即可  
Gradle 8.0+ 兼容，JDK 17 推荐



 7. 已知"坑"清单
| 现象 | 原因 | 一键解决 |
||||
| @Parcelize 报错 | kotlinparcelize 插件配置问题 | 已改用手动实现 Parcelable |
| viewModels() 报错 | 缺少 activityktx 依赖 | 已改用 ViewModelProvider |
| 图片加载失败 | 网络权限或图片URL失效 | 使用 https://picsum.photos 测试图片 |
| 音频播放失败 | 测试音频文件URL失效 | 使用 www.soundjay.com 测试音频 |
| 布局预览不显示 | 缺少主题配置 | 检查 Theme.IFMPreview 继承关系 |



 8. 一键重置
bash
 在 Android Studio 中：
1. Build → Clean Project
2. Build → Rebuild Project  
3. File → Invalidate Caches / Restart...
4. 选择 "Invalidate and Restart"

 或者命令行：
./gradlew clean
./gradlew build


项目无复杂配置、无隐藏缓存，清理重建即可恢复初始状态。



 9. 测试账号

邮箱：test@example.com
密码：123456

或点击"跳过登录"直接体验



 10. 开发建议
 架构清晰：MVVM 架构，数据层与UI层分离
 扩展友好：Repository 模式，轻松切换数据源
 代码规范：清晰的包结构和命名约定
 虚拟数据：开发阶段使用 Mock 数据，不影响真实功能测试
