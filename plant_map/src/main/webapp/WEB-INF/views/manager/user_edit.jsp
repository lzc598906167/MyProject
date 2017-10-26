<%--
  Created by IntelliJ IDEA.
  User: gome
  Date: 2017/1/24
  Time: 18:26
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title>注册账户</title>
</head>
<body>
    <h1 align="center">注册管理账户</h1>
    <form action="${rootPath}/user/user_add" method="post">
        <h3 align="center">
            登录账号：<input type="text" name="user.name"/><br/>
            登录密码：<input type="password" name="user.passwd"/><br/>
            <input type="submit" value="提交"/>
        </h3>
    </form>
</body>
</html>
