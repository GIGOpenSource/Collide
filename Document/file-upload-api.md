# 文件上传 API 接口文档

## 📋 目录
- [模块概述](#模块概述)
- [接口列表](#接口列表)
- [数据模型](#数据模型)
- [错误码定义](#错误码定义)
- [使用示例](#使用示例)

---

## 📚 模块概述

文件上传模块是 Collide 社交平台的基础功能，支持用户头像、内容图片/视频、附件等多种类型文件的上传和管理。

### 主要功能
- 单文件上传
- 批量文件上传
- 文件类型验证
- 文件大小限制
- 自动文件路径生成
- MinIO 对象存储集成

### 技术架构
- **存储**: MinIO 对象存储
- **认证**: Sa-Token 登录验证
- **验证**: 文件类型和大小校验
- **路径**: 自动生成唯一文件路径

### 支持的文件类型

| 文件类型 | 说明 | 支持格式 | 大小限制 |
|---------|------|---------|----------|
| **avatar** | 用户头像 | JPG、PNG、GIF、WebP | 5MB |
| **content** | 内容图片/视频 | JPG、PNG、GIF、WebP、MP4、AVI、MOV | 图片5MB，视频100MB |
| **attachment** | 通用附件 | 所有格式 | 20MB |

---

## 🔗 接口列表

### 1. 上传单个文件

**接口描述**: 上传单个文件

**请求信息**:
- **URL**: `POST /api/v1/files/upload`
- **Content-Type**: `multipart/form-data`
- **需要认证**: 是

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| file | File | 是 | 上传的文件 | profile.jpg |
| fileType | String | 是 | 文件类型 | avatar |
| fileName | String | 否 | 自定义文件名 | my-avatar.jpg |
| businessId | Long | 否 | 业务ID | 12345 |
| description | String | 否 | 文件描述 | 用户头像 |

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "fileId": "1744698765432123456",
    "fileUrl": "/files/avatar/20240125/abc123def456.jpg",
    "filePath": "avatar/20240125/abc123def456.jpg",
    "originalFileName": "profile.jpg",
    "fileType": "avatar",
    "fileSize": 1024000,
    "mimeType": "image/jpeg",
    "uploadTime": "2024-01-25T14:30:00",
    "uploadUserId": 12345
  },
  "traceId": "trace-123",
  "timestamp": 1706166600000
}
```

---

### 2. 批量上传文件

**接口描述**: 一次上传多个文件，常用于内容发布

**请求信息**:
- **URL**: `POST /api/v1/files/batch-upload`
- **Content-Type**: `multipart/form-data`
- **需要认证**: 是

**请求参数**:
| 参数名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| files | File[] | 是 | 上传的文件数组 | [image1.jpg, image2.png] |
| fileType | String | 是 | 文件类型 | content |
| businessId | Long | 否 | 业务ID（如内容ID） | 67890 |

**注意事项**:
- 一次最多上传 9 个文件
- 所有文件必须是同一类型

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "fileId": "1744698765432123456",
      "fileUrl": "/files/content/20240125/abc123def456.jpg",
      "filePath": "content/20240125/abc123def456.jpg",
      "originalFileName": "image1.jpg",
      "fileType": "content",
      "fileSize": 2048000,
      "mimeType": "image/jpeg",
      "uploadTime": "2024-01-25T14:30:00",
      "uploadUserId": 12345
    },
    {
      "fileId": "1744698765432123457",
      "fileUrl": "/files/content/20240125/def456ghi789.png",
      "filePath": "content/20240125/def456ghi789.png",
      "originalFileName": "image2.png",
      "fileType": "content",
      "fileSize": 1536000,
      "mimeType": "image/png",
      "uploadTime": "2024-01-25T14:30:01",
      "uploadUserId": 12345
    }
  ],
  "traceId": "trace-124",
  "timestamp": 1706166601000
}
```

---

### 3. 获取上传配置

**接口描述**: 获取文件上传的配置信息

**请求信息**:
- **URL**: `GET /api/v1/files/config`
- **需要认证**: 否

**响应示例**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "fileTypes": {
      "avatar": {
        "allowedTypes": ["image/jpeg", "image/png", "image/gif", "image/webp"],
        "maxSize": 5242880,
        "description": "用户头像"
      },
      "content": {
        "allowedTypes": ["image/jpeg", "image/png", "image/gif", "image/webp", "video/mp4", "video/avi", "video/mov"],
        "maxSize": 104857600,
        "description": "内容图片或视频"
      },
      "attachment": {
        "allowedTypes": ["*/*"],
        "maxSize": 20971520,
        "description": "通用附件"
      }
    },
    "maxBatchSize": 9,
    "uploadPath": "/api/v1/files/upload",
    "batchUploadPath": "/api/v1/files/batch-upload"
  },
  "traceId": "trace-125",
  "timestamp": 1706166602000
}
```

---

## 📊 数据模型

### FileUploadRequest

文件上传请求参数

```json
{
  "fileType": "avatar",
  "fileName": "profile.jpg",
  "businessId": 12345,
  "description": "用户头像"
}
```

| 字段名 | 类型 | 必填 | 验证规则 | 说明 |
|--------|------|------|----------|------|
| fileType | String | 是 | avatar/content/attachment | 文件类型分类 |
| fileName | String | 是 | 非空 | 文件名称 |
| businessId | Long | 否 | - | 业务关联ID |
| description | String | 否 | - | 文件描述 |

### FileUploadResponse

文件上传响应对象

```json
{
  "fileId": "1744698765432123456",
  "fileUrl": "/files/avatar/20240125/abc123def456.jpg",
  "filePath": "avatar/20240125/abc123def456.jpg",
  "originalFileName": "profile.jpg",
  "fileType": "avatar",
  "fileSize": 1024000,
  "mimeType": "image/jpeg",
  "uploadTime": "2024-01-25T14:30:00",
  "uploadUserId": 12345
}
```

| 字段名 | 类型 | 说明 |
|--------|------|------|
| fileId | String | 系统生成的文件唯一标识 |
| fileUrl | String | 文件访问URL |
| filePath | String | 文件相对路径 |
| originalFileName | String | 原始文件名 |
| fileType | String | 文件类型 |
| fileSize | Long | 文件大小（字节） |
| mimeType | String | 文件MIME类型 |
| uploadTime | DateTime | 上传时间 |
| uploadUserId | Long | 上传用户ID |

---

## ❌ 错误码定义

### 文件上传错误码

| 错误码 | HTTP状态码 | 说明 |
|--------|-----------|------|
| PARAM_ERROR | 400 | 参数错误 |
| UPLOAD_ERROR | 500 | 上传失败 |

### 具体错误信息

| 错误信息 | 说明 | 解决方案 |
|---------|------|----------|
| 文件不能为空 | 未选择文件 | 选择要上传的文件 |
| 文件类型只能是 avatar、content 或 attachment | 文件类型参数错误 | 使用正确的fileType参数 |
| 头像只支持JPG、PNG、GIF、WebP格式 | 头像文件格式不支持 | 转换为支持的图片格式 |
| 头像大小不能超过5MB | 头像文件过大 | 压缩图片大小 |
| 视频只支持MP4、AVI、MOV、WMV、FLV格式 | 视频格式不支持 | 转换为支持的视频格式 |
| 视频文件大小不能超过100MB | 视频文件过大 | 压缩视频文件 |
| 一次最多只能上传9个文件 | 批量上传文件数量超限 | 减少文件数量 |

---

## 💡 使用示例

### 1. 上传用户头像

```bash
# 使用 curl 上传头像
curl -X POST "http://localhost:9501/api/v1/files/upload" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "file=@avatar.jpg" \
  -F "fileType=avatar" \
  -F "description=用户头像"
```

### 2. 批量上传内容图片

```bash
# 使用 curl 批量上传
curl -X POST "http://localhost:9501/api/v1/files/batch-upload" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -F "files=@image1.jpg" \
  -F "files=@image2.png" \
  -F "files=@image3.gif" \
  -F "fileType=content" \
  -F "businessId=12345"
```

### 3. JavaScript 前端集成

```javascript
class FileUploadService {
  constructor() {
    this.baseURL = '/api/v1/files';
    this.maxRetries = 3;
  }

  /**
   * 上传单个文件
   */
  async uploadSingleFile(file, fileType, options = {}) {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('fileType', fileType);
    
    if (options.fileName) formData.append('fileName', options.fileName);
    if (options.businessId) formData.append('businessId', options.businessId);
    if (options.description) formData.append('description', options.description);

    try {
      const response = await fetch(`${this.baseURL}/upload`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${this.getToken()}`
        },
        body: formData
      });

      const result = await response.json();
      
      if (result.code === 200) {
        return { success: true, data: result.data };
      } else {
        return { success: false, message: result.message };
      }
    } catch (error) {
      console.error('文件上传失败:', error);
      return { success: false, message: '网络错误' };
    }
  }

  /**
   * 批量上传文件
   */
  async uploadMultipleFiles(files, fileType, businessId) {
    if (files.length > 9) {
      return { success: false, message: '一次最多上传9个文件' };
    }

    const formData = new FormData();
    
    // 添加所有文件
    Array.from(files).forEach(file => {
      formData.append('files', file);
    });
    
    formData.append('fileType', fileType);
    if (businessId) formData.append('businessId', businessId);

    try {
      const response = await fetch(`${this.baseURL}/batch-upload`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${this.getToken()}`
        },
        body: formData
      });

      const result = await response.json();
      
      if (result.code === 200) {
        return { success: true, data: result.data };
      } else {
        return { success: false, message: result.message };
      }
    } catch (error) {
      console.error('批量文件上传失败:', error);
      return { success: false, message: '网络错误' };
    }
  }

  /**
   * 获取上传配置
   */
  async getUploadConfig() {
    try {
      const response = await fetch(`${this.baseURL}/config`);
      const result = await response.json();
      
      if (result.code === 200) {
        return { success: true, data: result.data };
      } else {
        return { success: false, message: result.message };
      }
    } catch (error) {
      console.error('获取上传配置失败:', error);
      return { success: false, message: '网络错误' };
    }
  }

  /**
   * 文件预验证
   */
  validateFile(file, fileType, config) {
    const typeConfig = config.fileTypes[fileType];
    if (!typeConfig) {
      return { valid: false, message: '不支持的文件类型' };
    }

    // 检查文件大小
    if (file.size > typeConfig.maxSize) {
      const maxSizeMB = Math.round(typeConfig.maxSize / 1024 / 1024);
      return { valid: false, message: `文件大小不能超过${maxSizeMB}MB` };
    }

    // 检查文件格式
    const allowedTypes = typeConfig.allowedTypes;
    if (!allowedTypes.includes('*/*') && !allowedTypes.includes(file.type)) {
      return { valid: false, message: '不支持的文件格式' };
    }

    return { valid: true };
  }

  /**
   * 获取认证Token
   */
  getToken() {
    return localStorage.getItem('auth_token') || sessionStorage.getItem('auth_token');
  }
}

// 使用示例
const fileUploadService = new FileUploadService();

// 上传头像
document.getElementById('avatar-input').addEventListener('change', async (e) => {
  const file = e.target.files[0];
  if (!file) return;

  const result = await fileUploadService.uploadSingleFile(file, 'avatar', {
    description: '用户头像'
  });

  if (result.success) {
    console.log('头像上传成功:', result.data.fileUrl);
    // 更新页面显示
    document.getElementById('avatar-img').src = result.data.fileUrl;
  } else {
    alert('上传失败: ' + result.message);
  }
});

// 批量上传内容图片
document.getElementById('content-input').addEventListener('change', async (e) => {
  const files = e.target.files;
  if (!files.length) return;

  const result = await fileUploadService.uploadMultipleFiles(files, 'content', 12345);

  if (result.success) {
    console.log('批量上传成功:', result.data);
    // 显示上传的图片
    result.data.forEach(fileInfo => {
      const img = document.createElement('img');
      img.src = fileInfo.fileUrl;
      img.className = 'uploaded-image';
      document.getElementById('content-preview').appendChild(img);
    });
  } else {
    alert('批量上传失败: ' + result.message);
  }
});
```

### 4. React 组件示例

```jsx
import React, { useState, useCallback } from 'react';

const FileUploadComponent = ({ fileType = 'content', onUploadSuccess }) => {
  const [uploading, setUploading] = useState(false);
  const [uploadProgress, setUploadProgress] = useState(0);

  const handleFileUpload = useCallback(async (files) => {
    if (!files || files.length === 0) return;

    setUploading(true);
    setUploadProgress(0);

    try {
      const fileUploadService = new FileUploadService();
      
      if (files.length === 1) {
        // 单文件上传
        const result = await fileUploadService.uploadSingleFile(files[0], fileType);
        if (result.success) {
          onUploadSuccess?.([result.data]);
        } else {
          throw new Error(result.message);
        }
      } else {
        // 批量上传
        const result = await fileUploadService.uploadMultipleFiles(files, fileType);
        if (result.success) {
          onUploadSuccess?.(result.data);
        } else {
          throw new Error(result.message);
        }
      }
    } catch (error) {
      console.error('上传失败:', error);
      alert('上传失败: ' + error.message);
    } finally {
      setUploading(false);
      setUploadProgress(0);
    }
  }, [fileType, onUploadSuccess]);

  const handleDrop = useCallback((e) => {
    e.preventDefault();
    const files = Array.from(e.dataTransfer.files);
    handleFileUpload(files);
  }, [handleFileUpload]);

  const handleFileSelect = useCallback((e) => {
    const files = Array.from(e.target.files);
    handleFileUpload(files);
  }, [handleFileUpload]);

  return (
    <div 
      className="file-upload-area"
      onDrop={handleDrop}
      onDragOver={(e) => e.preventDefault()}
    >
      <input
        type="file"
        multiple={fileType === 'content'}
        accept={fileType === 'avatar' ? 'image/*' : fileType === 'content' ? 'image/*,video/*' : '*/*'}
        onChange={handleFileSelect}
        disabled={uploading}
        style={{ display: 'none' }}
        id="file-input"
      />
      
      <label htmlFor="file-input" className="upload-label">
        {uploading ? (
          <div>
            <div>上传中... {uploadProgress}%</div>
            <div className="progress-bar">
              <div 
                className="progress-fill" 
                style={{ width: `${uploadProgress}%` }}
              />
            </div>
          </div>
        ) : (
          <div>
            <div>点击选择文件或拖拽文件到此处</div>
            <div className="upload-hint">
              {fileType === 'avatar' && '支持 JPG、PNG、GIF、WebP 格式，最大 5MB'}
              {fileType === 'content' && '支持图片和视频，图片最大 5MB，视频最大 100MB'}
              {fileType === 'attachment' && '支持所有格式，最大 20MB'}
            </div>
          </div>
        )}
      </label>
    </div>
  );
};

export default FileUploadComponent;
```

---

## 🔧 部署配置

### MinIO 配置

在 `collide-users/src/main/resources/application.yml` 中配置：

```yaml
spring:
  oss:
    enabled: true
    bucket: collide-files
    endPoint: http://localhost:9000
    accessKey: minioadmin
    accessSecret: minioadmin
```

### 文件大小限制配置

在 Spring Boot 中配置文件上传大小限制：

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 100MB      # 单个文件最大大小
      max-request-size: 1000MB  # 请求最大大小（批量上传）
```

---

**文件上传功能现已完整实现，支持多种文件类型和安全验证！** 🚀 