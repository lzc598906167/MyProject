<%--
  Created by IntelliJ IDEA.
  User: gome
  Date: 2017/1/25
  Time: 22:01
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head>
    <title>注册申请</title>
</head>
<body>
<div class="check-div form-inline">
    <div class="col-xs-5">
        <input type="text" class=" form-control input-sm" placeholder="输入文字搜索"
               style="height: 40px!important;">
        <button class="btn btn-white btn-xs ">查 询</button>
    </div>
    <div class="col-xs-4 col-lg-4  col-md-5"
         style="padding-right: 40px;text-align: right;float: right;">
    </div>

</div>
<div class="data-div" style="width: 97%;margin:0 auto;">
    <div class="table-responsive">
        <table class="table table-striped table-condensed table-hover" style="table-layout:fixed;"
               id="table1">
            <caption style="text-align:center;font-size: 25px;margin-bottom: 10px;">
                管理员账号注册信息
            </caption>
            <thead>
            <tr>
                <th>账号</th>
                <th>密码</th>
                <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <s:iterator value="users" var="user" status="status">
                <tr>
                    <td>${user.name}</td>
                    <td>${user.passwd}</td>
                    <td>
                        <a href="/user/user_update?user.id=${user.id}" class="btn btn-green btn-xs">
                            同意</a>
                    </td>
                </tr>
            </s:iterator>
            </tbody>
        </table>
    </div>

</div>


</body>
</html>
