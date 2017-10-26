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
    <h4 class="modal-title" id="myModalLabel">添加/修改植物</h4>
</div>
<div class="modal-body">
    <div class="container-fluid">
        <form class="form-horizontal" action="plant/plant_save">
            <div class="form-group ">
                <label class="col-xs-3 control-label">学名：</label>
                <div class="col-xs-8">
                    <s:textarea class="form-control input-sm duiqi"
                                name="plant.name" rows="1"/>
                </div>
            </div>
            <div class="form-group">
                <label class="col-xs-3 control-label">拉丁名：</label>
                <div class="col-xs-8 ">
                    <s:textarea class="form-control input-sm duiqi"
                                name="plant.latName" rows="1"></s:textarea>
                </div>
            </div>
            <div class="form-group">
                <label class="col-xs-3 control-label">别名：</label>
                <div class="col-xs-8">
                    <s:textarea class="form-control input-sm duiqi"
                                name="plant.aliaseName" rows="1"></s:textarea>
                </div>
            </div>
            <div class="form-group">
                <label class="col-xs-3 control-label">科：</label>
                <div class="col-xs-8">
                    <s:textarea class="form-control input-sm duiqi"
                                name="plant.familyName" rows="1"></s:textarea>
                </div>
            </div>
            <div class="form-group">
                <label class="col-xs-3 control-label">属名：</label>
                <div class="col-xs-8">
                    <s:textarea class="form-control input-sm duiqi"
                                name="plant.genusName" rows="1"></s:textarea>
                </div>
            </div>
            <div class="form-group">
                <label class="col-xs-3 control-label">分布：</label>
                <div class="col-xs-8">
                    <s:textarea class="form-control input-sm duiqi"
                                name="plant.distribution" rows="3"></s:textarea>
                </div>
            </div>
            <div class="form-group">
                <label class="col-xs-3 control-label">外观：</label>
                <div class="col-xs-8">
                    <s:textarea class="form-control input-sm duiqi"
                                name="plant.exterion" rows="3"></s:textarea>
                </div>
            </div>
            <div class="form-group">
                <label class="col-xs-3 control-label">花：</label>
                <div class="col-xs-8">
                    <s:textarea class="form-control input-sm duiqi"
                                name="plant.flower" rows="3"></s:textarea>
                </div>
            </div>
            <div class="form-group">
                <label class="col-xs-3 control-label">枝条：</label>
                <div class="col-xs-8">
                    <s:textarea class="form-control input-sm duiqi"
                                name="plant.branch" rows="3"></s:textarea>
                </div>
            </div>
            <div class="form-group">
                <label class="col-xs-3 control-label">叶片：</label>
                <div class="col-xs-8">
                    <s:textarea class="form-control input-sm duiqi"
                                name="plant.leaf" rows="3"></s:textarea>
                </div>
            </div>
            <div class="form-group">
                <label class="col-xs-3 control-label">果实：</label>
                <div class="col-xs-8">
                    <s:textarea class="form-control input-sm duiqi"
                                name="plant.fruit" rows="3"></s:textarea>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-sm btn-white" data-dismiss="modal">取
                    消
                </button>
                <button type="submit" class="btn btn-sm btn-green">保 存</button>
                <s:hidden name="plant.id"/>
            </div>
        </form>
    </div>
</div>
</body>
</html>
