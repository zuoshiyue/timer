# GitHub推送和自动构建指南

## 已完成的工作

✅ **Git仓库初始化** - 项目已初始化为Git仓库  
✅ **GitHub Actions配置** - 创建了两个自动化工作流  
✅ **.gitignore配置** - 排除了不必要的文件  
✅ **README文档** - 完整的项目说明  
✅ **初始提交** - 所有代码文件已提交到本地仓库  

## 下一步操作指南

### 步骤1：在GitHub上创建新仓库

1. 访问 [GitHub.com](https://github.com) 并登录
2. 点击右上角"+"号，选择"New repository"
3. 填写仓库信息：
   - **Repository name**: `workout-timer-app` (或其他您喜欢的名称)
   - **Description**: `训练间隔计时器Android应用`
   - **Visibility**: Public (推荐) 或 Private
   - **Initialize this repository with**: 不要勾选任何选项（我们已经有了代码）
4. 点击"Create repository"

### 步骤2：添加远程仓库并推送代码

在终端中执行以下命令（替换为您的实际仓库URL）：

```bash
cd /Users/lpf/Documents/docker/timer

# 添加远程仓库（替换为您的仓库URL）
git remote add origin https://github.com/您的用户名/您的仓库名.git

# 重命名主分支（如果需要）
git branch -M main

# 推送代码到GitHub
git push -u origin main
```

### 步骤3：启用GitHub Actions

1. 推送完成后，访问您的GitHub仓库页面
2. 点击顶部的"Actions"标签页
3. 您会看到两个工作流：
   - **Android CI** - 每次代码推送时自动构建
   - **Build and Release APK** - 创建版本标签时发布APK
4. 点击"I understand my workflows, go ahead and enable them"

### 步骤4：验证自动构建

1. 第一次推送后，GitHub Actions会自动开始构建
2. 在"Actions"页面查看构建状态：
   - 绿色对勾表示构建成功
   - 红色叉号表示构建失败（可查看详细日志）
3. 构建成功后，可以下载生成的APK文件

## GitHub Actions工作流说明

### 1. Android CI工作流 (`android-ci.yml`)

**触发条件：**
- 每次push到main/master分支
- 创建Pull Request时

**执行操作：**
- 设置Java 11环境
- 配置Android SDK
- 构建Debug版本APK
- 运行单元测试
- 上传构建产物

**产物下载：**
- 在Actions页面找到对应的运行记录
- 下载`app-debug-apk`压缩包
- 解压后获得`app-debug.apk`文件

### 2. 发布构建工作流 (`release-apk.yml`)

**触发条件：**
- 创建版本标签时（如 `v1.0.0`, `v1.1.0`）

**执行操作：**
- 构建Debug和Release版本APK
- 自动创建GitHub Release
- 上传APK到Release页面

**使用方法：**
```bash
# 创建版本标签
git tag v1.0.0

# 推送标签到GitHub
git push origin v1.0.0
```

## 常用Git命令参考

### 日常开发流程
```bash
# 查看当前状态
git status

# 添加更改的文件
git add .

# 提交更改
git commit -m "描述更改内容"

# 推送到GitHub
git push
```

### 版本发布流程
```bash
# 创建新版本标签
git tag v1.0.0

# 推送标签
git push origin v1.0.0

# 查看所有标签
git tag

# 删除本地标签
git tag -d v1.0.0

# 删除远程标签
git push origin --delete v1.0.0
```

## 故障排除

### 常见问题

**1. 推送失败：权限被拒绝**
```bash
# 检查远程仓库URL是否正确
git remote -v

# 如果使用HTTPS，可能需要配置个人访问令牌
# 或者改用SSH方式
```

**2. GitHub Actions构建失败**
- 检查构建日志中的具体错误信息
- 确认`gradle/wrapper/gradle-wrapper.properties`配置正确
- 确认Android SDK版本兼容性

**3. APK无法安装**
- 确保设备已开启"未知来源"安装权限
- 检查Android版本兼容性（minSdk: 26）
- 尝试卸载旧版本后重新安装

## 最佳实践建议

### 代码管理
- 定期提交代码，保持提交信息清晰
- 使用特性分支开发，通过Pull Request合并
- 及时解决合并冲突

### 版本发布
- 使用语义化版本号（如 v1.2.3）
- 每次发布前进行充分测试
- 在Release页面添加详细的更新说明

### 持续集成
- 关注构建状态，及时修复失败
- 定期更新依赖库版本
- 配置代码质量检查工具

## 获取帮助

如果遇到问题，可以：

1. **查看GitHub文档** - [GitHub Actions文档](https://docs.github.com/actions)
2. **搜索类似问题** - Stack Overflow或GitHub社区
3. **查看项目Issues** - 在仓库中创建Issue描述问题

---

**恭喜！** 您的训练间隔计时器APP现在已经具备了完整的持续集成和自动发布能力。每次代码更新都会自动构建新的APK，让团队协作和版本管理变得更加高效。