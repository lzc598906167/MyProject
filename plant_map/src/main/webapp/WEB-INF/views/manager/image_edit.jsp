<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %><%--
  Created by IntelliJ IDEA.
  User: 小恒的
  Date: 2016/11/13
  Time: 20:37
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head lang="en">
    <title>图片信息编辑</title>
</head>
<body>
<div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
            aria-hidden="true">&times;</span></button>
    <h4 class="modal-title" id="myModalLabel">添加/修改图片</h4>
</div>
<div class="modal-body">
    <div class="container-fluid">
        <form class="form-horizontal" action="image/image_save"
              enctype="multipart/form-data" method="post">
            <div class="form-group ">
                <label class="col-xs-3 control-label">植物编号：</label>
                <div class="col-xs-8">
                    <s:textarea class="form-control input-sm duiqi"
                                name="image.plantId" rows="1"/>
                </div>
            </div>
            <div class="form-group">
                <label class="col-xs-3 control-label">图片类型：</label>
                <div class="col-xs-8 ">
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
                </div>
            </div>
            <div class="form-group">
                <label class="col-xs-3 control-label">图片地址：</label>
                <div class="col-xs-8">
                    <s:textarea class="form-control input-sm duiqi"
                                name="image.urladdress" rows="1" disabled="true"></s:textarea>
                    <s:hidden name="image.urladdress"/>
                    <input class="form-control input-sm duiqi" type="file" name="upload" />
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-sm btn-white" data-dismiss="modal">取
                    消
                </button>
                <button type="submit" class="btn btn-sm btn-green">保 存</button>
                <s:hidden name="image.id"/>
            </div>
        </form>
    </div>
</div>
</body>
</html>

