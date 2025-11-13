# 训练间隔计时器APP需求文档

## 项目概述

### 项目名称
- **中文名称**: 训练间隔计时器
- **英文名称**: TIMER

### 项目目标
开发一款专为健身训练设计的Android间隔计时器应用，支持自定义训练计划、多种提醒方式，并提供完整的训练记录功能。

### 目标用户
- 健身爱好者
- HIIT（高强度间歇训练）用户
- 自重训练用户
- 需要精确时间控制的运动人群

## 功能需求

### 1. 核心功能模块

#### 1.1 计时器管理
- **自定义间隔设置**: 支持设置多个训练间隔和休息间隔
- **循环次数设置**: 可设置训练循环次数（1-50次）
- **预设模板**: 提供常用训练模板，支持用户自定义保存
- **实时倒计时**: 大字体显示剩余时间，颜色区分训练/休息状态

#### 1.5 多阶段训练计划
- **阶段管理**: 支持创建包含多个阶段的复杂训练计划
- **阶段参数**: 每个阶段可设置时间、坡度、速度、心率等参数
- **阶段过渡**: 支持阶段间的平滑过渡和准备时间
- **阶段描述**: 支持详细的训练要点和技巧说明

#### 1.6 快速添加方案
- **预设模板**: 提供常用训练模板的一键添加
- **模板复制**: 支持复制现有训练计划快速创建新计划
- **快速编辑**: 批量编辑多个阶段的参数
- **智能推荐**: 基于用户历史训练推荐相似方案

#### 1.2 提醒系统
- **声音提醒**: 支持自定义铃声选择
- **震动提醒**: 支持震动模式设置
- **通知提醒**: 后台运行时通过系统通知提醒
- **视觉提醒**: 通过颜色变化和动画效果提醒

#### 1.3 训练记录
- **训练日志**: 记录每次训练的时间、类型、完成情况
- **统计数据**: 按日/周/月统计训练时长和频率
- **训练历史**: 查看历史训练记录和详情

#### 1.4 后台运行
- **后台计时**: APP在后台时继续计时
- **系统通知**: 时间到达时发送系统通知
- **锁屏显示**: 支持锁屏界面显示计时信息

### 2. 详细功能规格

#### 2.1 计时器设置
```
训练间隔: 2秒 - 8分钟
休息间隔: 0秒 - 8分钟
循环次数: 1-50次
预备时间: 0-60秒
冷却时间: 0-60秒
```

#### 2.2 提醒方式
- **声音选项**: 默认铃声、自定义铃声、静音
- **震动模式**: 短震动、长震动、震动模式组合
- **视觉提示**:
  - 训练状态: 红色/橙色背景
  - 休息状态: 绿色/蓝色背景
  - 倒计时: 数字颜色变化

#### 2.3 预设模板
- **内置模板**:
  - HIIT训练 (30秒训练/10秒休息)
  - Tabata训练 (20秒训练/10秒休息)
  - 自重训练 (45秒训练/15秒休息)
  - 爬坡训练 (多阶段坡度变化训练)
- **自定义模板**: 用户可保存常用训练配置

#### 2.4 多阶段训练规格
```
阶段数量: 1-10个阶段
阶段时长: 10秒 - 30分钟
参数类型:
  - 坡度: 0%-15% (递增/递减/恒定)
  - 速度: 3.0-8.0 km/h
  - 心率区间: 50%-85%最大心率
  - 训练要点: 详细技巧说明
循环设置: 支持阶段内循环和全局循环
```

#### 2.5 快速添加方案规格
```
预设模板:
  - HIIT训练: 30秒训练/10秒休息 × 8组
  - Tabata训练: 20秒训练/10秒休息 × 8组
  - 自重训练: 45秒训练/15秒休息 × 5组
  - 爬坡训练: 45分钟5阶段完整训练

快速操作:
  - 一键复制: 复制现有训练计划
  - 批量编辑: 同时修改多个阶段参数
  - 智能填充: 基于模板自动填充参数
  - 快速保存: 3步完成新计划创建

推荐算法:
  - 基于历史训练推荐相似方案
  - 根据训练目标推荐模板
  - 个性化参数调整建议
```

## 技术架构

### 1. 技术选型
- **开发框架**: Android Native (Java/Kotlin)
- **最低版本**: Android 8.0 (API Level 26)
- **目标版本**: Android 13 (API Level 33)

### 2. 系统架构
```
┌─────────────────┐
│    UI层         │
│  - Activity     │
│  - Fragment     │
│  - ViewModel    │
└─────────────────┘
         │
┌─────────────────┐
│   业务逻辑层     │
│  - Use Cases    │
│  - Services     │
└─────────────────┘
         │
┌─────────────────┐
│   数据层        │
│  - Repository   │
│  - Local DB     │
└─────────────────┘
```

### 3. 核心组件
- **计时器服务**: 后台计时服务
- **通知管理器**: 系统通知管理
- **声音管理器**: 音频播放控制
- **震动管理器**: 震动反馈控制
- **数据存储**: 本地数据库操作

## 用户界面设计

### 1. 设计原则
- **简洁直观**: 大字体显示，减少复杂操作
- **色彩区分**: 用颜色区分不同计时状态
- **易于操作**: 手势操作，快速设置
- **视觉反馈**: 动画效果增强用户体验

### 2. 界面结构

#### 2.1 主界面
```
┌─────────────────────────┐
│   [计划] [预设] [历史]    │  ← 底部导航
├─────────────────────────┤
│        ╔═══════╗        │
│        ║ 02:30 ║        │  ← 大字体计时器
│        ╚═══════╝        │
│                         │
│   阶段3: 间歇爬坡        │  ← 当前阶段
│   进度: ●●●●○○ 第2/4组   │  ← 阶段进度
│                         │
│   坡度: 10%  速度: 5.5   │  ← 训练参数
│   心率: 70-75%          │
│                         │
│   [开始] [暂停] [重置]    │  ← 控制按钮
└─────────────────────────┘
```

#### 2.2 计时器设置界面
```
┌─────────────────────────┐
│        ← 新建计时器      │
├─────────────────────────┤
│ 名称: [高强度HIIT训练]    │
│                         │
│ 训练时间: [30] 秒        │
│ 休息时间: [10] 秒        │
│ 循环次数: [8] 次         │
│ 预备时间: [5] 秒         │
│                         │
│ 提醒设置:                │
│   □ 声音提醒  □ 震动提醒  │
│                         │
│        [保存]            │
└─────────────────────────┘
```

#### 2.4 预设模板界面
```
┌─────────────────────────┐
│        ← 预设模板        │
├─────────────────────────┤
│  ○ HIIT训练              │
│    30秒训练 / 10秒休息    │
│                         │
│  ○ Tabata训练            │
│    20秒训练 / 10秒休息    │
│                         │
│  ○ 自重训练              │
│    45秒训练 / 15秒休息    │
│                         │
│  ○ 爬坡训练              │
│    45分钟多阶段训练       │
│                         │
│  + 自定义模板            │
└─────────────────────────┘
```

#### 2.6 快速添加界面
```
┌─────────────────────────┐
│        ← 快速添加        │
├─────────────────────────┤
│  推荐方案:               │
│  ○ 基于您上次的训练       │
│    45分钟爬坡训练         │
│                         │
│  常用模板:               │
│  ○ HIIT训练              │
│  ○ Tabata训练            │
│  ○ 爬坡训练              │
│                         │
│  快速操作:               │
│  [复制最近] [批量编辑]    │
│                         │
│  智能建议:               │
│  根据您的训练历史，推荐... │
└─────────────────────────┘
```

#### 2.7 批量编辑界面
```
┌─────────────────────────┐
│        ← 批量编辑        │
├─────────────────────────┤
│  选择阶段:               │
│  □ 阶段1: 动态热身        │
│  □ 阶段2: 基础爬坡        │
│  □ 阶段3: 间歇爬坡        │
│  □ 阶段4: 主动恢复        │
│  □ 阶段5: 冷身拉伸        │
│                         │
│  批量操作:               │
│  坡度调整: [+2%]         │
│  速度调整: [+0.5] km/h   │
│  时间调整: [+1] 分钟      │
│                         │
│        [应用]            │
└─────────────────────────┘
```

### 3. 颜色方案
- **主色调**: #FF6B35 (活力橙色)
- **训练状态**: #E74C3C (红色)
- **休息状态**: #2ECC71 (绿色)
- **背景色**: #F8F9FA (浅灰色)
- **文字色**: #2C3E50 (深蓝色)

## 交互设计

### 1. 用户流程

#### 1.1 新建训练流程
```
主界面 → 点击"+" → 设置界面 → 输入参数 → 保存 → 返回主界面
```

#### 1.2 快速添加流程
```
主界面 → 点击"快速添加" → 选择模板 → 智能填充 → 微调参数 → 保存
```

#### 1.3 模板复制流程
```
训练计划列表 → 长按计划 → 选择"复制" → 修改名称 → 保存为新计划
```

#### 1.4 批量编辑流程
```
训练计划详情 → 点击"批量编辑" → 选择阶段 → 设置批量参数 → 应用 → 保存
```

#### 1.5 开始训练流程
```
主界面 → 选择训练计划 → 点击"开始" → 计时运行 → 完成提醒 → 训练记录
```

#### 1.6 后台运行流程
```
开始训练 → 切换到其他APP → 后台继续计时 → 时间到达 → 系统通知 → 点击返回
```

### 2. 手势操作
- **左滑**: 删除计时器
- **右滑**: 编辑计时器
- **长按**: 重新排序
- **双击**: 快速开始/暂停

## 数据存储设计

### 1. 数据库设计

#### 1.1 训练计划表 (WorkoutPlan)
```sql
CREATE TABLE workout_plan (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,                    -- 训练计划名称
    description TEXT,                      -- 计划描述
    total_duration INTEGER,                -- 总时长(秒)
    type TEXT DEFAULT 'simple',            -- 类型: simple/multi_stage
    sound_enabled BOOLEAN DEFAULT 1,       -- 声音提醒
    vibration_enabled BOOLEAN DEFAULT 1,   -- 震动提醒
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
```

#### 1.2 训练阶段表 (WorkoutStage)
```sql
CREATE TABLE workout_stage (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    plan_id INTEGER NOT NULL,              -- 关联训练计划ID
    stage_order INTEGER NOT NULL,          -- 阶段顺序
    name TEXT NOT NULL,                    -- 阶段名称
    duration INTEGER NOT NULL,             -- 阶段时长(秒)
    stage_type TEXT NOT NULL,              -- 阶段类型: warmup/workout/rest/cooldown

    -- 训练参数
    incline_type TEXT,                     -- 坡度类型: constant/increasing/decreasing
    incline_start REAL,                    -- 起始坡度(%)
    incline_end REAL,                      -- 结束坡度(%)
    speed_start REAL,                      -- 起始速度(km/h)
    speed_end REAL,                        -- 结束速度(km/h)
    heart_rate_min INTEGER,                -- 最低心率
    heart_rate_max INTEGER,                -- 最高心率

    -- 循环设置(用于间歇训练)
    cycles INTEGER DEFAULT 1,              -- 循环次数
    work_duration INTEGER,                 -- 工作时长(秒)
    rest_duration INTEGER,                 -- 休息时长(秒)

    -- 描述信息
    description TEXT,                      -- 阶段描述
    tips TEXT,                             -- 训练技巧

    FOREIGN KEY (plan_id) REFERENCES workout_plan(id)
);
```

#### 1.2 训练记录表 (WorkoutRecord)
```sql
CREATE TABLE workout_record (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    timer_id INTEGER,                      -- 关联计时器ID
    start_time DATETIME NOT NULL,          -- 开始时间
    end_time DATETIME,                     -- 结束时间
    total_duration INTEGER,                -- 总时长(秒)
    completed_cycles INTEGER,              -- 完成循环数
    status TEXT DEFAULT 'completed',       -- 状态: completed/cancelled
    notes TEXT,                            -- 备注
    FOREIGN KEY (timer_id) REFERENCES timer(id)
);
```

#### 1.3 应用设置表 (AppSettings)
```sql
CREATE TABLE app_settings (
    key TEXT PRIMARY KEY,
    value TEXT NOT NULL
);
```

### 2. 数据模型

#### 2.1 数据模型

##### 2.1.1 WorkoutPlan数据模型
```kotlin
data class WorkoutPlan(
    val id: Long = 0,
    val name: String,
    val description: String? = null,
    val totalDuration: Int,
    val type: WorkoutType = WorkoutType.SIMPLE,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val stages: List<WorkoutStage> = emptyList(),
    val createdAt: Date = Date()
)
```

##### 2.1.2 WorkoutStage数据模型
```kotlin
data class WorkoutStage(
    val id: Long = 0,
    val planId: Long,
    val stageOrder: Int,
    val name: String,
    val duration: Int,
    val stageType: StageType,

    // 训练参数
    val inclineType: InclineType? = null,
    val inclineStart: Float? = null,
    val inclineEnd: Float? = null,
    val speedStart: Float? = null,
    val speedEnd: Float? = null,
    val heartRateMin: Int? = null,
    val heartRateMax: Int? = null,

    // 循环设置
    val cycles: Int = 1,
    val workDuration: Int? = null,
    val restDuration: Int? = null,

    // 描述信息
    val description: String? = null,
    val tips: String? = null
)
```

##### 2.1.3 WorkoutRecord数据模型
```kotlin
data class WorkoutRecord(
    val id: Long = 0,
    val planId: Long,
    val startTime: Date,
    val endTime: Date? = null,
    val totalDuration: Int? = null,
    val completedStages: Int? = null,
    val status: WorkoutStatus = WorkoutStatus.COMPLETED,
    val notes: String? = null
)
```

## 系统权限

### 1. 必要权限
- `WAKE_LOCK`: 防止设备休眠
- `VIBRATE`: 震动提醒
- `FOREGROUND_SERVICE`: 前台服务
- `POST_NOTIFICATIONS`: 发送通知(Android 13+)

### 2. 可选权限
- `READ_EXTERNAL_STORAGE`: 读取自定义铃声
- `WRITE_EXTERNAL_STORAGE`: 导出训练数据

## 性能要求

### 1. 响应时间
- 应用启动时间: < 2秒
- 界面切换: < 0.5秒
- 计时精度: ±100毫秒

### 2. 资源消耗
- 内存使用: < 100MB
- 后台运行: 低功耗模式
- 电池消耗: 优化后台服务

## 测试策略

### 1. 功能测试
- 计时器准确性测试
- 后台运行稳定性测试
- 通知系统测试
- 数据持久化测试

### 2. 兼容性测试
- Android 8.0 - 13 版本兼容
- 不同屏幕尺寸适配
- 不同厂商系统测试

### 3. 性能测试
- 长时间运行测试
- 多任务切换测试
- 电池消耗测试

## 发布计划

### 1. 开发阶段
- **阶段1**: 核心计时功能 (4周)
- **阶段2**: 用户界面和交互 (3周)
- **阶段3**: 数据存储和后台服务 (3周)
- **阶段4**: 测试和优化 (2周)

### 2. 发布渠道
- Google Play Store
- 国内应用市场(可选)

## 后续迭代

### 1. 功能扩展
- **训练计划分享**: 支持导出/导入训练计划
- **参数可视化**: 训练参数变化图表
- **语音指导**: 阶段切换语音提示
- **心率集成**: 与心率设备集成
- **训练数据分析**: 详细的训练统计和分析

### 2. 平台扩展
- iOS版本开发
- Wear OS支持
- Web版本

---

**文档版本**: v1.0
**创建日期**: 2025-11-13
**最后更新**: 2025-11-13
