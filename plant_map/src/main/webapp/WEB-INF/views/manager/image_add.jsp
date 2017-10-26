<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%--
  Created by IntelliJ IDEA.
  User: gome
  Date: 2017/3/21
  Time: 20:32
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head lang="en">
    <title>添加图片信息</title>
</head>
<body>
<div class="container">
    <div class="header">
        <%@include file="../include/header.jsp"%>
    </div>
    <div class="middle">
        <div class="leftMenu">
            <%@include file="../include/leftmenu.jsp"%>
        </div>
        <div class="mainFrame">
            <s:form action="image_add" enctype="multipart/form-data" method="post">
                <table border="1" width="400">
                    <caption>添加图片信息</caption>
                    <tr>
                        <th width="60">植物id：</th>
                        <td>
                            <s:textfield name="image.plantId" />
                        </td>
                    </tr>
                    <tr>
                        <th width="60">图片类型：</th>
                        <td>
                            <select name="image.type">
                                <option value="exterior" name="image.type">外观</option>
                                <option value="flower" name="image.type">花</option>
                                <option value="branch" name="image.type">枝条</option>
                                <option value="fruit" name="image.type">果实</option>
                                <option value="leaf" name="image.type">叶子</option>
                            </select>
                                <%--<s:textfield name="image.type" />--%>
                        </td>
                    </tr>
                    <tr>
                        <th width="60">图片地址：</th>
                        <td>
                            <input type="file" name="upload"/>
                        </td>
                    </tr>
                    <tr>
                        <th colspan="2">
                            <input type="submit" value="确定" />
                        </th>
                    </tr>
                </table>
            </s:form>

        </div>
    </div>
    <div class="footer">
        <%@include file="../include/footer.jsp"%>
    </div>
</div>
</body>
</html>