<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2016/7/14
  Time: 14:14
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<html>
<head lang="en">
    <title>植物信息管理</title>
</head>
<body>
<div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
            aria-hidden="true">&times;</span></button>
    <h4 class="modal-title" id="myModalLabel">添加/修改区域</h4>
</div>
<div class="modal-body">
    <div class="container-fluid">
        <form class="form-horizontal" action="section/section_save">
            <div class="form-group ">
                <label class="col-xs-3 control-label">代表建筑名：</label>
                <div class="col-xs-8">
                    <s:textarea class="form-control input-sm duiqi"
                                name="section.name" rows="1"></s:textarea>
                </div>
            </div>
            <div class="form-group">
                <label class="col-xs-3 control-label">区域编号：</label>
                <div class="col-xs-8 ">
                    <s:textarea class="form-control input-sm duiqi"
                                name="section.compusId" rows="1"></s:textarea>
                </div>
            </div>
            <div class="form-group">
                <label class="col-xs-3 control-label">经度：</label>
                <div class="col-xs-8">
                    <s:textarea class="form-control input-sm duiqi"
                                name="section.lat" rows="1"></s:textarea>
                </div>
            </div>
            <div class="form-group">
                <label class="col-xs-3 control-label">纬度：</label>
                <div class="col-xs-8">
                    <s:textarea class="form-control input-sm duiqi"
                                name="section.log" rows="1"></s:textarea>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-sm btn-white" data-dismiss="modal">取
                    消
                </button>
                <button type="submit" class="btn btn-sm btn-green">保 存</button>
                <s:hidden name="section.id"/>
            </div>
        </form>
    </div>
</div>
</body>
</html>
