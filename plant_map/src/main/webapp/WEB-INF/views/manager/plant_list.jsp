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
                植物列表
                <a class="btn btn-yellow btn-xs" data-toggle="modal"
                   href="plant/plant_add" data-target="#addPlant" style="position: relative;
                                        font-size: 18px;float: right;bottom: -20px;right: 20px;">添加植物
                </a>
            </caption>
            <thead>
            <tr>
                <th width="10%">植物编号</th>
                <th width="16%">学名</th>
                <th width="16%">科</th>
                <th width="16%">属名</th>
                <th width="16%">拉丁名</th>
                <th width="10%">更多</th>
                <th width="16%">操作</th>
            </tr>
            </thead>
            <tbody>
            <s:iterator value="plants" var="plant" status="status">
                <tr>
                    <td>${plant.id}</td>
                    <td>${plant.name}</td>
                    <td>${plant.familyName}</td>
                    <td>${plant.genusName}</td>
                    <td>${plant.latName}</td>
                    <script type="text/javascript">
                        $(function () {
                            var morestring = "<table class='table table-condensed " +
                                    "table-bordered' width='1114.520px'" +
                                    "style='TABLE-LAYOUT:fixed;word-wrap:break-word;word-break:break-all;white-space:normal;'>"
                                    + "<tr class='success'>"
                                    + "<th width='100px'>别名</th>"
                                    + "<td width='1014.520px'>${plant.aliaseName}</td>"
                                    + "</tr>"
                                    + "<tr class='active'>"
                                    + "<th>分布</th>"
                                    + "<td>${plant.distribution}</td>"
                                    + "</tr>"
                                    + "<tr class='warning'>"
                                    + "<th>外观</th>"
                                    + "<td>${plant.exterion}</td>"
                                    + "</tr>"
                                    + "<tr class='active'>"
                                    + "<th>花</th>"
                                    + "<td>${plant.flower}</td>"
                                    + "</tr>"
                                    + "<tr class='info'>"
                                    + "<th>枝条</th>"
                                    + "<td>${plant.branch}</td>"
                                    + "</tr>"
                                    + "<tr class='active'>"
                                    + "<th>叶片</th>"
                                    + "<td>${plant.leaf}</td>"
                                    + "</tr>"
                                    + "<tr class='danger'>"
                                    + "<th>果实</th>"
                                    + "<td>${plant.fruit}</td>"
                                    + "</tr>"
                                    + "<table>";
                            $("#${status.count+100}").popover({
                                container: 'body',
                                trigger: 'focus',
                                placement: 'auto',
                                container: 'body',
                                /*   title : '<div style="text-align:center; color:red; text-decoration:underline; font-size:14px;"> Muah ha ha</div>', */
                                title: '<div style="text-align:center;font-size:14px;">更多植物信息</div>',
                                html: 'true',
                                content: morestring,
                                animation: true
                            });
                        });
                    </script>
                    <th><a id="${status.count+100}" tabindex="0" role="button">更多</a></th>
                    <td>
                        <a href="plant/plant_edit?plant.id=${plant.id}" data-toggle="modal"
                           class="btn btn-green btn-xs"
                           data-target="#editPlant${status.count}" id="${status.count+200}">编辑</a>
                        <a class="btn btn-danger btn-xs" data-toggle="modal"
                           data-target="#deletePlant${status.count}">删除
                        </a>
                    </td>
                </tr>
                <!--弹出模态窗口 编辑植物-->
                <div class="modal fade" id="editPlant${status.count}" role="dialog" tabindex="-1">
                    <div class="modal-dialog" role="document">
                        <div class="modal-content">
                        </div>
                    </div>
                </div>

                <!--弹出删除警告窗口-->

                <div class="modal fade" id="deletePlant${status.count}" role="dialog" aria-labelledby="gridSystemModalLabel">
                    <div class="modal-dialog" role="document">
                        <div class="modal-content">
                            <div class="modal-header">
                                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                                        aria-hidden="true">&times;</span></button>
                                <h4 class="modal-title" id="gridSystemModalLabel">提示</h4>
                            </div>
                            <div class="modal-body">
                                <div class="container-fluid">
                                    确定要删除该植物？删除后不可恢复！
                                </div>
                            </div>
                            <form class="form-horizontal" action="plant/plant_delete">
                                <div class="modal-footer">
                                    <input type="hidden" value="${plant.id}" name="plant.id"/>
                                    <button type="button" class="btn btn-xs btn-white" data-dismiss="modal">取 消</button>
                                    <button type="submit" class="btn btn-xs btn-danger">确 定</button>
                                </div>
                            </form>
                        </div>
                        <!-- /.modal-content -->
                    </div>
                    <!-- /.modal-dialog -->
                </div>
                <!-- /.modal -->
            </s:iterator>
            </tbody>
        </table>
    </div>

</div>
<!--页码块-->
<footer class="footer">
    <ul class="pagination">
        <li>
            <select>
                <option>1</option>
                <option>2</option>
                <option>3</option>
                <option>4</option>
                <option>5</option>
                <option>6</option>
                <option>7</option>
                <option>8</option>
                <option>9</option>
                <option>10</option>
            </select>
            页
        </li>
        <li class="gray">
            共20页
        </li>
        <li>
            <a href="">上一页</a>
        </li>
        <li>
            <a href="">下一页</a>
        </li>
    </ul>
</footer>


<!--弹出模态窗口 添加植物-->
<div class="modal fade" id="addPlant" role="dialog" tabindex="-1">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
        </div>
    </div>
</div>

</body>
</html>
