<%@ taglib prefix="s" uri="/struts-tags" %>
<%--
  Created by IntelliJ IDEA.
  User: 小恒的
  Date: 2016/11/6
  Time: 10:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta HTTP-EQUIV="pragma" CONTENT="no-cache">
    <meta HTTP-EQUIV="Cache-Control" CONTENT="no-cache, must-revalidate">
    <meta HTTP-EQUIV="expires" CONTENT="Wed, 26 Feb 1997 08:21:57 GMT">
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <meta name="format-detection" content="telephone=no">
    <script src="${rootPath}/js/jquery.min.js"></script>
    <script src="${rootPath}/js/bootstrap.min.js"></script>
    <title>管理员登陆</title>
    <link rel="stylesheet" href="${rootPath}/css/main.css" />
    <meta http-equiv="x-ua-compatible" content="IE=11"/>
    <!--[if lt IE 9]>
    <script src="${rootPath}/js/html5shiv.min.js"></script>
    <script src="${rootPath}/js/respond.min.js"></script>
    <![endif]-->
    <link rel="stylesheet" type="text/css" href="${rootPath}/css/htmleaf-demo.css">
    <link href="${rootPath}/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" type="text/css" href="${rootPath}/css/bootstrap.min.css"/>
</head>
<body>
<div class="htmleaf-container">
    <header class="htmleaf-header">
        <h1>西北农林科技大学校园植物管理系统<span>版权所有</span></h1>
    </header>
    <div class="demo form-bg" style="padding: 20px 0;">
        <div class="container">
            <div class="row">
                <div class="col-md-offset-3 col-md-6">
                    <form method="post" action="${rootPath}/user/user_login" class="form-horizontal">
                        <span class="heading">用户登录</span>
                        <div class="form-group">
                            <input type="text" class="form-control" id="inputEmail3" placeholder="用户名" name="user.name">
                            <i class="fa fa-user"></i>
                        </div>
                        <div class="form-group help">
                            <input type="password" class="form-control" id="inputPassword3" placeholder="密　码" name="user.passwd">
                            <i class="fa fa-lock"></i>
                            <a href="#" class="fa fa-question-circle"></a>
                        </div>
                        <div class="form-group">
                            <span class="text">
                                <a href="${rootPath}/user/user_regist" class="btn btn-default text">注册</a>
                            </span>
                            <button type="submit" value="登录" class="btn btn-default">登录</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
    <div class="related">
    </div>
</div>
</body>
</html>
