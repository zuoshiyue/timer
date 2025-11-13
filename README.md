# 训练间隔计时器APP

> **AI生成声明**: 本项目由AI助手协助开发完成，代码实现和文档编写均经过AI技术辅助。项目遵循最佳实践和行业标准，具备完整的生产就绪功能。

一个功能完整的Android训练间隔计时器应用，支持多阶段训练计划、声音震动提醒和训练记录功能。

## 功能特性

- 🏋️ **多阶段训练计划** - 支持复杂的训练流程配置
- ⏱️ **智能计时器** - 精确的阶段计时和自动切换
- 🔊 **声音提醒** - 支持蜂鸣声、警报声等多种提示音
- 📳 **震动提醒** - 多种震动模式可选
- 📊 **训练记录** - 自动保存训练历史和统计数据
- ⚙️ **个性化设置** - 丰富的配置选项

## 技术栈

- **语言**: Kotlin
- **框架**: Android Jetpack (Room, Navigation, WorkManager)
- **架构**: MVVM + Repository模式
- **构建工具**: Gradle

## GitHub Actions 自动构建

项目配置了GitHub Actions，支持自动构建和发布：

### 工作流说明

1. **CI构建** (`android-ci.yml`)
   - 在每次push到main/master分支时触发
   - 自动构建Debug版本APK
   - 运行单元测试
   - 上传构建产物

2. **发布构建** (`release-apk.yml`)
   - 在创建版本标签时触发（如 `v1.0.0`）
   - 构建Debug和Release版本APK
   - 自动创建GitHub Release
   - 上传APK到Release页面

## 快速开始

### 1. 将项目推送到GitHub

```bash
# 初始化Git仓库（已完成）
git init

# 添加所有文件
git add .

# 提交更改
git commit -m "初始提交：训练间隔计时器APP"

# 在GitHub上创建新仓库，然后添加远程仓库
git remote add origin https://github.com/你的用户名/你的仓库名.git

# 推送代码
git branch -M main
git push -u origin main
```

### 2. 启用GitHub Actions

1. 在GitHub仓库页面，进入"Actions"标签页
2. 点击"I understand my workflows, go ahead and enable them"
3. 工作流将自动开始运行

### 3. 下载构建产物

- **CI构建**: 在Actions页面找到对应的运行记录，下载`app-debug-apk`产物
- **发布构建**: 在Releases页面下载已签名的APK文件

## 本地开发

### 环境要求

- Android Studio Arctic Fox 或更高版本
- Java 11
- Android SDK 33

### 构建命令

```bash
# 清理项目
./gradlew clean

# 构建Debug版本
./gradlew assembleDebug

# 构建Release版本
./gradlew assembleRelease

# 运行测试
./gradlew test
```

## 项目结构

```
app/
├── src/main/java/com/timer/workout/
│   ├── ui/                    # 界面相关
│   ├── data/                  # 数据层
│   ├── domain/                # 业务逻辑
│   └── service/               # 后台服务
├── res/                       # 资源文件
└── build.gradle              # 模块配置
```

## 配置说明

### 声音提醒类型
- 蜂鸣声 (beep)
- 警报声 (alert) 
- 自定义声音 (custom)

### 震动提醒类型
- 短震动 (short)
- 长震动 (long)
- 提醒模式 (pattern)
- 心跳模式 (heartbeat)

## AI生成说明

本项目由AI助手全程协助开发，包括：
- 代码架构设计和实现
- UI界面设计和布局
- 功能逻辑和业务实现
- 文档编写和配置管理
- 持续集成流水线配置

项目遵循现代Android开发最佳实践，采用MVVM架构、Jetpack组件和Kotlin语言。

## 许可证

本项目采用MIT许可证。AI生成的代码同样遵循开源协议，可自由使用和修改。

## 贡献

欢迎提交Issue和Pull Request来改进这个项目。

## 联系方式

如有问题或建议，请通过GitHub Issues联系我们。