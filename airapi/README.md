# AirAPI

## 总览

baseURL：http://airapi.jptx.me

不区分GET和POST，除特殊说明以外，任何参数都不能为空。

------

# ETH地址



## 获取ETH地址

/ethAddr/get

根据不同币种，随机获取一个未使用过的eth地址

|  参数  |  说明  |
| :--: | :--: |
| coin |  币种  |

返回示例

```javascript
{
    "msg": "success",
    "data": {
        "address": "0x0000000000000000000000000000000000"
    },
    "status": 200
}

```

``

## 标记此ETH地址为已用

/ethAddr/markUsed

标记此eth地址为此币种已使用

|   参数    |  说明   |
| :-----: | :---: |
|  coin   |  币种   |
| address | eth地址 |

返回示例

```javascript
{
    "msg": "success",
    "data": {},
    "status": 200
}
```

------

# 账号密码



## 记录账户密码

/account/add

记录下此账户的邮箱和密码

|   参数   |         说明         |
| :------: | :------------------: |
|  email   |         邮箱         |
| password |         密码         |
|   coin   |         币种         |
|   flag   | 自定义标识（可为空） |

返回示例

```javascript
{
    "msg": "success",
    "data": {},
    "status": 200
}
```



## 获取账户和密码

/account/get

根据不同的币种，随机获取邮箱和密码，此邮箱和密码为以前存过的，同时可以根据自定义标识来获取对应的邮箱和密码。

| 参数 |         说明         |
| :--: | :------------------: |
| coin |         币种         |
| flag | 自定义标识（可为空） |

返回示例

```javascript
{
    "msg": "success",
    "data": {
        "password": "vehih@20boxme.org",
        "email": "vehih@20boxme.org"
    },
    "status": 200
}
```



## 更新账户标识

/account/setFlag

修改此账户的自定义标识。

| 参数  |    说明    |
| :---: | :--------: |
| email |    邮箱    |
| coin  |    币种    |
| flag  | 自定义标识 |

返回示例

```javascript
{
    "msg": "success",
    "data": {},
    "status": 200
}
```



## 获取账户标识

/account/getFlag

获取此账户的自定义标识

| 参数  | 说明 |
| :---: | :--: |
| email | 邮箱 |
| coin  | 币种 |

返回示例

```javascript
{
    "msg": "success",
    "data": {
        "flag": "0"
    },
    "status": 200
}
```

------

# 临时邮箱

临时邮箱仅限本服务器接受到的邮件

## 获取邮件列表

/email/list

获取某账户下的邮件列表

|  参数   | 说明 |
| :-----: | :--: |
| account | 邮箱 |

返回示例

```javascript
{
    "msg": "success",
    "data": [
        {
            "receiveTime": "2018-04-07 15:58:55",
            "sender": "admin@jptxpc.com",
            "subject": "test mail",
            "id": 4
        },
        {
            "receiveTime": "2018-04-07 15:21:16",
            "sender": "admin@jptxpc.com",
            "subject": "来自管理员的邮件",
            "id": 1
        }
    ],
    "status": 200
}
```



## 查找特定发送者来获取邮件列表

/email/findBySender

查找特定发送者来获取邮件列表

|  参数   |    说明    |
| :-----: | :--------: |
| account |    邮箱    |
| sender  | 发送者邮箱 |

返回示例

```javascript
{
    "msg": "success",
    "data": [
        {
            "receiveTime": "2018-04-07 15:58:55",
            "sender": "admin@jptxpc.com",
            "subject": "test mail",
            "id": 4
        },
        {
            "receiveTime": "2018-04-07 15:21:16",
            "sender": "admin@jptxpc.com",
            "subject": "来自管理员的邮件",
            "id": 1
        }
    ],
    "status": 200
}
```



## 查找特定主题来获取邮件列表

/email/findBySubject

查找特定主题来获取邮件列表

|  参数   |   说明   |
| :-----: | :------: |
| account |   邮箱   |
| subject | 邮件主题 |

返回示例

```javascript
{
    "msg": "success",
    "data": [
        {
            "receiveTime": "2018-04-07 15:58:55",
            "sender": "admin@jptxpc.com",
            "subject": "test mail",
            "id": 4
        }
    ],
    "status": 200
}
```



## 获取邮件内容

/email/getContent

获取邮件内容

| 参数 |  说明  |
| :--: | :----: |
|  id  | 邮件id |

返回示例

```javascript
{
    "msg": "success",
    "data": {
        "content": "测试内容"
    },
    "status": 200
}
```



## 删除邮件

/email/delete

删除邮件

| 参数 |  说明  |
| :--: | :----: |
|  id  | 邮件id |

返回示例

```javascript
{
    "msg": "success",
    "data": {
        "rows": 1
    },
    "status": 200
}
```



## 清空账户下的邮件

/email/clearAccount

清空账户下的邮件

|  参数   | 说明 |
| :-----: | :--: |
| account | 邮箱 |

返回示例

```javascript
{
    "msg": "success",
    "data": {
        "rows": 1
    },
    "status": 200
}
```







